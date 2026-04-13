# Deployment and Secrets

## Android App

- Place Firebase config file at `app/google-services.json`.
- Enable Email/Password and Phone authentication in Firebase Authentication.
- Ensure Firestore database exists and rules are deployed.

## Cloud Functions

- Deploy functions from `functions/`.
- Configure environment values:
  - `TWILIO_SID`
  - `TWILIO_AUTH_TOKEN`
  - `TWILIO_FROM_NUMBER`
  - `GMAIL_USER`
  - `GMAIL_APP_PASSWORD`

## Security

- Never commit live keys.
- Rotate Twilio token and Gmail app password before production submission.
- Keep CI secrets in GitHub repository secrets.
