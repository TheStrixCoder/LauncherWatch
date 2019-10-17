package com.bid.launcherwatch;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircleScoller extends View {
    private Paint backgroundpaint;
    private float highlightbarwidth = 0.0f;
    private Paint highlightpaint;
    private float highlightstartdegree = 320.0f;
    private RectF oval;
    private int screenCenterX = 0;
    private int screenCenterY = 0;

    public CircleScoller(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initdata();
    }

    public CircleScoller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initdata();
    }

    public CircleScoller(Context context) {
        super(context);
        initdata();
    }

    private void initdata() {
        this.backgroundpaint = new Paint();
        this.highlightpaint = new Paint();
        this.highlightstartdegree = 320.0f;
        this.highlightbarwidth = 25.0f;
    }

    public void setScreenSize(int width, int height) {
        this.screenCenterX = width / 2;
        this.screenCenterY = height / 2;
        this.oval = new RectF(6.0f, 6.0f, (float) ((this.screenCenterX * 2) - 6), (float) ((this.screenCenterY * 2) - 6));
    }

    public void setHighLightPercent(int current, int visiable, int total) {
        Log.i("TAG", "total" + total);
        if (total > visiable) {
            this.highlightstartdegree = ((((float) current) * (80.0f - this.highlightbarwidth)) / ((float) (total - visiable))) + 320.0f;
            Log.i("TAG", "highlightstartdegree" + this.highlightstartdegree);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.backgroundpaint.setColor(getContext().getResources().getColor(R.color.bluelight));
        this.backgroundpaint.setStrokeWidth(6.0f);
        this.backgroundpaint.setStyle(Style.STROKE);
        this.backgroundpaint.setDither(true);
        this.backgroundpaint.setAntiAlias(true);
        this.backgroundpaint.setStrokeCap(Cap.ROUND);
        this.backgroundpaint.setAlpha(70);
        canvas.drawArc(this.oval, 320.0f, 80.0f, false, this.backgroundpaint);
        canvas.save();
        this.highlightpaint.setColor(getContext().getResources().getColor(R.color.bluelight));
        this.highlightpaint.setStrokeWidth(6.0f);
        this.highlightpaint.setStyle(Style.STROKE);
        this.highlightpaint.setAntiAlias(true);
        this.highlightpaint.setStrokeCap(Cap.ROUND);
        canvas.drawArc(this.oval, this.highlightstartdegree, this.highlightbarwidth, false, this.highlightpaint);
        canvas.restore();
    }
}
