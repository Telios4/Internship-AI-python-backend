# MobileApplicationProject
This project is designed to help students discover internship, industrial attachment, and entry-level job opportunities that align with their academic backgrounds, technical skills, career interests, and experience.

The application aims to reduce the time students spend manually searching for opportunities and comparing their qualifications against job requirements.

It combines a native Android application, a Python backend, a PostgreSQL database, and AI-powered matching capabilities.

## Android Setup

Requirements:
- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- Android emulator or physical Android device
- Internet access for dependency downloads

Clone the repository

## API Integration

The Android application uses Retrofit to communicate with the backend.

For the standard Android emulator:

private const val BASE_URL = "http://10.0.2.2:8000/"

10.0.2.2 connects the Android emulator to the development computer's localhost.