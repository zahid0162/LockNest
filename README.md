# LockNest - Secure Offline Password Manager

LockNest is a modern, secure, and offline password manager for Android. It helps users store and manage their passwords locally on their device without relying on cloud services, providing enhanced privacy and security.

## Features

### Security
- **Offline Storage**: All data is stored locally on your device
- **PIN Protection**: Access the app with a secure PIN
- **Biometric Authentication**: Unlock using fingerprint or face recognition
- **Auto-Lock**: Automatically locks after a configurable period of inactivity
- **Encrypted Storage**: All passwords are encrypted using AES-256 encryption

### Password Management
- **Categorized Storage**: Organize passwords by categories (Social, Work, Finance, etc.)
- **Password Generator**: Create strong, random passwords with customizable options
- **Password Strength Meter**: Visual indicator of password security
- **Search & Filter**: Quickly find passwords by name or category

### Data Management
- **Secure Backup & Restore**: Export and import encrypted backups
- **PDF Export**: Export passwords to PDF with optional PIN protection
- **Dark Mode**: Eye-friendly dark theme support

## Screenshots
(Screenshots will be added here)

## Architecture

LockNest is built using modern Android development practices:

- **MVVM Architecture**: Clean separation of UI and business logic
- **Jetpack Compose**: Modern, declarative UI toolkit
- **Room Database**: SQLite abstraction for local data storage
- **Kotlin Coroutines & Flow**: Asynchronous programming
- **Hilt**: Dependency injection
- **EncryptedSharedPreferences**: Secure storage for sensitive settings
- **Biometric Authentication**: Integration with device biometric systems

## Security Measures

- **AES-256 Encryption**: Industry-standard encryption for all sensitive data
- **SHA-256 Hashing**: Secure hashing for PIN verification
- **AndroidKeyStore**: Hardware-backed security for encryption keys
- **No Internet Permission**: The app does not require internet access
- **No Analytics or Tracking**: Your data stays on your device

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Gradle 7.0 or higher

### Installation
1. Clone the repository:
   ```
   git clone https://github.com/yourusername/LockNest.git
   ```

2. Open the project in Android Studio

3. Build and run the app on your device or emulator

### First-Time Setup
1. Create a PIN when you first launch the app
2. Optionally enable biometric authentication
3. Start adding your passwords

## Usage

### Adding a Password
1. Tap the "+" button on the home screen
2. Enter the title, username, and password
3. Optionally add website URL and notes
4. Select a category
5. Tap "Save"

### Generating a Strong Password
1. When adding or editing a password, tap the refresh icon in the password field
2. Customize the password options (length, character types)
3. Tap "Use This Password"

### Exporting Passwords
1. Go to Settings > Data Management
2. Tap "Export Passwords to PDF"
3. Enter your PIN to confirm
4. Choose whether to include actual passwords in the export
5. Select a location to save the PDF file

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Material Design 3](https://m3.material.io/) for UI components
- [iText PDF](https://itextpdf.com/) for PDF generation
- [Android Jetpack](https://developer.android.com/jetpack) for modern Android development

## Contact

For questions or feedback, please open an issue on the GitHub repository. 