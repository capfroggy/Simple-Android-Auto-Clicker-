package com.capfroggy.simpleautoclicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TargetView extends View {
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint crossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean active = false;

    public TargetView(Context context) {
        super(context);
        fillPaint.setColor(0xAA111318);
        ringPaint.setColor(0xFFFFFFFF);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(dp(3));
        crossPaint.setColor(0xFFFFFFFF);
        crossPaint.setStrokeWidth(dp(2));
    }

    public void setActive(boolean active) {
        this.active = active;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) * 0.38f;

        fillPaint.setColor(active ? 0xAA0E7A3F : 0xAA111318);
        canvas.drawCircle(cx, cy, radius, fillPaint);
        canvas.drawCircle(cx, cy, radius, ringPaint);
        canvas.drawLine(cx - radius * 0.55f, cy, cx + radius * 0.55f, cy, crossPaint);
        canvas.drawLine(cx, cy - radius * 0.55f, cx, cy + radius * 0.55f, crossPaint);
        canvas.drawCircle(cx, cy, dp(3), crossPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
