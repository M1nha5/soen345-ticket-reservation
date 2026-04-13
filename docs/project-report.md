# Final Project Report

## Project Information

- Course: SOEN 345 Software Testing, Verification and Quality Assurance
- Term: Winter 2026
- Project: Cloud-based Ticket Reservation Application
- Submission date: April 13, 2026

## Software Development Method

The project used a Scrum-style iterative approach with short implementation cycles and continuous validation.

Iteration 1 focused on project setup, Firebase integration, and domain modeling.
Iteration 2 implemented core customer and admin/organizer workflows.
Iteration 3 focused on confirmations, hardening, testing, CI, and deployment readiness.

Definition of done for each story required:

- Complete implementation in app and backend layers
- Verification by unit/component tests or acceptance checks
- Integration validation against Firebase project
- Documentation updates in `docs/`

## System Design

### Users and Roles

- Customer: browse/filter events, reserve tickets, cancel reservations
- Organizer: create/edit/cancel events after admin approval
- Admin: approve/reject organizers and manage events

### Core Data Model (Firestore)

- `users`: uid, name, email, phone, role, status, createdAt
- `events`: title, category, location, dateTimeMillis, totalTickets, availableTickets, organizerId, status, createdAt
- `reservations`: userId, eventId, eventTitle, eventLocation, eventDateTimeMillis, tickets, status, createdAt
- `confirmations`: reservationId, userId, eventId, action, smsStatus, emailStatus, createdAt

### Concurrency Strategy

Reservation creation and cancellation use Firestore transactions to ensure ticket counts remain consistent under concurrent access.

## Testing Method and Results

### Unit and Component Testing

Executed on April 13, 2026 with Gradle:

- Command: `./gradlew testDebugUnitTest`
- Total tests: 10
- Passed: 10
- Failed: 0
- Errors: 0
- Skipped: 0

Covered areas:

- Search and filtering logic
- Role and approval policy logic
- Reservation capacity validation logic
- Combined booking flow policy behavior

### Functional and Acceptance Testing

Functional UI checks include login screen visibility validation in Espresso and manual acceptance scenarios for end-to-end business flows.

Manual acceptance scenarios covered:

- Customer email registration and login
- Phone OTP login
- Organizer registration entering pending status
- Admin organizer approval/rejection
- Event add/edit/cancel by authorized users
- Customer browse/filter/reserve/cancel flows
- Confirmation trigger on reserve/cancel

Detailed checklist and evidence template are tracked in `docs/testing-plan.md` and `docs/acceptance-results.md`.

## Development Tools

- IDE: Android Studio
- Version Control: Git and GitHub
- CI/CD: GitHub Actions workflow at `.github/workflows/android-ci.yml`
- Unit Testing: JUnit
- Functional Testing: Espresso
- Cloud Platform: Firebase Authentication, Firestore, Cloud Functions

## Non-Functional Requirements Coverage

### Concurrent Users Without Performance Degradation

- Transactional reservation updates prevent overselling under concurrent requests.
- Firestore handles horizontal scaling for concurrent reads/writes.
- Data writes are scoped to minimal documents per transaction to reduce contention.

### Cloud-Based High Availability

- Authentication, data storage, and callable backend logic are hosted on Firebase managed services.
- Cloud Functions in `us-central1` provide managed execution for confirmations.
- Firestore and Functions provide managed reliability and automatic service scaling.

### Simple and User-Friendly UI

- Mobile-first screens with focused actions per role
- Clear navigation for browse, reservations, event management, and approvals
- Input validation and error toasts for common failure cases

## Outstanding Notes

- Cloud Functions runtime is configured for Node.js 22 to avoid Node.js 20 deprecation window.
- Firebase project currently hosts live Firestore rules and `sendConfirmation` callable function.
