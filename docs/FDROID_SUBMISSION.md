# F-Droid submission

This repository is prepared for inclusion in the official F-Droid repository.

## Application ID

`com.capfroggy.simpleautoclicker`

## Upstream metadata

F-Droid-compatible store metadata lives in:

`fastlane/metadata/android/`

Locales currently provided:

- `en-US`
- `es-ES`

The metadata includes title, short description, full description, screenshots, icon and changelog.

## Proposed fdroiddata metadata

A ready-to-submit metadata file is stored at:

`fdroid/com.capfroggy.simpleautoclicker.yml`

For the official F-Droid repository this file must be copied to:

`metadata/com.capfroggy.simpleautoclicker.yml`

inside a fork of https://gitlab.com/fdroid/fdroiddata.

## Build

The application has no external runtime libraries and does not request the Android INTERNET permission.

F-Droid can build version 1.2.0 with:

`gradle assembleRelease`

F-Droid signs the resulting APK with its repository signing key before publication.

## Update strategy

Official releases are tagged with `v<versionName>`. The proposed metadata uses:

- `UpdateCheckMode: Tags`
- `AutoUpdateMode: Version v%v`

Future releases only need a version bump and matching Git tag for F-Droid's automatic update process.

## Final external submission step

The final inclusion request must be submitted to F-Droid's GitLab infrastructure because the official `fdroiddata` repository and submission queue are hosted there.

The source project itself requires no paid service or Google Play account.
