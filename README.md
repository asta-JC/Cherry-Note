<div align="center">

# 🍒 Cherry Note

<p align="center">
  <img src="app/src/main/res/drawable-nodpi/icon.png" width="108" alt="Cherry Note Logo" />
</p>

> A calm little home for your thoughts. A beautiful, offline Android notebook with warm cherry colors, a simple editor, and Material 3 design. 📝✨

</div>

---

## 📱 Overview

**Cherry Note** is a local-first note-taking app built with Kotlin and Jetpack Compose. Capture a quick thought, keep a checklist, or organize your journal into collections. Notes save automatically, search lives right on the home screen, and extra editor controls stay tucked into a single **Note options** panel.

Creamy light mode and near-black plum dark mode share a distinctive cherry accent. Five note colors keep their personality in both themes, with little hand-drawn cherries on each note card.

---

## ✨ Key Features

- 📝 **Simple, Auto-Saving Editor**: Write a title and body, then tap Done. Changes are stored locally as you type.
- 🔎 **Instant Search**: Find notes by title, content, or tags without leaving Home.
- 📌 **Pinned Notes**: Keep important thoughts at the top of your notebook.
- ✅ **Interactive Checklists**: Add, edit, complete, and remove individual items.
- 📂 **Custom Collections**: Choose a name, symbol, and color; create notes directly inside a collection.
- 🎨 **Five Note Colors**: Cherry, Sand, Sage, Lavender, and Honey, with distinct light and dark variants.
- 🌗 **Personal Appearance**: Light, dark, or system theme, plus Modern and Literary typography.
- 💾 **Local Storage**: Room persists notes and collections; DataStore remembers your appearance preferences. No account or internet permission required.
- 📱 **Comfortable Layouts**: Adaptive note columns, keyboard insets, labeled controls, and smooth screen transitions.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: MVVM with a repository layer and lifecycle-aware `StateFlow`
- **Navigation**: Navigation Compose
- **Local Storage**: Room Persistence Library & Preferences DataStore
- **Concurrency**: Kotlin Coroutines
- **Build Tools**: Gradle Kotlin DSL, version catalog, and KSP
- **Branding**: SVG source artwork, PNG launcher assets, adaptive icons, and themed monochrome icon support

---

## 📂 Project Structure

```text
Cherry-Note/
├── app/src/main/
│   ├── java/com/asta/cherry_note/
│   │   ├── data/
│   │   │   ├── NoteStore.kt       # Active database, repository, preferences & ViewModel
│   │   │   ├── local/            # Read-only compatibility database for note recovery
│   │   │   └── model/            # Compatibility entities & checklist serialization
│   │   ├── ui/theme/             # Cherry theme, typography & light/dark note palettes
│   │   └── MainActivity.kt       # Navigation, screens & reusable Compose components
│   └── res/                      # PNG icons, adaptive launcher & Android resources
├── app/src/test/                 # Search & color/contrast regression tests
├── app/src/androidTest/          # On-device note persistence test
├── artwork/cherry-note.svg       # Editable source for the app icon
└── gradle/                       # Version catalog & Gradle wrapper
```

---

## 🚀 Getting Started & Build Instructions

### Prerequisites

- Android Studio with support for the project's Android Gradle Plugin
- JDK 25 (the included Gradle daemon toolchain requests version 25)
- Android SDK 37
- Android 7.0 / API 24 or newer to run the app

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/asta-JC/Cherry-Note.git
   cd Cherry-Note
   ```

2. **Open in Android Studio**:
   - Select **Open**, choose the project folder, and let Gradle sync.
   - Use your Android SDK installation and a compatible Gradle JDK.

3. **Build & Run**:
   - Connect an Android device or start an emulator, then run the **app** configuration.
   - Or build from the terminal:
     ```bash
     ./gradlew :app:assembleDebug
     ```
   - The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

4. **Run Tests**:
   ```bash
   ./gradlew :app:testDebugUnitTest
   ```
   With a connected test device or emulator:
   ```bash
   ./gradlew :app:connectedDebugAndroidTest
   ```

---

## 🔒 Your Notes & Privacy

Notes live in the local Room database and survive app restarts. Appearance preferences are stored in DataStore. Android backup follows the device's system policy; clearing app storage or uninstalling removes local data unless restored by Android backup.

For users upgrading from an earlier experimental build, a one-time compatibility import recovers active notes and collections from its separate database. Existing notes in the current database are retained.

---

## 📄 License

This project is developed as part of Asta's project series. Feel free to use and adapt for personal and educational use!

---

<p align="center">
  Made with ❤️ & 🍒 by Asta
</p>
