package com.bid.launcherwatch.cleananimation;



import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;
import com.bid.launcherwatch.R;
import com.bid.launcherwatch.R.styleable;
import java.util.ArrayList;
import java.util.List;

public class MovingDotView extends ViewGroup {
    /* access modifiers changed from: private */
    public boolean isCleaned;
    private long mAnimatorDuration;
    private int mBtnTextColor;
    /* access modifiers changed from: private */
    public CenterDot mCenterDot;
    private int mCenterDotRadius;
    private Drawable mCenterDotRes;
    /* access modifiers changed from: private */
    public Context mContext;
    private int mDotColor;
    /* access modifiers changed from: private */
    public List<Dot> mDots;
    /* access modifiers changed from: private */
    public int mDotsCount;
    private int mMaxDotRadius;
    /* access modifiers changed from: private */
    public int mMaxDotSpeed;
    private int mMinDotRadius;
    /* access modifiers changed from: private */
    public int mMinDotSpeed;
    private Paint mPaint;
    /* access modifiers changed from: private */
    public int mProgress;
    private int mTextColor;
    private int mTextSize;
    private int mWidth;
    private int toProgress;

    public MovingDotView(Context context) {
        this(context, null);
    }

    public MovingDotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isCleaned = false;
        setWillNotDraw(true);
        initObtainStyled(context, attrs);
    }

    public void setDotsCount(int dotsCount) {
        this.mDotsCount = dotsCount;
    }

    public void setMaxDotSpeed(int maxDotSpeed) {
        this.mMaxDotSpeed = maxDotSpeed;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setMinDotSpeed(int minDotSpeed) {
        this.mMinDotSpeed = minDotSpeed;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean b, int i, int i1, int i2, int i3) {
        getChildAt(0).layout((getWidth() / 2) - this.mCenterDotRadius, (getWidth() / 2) - this.mCenterDotRadius, (getWidth() / 2) + this.mCenterDotRadius, (getWidth() / 2) + this.mCenterDotRadius);
    }

    private void initObtainStyled(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MovingDotView);
        this.mDotsCount = array.getInteger(0, 10);
        this.mCenterDotRadius = (int) array.getDimension(1, 0.0f);
        this.mCenterDotRes = array.getDrawable(2);
        this.mDotColor = array.getColor(3, getResources().getColor(R.color.colorPrimary));
        this.mMaxDotRadius = (int) array.getDimension(4, SizeUtil.Dp2Px(getContext(), 10.0f));
        this.mMinDotRadius = (int) array.getDimension(5, SizeUtil.Dp2Px(getContext(), 5.0f));
        this.mMaxDotSpeed = array.getInteger(6, 10);
        this.mMinDotSpeed = array.getInteger(7, 1);
        this.mTextSize = (int) array.getDimension(8, SizeUtil.Sp2Px(getContext(), 70.0f));
        this.mBtnTextColor = array.getColor(10, getResources().getColor(R.color.colorPrimary));
        this.mAnimatorDuration = (long) array.getInteger(11, 1500);
        this.mTextColor = array.getColor(9, -1);
        array.recycle();
        this.mDots = new ArrayList();
        this.mPaint = new Paint();
        this.mPaint.setDither(true);
        this.mPaint.setAntiAlias(true);
        setBackgroundColor(0);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (!(widthMode == 1073741824 || heightMode == 1073741824)) {
            try {
                throw new Exception("宽高不能都为wrap_content");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mWidth = Math.min(widthSize, heightSize);
        setMeasuredDimension(this.mWidth, this.mWidth);
        Dot.WIDTH = this.mWidth;
        Dot.SPEED = this.mMinDotSpeed;
        Dot.sMaxDotRadius = this.mMaxDotRadius;
        Dot.sMinDotRadius = this.mMinDotRadius;
    }

    public void delAllRecentTask() {
        PackageManager pm = this.mContext.getPackageManager();
        ActivityManager am = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RecentTaskInfo> recentTasks = am.getRecentTasks(21, 6);
        int numTasks = recentTasks.size();
        ActivityInfo homeInfo = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").resolveActivityInfo(pm, 0);
        for (int i = 0; i < numTasks; i++) {
            RecentTaskInfo recentInfo = (RecentTaskInfo) recentTasks.get(i);
            Intent intent = new Intent(recentInfo.baseIntent);
            if (recentInfo.origActivity != null) {
                intent.setComponent(recentInfo.origActivity);
            }
            if (!isCurrentHomeActivity(intent.getComponent(), homeInfo) && !intent.getComponent().getPackageName().equals("com.wiitetech.WiiWatchPro") && !intent.getComponent().getPackageName().equals("com.sprots.wiiteer.fitness")) {

                am.killBackgroundProcesses(intent.getComponent().getPackageName().toString());
            }
        }
    }

    private boolean isCurrentHomeActivity(ComponentName component, ActivityInfo homeInfo) {
        if (homeInfo == null) {
            homeInfo = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").resolveActivityInfo(this.mContext.getPackageManager(), 0);
        }
        if (homeInfo == null || !homeInfo.packageName.equals(component.getPackageName())) {
            return false;
        }
        return homeInfo.name.equals(component.getClassName());
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mCenterDotRadius == 0) {
            this.mCenterDotRadius = w / 4;
        }
        this.mDots = new ArrayList();
        this.mCenterDot = new CenterDot(getContext(), this.mCenterDotRadius * 2);
        this.mCenterDot.setClickable(true);
        this.mCenterDot.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!MovingDotView.this.isCleaned) {
                    for (int i = 0; i < MovingDotView.this.mDotsCount; i++) {
                        MovingDotView.this.mDots.add(new Dot());
                    }
                    MovingDotView.this.startClean();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            MovingDotView.this.mDots = new ArrayList();
                            MovingDotView.this.backClean();
                            MovingDotView.this.delAllRecentTask();
                            Toast.makeText(MovingDotView.this.mContext, R.string.app_clean_done, Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            }
        });
        if (this.mCenterDotRes == null) {
            this.mCenterDotRes = getResources().getDrawable(R.drawable.saoba);
        }
        this.mCenterDot.setBackground(this.mCenterDotRes);
        this.mCenterDot.setProgressTextSize(this.mTextSize);
        this.mCenterDot.setProgressTextColor(this.mTextColor);
        this.mCenterDot.setBtnTextColor(this.mBtnTextColor);
        this.mCenterDot.setProgress(this.mProgress);
        addView(this.mCenterDot);
    }

    public void startClean() {
        startAnimation(0.9f, 1.0f, this.toProgress);
        this.isCleaned = true;
    }

    public void backClean() {
        startAnimation(0.0f, 0.0f, this.toProgress);
        this.isCleaned = false;
    }

    public void startAnimation(float from, float to, final int toProgress2) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{from, to});
        valueAnimator.setDuration(1);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                Dot.SPEED = (int) (((float) MovingDotView.this.mMinDotSpeed) + (((float) (MovingDotView.this.mMaxDotSpeed - MovingDotView.this.mMinDotSpeed)) * progress));
                MovingDotView.this.mCenterDot.setAnimationPogress(progress);
                MovingDotView.this.mCenterDot.setProgress((int) (((float) MovingDotView.this.mProgress) + (((float) (toProgress2 - MovingDotView.this.mProgress)) * progress)));
            }
        });
        valueAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(this.mDotColor);
        canvas.save();
        canvas.translate((float) (getWidth() / 2), (float) (getHeight() / 2));
        for (int j = 0; j < this.mDots.size(); j++) {
            Dot dot = (Dot) this.mDots.get(j);
            float progress = (float) ((dot.getZ() - ((double) this.mCenterDotRadius)) / (new Dot((float) ((-getWidth()) / 2), (float) ((-getWidth()) / 2), 0.0f).getZ() - ((double) this.mCenterDotRadius)));
            if (progress > 1.0f) {
                progress = 1.0f;
            }
            if (progress < 0.0f) {
                progress = 0.0f;
            }
            int alpha = (int) (((1.0f - progress) * 200.0f) + 75.0f);
            Paint paint = this.mPaint;
            if (alpha > 255) {
                alpha = 255;
            }
            paint.setAlpha(alpha);
            canvas.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), this.mPaint);
            dot.checkAndChange();
        }
        postInvalidateDelayed(10);
        canvas.restore();
    }
}

