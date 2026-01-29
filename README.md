# Firebase-based Chat Feature

This project includes a chat feature powered by Firebase. Firebase is used for real-time data synchronization, user authentication, and message storage, enabling seamless communication between users.

## Features
- Real-time messaging between users
- Secure message storage in Firebase Realtime Database or Firestore

## Setup Instructions
1. **Firebase Project**: Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. **Add Android App**: Register your Android app in the Firebase project and download the `google-services.json` file.
3. **Add Config File**: Place the `google-services.json` file in the `app/` directory of your project.
4. **Enable Authentication**: In the Firebase Console, enable the desired authentication method (e.g., Email/Password, Google Sign-In).
5. **Enable Database**: Set up either Firebase Realtime Database or Firestore and configure the security rules as needed.
6. **Sync Gradle**: Make sure your project dependencies include Firebase libraries. Sync your project with Gradle files.

## Usage
- Launch the app and sign in using the enabled authentication method.
- Access the chat feature to send and receive messages in real time.
- All messages are stored securely in Firebase and synchronized across devices.
