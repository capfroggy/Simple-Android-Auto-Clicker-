# Simple Android Auto Clicker

A small, free and open-source auto clicker for Android.

**No ads. No tracking. No accounts. No unnecessary features.**

## Features

- Drag a floating target anywhere on the screen.
- Hold **Volume Up for 3 seconds** to start auto-clicking.
- Double-press **Volume Up** to stop.
- Adjustable click interval from **50 ms to 2000 ms**.
- Works without root.
- Uses Android's Accessibility Service only for the features required by the app.
- Stores configuration locally on the device.

## Install

Download the APK from the latest GitHub Actions build artifact.

Because the app is not distributed through Google Play, Android may ask you to allow installation from your browser or file manager.

## Setup

1. Install and open **Simple Auto Clicker**.
2. Tap **Open Accessibility Settings**.
3. Enable **Simple Auto Clicker**.
4. Return to the app and set the click speed.
5. Drag the floating target to the point you want to tap.
6. Hold **Volume Up** for 3 seconds to start.
7. Double-press **Volume Up** to stop.

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
