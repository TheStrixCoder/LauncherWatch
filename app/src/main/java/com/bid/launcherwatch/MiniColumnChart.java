package com.bid.launcherwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MiniColumnChart extends View implements Runnable {
    private float animHeight;
    private float currentHeight;
    private String[] data;
    private Paint dataPaint;
    private Handler handler;
    private boolean mAllowDraw;
    private boolean mIsAnim;
    private int mLineDist;
    private int mLineHeight;
    private int mLinePointX;
    private int mLinePointY;
    private int mLineWidth;
    private boolean mStopAnim;

    public void setData(String[] data2) {
        this.data = data2;
        this.mAllowDraw = true;
        invalidate();
    }

    public MiniColumnChart(Context context) {
        this(context, null);
    }

    public MiniColumnChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniColumnChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.handler = new Handler();
        this.mIsAnim = false;
        this.mStopAnim = true;
        this.mAllowDraw = false;
        this.mLineDist = 30;
        this.mLinePointX = 68;
        this.mLinePointY = 132;
        this.mLineHeight = 180;
        this.mLineWidth = 20;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.dataPaint = new Paint();
        this.dataPaint.setColor(-16711681);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mAllowDraw) {
            if (this.animHeight < 100.0f) {
                this.handler.postDelayed(this, 1);
            }
            for (int i = 0; i < this.data.length; i++) {
                RectF dataRectF = new RectF();
                dataRectF.left = (float) (this.mLinePointX + ((this.mLineDist + this.mLineWidth) * i));
                dataRectF.right = (float) (this.mLinePointX + this.mLineWidth + ((this.mLineDist + this.mLineWidth) * i));
                this.dataPaint.setColor(Color.parseColor("#29edf7"));
                this.currentHeight = Float.parseFloat(this.data[i]);
                if (this.animHeight >= this.currentHeight) {
                    dataRectF.top = ((float) this.mLinePointY) + (((float) this.mLineHeight) - ((this.currentHeight * ((float) this.mLineHeight)) / 100.0f));
                } else {
                    dataRectF.top = ((float) this.mLinePointY) + (((float) this.mLineHeight) - ((this.animHeight * ((float) this.mLineHeight)) / 100.0f));
                }
                dataRectF.bottom = (float) (this.mLinePointY + this.mLineHeight);
                canvas.drawRoundRect(dataRectF, (float) this.mLineWidth, (float) this.mLineWidth, this.dataPaint);
            }
        }
    }

    public void run() {
        this.animHeight += 5.0f;
        if (this.animHeight <= 100.0f) {
            invalidate();
        }
    }

    public void runAnim(String[] data2) {
        this.animHeight = 0.0f;
        setData(data2);
    }

    public void cleanAnim() {
        this.animHeight = 0.0f;
        this.mAllowDraw = false;
    }
}

