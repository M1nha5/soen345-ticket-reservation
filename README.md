# SOEN 345 Ticket Reservation App

Cloud-based Android ticket reservation system for SOEN 345 (Winter 2026).

## Student Information

- Name: Harjot Minhas
- Student ID: 40315397
- Public Repository: [https://github.com/M1nha5/soen345-ticket-reservation](https://github.com/M1nha5/soen345-ticket-reservation)

## Project Scope

The application supports ticket reservations for events such as concerts, sports, and movies with role-based access:

- Customer
- Organizer
- Admin

## Functional Features

- Register/login with email and password
- Login with phone number and OTP (Firebase Phone Auth)
- Browse active events
- Search and filter events by category, location, and date
- Reserve tickets
- Cancel reservations
- Organizer registration with admin approval workflow
- Admin approval/rejection of organizer accounts
- Organizer/admin event management:
  - add event
  - edit event
  - cancel event
- Confirmation records on reserve/cancel via Firebase Cloud Functions

## Non-Functional Coverage

- Concurrent reservation consistency through Firestore transactions
- Cloud-based high availability via Firebase managed services
- Mobile-first user-friendly UI with improved empty states and visual polish

## Tech Stack

- Android Studio (Java + XML)
- Firebase Authentication
- Cloud Firestore
- Firebase Cloud Functions (Node.js 22)
- JUnit + Mockito (unit/component testing)
- Espresso (functional UI testing)
- GitHub + GitHub Actions (CI)

## Repository Structure

- `app/` Android application source
- `functions/` Firebase Cloud Functions source
- `docs/` project documentation, testing artifacts, report content
- `.github/workflows/` CI pipeline
- `firestore.rules` Firestore security rules

## Firebase Configuration

1. Place `google-services.json` in:
   - `app/google-services.json`
2. Enable authentication providers:
   - Email/Password
   - Phone
3. Firestore database mode:
   - Production
4. Enable Cloud Functions on Blaze plan
5. Ensure Android app package is:
   - `com.harjot.ticketreservation`
6. Ensure debug and release SHA fingerprints are registered for the Firebase Android app.

## Build and Run

From project root:

1. Unit tests:
   - `./gradlew testDebugUnitTest`
2. Debug build:
   - `./gradlew :app:assembleDebug`
3. Signed release build:
   - `./gradlew :app:assembleRelease`

APK outputs:

- Debug: `app/build/outputs/apk/debug/`
- Release: `app/build/outputs/apk/release/`

## Test Accounts (Example)

- Admin:
  - Email: `new.admin@soen.local`
  - Password: `Admin@12345`
- Organizer:
  - Email: `system.organizer@soen.local`
  - Password: `System@12345`
- Customer:
  - Email: `demo.customer@soen.local`
  - Password: `Customer@12345`

## Testing Documentation

- Test plan: `docs/testing-plan.md`
- Acceptance results: `docs/acceptance-results.md`
- User stories: `docs/user-stories.md`
- Deployment notes: `docs/deployment.md`
- Final report source: `docs/final-report.tex`

## CI

GitHub Actions workflow:

- `.github/workflows/android-ci.yml`

The pipeline validates build and unit tests on pushes to `main`.
