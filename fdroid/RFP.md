# F-Droid Request For Packaging

Use this text for the official F-Droid RFP if a direct fdroiddata merge request is not opened first.

- [x] The app complies with the F-Droid inclusion criteria.
- [x] The app is not currently listed in the official F-Droid repository.
- [x] Upstream includes Fastlane metadata, localized descriptions, icon, screenshot and changelog.
- [x] The original author is requesting/approving inclusion.

## Application ID

`com.capfroggy.simpleautoclicker`

## Source code

https://github.com/capfroggy/Simple-Android-Auto-Clicker-

## Upstream release

https://github.com/capfroggy/Simple-Android-Auto-Clicker-/releases/tag/v1.2.1

## License

MIT

## Category

System

## Summary

Simple, private Android auto clicker with no ads or tracking

## Description

Simple Android Auto Clicker is a small, free and open-source utility for repetitive taps.

It has one movable click target, an adjustable interval from 50 ms to 2000 ms, and a user-selectable Volume Up shortcut (hold for 3 seconds or double press) to toggle clicking on and off.

The app has no ads, analytics, tracking SDKs, accounts, cloud sync or Internet permission. Settings remain on the device.

Android requires an Accessibility Service to generate taps inside other apps. This app uses Accessibility only to receive the selected Volume Up shortcut, show the target overlay and dispatch user-configured taps. Window-content retrieval is explicitly disabled with `android:canRetrieveWindowContent="false"`.

## F-Droid build metadata

A proposed `fdroiddata` file is maintained upstream at:

`fdroid/com.capfroggy.simpleautoclicker.yml`

The v1.2.1 recipe points to exact upstream commit:

`d3477a71af4c602cfaf5d41e90c8d6b24ede0faa`

The release build has been validated with `assembleRelease`.

Gradle 8.9 is pinned through `gradle/wrapper/gradle-wrapper.properties` with the official SHA-256 distribution checksum.
