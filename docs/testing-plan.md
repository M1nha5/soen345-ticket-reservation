# Test Plan

## Unit and Component Tests

- EventFilterServiceTest
  - filterByLocationAndCategoryReturnsMatches
  - filterByDateMatchesExactDay
- RolePolicyServiceTest
  - adminCanManageAndApprove
  - pendingOrganizerCannotManage
  - approvedOrganizerCanManage
- ReservationPolicyServiceTest
  - canReserveWhenCapacityIsEnough
  - cannotReserveWhenCapacityIsNotEnough
  - cannotReserveWithInvalidTicketCount
- BookingFlowComponentTest
  - filteredEventCanBeReservedWhenAvailable

## Functional and Acceptance Tests

- LoginActivityTest
  - loginScreenShowsRequiredActions
- Manual acceptance scenarios
  - Email registration as customer succeeds
  - Email registration as organizer creates pending state
  - Admin can approve organizer
  - Approved organizer can add/edit/cancel event
  - Customer can browse, filter, reserve, and cancel
  - Reservation reserve/cancel triggers confirmation records and outbound SMS/email when secrets are configured

## Evidence Checklist

- Capture screenshots for each acceptance scenario in `docs/screenshots/`
- Export CI workflow run logs for build and unit tests
- Include Firebase console screenshots for Auth providers, Firestore, and Functions deployment
