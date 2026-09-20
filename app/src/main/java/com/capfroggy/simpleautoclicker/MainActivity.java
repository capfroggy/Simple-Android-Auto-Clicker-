package com.capfroggy.simpleautoclicker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    private TextView serviceStatus;
    private TextView speedLabel;
    private TextView stateLabel;
    private Button targetButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(AutoClickAccessibilityService.PREFS, MODE_PRIVATE);
        setContentView(buildUi());
    }

    private View buildUi() {
        int pad = dp(20);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(24), pad, pad);
        root.setBackgroundColor(Color.rgb(17, 19, 24));

        TextView title = text("SIMPLE AUTO CLICKER", 26, Color.WHITE, true);
        root.addView(title, lpMatchWrap(0));

        TextView subtitle = text("Free, local and ad-free", 15, 0xFFB7BEC9, false);
        LinearLayout.LayoutParams subLp = lpMatchWrap(dp(4));
        subLp.bottomMargin = dp(24);
        root.addView(subtitle, subLp);

        serviceStatus = text("Service: checking…", 16, Color.WHITE, true);
        root.addView(serviceStatus, lpMatchWrap(dp(6)));

        Button accessibilityButton = button("Open accessibility settings");
        accessibilityButton.setOnClickListener(
                v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        );
        LinearLayout.LayoutParams buttonLp = lpMatchWrap(dp(8));
        buttonLp.bottomMargin = dp(28);
        root.addView(accessibilityButton, buttonLp);

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
        seekLp.bottomMargin = dp(24);
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
        targetLp.bottomMargin = dp(14);
        root.addView(targetButton, targetLp);

        Button stopButton = button("Stop now");
        stopButton.setOnClickListener(v -> {
            if (AutoClickAccessibilityService.instance != null) {
                AutoClickAccessibilityService.instance.stopAutoClicker();
                refreshStatus();
            }
        });

        LinearLayout.LayoutParams stopLp = lpMatchWrap(0);
        stopLp.bottomMargin = dp(28);
        root.addView(stopButton, stopLp);

        root.addView(text("Controls", 18, Color.WHITE, true), lpMatchWrap(0));

        TextView controls = text(
                "Hold Volume Up for 3 seconds to start.\n\n" +
                "While active, double-press Volume Up to stop.\n\n" +
                "Drag the floating target to the exact point before starting.",
                15,
                0xFFD7DBE2,
                false
        );

        LinearLayout.LayoutParams controlsLp = lpMatchWrap(dp(6));
        controlsLp.bottomMargin = dp(24);
        root.addView(controls, controlsLp);

        stateLabel = text("State: stopped", 16, 0xFFB7BEC9, true);
        root.addView(stateLabel, lpMatchWrap(0));

        TextView note = text(
                "While the accessibility service is enabled, Volume Up is reserved for the auto clicker shortcut.",
                13,
                0xFF8F98A6,
                false
        );

        root.addView(note, lpMatchWrap(dp(16)));

        return root;
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
