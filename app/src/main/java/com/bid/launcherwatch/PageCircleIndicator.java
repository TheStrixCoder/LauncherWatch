package com.bid.launcherwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class PageCircleIndicator extends LinearLayout implements OnPageChangeListener {
    private Animator mAnimationIn;
    private Animator mAnimationOut;
    private int mAnimatorResId = R.animator.scale_with_alpha;
    private int mAnimatorReverseResId = 0;
    private Context mContext;
    private int mCurrentPosition = 0;
    private int mIndicatorBackgroundResId = R.drawable.white_radius;
    private int mIndicatorHeight = -1;
    private int mIndicatorMargin = -1;
    private int mIndicatorUnselectedBackgroundResId = R.drawable.white_radius;
    private int mIndicatorWidth = -1;
    private ViewPager mViewpager;

    private class ReverseInterpolator implements Interpolator {
        /* synthetic */ ReverseInterpolator(PageCircleIndicator this$02, ReverseInterpolator reverseInterpolator) {
            this();
        }

        private ReverseInterpolator() {
        }

        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    public PageCircleIndicator(Context context) {
        super(context);
        this.mContext = context;
        init(context, null);
    }

    public PageCircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(17);
        handleTypedArray(context, attrs);
        checkIndicatorConfig(context);
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageCircleIndicator);
            this.mIndicatorWidth = typedArray.getDimensionPixelSize(0, 3);
            this.mIndicatorHeight = typedArray.getDimensionPixelSize(1, 3);
            this.mIndicatorMargin = typedArray.getDimensionPixelSize(2, 8);
            this.mAnimatorResId = typedArray.getResourceId(3, R.animator.scale_with_alpha);
            this.mAnimatorReverseResId = typedArray.getResourceId(4, 0);
            this.mIndicatorBackgroundResId = typedArray.getResourceId(5, R.drawable.white_radius);
            this.mIndicatorUnselectedBackgroundResId = typedArray.getResourceId(6, this.mIndicatorBackgroundResId);
            typedArray.recycle();
        }
    }

    private void checkIndicatorConfig(Context context) {
        int i;
        int i2;
        this.mIndicatorWidth = this.mIndicatorWidth < 0 ? dip2px(5.0f) : this.mIndicatorWidth;
        this.mIndicatorHeight = this.mIndicatorHeight < 0 ? dip2px(5.0f) : this.mIndicatorHeight;
        this.mIndicatorMargin = this.mIndicatorMargin < 0 ? dip2px(5.0f) : this.mIndicatorMargin;
        this.mAnimatorResId = this.mAnimatorResId == 0 ? R.animator.scale_with_alpha : this.mAnimatorResId;
        this.mAnimationOut = AnimatorInflater.loadAnimator(context, this.mAnimatorResId);
        if (this.mAnimatorReverseResId == 0) {
            this.mAnimationIn = AnimatorInflater.loadAnimator(context, this.mAnimatorResId);
            this.mAnimationIn.setInterpolator(new ReverseInterpolator(this, null));
        } else {
            this.mAnimationIn = AnimatorInflater.loadAnimator(context, this.mAnimatorReverseResId);
        }
        if (this.mIndicatorBackgroundResId == 0) {
            i = R.drawable.white_radius;
        } else {
            i = this.mIndicatorBackgroundResId;
        }
        this.mIndicatorBackgroundResId = i;
        if (this.mIndicatorUnselectedBackgroundResId == 0) {
            i2 = this.mIndicatorBackgroundResId;
        } else {
            i2 = this.mIndicatorUnselectedBackgroundResId;
        }
        this.mIndicatorUnselectedBackgroundResId = i2;
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewpager = viewPager;
        this.mCurrentPosition = this.mViewpager.getCurrentItem();
        createIndicators(viewPager);
        this.mViewpager.setOnPageChangeListener(this);
        onPageSelected(this.mCurrentPosition);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        Log.d("PageCircleIndicator", "onPageSelected position = " + position);
        if (this.mViewpager.getAdapter() != null && this.mViewpager.getAdapter().getCount() > 0) {
            if (this.mAnimationIn.isRunning()) {
                this.mAnimationIn.end();
            }
            if (this.mAnimationOut.isRunning()) {
                this.mAnimationOut.end();
            }
            View currentIndicator = getChildAt(this.mCurrentPosition);
            currentIndicator.setBackgroundResource(this.mIndicatorUnselectedBackgroundResId);
            this.mAnimationIn.setTarget(currentIndicator);
            this.mAnimationIn.start();
            View selectedIndicator = getChildAt(position);
            selectedIndicator.setBackgroundResource(this.mIndicatorBackgroundResId);
            this.mAnimationOut.setTarget(selectedIndicator);
            this.mAnimationOut.start();
            this.mCurrentPosition = position;
            Intent mIntent = new Intent("com.step.count.page");
            if (this.mCurrentPosition == 2) {
                Log.d("PageCircleIndicator", "STEP_COUNT_PAGE_SELECTED true");
                mIntent.putExtra("selected", true);
            } else {
                Log.d("PageCircleIndicator", "STEP_COUNT_PAGE_SELECTED false");
                mIntent.putExtra("selected", false);
            }
            this.mContext.sendBroadcast(mIntent);
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    private void createIndicators(ViewPager viewPager) {
        removeAllViews();
        if (viewPager.getAdapter() != null) {
            int count = viewPager.getAdapter().getCount();
            Log.d("PageCircleIndicator", "createIndicators count = " + count);
            if (count > 0) {
                addIndicator(this.mIndicatorBackgroundResId, this.mAnimationOut);
                for (int i = 1; i < count; i++) {
                    addIndicator(this.mIndicatorUnselectedBackgroundResId, this.mAnimationIn);
                }
            }
        }
    }

    private void addIndicator(@DrawableRes int backgroundDrawableId, Animator animator) {
        if (animator.isRunning()) {
            animator.end();
        }
        View Indicator = new View(getContext());
        Indicator.setBackgroundResource(backgroundDrawableId);
        addView(Indicator, this.mIndicatorWidth, this.mIndicatorHeight);
        LayoutParams lp = (LayoutParams) Indicator.getLayoutParams();
        lp.leftMargin = this.mIndicatorMargin;
        lp.rightMargin = this.mIndicatorMargin;
        Indicator.setLayoutParams(lp);
        animator.setTarget(Indicator);
        animator.start();
    }

    public int dip2px(float dpValue) {
        return (int) ((dpValue * getResources().getDisplayMetrics().density) + 0.5f);
    }
}

