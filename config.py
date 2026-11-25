
from pathlib import Path


# --------------------------------------------------
# PROJECT DIRECTORIES
# --------------------------------------------------

BASE_DIR = Path(__file__).resolve().parent

DATA_DIR = BASE_DIR / "data"

CV_DIRECTORY = DATA_DIR / "cvs"

INTERNSHIP_DIRECTORY = DATA_DIR / "internships"

PROCESSED_DIRECTORY = DATA_DIR / "processed"


# --------------------------------------------------
# DATABASE FILES
# --------------------------------------------------

INTERNSHIP_DATABASE = (
    INTERNSHIP_DIRECTORY / "internships.json"
)

APPLICATION_DATABASE = (
    DATA_DIR / "applications.json"
)


# --------------------------------------------------
# CV UPLOAD CONFIGURATION
# --------------------------------------------------

MAX_CV_SIZE = 5 * 1024 * 1024  # 5 MB

ALLOWED_CV_EXTENSIONS = {
    ".pdf",
}

ALLOWED_CV_MIME_TYPES = {
    "application/pdf",
}


# --------------------------------------------------
# API CONFIGURATION
# --------------------------------------------------

API_TITLE = "AI Internship Recommendation System"

API_DESCRIPTION = (
    "Backend API for CV analysis, candidate profiling, "
    "internship recommendations, cover letter generation, "
    "and application tracking."
)

API_VERSION = "1.0.0"

API_PREFIX = "/api/v1"


# --------------------------------------------------
# CORS CONFIGURATION
# --------------------------------------------------

# Development configuration.
# Restrict origins before production deployment.

CORS_ORIGINS = [
    "http://localhost:3000",
    "http://127.0.0.1:3000",
]

CORS_ALLOW_CREDENTIALS = True

CORS_ALLOW_METHODS = ["*"]

CORS_ALLOW_HEADERS = ["*"]


# --------------------------------------------------
# RECOMMENDATION CONFIGURATION
# --------------------------------------------------

DEFAULT_RECOMMENDATION_LIMIT = 100

MAX_RECOMMENDATION_LIMIT = 100


# --------------------------------------------------
# APPLICATION TRACKING CONFIGURATION
# --------------------------------------------------

APPLICATION_STATUSES = {
    "saved",
    "applied",
    "shortlisted",
    "rejected",
    "accepted",
}


# --------------------------------------------------
# DIRECTORY INITIALIZATION
# --------------------------------------------------

for directory in (
    DATA_DIR,
    CV_DIRECTORY,
    INTERNSHIP_DIRECTORY,
    PROCESSED_DIRECTORY,
):
    directory.mkdir(
        parents=True,
        exist_ok=True,
    )
