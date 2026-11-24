
import os
import re
import time

import requests
from dotenv import load_dotenv

from config import INTERNSHIP_DATABASE
from src.internship_model import Internship
from src.internship_repository import InternshipRepository


load_dotenv()

API_URL = "https://api.headcountlabs.com/v1/jobs"

MAX_RETRIES = 5
REQUEST_TIMEOUT = 60

INTERNSHIP_TITLE_PATTERN = re.compile(
    r"\b("
    r"intern(ship)?|"
    r"industrial attachment|"
    r"attachment|"
    r"trainee|"
    r"apprentice(ship)?|"
    r"graduate program(me)?"
    r")\b",
    re.IGNORECASE,
)


def unwrap(value):
    """Extract the value from a Headcount state/value field."""
    if isinstance(value, dict) and "state" in value:
        return value.get("value")

    return value


def get_text(value):
    """Extract readable text from Headcount Labs fields."""
    value = unwrap(value)

    if value is None:
        return ""

    if isinstance(value, str):
        return value.strip()

    if isinstance(value, (int, float)):
        return str(value)

    if isinstance(value, dict):
        if "value" in value:
            return get_text(value["value"])

        for key in ("name", "text", "label"):
            if key in value:
                return get_text(value[key])

    return ""


def request_page(headers, params):
    """Request one API page, retrying temporary errors."""
    for attempt in range(MAX_RETRIES):
        try:
            response = requests.get(
                API_URL,
                headers=headers,
                params=params,
                timeout=REQUEST_TIMEOUT,
            )

            if response.status_code in (429, 502, 503, 504):
                if attempt == MAX_RETRIES - 1:
                    response.raise_for_status()

                retry_after = response.headers.get("Retry-After")

                if retry_after and retry_after.isdigit():
                    wait_seconds = int(retry_after)
                else:
                    wait_seconds = 2 ** attempt

                print(
                    f"API returned {response.status_code}. "
                    f"Retrying in {wait_seconds} seconds "
                    f"(attempt {attempt + 1}/{MAX_RETRIES})..."
                )

                time.sleep(wait_seconds)
                continue

            response.raise_for_status()
            return response.json()

        except (
            requests.exceptions.Timeout,
            requests.exceptions.ConnectionError,
        ) as error:
            if attempt == MAX_RETRIES - 1:
                raise

            wait_seconds = 2 ** attempt

            print(
                f"Temporary connection error: {error.__class__.__name__}. "
                f"Retrying in {wait_seconds} seconds "
                f"(attempt {attempt + 1}/{MAX_RETRIES})..."
            )

            time.sleep(wait_seconds)

    raise RuntimeError("Could not retrieve API page after retries.")


def get_locations(job):
    """Collect and return Kenya locations from a job."""
    found = []

    raw_locations = unwrap(job.get("locations"))

    if isinstance(raw_locations, dict):
        found.append(raw_locations)

    elif isinstance(raw_locations, list):
        for item in raw_locations:
            item = unwrap(item)

            if isinstance(item, dict):
                found.append(item)

    postings = job.get("postings") or []

    if isinstance(postings, dict):
        postings = [postings]

    for posting in postings:
        if not isinstance(posting, dict):
            continue

        location = unwrap(posting.get("location"))

        if isinstance(location, dict):
            found.append(location)

    kenya_locations = []
    seen = set()

    for location in found:
        country = get_text(location.get("country")).upper()

        if country not in ("KE", "KEN", "KENYA"):
            continue

        city = (
            get_text(location.get("city"))
            or get_text(location.get("region"))
            or "Kenya"
        )

        key = (city.strip().lower(), country)

        if key not in seen:
            seen.add(key)
            kenya_locations.append(location)

    return kenya_locations


def get_application_url(job):
    """Find a usable application or posting URL."""
    postings = job.get("postings") or []

    if isinstance(postings, dict):
        postings = [postings]

    for posting in postings:
        if not isinstance(posting, dict):
            continue

        application_url = get_text(
            posting.get("application_url")
        )

        posting_url = get_text(
            posting.get("posting_url")
        )

        if application_url.startswith("http"):
            return application_url

        if posting_url.startswith("http"):
            return posting_url

    return None


def map_work_arrangement(job):
    """Convert Headcount work arrangement to our model values."""
    value = get_text(
        job.get("work_arrangement")
    ).lower()

    if value == "remote":
        return "remote"

    if value == "hybrid":
        return "hybrid"

    return "onsite"


def map_job(job):
    """Convert one Headcount Labs job into our Internship model."""
    title = get_text(job.get("title"))
    company = get_text(job.get("employer_name"))
    description = get_text(job.get("description"))

    if not title or not company:
        return None

    if not INTERNSHIP_TITLE_PATTERN.search(title):
        return None

    kenya_locations = get_locations(job)

    if not kenya_locations:
        return None

    city_names = []

    for location in kenya_locations:
        city = (
            get_text(location.get("city"))
            or get_text(location.get("region"))
        )

        if city:
            city_names.append(city.strip())

    city_names = list(dict.fromkeys(city_names))

    location_text = (
        ", ".join(city_names)
        if city_names
        else "Kenya"
    )

    if "Kenya" not in location_text:
        location_text += ", Kenya"

    application_url = get_application_url(job)

    if not application_url:
        return None

    job_id = get_text(job.get("job_id"))

    if not job_id:
        return None

    return Internship(
        id=f"headcount-{job_id}",
        title=title,
        organization=company,
        description=description or title,
        required_skills=[],
        preferred_skills=[],
        education_requirements=[],
        minimum_experience_months=0,
        eligibility_criteria=[],
        location=location_text,
        work_arrangement=map_work_arrangement(job),
        application_deadline=None,
        application_url=application_url,
        is_active=True,
    )


def fetch_kenya_jobs(api_key, max_pages=10):
    """Fetch current Kenya jobs, following the API cursor."""
    headers = {
        "Authorization": f"Bearer {api_key}",
    }

    params = {
        "country": "KE",
        "currentness": "current",
        "stale": "false",
        "limit": 250,
    }

    all_jobs = []
    page_count = 0

    while page_count < max_pages:
        page = request_page(headers, params)

        jobs = page.get("data", [])

        if not isinstance(jobs, list):
            raise ValueError(
                "Unexpected API response: data is not a list."
            )

        all_jobs.extend(jobs)
        page_count += 1

        cursor = page.get("next_cursor")

        if not cursor:
            break

        params["cursor"] = cursor

    return all_jobs, page_count


def normalize_for_deduplication(value):
    """Normalize text for duplicate detection."""
    value = (value or "").lower().strip()
    value = re.sub(r"[^\w]+", " ", value)

    return " ".join(value.split())


def internship_deduplication_key(internship):
    """Create a company/title/location key."""
    return (
        normalize_for_deduplication(
            internship.organization
        ),
        normalize_for_deduplication(
            internship.title
        ),
        normalize_for_deduplication(
            internship.location
        ),
    )


def import_kenya_internships(dry_run=True):
    """Fetch, filter, deduplicate, and optionally save internships."""
    api_key = os.getenv("HEADCOUNTLABS_API_KEY")

    if not api_key:
        raise RuntimeError(
            "HEADCOUNTLABS_API_KEY is missing from your .env file."
        )

    jobs, pages = fetch_kenya_jobs(api_key)

    mapped_jobs = []

    # Map every job returned by the API.
    for job in jobs:
        internship = map_job(job)

        if internship:
            mapped_jobs.append(internship)

    repository = InternshipRepository(
        INTERNSHIP_DATABASE
    )

    existing = repository.load_all()

    existing_keys = {
        internship_deduplication_key(item)
        for item in existing
    }

    unique_new_jobs = []
    seen_keys = set()

    for internship in mapped_jobs:
        key = internship_deduplication_key(
            internship
        )

        # Skip listings already in the database.
        if key in existing_keys:
            continue

        # Skip duplicate listings in this API import.
        if key in seen_keys:
            continue

        seen_keys.add(key)
        unique_new_jobs.append(internship)

    # Preserve existing listings by ID.
    combined = {
        internship.id: internship
        for internship in existing
    }

    # Add new listings.
    for internship in unique_new_jobs:
        combined[internship.id] = internship

    print(f"API pages fetched: {pages}")
    print(f"Kenya jobs received: {len(jobs)}")
    print(
        f"Internship-like matches: {len(mapped_jobs)}"
    )
    print(
        f"Unique new listings: {len(unique_new_jobs)}"
    )
    print(f"Existing listings: {len(existing)}")
    print(f"Total after import: {len(combined)}")

    print("\nUnique listings to add:")

    for internship in unique_new_jobs:
        print(
            f"- {internship.organization}: "
            f"{internship.title} "
            f"({internship.location})"
        )

    # Dry run is the default; do not save.
    if dry_run:
        print("\nDRY RUN: no changes were saved.")
        return

    # Save only when --apply was provided.
    repository.save_all(
        list(combined.values())
    )

    print(
        "\nImport complete. "
        "Internship JSON database updated."
    )


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--apply",
        action="store_true",
        help=(
            "Save imported listings. "
            "Without this flag, run a dry run."
        ),
    )

    args = parser.parse_args()

    import_kenya_internships(
        dry_run=not args.apply
    )
