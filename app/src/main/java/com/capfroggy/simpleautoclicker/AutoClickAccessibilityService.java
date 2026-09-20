package com.capfroggy.simpleautoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class AutoClickAccessibilityService extends AccessibilityService {
    public static volatile AutoClickAccessibilityService instance;

    public static final String PREFS = "autoclicker_prefs";
    public static final String KEY_INTERVAL = "interval_ms";
    public static final String KEY_TARGET_X = "target_x";
    public static final String KEY_TARGET_Y = "target_y";
    public static final String KEY_TARGET_VISIBLE = "target_visible";
    public static final String KEY_TRIGGER_MODE = "trigger_mode";

    public static final String MODE_LONG_PRESS = "long_press";
    public static final String MODE_DOUBLE_PRESS = "double_press";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Handler keyHandler = new Handler(Looper.getMainLooper());

    private WindowManager windowManager;
    private TargetView targetView;
    private WindowManager.LayoutParams targetParams;
    private int targetSizePx;

    private float targetX;
    private float targetY;
    private long intervalMs = 100L;
    private boolean autoClicking = false;
    private boolean targetVisible = true;
    private String triggerMode = MODE_LONG_PRESS;

    private boolean volumeDown = false;
    private boolean longPressTriggered = false;
    private long volumeDownAt = 0L;
    private long lastShortRelease = 0L;

    private final Runnable longPressRunnable = () -> {
        if (volumeDown && MODE_LONG_PRESS.equals(triggerMode)) {
            longPressTriggered = true;
            toggleAutoClicker();
        }
    };

    private final Runnable clickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!autoClicking) return;
            performTap(targetX, targetY);
            handler.postDelayed(this, intervalMs);
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;

        AccessibilityServiceInfo info = getServiceInfo();
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        setServiceInfo(info);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        intervalMs = prefs.getLong(KEY_INTERVAL, 100L);
        targetVisible = prefs.getBoolean(KEY_TARGET_VISIBLE, true);
        triggerMode = prefs.getString(KEY_TRIGGER_MODE, MODE_LONG_PRESS);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        targetSizePx = dp(72);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        targetX = prefs.getFloat(KEY_TARGET_X, screenWidth / 2f);
        targetY = prefs.getFloat(KEY_TARGET_Y, screenHeight / 2f);

        createTargetOverlay();
        updateTargetVisibility();
    }

    private void createTargetOverlay() {
        if (targetView != null || windowManager == null) return;

        targetView = new TargetView(this);
        targetParams = new WindowManager.LayoutParams(
                targetSizePx,
                targetSizePx,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        targetParams.gravity = Gravity.TOP | Gravity.START;
        targetParams.x = Math.round(targetX - targetSizePx / 2f);
        targetParams.y = Math.round(targetY - targetSizePx / 2f);

        targetView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            private float startRawX;
            private float startRawY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (autoClicking) return false;

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = targetParams.x;
                        startY = targetParams.y;
                        startRawX = event.getRawX();
                        startRawY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        targetParams.x = startX + Math.round(event.getRawX() - startRawX);
                        targetParams.y = startY + Math.round(event.getRawY() - startRawY);
                        windowManager.updateViewLayout(targetView, targetParams);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        targetX = targetParams.x + targetSizePx / 2f;
                        targetY = targetParams.y + targetSizePx / 2f;
                        saveTarget();
                        return true;

                    default:
                        return false;
                }
            }
        });

        windowManager.addView(targetView, targetParams);
    }

    public void setIntervalMs(long value) {
        intervalMs = Math.max(50L, Math.min(2000L, value));
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putLong(KEY_INTERVAL, intervalMs)
                .apply();
    }

    public void setTriggerMode(String mode) {
        if (!MODE_DOUBLE_PRESS.equals(mode)) {
            mode = MODE_LONG_PRESS;
        }

        triggerMode = mode;
        volumeDown = false;
        longPressTriggered = false;
        lastShortRelease = 0L;
        keyHandler.removeCallbacks(longPressRunnable);

        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putString(KEY_TRIGGER_MODE, triggerMode)
                .apply();
    }

    public String getTriggerMode() {
        return triggerMode;
    }

    public boolean isAutoClicking() {
        return autoClicking;
    }

    public void setTargetVisible(boolean visible) {
        targetVisible = visible;
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_TARGET_VISIBLE, visible)
                .apply();
        updateTargetVisibility();
    }

    private void updateTargetVisibility() {
        if (targetView != null) {
            targetView.setVisibility(targetVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void toggleAutoClicker() {
        if (autoClicking) {
            stopAutoClicker();
        } else {
            startAutoClicker();
        }
    }

    public void startAutoClicker() {
        if (autoClicking) return;

        autoClicking = true;
        lastShortRelease = 0L;
        makeTargetTouchable(false);

        if (targetView != null) {
            targetView.setActive(true);
        }

        vibrate(80);
        handler.removeCallbacks(clickRunnable);
        handler.post(clickRunnable);
    }

    public void stopAutoClicker() {
        if (!autoClicking) return;

        autoClicking = false;
        handler.removeCallbacks(clickRunnable);
        makeTargetTouchable(true);

        if (targetView != null) {
            targetView.setActive(false);
        }

        vibrate(40);
    }

    private void makeTargetTouchable(boolean touchable) {
        if (targetView == null || targetParams == null || windowManager == null) return;

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        if (!touchable) {
            flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

        targetParams.flags = flags;

        try {
            windowManager.updateViewLayout(targetView, targetParams);
        } catch (Exception ignored) {
        }
    }

    private void performTap(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, 1);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_UP) {
            return super.onKeyEvent(event);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getRepeatCount() == 0 && !volumeDown) {
                volumeDown = true;
                volumeDownAt = SystemClock.elapsedRealtime();
                longPressTriggered = false;

                if (MODE_LONG_PRESS.equals(triggerMode)) {
                    keyHandler.removeCallbacks(longPressRunnable);
                    keyHandler.postDelayed(longPressRunnable, 3000L);
                }
            }
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_UP) {
            long heldMs = SystemClock.elapsedRealtime() - volumeDownAt;
            volumeDown = false;
            keyHandler.removeCallbacks(longPressRunnable);

            if (MODE_DOUBLE_PRESS.equals(triggerMode) && heldMs < 1000L) {
                long now = SystemClock.elapsedRealtime();

                if (lastShortRelease != 0L && now - lastShortRelease <= 500L) {
                    lastShortRelease = 0L;
                    toggleAutoClicker();
                } else {
                    lastShortRelease = now;
                }
            }

            return true;
        }

        return true;
    }

    private void saveTarget() {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putFloat(KEY_TARGET_X, targetX)
                .putFloat(KEY_TARGET_Y, targetY)
                .apply();
    }

    private void vibrate(long ms) {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE)
                );
            } else {
                vibrator.vibrate(ms);
            }
        } catch (Exception ignored) {
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        stopAutoClicker();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        keyHandler.removeCallbacksAndMessages(null);
        stopAutoClicker();

        if (targetView != null && windowManager != null) {
            try {
                windowManager.removeView(targetView);
            } catch (Exception ignored) {
            }
        }

        targetView = null;
        instance = null;
        super.onDestroy();
    }
}
