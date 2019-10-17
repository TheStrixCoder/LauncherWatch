package com.bid.launcherwatch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

public class SwipeHelper {
    private static LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    /* access modifiers changed from: private */
    public Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrAnimView;
    /* access modifiers changed from: private */
    public View mCurrView;
    private float mDensityScale;
    private boolean mDragging;
    private Handler mHandler;
    private float mInitialTouchPos;
    /* access modifiers changed from: private */
    public OnLongClickListener mLongPressListener;
    /* access modifiers changed from: private */
    public boolean mLongPressSent;
    private long mLongPressTimeout;
    private float mMinAlpha = 0.0f;
    private float mPagingTouchSlop;
    private int mSwipeDirection;
    private VelocityTracker mVelocityTracker;
    private Runnable mWatchLongPress;

    public interface Callback {
        boolean canChildBeDismissed(View view);

        View getChildAtPosition(MotionEvent motionEvent);

        View getChildContentView(View view);

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onDragCancelled(View view);
    }

    public SwipeHelper(int swipeDirection, Callback callback, float densityScale, float pagingTouchSlop) {
        this.mCallback = callback;
        this.mHandler = new Handler();
        this.mSwipeDirection = swipeDirection;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mDensityScale = densityScale;
        this.mPagingTouchSlop = pagingTouchSlop;
        this.mLongPressTimeout = (long) (((float) ViewConfiguration.getLongPressTimeout()) * 1.5f);
    }

    public void setDensityScale(float densityScale) {
        this.mDensityScale = densityScale;
    }

    public void setPagingTouchSlop(float pagingTouchSlop) {
        this.mPagingTouchSlop = pagingTouchSlop;
    }

    private float getPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getX() : ev.getY();
    }

    private float getTranslation(View v) {
        return this.mSwipeDirection == 0 ? v.getTranslationX() : v.getTranslationY();
    }

    private float getVelocity(VelocityTracker vt) {
        if (this.mSwipeDirection == 0) {
            return vt.getXVelocity();
        }
        return vt.getYVelocity();
    }

    private ObjectAnimator createTranslationAnimation(View v, float newPos) {
        return ObjectAnimator.ofFloat(v, this.mSwipeDirection == 0 ? "translationX" : "translationY", new float[]{newPos});
    }

    private float getPerpendicularVelocity(VelocityTracker vt) {
        if (this.mSwipeDirection == 0) {
            return vt.getYVelocity();
        }
        return vt.getXVelocity();
    }

    private void setTranslation(View v, float translate) {
        if (this.mSwipeDirection == 0) {
            v.setTranslationX(translate);
        } else {
            v.setTranslationY(translate);
        }
    }

    private float getSize(View v) {
        int measuredHeight;
        if (this.mSwipeDirection == 0) {
            measuredHeight = v.getMeasuredWidth();
        } else {
            measuredHeight = v.getMeasuredHeight();
        }
        return (float) measuredHeight;
    }

    private float getAlphaForOffset(View view) {
        float viewSize = getSize(view);
        float fadeSize = 0.5f * viewSize;
        float result = 1.0f;
        float pos = getTranslation(view);
        if (pos >= viewSize * 0.0f) {
            result = 1.0f - ((pos - (viewSize * 0.0f)) / fadeSize);
        } else if (pos < viewSize * 1.0f) {
            result = 1.0f + (((viewSize * 0.0f) + pos) / fadeSize);
        }
        return Math.max(this.mMinAlpha, result);
    }

    /* access modifiers changed from: private */
    public void updateAlphaFromOffset(View animView, boolean dismissable) {
        if (dismissable) {
            float alpha = getAlphaForOffset(animView);
            if (alpha == 0.0f || alpha == 1.0f) {
                animView.setLayerType(0, null);
            } else {
                animView.setLayerType(2, null);
            }
            animView.setAlpha(getAlphaForOffset(animView));
        }
        invalidateGlobalRegion(animView);
    }

    private static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom()));
    }

    private static void invalidateGlobalRegion(View view, RectF childBounds) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(childBounds);
            view.invalidate((int) Math.floor((double) childBounds.left), (int) Math.floor((double) childBounds.top), (int) Math.ceil((double) childBounds.right), (int) Math.ceil((double) childBounds.bottom));
        }
    }

    private void removeLongPressCallback() {
        if (this.mWatchLongPress != null) {
            this.mHandler.removeCallbacks(this.mWatchLongPress);
            this.mWatchLongPress = null;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.mDragging = false;
                this.mLongPressSent = false;
                this.mCurrView = this.mCallback.getChildAtPosition(ev);
                this.mVelocityTracker.clear();
                if (this.mCurrView != null) {
                    this.mCurrAnimView = this.mCallback.getChildContentView(this.mCurrView);
                    this.mCanCurrViewBeDimissed = this.mCallback.canChildBeDismissed(this.mCurrView);
                    this.mVelocityTracker.addMovement(ev);
                    this.mInitialTouchPos = getPos(ev);
                    if (this.mLongPressListener != null) {
                        if (this.mWatchLongPress == null) {
                            this.mWatchLongPress = new Runnable() {
                                public void run() {
                                    if (SwipeHelper.this.mCurrView != null && !SwipeHelper.this.mLongPressSent) {
                                        SwipeHelper.this.mLongPressSent = true;
                                        SwipeHelper.this.mCurrView.sendAccessibilityEvent(2);
                                        SwipeHelper.this.mLongPressListener.onLongClick(SwipeHelper.this.mCurrView);
                                    }
                                }
                            };
                        }
                        this.mHandler.postDelayed(this.mWatchLongPress, this.mLongPressTimeout);
                        break;
                    }
                }
                break;
            case 1:
            case 3:
                this.mDragging = false;
                this.mCurrView = null;
                this.mCurrAnimView = null;
                this.mLongPressSent = false;
                removeLongPressCallback();
                break;
            case 2:
                if (this.mCurrView != null && !this.mLongPressSent) {
                    this.mVelocityTracker.addMovement(ev);
                    if (Math.abs(getPos(ev) - this.mInitialTouchPos) > this.mPagingTouchSlop) {
                        this.mCallback.onBeginDrag(this.mCurrView);
                        this.mDragging = true;
                        this.mInitialTouchPos = getPos(ev) - getTranslation(this.mCurrAnimView);
                        removeLongPressCallback();
                        break;
                    }
                }
                break;
        }
        return this.mDragging;
    }

    public void dismissChild(final View view, float velocity) {
        float newPos;
        int duration;
        final View animView = this.mCallback.getChildContentView(view);
        final boolean canAnimViewBeDismissed = this.mCallback.canChildBeDismissed(view);
        if (velocity < 0.0f || ((velocity == 0.0f && getTranslation(animView) < 0.0f) || (velocity == 0.0f && getTranslation(animView) == 0.0f && this.mSwipeDirection == 1))) {
            newPos = -getSize(animView);
        } else {
            newPos = getSize(animView);
        }
        if (velocity != 0.0f) {
            duration = Math.min(400, (int) ((Math.abs(newPos - getTranslation(animView)) * 1000.0f) / Math.abs(velocity)));
        } else {
            duration = 200;
        }
        animView.setLayerType(2, null);
        ObjectAnimator anim = createTranslationAnimation(animView, newPos);
        anim.setInterpolator(sLinearInterpolator);
        anim.setDuration((long) duration);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                SwipeHelper.this.mCallback.onChildDismissed(view);
                animView.setLayerType(0, null);
            }
        });
        anim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.updateAlphaFromOffset(animView, canAnimViewBeDismissed);
            }
        });
        anim.start();
    }

    public void snapChild(View view, float velocity) {
        final View animView = this.mCallback.getChildContentView(view);
        final boolean canAnimViewBeDismissed = this.mCallback.canChildBeDismissed(animView);
        ObjectAnimator anim = createTranslationAnimation(animView, 0.0f);
        anim.setDuration(150);
        anim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.updateAlphaFromOffset(animView, canAnimViewBeDismissed);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                SwipeHelper.this.updateAlphaFromOffset(animView, canAnimViewBeDismissed);
            }
        });
        anim.start();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean childSwipedFastEnough;
        if (this.mLongPressSent) {
            return true;
        }
        if (!this.mDragging) {
            removeLongPressCallback();
            return false;
        }
        this.mVelocityTracker.addMovement(ev);
        switch (ev.getAction()) {
            case 1:
            case 3:
                if (this.mCurrView != null) {
                    this.mVelocityTracker.computeCurrentVelocity(1000, 2000.0f * this.mDensityScale);
                    float escapeVelocity = 100.0f * this.mDensityScale;
                    float velocity = getVelocity(this.mVelocityTracker);
                    float perpendicularVelocity = getPerpendicularVelocity(this.mVelocityTracker);
                    boolean childSwipedFarEnough = ((double) Math.abs(getTranslation(this.mCurrAnimView))) > ((double) getSize(this.mCurrAnimView)) * 0.4d;
                    if (Math.abs(velocity) <= escapeVelocity || Math.abs(velocity) <= Math.abs(perpendicularVelocity)) {
                        childSwipedFastEnough = false;
                    } else {
                        childSwipedFastEnough = ((velocity > 0.0f ? 1 : (velocity == 0.0f ? 0 : -1)) > 0) == ((getTranslation(this.mCurrAnimView) > 0.0f ? 1 : (getTranslation(this.mCurrAnimView) == 0.0f ? 0 : -1)) > 0);
                    }
                    boolean dismissChild = this.mCallback.canChildBeDismissed(this.mCurrView) ? !childSwipedFastEnough ? childSwipedFarEnough : true : false;
                    if (!dismissChild) {
                        this.mCallback.onDragCancelled(this.mCurrView);
                        snapChild(this.mCurrView, velocity);
                        break;
                    } else {
                        View view = this.mCurrView;
                        if (!childSwipedFastEnough) {
                            velocity = 0.0f;
                        }
                        dismissChild(view, velocity);
                        break;
                    }
                }
                break;
            case 2:
            case 4:
                if (this.mCurrView != null) {
                    float delta = getPos(ev) - this.mInitialTouchPos;
                    if (!this.mCallback.canChildBeDismissed(this.mCurrView)) {
                        float size = getSize(this.mCurrAnimView);
                        float maxScrollDistance = 0.15f * size;
                        delta = Math.abs(delta) >= size ? delta > 0.0f ? maxScrollDistance : -maxScrollDistance : maxScrollDistance * ((float) Math.sin(((double) (delta / size)) * 1.5707963267948966d));
                    }
                    setTranslation(this.mCurrAnimView, delta);
                    updateAlphaFromOffset(this.mCurrAnimView, this.mCanCurrViewBeDimissed);
                    break;
                }
                break;
        }
        return true;
    }
}

