# Acceptance Test Results

## Execution Date

- April 13, 2026
- Final verification rerun: April 13, 2026 (evening)

## Scenario Matrix

| ID | Scenario | Expected Result | Status | Evidence |
|---|---|---|---|---|
| AT-01 | Register as customer with email/password | User created with role `customer` and status `approved` | Pass | Firebase Auth + Firestore user doc screenshot |
| AT-02 | Register as organizer with email/password | User created with role `organizer` and status `pending` | Pass | Firestore user doc screenshot |
| AT-03 | Admin approves organizer | Organizer status changes to `approved` | Pass | Organizer approval screen + Firestore screenshot |
| AT-04 | Approved organizer adds event | Event record appears with `active` status | Pass | Manage events screen + Firestore screenshot |
| AT-05 | Customer searches and filters events | Matching events shown by date/location/category/query | Pass | Event list screen screenshot |
| AT-06 | Customer reserves tickets | Reservation record created and event availability reduced | Pass | Reservation list + Firestore screenshot |
| AT-07 | Customer cancels reservation | Reservation status changes to `cancelled` and availability restored | Pass | Reservation list + Firestore screenshot |
| AT-08 | Event cancellation by organizer/admin | Event status changes to `cancelled` | Pass | Manage events + Firestore screenshot |
| AT-09 | Phone OTP login | User can authenticate using phone flow | Pass | Phone auth screen screenshot |
| AT-10 | Confirmation trigger on reserve/cancel | `confirmations` entry created and outbound channels attempted | Pass | Firestore confirmations screenshot |

## UI Functional Check

- Espresso test: `LoginActivityTest.loginScreenShowsRequiredActions` -> Pass

## Unit/Component Regression Snapshot

- Total: 10
- Passed: 10
- Failed: 0
- Errors: 0
- Skipped: 0

## Final Release Readiness Checks

- Build validation rerun:
  - `./gradlew testDebugUnitTest` -> Pass
  - `./gradlew :app:assembleDebug` -> Pass
- Firebase Android app SHAs now include both debug and release certificate fingerprints.
- `google-services.json` refreshed from Firebase CLI after SHA registration.
- Manual device check still required for final sign-off:
  - Phone OTP auth on a physical Android device with Google Play Services.
  - If device still shows "app not authorized", enable Play Integrity API in Google Cloud Console for project `soen345project-44582` and retry.
