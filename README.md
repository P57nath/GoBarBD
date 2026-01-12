# GoBarBD

## Firebase setup
- Add `app/google-services.json`
- Enable Email/Password in Firebase Auth
- Create Firestore collections: `users`, `shops`, `shops/{shopId}/services`, `shops/{shopId}/barbers`, `bookings`, `reviews`, `chats`
- Optional: upload `firestore.rules` from this repo in Firebase Console

## QA checklist
- Register → verify email → login → role routing
- Forgot password email sent
- Browse shops → detail → book appointment → invoice
- Booking appears in Active; cancel moves to History
- Review submits and completes booking
- Chat list loads and messages send
