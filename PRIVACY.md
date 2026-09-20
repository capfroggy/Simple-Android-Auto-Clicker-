# Privacy

Simple Android Auto Clicker is designed to work entirely on the user's device.

## Data collection

The app does **not** collect, transmit, sell, share, or upload personal data.

It contains:

- no analytics;
- no advertising SDKs;
- no crash-reporting SDKs;
- no trackers;
- no account system;
- no cloud synchronization;
- no remote-control functionality.

## Network access

The Android manifest does **not** request the `INTERNET` permission.

The application therefore does not require a network connection for normal operation.

## Accessibility Service

The Accessibility Service is necessary because Android does not allow ordinary applications to generate taps inside other applications.

It is used only to:

1. receive the selected Volume Up shortcut;
2. display the movable target overlay;
3. dispatch tap gestures at the position chosen by the user.

The application does not intentionally inspect or transmit screen contents, text, passwords, messages, financial information, or other personal data.

## Local storage

The app stores a small amount of configuration locally using Android preferences, including:

- click interval;
- target position;
- selected activation shortcut;
- target visibility.

These settings remain on the device.

## Transparency

The complete source code is public so users can inspect how the application works and verify which Android permissions are requested.

If the privacy model changes in a future version, this document and the release notes should be updated before publication.
