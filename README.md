# Simple Android Auto Clicker

![Build](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/actions/workflows/build.yml/badge.svg)

A small, free and open-source auto clicker for Android.

**No ads. No tracking. No accounts. No unnecessary features.**

## Features

- Drag a floating target anywhere on the screen.
- Choose how you want to toggle the auto clicker:
  - hold **Volume Up for 3 seconds**; or
  - **double-press Volume Up**.
- The selected shortcut toggles clicking ON and OFF.
- Adjustable click interval from **50 ms to 2000 ms**.
- Works without root.
- Uses Android's Accessibility Service only for the features required by the app.
- Stores configuration locally on the device.

## Install

Download the APK from the latest GitHub Actions build artifact.

Because the app is installed from an APK, Android may ask you to allow installation from your browser or file manager. On Android 13+, Accessibility can also be blocked by **Restricted settings**. Open **App info → ⋮ → Allow restricted settings**, then enable the service under Accessibility.

## Setup

1. Install and open **Simple Auto Clicker**.
2. Tap **Open Accessibility Settings**.
3. Enable **Simple Auto Clicker**.
4. Return to the app and set the click speed.
5. Drag the floating target to the point you want to tap.
6. Choose your preferred activation shortcut.
7. Use that same shortcut to toggle clicking ON and OFF.

## Android 13+ restricted settings

If **Simple Auto Clicker** appears disabled in Accessibility or Android says access was denied:

1. Open **Settings → Apps → Simple Auto Clicker**.
2. Tap the **⋮** menu in the top-right corner.
3. Choose **Allow restricted settings**.
4. Authenticate with your PIN, fingerprint, or screen lock if Android asks.
5. Return to **Settings → Accessibility → Downloaded apps → Simple Auto Clicker** and enable it.

This is an Android security protection for apps installed from APK files; it is not an extra permission requested by the app.

## Accessibility permission

Android does not allow ordinary apps to generate taps inside other apps. Simple Auto Clicker uses an Accessibility Service only to:

- receive the Volume Up shortcut;
- display the movable target overlay;
- dispatch the configured tap gesture.

The app has no networking permission and does not collect or transmit screen contents, passwords, messages, or personal information.

## Build from source

Requirements:

- JDK 17
- Android SDK 35
- Gradle 8.9

Build with:

```bash
gradle assembleDebug
```

The APK is generated at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Privacy

Simple Auto Clicker:

- has no ads;
- has no analytics;
- has no tracking SDKs;
- has no account system;
- makes no network requests;
- stores settings only on the local device.

## License

MIT License. See [LICENSE](LICENSE).
