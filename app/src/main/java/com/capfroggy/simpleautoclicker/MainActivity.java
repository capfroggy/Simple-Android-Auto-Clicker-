package com.capfroggy.simpleautoclicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    private TextView serviceStatus;
    private TextView speedLabel;
    private TextView stateLabel;
    private TextView shortcutHint;
    private Button targetButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(AutoClickAccessibilityService.PREFS, MODE_PRIVATE);
        setContentView(buildUi());

        if (!prefs.getBoolean("intro_shown", false)) {
            showFirstRunDialog();
            prefs.edit().putBoolean("intro_shown", true).apply();
        }
    }

    private View buildUi() {
        int pad = dp(20);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(17, 19, 24));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(24), pad, dp(32));
        root.setBackgroundColor(Color.rgb(17, 19, 24));
        scroll.addView(root);

        TextView title = text("SIMPLE AUTO CLICKER", 26, Color.WHITE, true);
        root.addView(title, lpMatchWrap(0));

        TextView subtitle = text("Free • Open source • No ads • No tracking", 15, 0xFFB7BEC9, false);
        LinearLayout.LayoutParams subLp = lpMatchWrap(dp(4));
        subLp.bottomMargin = dp(20);
        root.addView(subtitle, subLp);

        TextView trust = text(
                "Runs entirely on your phone. No Internet permission, no account, no analytics and no remote control.",
                14,
                0xFF9FE3B0,
                false
        );
        LinearLayout.LayoutParams trustLp = lpMatchWrap(0);
        trustLp.bottomMargin = dp(22);
        root.addView(trust, trustLp);

        serviceStatus = text("Service: checking…", 16, Color.WHITE, true);
        root.addView(serviceStatus, lpMatchWrap(0));

        Button appInfoButton = button("1. Open app info");
        appInfoButton.setOnClickListener(v -> openAppInfo());
        root.addView(appInfoButton, lpMatchWrap(dp(10)));

        TextView restrictedHelp = text(
                "If Android says “restricted settings”, open App info, tap the ⋮ menu and choose “Allow restricted settings”.",
                13,
                0xFFFFD59A,
                false
        );
        LinearLayout.LayoutParams restrictedLp = lpMatchWrap(dp(8));
        restrictedLp.bottomMargin = dp(10);
        root.addView(restrictedHelp, restrictedLp);

        Button accessibilityButton = button("2. Open accessibility settings");
        accessibilityButton.setOnClickListener(
                v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        );
        LinearLayout.LayoutParams accessLp = lpMatchWrap(0);
        accessLp.bottomMargin = dp(26);
        root.addView(accessibilityButton, accessLp);

        root.addView(text("Activation shortcut", 18, Color.WHITE, true), lpMatchWrap(0));

        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);

        RadioButton longPress = new RadioButton(this);
        longPress.setText("Hold Volume Up for 3 seconds");
        longPress.setTextColor(Color.WHITE);
        longPress.setTextSize(15);

        RadioButton doublePress = new RadioButton(this);
        doublePress.setText("Double-press Volume Up");
        doublePress.setTextColor(Color.WHITE);
        doublePress.setTextSize(15);

        group.addView(longPress);
        group.addView(doublePress);

        String savedMode = prefs.getString(
                AutoClickAccessibilityService.KEY_TRIGGER_MODE,
                AutoClickAccessibilityService.MODE_LONG_PRESS
        );

        if (AutoClickAccessibilityService.MODE_DOUBLE_PRESS.equals(savedMode)) {
            doublePress.setChecked(true);
        } else {
            longPress.setChecked(true);
        }

        group.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            String mode = (checkedId == doublePress.getId())
                    ? AutoClickAccessibilityService.MODE_DOUBLE_PRESS
                    : AutoClickAccessibilityService.MODE_LONG_PRESS;

            prefs.edit()
                    .putString(AutoClickAccessibilityService.KEY_TRIGGER_MODE, mode)
                    .apply();

            if (AutoClickAccessibilityService.instance != null) {
                AutoClickAccessibilityService.instance.setTriggerMode(mode);
            }

            updateShortcutHint(mode);
        });

        LinearLayout.LayoutParams groupLp = lpMatchWrap(dp(6));
        groupLp.bottomMargin = dp(6);
        root.addView(group, groupLp);

        shortcutHint = text("", 13, 0xFFB7BEC9, false);
        LinearLayout.LayoutParams hintLp = lpMatchWrap(0);
        hintLp.bottomMargin = dp(24);
        root.addView(shortcutHint, hintLp);
        updateShortcutHint(savedMode);

        root.addView(text("Click speed", 18, Color.WHITE, true), lpMatchWrap(0));

        speedLabel = text("", 15, 0xFFB7BEC9, false);
        root.addView(speedLabel, lpMatchWrap(dp(4)));

        SeekBar speedBar = new SeekBar(this);
        speedBar.setMax(1950);

        long savedInterval = prefs.getLong(AutoClickAccessibilityService.KEY_INTERVAL, 100L);
        speedBar.setProgress((int) Math.max(0, Math.min(1950, savedInterval - 50L)));

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long interval = progress + 50L;
                updateSpeedLabel(interval);

                prefs.edit()
                        .putLong(AutoClickAccessibilityService.KEY_INTERVAL, interval)
                        .apply();

                if (AutoClickAccessibilityService.instance != null) {
                    AutoClickAccessibilityService.instance.setIntervalMs(interval);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        LinearLayout.LayoutParams seekLp = lpMatchWrap(dp(2));
        seekLp.bottomMargin = dp(22);
        root.addView(speedBar, seekLp);
        updateSpeedLabel(savedInterval);

        targetButton = button("Show / hide target");
        targetButton.setOnClickListener(v -> {
            boolean current = prefs.getBoolean(
                    AutoClickAccessibilityService.KEY_TARGET_VISIBLE,
                    true
            );
            boolean next = !current;

            prefs.edit()
                    .putBoolean(AutoClickAccessibilityService.KEY_TARGET_VISIBLE, next)
                    .apply();

            if (AutoClickAccessibilityService.instance != null) {
                AutoClickAccessibilityService.instance.setTargetVisible(next);
            }

            updateTargetButton(next);
        });

        LinearLayout.LayoutParams targetLp = lpMatchWrap(0);
        targetLp.bottomMargin = dp(12);
        root.addView(targetButton, targetLp);

        Button stopButton = button("Stop now");
        stopButton.setOnClickListener(v -> {
            if (AutoClickAccessibilityService.instance != null) {
                AutoClickAccessibilityService.instance.stopAutoClicker();
                refreshStatus();
            }
        });

        LinearLayout.LayoutParams stopLp = lpMatchWrap(0);
        stopLp.bottomMargin = dp(22);
        root.addView(stopButton, stopLp);

        root.addView(text("How it works", 18, Color.WHITE, true), lpMatchWrap(0));

        TextView controls = text(
                "1. Enable the accessibility service once.\n\n" +
                "2. Drag the floating target to the point you want to tap.\n\n" +
                "3. Use your selected Volume Up shortcut to start or stop.\n\n" +
                "The same shortcut toggles the auto clicker ON and OFF.",
                15,
                0xFFD7DBE2,
                false
        );

        LinearLayout.LayoutParams controlsLp = lpMatchWrap(dp(6));
        controlsLp.bottomMargin = dp(20);
        root.addView(controls, controlsLp);

        stateLabel = text("State: stopped", 16, 0xFFB7BEC9, true);
        root.addView(stateLabel, lpMatchWrap(0));

        TextView sourceNote = text(
                "Everything is local and open source. You can inspect the complete source code on GitHub before installing.",
                13,
                0xFF8F98A6,
                false
        );
        root.addView(sourceNote, lpMatchWrap(dp(16)));

        return scroll;
    }

    private void showFirstRunDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Why Accessibility is needed")
                .setMessage(
                        "Simple Auto Clicker uses Android Accessibility only to detect the Volume Up shortcut, show the movable target, and generate taps.\n\n" +
                        "It has no Internet permission, no ads, no analytics, no account system and no remote access.\n\n" +
                        "On Android 13+, apps installed from an APK may require you to allow “restricted settings” from the app info screen first."
                )
                .setPositiveButton("Got it", null)
                .show();
    }

    private void openAppInfo() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        boolean enabled = isAccessibilityServiceEnabled();

        serviceStatus.setText(enabled ? "Service: ACTIVE" : "Service: INACTIVE");
        serviceStatus.setTextColor(enabled ? 0xFF69D58C : 0xFFFF8A80);

        boolean visible = prefs.getBoolean(
                AutoClickAccessibilityService.KEY_TARGET_VISIBLE,
                true
        );
        updateTargetButton(visible);

        boolean clicking = AutoClickAccessibilityService.instance != null
                && AutoClickAccessibilityService.instance.isAutoClicking();

        stateLabel.setText(clicking ? "State: CLICKING" : "State: stopped");
        stateLabel.setTextColor(clicking ? 0xFF69D58C : 0xFFB7BEC9);
    }

    private void updateTargetButton(boolean visible) {
        if (targetButton != null) {
            targetButton.setText(visible ? "Hide target" : "Show target");
        }
    }

    private void updateShortcutHint(String mode) {
        if (shortcutHint == null) return;

        if (AutoClickAccessibilityService.MODE_DOUBLE_PRESS.equals(mode)) {
            shortcutHint.setText("Double-press Volume Up to toggle the auto clicker ON or OFF.");
        } else {
            shortcutHint.setText("Hold Volume Up for 3 seconds to toggle the auto clicker ON or OFF.");
        }
    }

    private void updateSpeedLabel(long interval) {
        double cps = 1000.0 / Math.max(1L, interval);
        speedLabel.setText(
                interval + " ms  •  " +
                String.format(Locale.US, "%.1f", cps) +
                " clicks/s"
        );
    }

    private boolean isAccessibilityServiceEnabled() {
        ComponentName componentName = new ComponentName(
                this,
                AutoClickAccessibilityService.class
        );
        String expected = componentName.flattenToString();

        String enabledServices = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );

        if (TextUtils.isEmpty(enabledServices)) return false;

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabledServices);

        while (splitter.hasNext()) {
            if (expected.equalsIgnoreCase(splitter.next())) return true;
        }

        return false;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(sp);
        tv.setTextColor(color);

        if (bold) {
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        }

        tv.setLineSpacing(0, 1.08f);
        return tv;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(48));
        return button;
    }

    private LinearLayout.LayoutParams lpMatchWrap(int topMargin) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = topMargin;
        return lp;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
