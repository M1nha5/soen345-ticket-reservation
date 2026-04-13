# SOEN 345 Ticket Reservation App

Android Java application backed by Firebase Authentication, Cloud Firestore, and Cloud Functions.

## Features

- Email/password registration and login
- Phone OTP login
- Customer event browsing with search and filters (date, location, category)
- Ticket reservation and cancellation
- Organizer registration with admin approval workflow
- Admin organizer approval and rejection
- Organizer/admin event management (add, edit, cancel)
- SMS and email confirmations through Cloud Functions

## Tech Stack

- Android Studio (Java, XML)
- Firebase Auth
- Firestore
- Firebase Functions (Node.js)
- JUnit and Espresso
- GitHub Actions

## Local Setup

1. Add `app/google-services.json` from Firebase console.
2. Enable Firebase Auth providers:
   - Email/Password
   - Phone
3. Create Firestore database and deploy `firestore.rules`.
4. Install function dependencies:
   - `cd functions`
   - `npm install`
5. Set function environment variables for Twilio and Gmail.
6. Build and run tests:
   - `./gradlew testDebugUnitTest`

## Testing

See:
- `docs/testing-plan.md`
- `docs/user-stories.md`
- `docs/deployment.md`
