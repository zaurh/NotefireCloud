Notefire Cloud is a note-taking app where you can note anything online. Once you register, you can access your notes from any phone. Firebase Auth handles accounts and Cloud Firestore stores and syncs the notes in real time (including offline).

# Preview

![App Screenshot](https://i.hizliresim.com/cues8po.png)

# Features

- Email sign up / sign in / password reset
- Real-time synced notes that also work offline
- Pin notes, color notes, search
- Delete with undo
- Light & dark theme (Material 3)
- In-app account deletion (required by Google Play)

# Tech stack

- Kotlin, Jetpack Compose, Material 3
- **MVI** architecture (State / Intent / Effect per screen)
- Clean Architecture layers: `domain` (models, repository interfaces, use cases, validation),
  `data` (Firebase implementations), `presentation` (screens, ViewModels, theme)
- Kotlin Coroutines & Flow
- Dagger Hilt (dependency injection)
- Navigation Compose with type-safe routes (kotlinx.serialization)
- Firebase Auth & Cloud Firestore
- AndroidX SplashScreen, edge-to-edge

# Project structure

```
core/mvi/MviViewModel.kt        base class: state StateFlow + onIntent() + one-off effects
core/di/                        Hilt modules (Firebase, repositories, app scope)
domain/                         Note, AuthUser, AppError, repositories, DeleteAccountUseCase, AuthValidator
data/                           FirebaseAuthRepository, FirestoreNoteRepository, Firestore mapping
presentation/
  auth/{signin,signup,forgot}/  <Name>Contract.kt, <Name>ViewModel.kt, <Name>Screen.kt
  notes/{list,editor}/          same pattern
  navigation/                   routes + NavHost (switches graphs from the auth state)
  theme/, components/, util/
```

Each screen follows the same MVI loop:

```
Screen ──Intent──▶ ViewModel ──setState──▶ State ──▶ Screen
                       └──────sendEffect──▶ Effect (navigate, snackbar) ──▶ Screen
```

# Firebase

- Firestore rules live in `firestore.rules`. Deploy them with `firebase deploy --only firestore:rules`.
- Run locally against the emulators with `firebase emulators:start --only auth,firestore` (needs JDK 21+).

# Release

1. Create `keystore.properties` in the project root (git-ignored):
   ```
   storeFile=/absolute/path/to/upload-keystore.jks
   storePassword=...
   keyAlias=...
   keyPassword=...
   ```
2. Bump `versionCode` / `versionName` in `app/build.gradle.kts`.
3. `./gradlew bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`
