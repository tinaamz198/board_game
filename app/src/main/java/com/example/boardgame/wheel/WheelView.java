package com.example.boardgame.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class WheelView extends View {
    private Paint paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<String> options = new ArrayList<>();
    private int[] colors = {0xFF0A84FF, 0xFF30D158, 0xFFFFD60A, 0xFFFF453A, 0xFFBF5AF2, 0xFF5AC8FA};

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(35f); // Чуть уменьшим, чтобы влезало больше
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setFakeBoldText(true); // Жирный текст
        paintText.setShadowLayer(5f, 2f, 2f, Color.BLACK); // Тень для читаемости
    }
    public void setOptions(List<String> options) {
        this.options = options;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (options == null || options.isEmpty()) return;

        float sweepAngle = 360f / options.size();
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY);
        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        for (int i = 0; i < options.size(); i++) {
            // Рисуем сектор
            paintArc.setColor(colors[i % colors.length]);
            canvas.drawArc(rect, i * sweepAngle, sweepAngle, true, paintArc);

            // Рисуем текст внутри сектора
            canvas.save();
            canvas.rotate(i * sweepAngle + sweepAngle / 2f, centerX, centerY);
            float textX = centerX + radius / 2f;
            // Обрезаем текст, если он слишком длинный
            String text = options.get(i);
            if (text.length() > 10) text = text.substring(0, 8) + "..";
            canvas.drawText(text, textX, centerY + 15f, paintText);
            canvas.restore();


        }
    }

}