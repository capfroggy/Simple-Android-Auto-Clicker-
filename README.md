# Simple Android Auto Clicker

![Simple Android Auto Clicker](docs/assets/simple-android-auto-clicker-banner.webp)

![Build](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/actions/workflows/build.yml/badge.svg)

**A tiny Android auto clicker that stays under your control.**

No ads. No tracking. No account. No Internet permission. Free and open source.

[**Download the latest APK**](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/releases/latest/download/Simple-Android-Auto-Clicker.apk)

[Español](README.es.md)

## Why this exists

Most auto clickers are overloaded with ads, trackers, subscriptions, or permissions that are hard to trust. This project intentionally does less:

- one movable click target;
- one adjustable click speed;
- one physical-button shortcut;
- no cloud services or remote control.

## Features

- Drag a floating target anywhere on screen.
- Adjustable interval from **50 ms to 2000 ms**.
- Choose one Volume Up shortcut to toggle clicking ON/OFF:
  - **Hold for 3 seconds**
  - **Double press**
- Works without root.
- Settings stay on the device.

## Privacy by design

| Capability | Included? |
|---|---|
| Ads | No |
| Analytics / telemetry | No |
| Tracking SDKs | No |
| Account | No |
| Internet permission | No |
| Remote control | No |
| Cloud sync | No |
| Local settings | Yes |
| Accessibility Service | Yes — only for shortcuts, overlay and taps |

See [PRIVACY.md](PRIVACY.md) for the full explanation.

## Install

1. Open the [latest release](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/releases/latest).
2. Download **Simple-Android-Auto-Clicker.apk**.
3. Install it on your Android device.
4. Open the app and follow the two setup buttons.

### Android 13+ — Restricted settings

Android may block Accessibility for APKs installed outside an app store.

If **Simple Auto Clicker** is greyed out:

1. Open **Settings → Apps → Simple Auto Clicker**.
2. Tap **⋮** in the top-right corner.
3. Choose **Allow restricted settings**.
4. Authenticate if Android asks.
5. Return to **Settings → Accessibility → Downloaded apps → Simple Auto Clicker**.
6. Enable the service.

This is an Android security measure. The app does not bypass it.

## How to use

1. Enable the Accessibility Service.
2. Drag the floating target to the point you want to tap.
3. Choose your shortcut:
   - hold **Volume Up** for 3 seconds, or
   - double-press **Volume Up**.
4. Use that shortcut to toggle auto-clicking ON.
5. Use the same shortcut again to stop.

## Why Accessibility is required

Android does not allow ordinary apps to create taps inside other apps. The Accessibility Service is used only to:

- detect the selected Volume Up shortcut;
- display the movable target overlay;
- dispatch taps at the selected position.

The app does **not** request Internet access and does not transmit screen contents or personal information.

## Verify the APK

Each release includes:

- `Simple-Android-Auto-Clicker.apk`
- `Simple-Android-Auto-Clicker.apk.sha256`

You can verify the file with:

```bash
sha256sum Simple-Android-Auto-Clicker.apk
```

## F-Droid

This repository is prepared for submission to the official F-Droid repository with Fastlane metadata, an F-Droid build recipe, localized store text and release-build validation. See [F-Droid submission notes](docs/FDROID_SUBMISSION.md).

The app is **not yet listed in the official F-Droid repository** until F-Droid completes its independent review and merges the submission.

## Build from source

Requirements:

- JDK 17
- Android SDK 35
- Gradle 8.9

```bash
gradle assembleDebug
```

Output:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Contributing

Bug reports, small improvements and focused pull requests are welcome. Please keep the project aligned with its core principle: **simple, local, transparent and ad-free**.

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

MIT — see [LICENSE](LICENSE).
