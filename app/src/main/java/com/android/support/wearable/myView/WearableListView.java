package com.android.support.wearable.myView;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WearableListView extends RecyclerView {
    public static OnItemClickListener mOnItemClickListener = null;
    private boolean mCanClick;
    boolean mCanScrollInTip;
    private ClickListener mClickListener;
    private boolean mGestureDirectionLocked;
    private boolean mGreedyTouchMode;
    private int mInitialOffset;
    private int mLastScrollChange;
    private final int[] mLocation;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private Set<OnScrollListener> mOnScrollListeners;
    private OnOverScrollListener mOverScrollListener;
    private boolean mPossibleVerticalSwipe;
    private Handler mPressedHandler;
    private View mPressedItem;
    private Runnable mPressedRunnable;
    private int mPreviousCentral;
    private AnimatorSet mScrollAnimator;
    private Scroller mScroller;
    private float mStartX;
    private float mStartY;
    private int mTapPositionX;
    private int mTapPositionY;
    private final float[] mTapRegions;
    private int mTouchSlop;

    /* renamed from: com.android.support.wearable.myView.WearableListView$1 */
    class C00931 implements Runnable {
        C00931() {
        }

        public void run() {
            if (WearableListView.this.mPressedItem != null) {
                WearableListView.this.mPressedItem.setPressed(true);
            }
        }
    }

    /* renamed from: com.android.support.wearable.myView.WearableListView$2 */
    class C00942 extends android.support.v7.widget.RecyclerView.OnScrollListener {
        C00942() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0 && WearableListView.this.getChildCount() > 0) {
                WearableListView.this.handleTouchUp(null);
            }
            for (OnScrollListener listener : WearableListView.this.mOnScrollListeners) {
                listener.onScrollStateChanged(newState);
            }
        }
    }

    public static abstract class Adapter extends android.support.v7.widget.RecyclerView.Adapter<ViewHolder> {
    }

    public interface ClickListener {
        void onClick(ViewHolder viewHolder);

        void onTopEmptyRegionClick();
    }

    public interface Item {
        float getCurrentProximityValue();

        float getProximityMaxValue();

        float getProximityMinValue();

        void onScaleDownStart();

        void onScaleUpStart();

        void setScalingAnimatorValue(float f);
    }

    private class LayoutManager extends android.support.v7.widget.RecyclerView.LayoutManager {
        private int mAbsoluteScroll;
        private int mFirstPosition;
        private boolean mPushFirstHigher;

        private LayoutManager() {
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            int parentBottom = getHeight() - getPaddingBottom();
            View childAt = getChildCount() > 0 ? getChildAt(0) : null;
            int oldTop = (getPaddingTop() + (WearableListView.this.getAdjustedHeight() / 3)) + WearableListView.this.mInitialOffset;
            if (this.mPushFirstHigher) {
                oldTop -= WearableListView.this.getAdjustedHeight() / 3;
            }
            if (childAt != null) {
                oldTop = childAt.getTop();
            }
            detachAndScrapAttachedViews(recycler);
            int top = oldTop;
            int left = getPaddingLeft();
            int right = getWidth() - getPaddingRight();
            int count = WearableListView.this.getAdapter().getItemCount();
            int i = 0;
            while (getFirstPosition() + i < count && top < parentBottom) {
                View v = recycler.getViewForPosition(getFirstPosition() + i);
                addView(v, i);
                measureView(v);
                int bottom = top + getItemHeight();
                if (i == 0 && getFirstPosition() == 0) {
                    top = WearableListView.this.getTopViewMaxTop();
                    bottom = top + getItemHeight();
                    v.layout(left, top, right, bottom);
                    Log.d("WearableListView", "onLayoutChildren  i =0 top = " + WearableListView.this.getTopViewMaxTop() + "  bottom = " + bottom);
                } else {
                    v.layout(left, top, right, bottom);
                    Log.d("WearableListView", "onLayoutChildren  i = " + i + " top = " + top + "  bottom = " + bottom);
                }
                i++;
                top = bottom;
            }
            Log.d("WearableListView", "onLayoutChildren   getFirstPosition = " + getFirstPosition());
            if (getChildCount() > 0) {
                WearableListView.this.notifyChildrenAboutProximity(false);
            }
            if (childAt == null) {
                int i2;
                if (this.mPushFirstHigher) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                setAbsoluteScroll((i2 + getFirstPosition()) * getItemHeight());
            }
        }

        private void setAbsoluteScroll(int absoluteScroll) {
            this.mAbsoluteScroll = absoluteScroll;
            for (OnScrollListener listener : WearableListView.this.mOnScrollListeners) {
                listener.onAbsoluteScrollChange(this.mAbsoluteScroll);
            }
        }

        private void measureView(View v) {
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            v.measure(android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(getWidth(), ((getPaddingLeft() + getPaddingRight()) + lp.leftMargin) + lp.rightMargin, lp.width, canScrollHorizontally()), android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(getHeight(), ((getPaddingTop() + getPaddingBottom()) + lp.topMargin) + lp.bottomMargin, getHeight() / 3, canScrollVertically()));
        }

        public LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(-1, -2);
        }

        public boolean canScrollVertically() {
            return true;
        }

        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            if (getChildCount() == 0) {
                return 0;
            }
            int scrollBy;
            int scrolled = 0;
            int left = getPaddingLeft();
            int right = getWidth() - getPaddingRight();
            if (dy < 0) {
                while (scrolled > dy) {
                    View topView = getChildAt(0);
                    if (getFirstPosition() > 0) {
                        scrollBy = Math.min(scrolled - dy, Math.max(-topView.getTop(), 0));
                        scrolled -= scrollBy;
                        offsetChildrenVertical(scrollBy);
                        if (getFirstPosition() <= 0 || scrolled <= dy) {
                            break;
                        }
                        this.mFirstPosition--;
                        View v = recycler.getViewForPosition(getFirstPosition());
                        addView(v, 0);
                        measureView(v);
                        int bottom = topView.getTop();
                        v.layout(left, bottom - getItemHeight(), right, bottom);
                    } else {
                        this.mPushFirstHigher = false;
                        scrollBy = Math.min(-dy, (WearableListView.this.mOverScrollListener != null ? getHeight() : WearableListView.this.getTopViewMaxTop()) - topView.getTop());
                        scrolled -= scrollBy;
                        offsetChildrenVertical(scrollBy);
                    }
                }
            }
            if (dy > 0) {
                int parentHeight = getHeight();
                while (scrolled < dy) {
                    View bottomView = getChildAt(getChildCount() - 1);
                    if (getItemCount() <= this.mFirstPosition + getChildCount()) {
                        scrollBy = Math.max(-dy, (getHeight() / 2) - bottomView.getBottom());
                        scrolled -= scrollBy;
                        offsetChildrenVertical(scrollBy);
                        break;
                    }
                    scrollBy = -Math.min(dy - scrolled, Math.max(bottomView.getBottom() - parentHeight, 0));
                    scrolled -= scrollBy;
                    offsetChildrenVertical(scrollBy);
                    if (scrolled >= dy) {
                        break;
                    }
                    View v = recycler.getViewForPosition(this.mFirstPosition + getChildCount());
                    int top = getChildAt(getChildCount() - 1).getBottom();
                    addView(v);
                    measureView(v);
                    v.layout(left, top, right, top + getItemHeight());
                }
            }
            recycleViewsOutOfBounds(recycler);
            WearableListView.this.notifyChildrenAboutProximity(true);
            WearableListView.this.onScroll(scrolled);
            setAbsoluteScroll(this.mAbsoluteScroll + scrolled);
            return scrolled;
        }

        public void scrollToPosition(int position) {
            Log.d("WearableListView", "scrollToPosition =" + position);
            if (position > 0) {
                this.mFirstPosition = position - 1;
                this.mPushFirstHigher = true;
                return;
            }
            this.mFirstPosition = position;
            this.mPushFirstHigher = false;
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
            Log.d("WearableListView", "scrollToPosition " + position);
            LinearSmoothScroller linearSmoothScroller = new SmoothScroller(recyclerView.getContext(), this);
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }

        private int getItemHeight() {
            return WearableListView.this.getAdjustedHeight() / 3;
        }

        private void recycleViewsOutOfBounds(Recycler recycler) {
            int i;
            Log.d("WearableListView", "recycleViewsOutOfBounds");
            int childCount = getChildCount();
            int parentWidth = getWidth();
            int parentHeight = getHeight();
            boolean foundFirst = false;
            int first = 0;
            int last = 0;
            for (i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (v.hasFocus() || (v.getRight() >= 0 && v.getLeft() <= parentWidth && v.getBottom() >= 0 && v.getTop() <= parentHeight)) {
                    if (!foundFirst) {
                        first = i;
                        foundFirst = true;
                    }
                    last = i;
                }
            }
            for (i = childCount - 1; i > last; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
            for (i = first - 1; i >= 0; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
            if (getChildCount() == 0) {
                this.mFirstPosition = 0;
            } else if (first > 0) {
                this.mPushFirstHigher = true;
                this.mFirstPosition += first;
            }
        }

        public int getFirstPosition() {
            return this.mFirstPosition;
        }

        public void onAdapterChanged(android.support.v7.widget.RecyclerView.Adapter oldAdapter, android.support.v7.widget.RecyclerView.Adapter newAdapter) {
            Log.d("WearableListView", "onAdapterChanged");
            removeAllViews();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnOverScrollListener {
        void onOverScroll();
    }

    public interface OnScrollListener {
        void onAbsoluteScrollChange(int i);

        void onCentralPositionChanged(int i);

        void onScroll(int i);

        void onScrollStateChanged(int i);
    }

    private static class SmoothScroller extends LinearSmoothScroller {
        private final LayoutManager mLayoutManager;

        public SmoothScroller(Context context, LayoutManager manager) {
            super(context);
            this.mLayoutManager = manager;
        }

        protected void onStart() {
            super.onStart();
        }

        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 100.0f / ((float) displayMetrics.densityDpi);
        }

        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return ((boxStart + boxEnd) / 2) - ((viewStart + viewEnd) / 2);
        }

        public PointF computeScrollVectorForPosition(int targetPosition) {
            if (targetPosition < this.mLayoutManager.getFirstPosition()) {
                return new PointF(0.0f, -1.0f);
            }
            return new PointF(0.0f, 1.0f);
        }
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
        private View mItemView;
        private float mMaxValue;
        private float mMinValue;
        private ObjectAnimator mScalingDownAnimator;
        private ObjectAnimator mScalingUpAnimator;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof Item) {
                Item item = (Item) itemView;
                this.mItemView = itemView;
                this.mMinValue = item.getProximityMinValue();
                item.setScalingAnimatorValue(this.mMinValue);
                this.mMaxValue = item.getProximityMaxValue();
                this.mScalingUpAnimator = ObjectAnimator.ofFloat(item, "scalingAnimatorValue", new float[]{this.mMinValue, this.mMaxValue});
                this.mScalingUpAnimator.setDuration(150);
                this.mScalingDownAnimator = ObjectAnimator.ofFloat(item, "scalingAnimatorValue", new float[]{this.mMaxValue, this.mMinValue});
                this.mScalingDownAnimator.setDuration(150);
                itemView.setOnClickListener(this);
            }
        }

        public void onCenterProximity(boolean isCentralItem, boolean animate) {
            Log.d("WearableListView", "onCenterProximity");
            if (this.itemView instanceof Item) {
                Item item = (Item) this.itemView;
                if (!isCentralItem) {
                    this.mScalingUpAnimator.cancel();
                    if (!animate) {
                        this.mScalingDownAnimator.cancel();
                        item.setScalingAnimatorValue(item.getProximityMinValue());
                    } else if (!this.mScalingDownAnimator.isRunning()) {
                        this.mScalingDownAnimator.setFloatValues(new float[]{item.getCurrentProximityValue(), this.mMinValue});
                        this.mScalingDownAnimator.start();
                        item.onScaleDownStart();
                    }
                } else if (animate) {
                    this.mScalingDownAnimator.cancel();
                    if (!this.mScalingUpAnimator.isRunning()) {
                        this.mScalingUpAnimator.setFloatValues(new float[]{item.getCurrentProximityValue(), this.mMaxValue});
                        this.mScalingUpAnimator.start();
                    }
                } else {
                    this.mScalingUpAnimator.cancel();
                    item.setScalingAnimatorValue(item.getProximityMaxValue());
                }
                if (isCentralItem) {
                    item.onScaleUpStart();
                } else {
                    item.onScaleDownStart();
                }
            }
        }

        public void onClick(View v) {
            if (WearableListView.mOnItemClickListener != null) {
                WearableListView.mOnItemClickListener.onItemClick(this.mItemView, getPosition());
            }
        }
    }

    public WearableListView(Context context) {
        this(context, null);
    }

    public WearableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCanClick = true;
        this.mCanScrollInTip = true;
        this.mOnScrollListeners = new HashSet();
        this.mInitialOffset = 0;
        this.mTapRegions = new float[2];
        this.mPreviousCentral = 0;
        this.mPressedItem = null;
        this.mLocation = new int[2];
        this.mPressedHandler = new Handler();
        this.mPressedRunnable = new C00931();
        setHasFixedSize(true);
        setOverScrollMode(2);
        setLayoutManager(new LayoutManager());
        setOnScrollListener(new C00942());
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d("WearableListView", "onInterceptTouchEvent");
        if (this.mGreedyTouchMode && getChildCount() > 0) {
            int action = event.getActionMasked();
            if (action == 0) {
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                this.mPossibleVerticalSwipe = true;
                this.mGestureDirectionLocked = false;
            } else if (action == 2 && this.mPossibleVerticalSwipe) {
                handlePossibleVerticalSwipe(event);
            }
            getParent().requestDisallowInterceptTouchEvent(this.mPossibleVerticalSwipe);
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean handlePossibleVerticalSwipe(MotionEvent event) {
        if (this.mGestureDirectionLocked) {
            return this.mPossibleVerticalSwipe;
        }
        float deltaX = Math.abs(this.mStartX - event.getX());
        float deltaY = Math.abs(this.mStartY - event.getY());
        if ((deltaX * deltaX) + (deltaY * deltaY) > ((float) (this.mTouchSlop * this.mTouchSlop))) {
            if (deltaX > deltaY) {
                this.mPossibleVerticalSwipe = false;
            }
            this.mGestureDirectionLocked = true;
        }
        return this.mPossibleVerticalSwipe;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (getChildCount() <= 0) {
            return result;
        }
        int action = event.getActionMasked();
        if (action == 0) {
            handleTouchDown(event);
            Log.i("WearableListView", "action down");
            return result;
        } else if (action == 1) {
            handleTouchUp(event);
            Log.i("WearableListView", "action up");
            getParent().requestDisallowInterceptTouchEvent(false);
            return result;
        } else if (action == 2) {
            Log.i("WearableListView", "action move");
            if (Math.abs(this.mTapPositionX - ((int) event.getX())) >= this.mTouchSlop || Math.abs(this.mTapPositionY - ((int) event.getY())) >= this.mTouchSlop) {
                releasePressedItem();
                this.mCanClick = false;
            }
            result |= handlePossibleVerticalSwipe(event);
            getParent().requestDisallowInterceptTouchEvent(this.mPossibleVerticalSwipe);
            return result;
        } else if (action != 3) {
            return result;
        } else {
            Log.i("WearableListView", "action cancel");
            getParent().requestDisallowInterceptTouchEvent(false);
            return result;
        }
    }

    private void releasePressedItem() {
        if (this.mPressedItem != null) {
            this.mPressedItem.setPressed(false);
            this.mPressedHandler.removeCallbacks(this.mPressedRunnable);
            this.mPressedItem = null;
        }
    }

    public void onScroll(int dy) {
        Log.d("WearableListView", "onScroll dy= " + dy);
        if (this.mCanScrollInTip || dy >= 0) {
            for (OnScrollListener listener : this.mOnScrollListeners) {
                listener.onScroll(dy);
            }
        }
    }

    private boolean checkForTap(MotionEvent event) {
        float rawY = event.getRawY();
        int index = findCenterViewIndex();
        ViewHolder holder = getChildViewHolder(getChildAt(index));
        computeTapRegions(this.mTapRegions);
        if (rawY > this.mTapRegions[0] && rawY < this.mTapRegions[1]) {
            if (this.mClickListener != null && isEnabled()) {
                this.mClickListener.onClick(holder);
            }
            return true;
        } else if (index > 0 && rawY <= this.mTapRegions[0]) {
            animateToMiddle(index - 1, index);
            return true;
        } else if (index < getChildCount() - 1 && rawY >= this.mTapRegions[1]) {
            animateToMiddle(index + 1, index);
            return true;
        } else if (index != 0 || rawY > this.mTapRegions[0] || this.mClickListener == null || !isEnabled()) {
            return false;
        } else {
            this.mClickListener.onTopEmptyRegionClick();
            return true;
        }
    }

    private void animateToMiddle(int newCenterIndex, int oldCenterIndex) {
        Log.d("WearableListView", "animateToMiddle");
        if (newCenterIndex == oldCenterIndex) {
            throw new IllegalArgumentException("newCenterIndex must be different from oldCenterIndex");
        }
        startScrollAnimation(new ArrayList(), (int) (((float) (getPaddingTop() + (getAdjustedHeight() / 3))) - getChildAt(newCenterIndex).getY()), 150);
    }

    private void startScrollAnimation(List<Animator> animators, int scroll, long duration) {
        Log.d("WearableListView", "startScrollAnimation1");
        startScrollAnimation(animators, scroll, duration, 0);
    }

    private void startScrollAnimation(List<Animator> animators, int scroll, long duration, long delay) {
        Log.d("WearableListView", "startScrollAnimation2");
        startScrollAnimation(animators, scroll, duration, delay, null);
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void startScrollAnimation(List<Animator> animators, int scroll, long duration, long delay, AnimatorListener listener) {
        Log.d("WearableListView", "startScrollAnimation3");
        if (this.mScrollAnimator != null) {
            this.mScrollAnimator.cancel();
        }
        this.mLastScrollChange = 0;
        animators.add(ObjectAnimator.ofInt(this, "scrollVertically", new int[]{0, -scroll}));
        this.mScrollAnimator = new AnimatorSet();
        this.mScrollAnimator.playTogether(animators);
        this.mScrollAnimator.setDuration(duration);
        if (listener != null) {
            this.mScrollAnimator.addListener(listener);
        }
        if (delay > 0) {
            this.mScrollAnimator.setStartDelay(delay);
        }
        this.mScrollAnimator.start();
    }

    public boolean fling(int velocityX, int velocityY) {
        Log.d("WearableListView", "fling = " + velocityX + "  velocityY=" + velocityY);
        int x = velocityX;
        int y = velocityY;
        if (getChildCount() == 0) {
            return false;
        }
        int currentPosition = getChildPosition(getChildAt(findCenterViewIndex()));
        Log.i("WearableListView", "getChildPosition  " + currentPosition);
        if ((currentPosition == 0 && velocityY < 0) || (currentPosition == getAdapter().getItemCount() - 1 && velocityY > 0)) {
            return super.fling(velocityX, velocityY);
        }
        if (Math.abs(velocityY) < this.mMinFlingVelocity) {
            return false;
        }
        velocityY = Math.max(Math.min(velocityY, this.mMaxFlingVelocity), -this.mMaxFlingVelocity);
        if (this.mScroller == null) {
            this.mScroller = new Scroller(getContext(), null, true);
        }
        this.mScroller.fling(0, 0, 0, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        int delta = this.mScroller.getFinalY() / (getPaddingTop() + (getAdjustedHeight() / 2));
        if (delta == 0) {
            delta = velocityY > 0 ? 1 : -1;
        }
        int finalPosition = Math.max(0, Math.min(getAdapter().getItemCount() - 1, currentPosition + delta));
        smoothScrollBy(velocityX, y / 4);
        Log.e("WearableListView", "smoothScrollBy x=" + velocityX + "  y=" + (y / 4));
        return true;
    }

    public ViewHolder getChildViewHolder(View child) {
        return (ViewHolder) super.getChildViewHolder(child);
    }

    private int findCenterViewIndex() {
        int count = getChildCount();
        int index = -1;
        int closest = Integer.MAX_VALUE;
        int centerY = getCenterYPos(this);
        for (int i = 0; i < count; i++) {
            int distance = Math.abs(centerY - (getTop() + getCenterYPos(getChildAt(i))));
            if (distance < closest) {
                closest = distance;
                index = i;
            }
        }
        if (index != -1) {
            return index;
        }
        throw new IllegalStateException("Can't find central view.");
    }

    private static int getCenterYPos(View v) {
        return (v.getTop() + v.getPaddingTop()) + (getAdjustedHeight(v) / 2);
    }

    private void handleTouchUp(MotionEvent event) {
        Log.d("WearableListView", "handleTouchUp");
        if (!(this.mCanClick && event != null && checkForTap(event)) && getScrollState() == 0) {
            if (isOverScrolling()) {
                this.mOverScrollListener.onOverScroll();
            } else {
                this.mCanClick = true;
            }
        }
    }

    private boolean isOverScrolling() {
        return getChildCount() > 0 && getChildAt(0).getTop() >= getTopViewMaxTop() && this.mOverScrollListener != null;
    }

    private int getTopViewMaxTop() {
        Log.i("WearableListView", "getTopViewMaxTop  =" + (getHeight() / 4));
        return getHeight() / 4;
    }

    private void handleTouchDown(MotionEvent event) {
        Log.d("WearableListView", "handleTouchDown");
        if (this.mCanClick) {
            this.mTapPositionX = (int) event.getX();
            this.mTapPositionY = (int) event.getY();
            float rawY = event.getRawY();
            computeTapRegions(this.mTapRegions);
            if (rawY > this.mTapRegions[0] && rawY < this.mTapRegions[1]) {
                View view = getChildAt(findCenterViewIndex());
                if (view instanceof Item) {
                    this.mPressedItem = view;
                    this.mPressedHandler.postDelayed(this.mPressedRunnable, (long) ViewConfiguration.getTapTimeout());
                }
            }
        }
    }

    private void notifyChildrenAboutProximity(boolean animate) {
        Log.d("WearableListView", "notifyChildrenAboutProximity");
        int count = getChildCount();
        int index = findCenterViewIndex();
        for (int i = Math.max(0, index - 1); i < Math.min(index + 2, count); i++) {
            boolean z;
            ViewHolder holder = getChildViewHolder(getChildAt(i));
            if (i == index) {
                z = true;
            } else {
                z = false;
            }
            holder.onCenterProximity(z, animate);
        }
        int position = getChildViewHolder(getChildAt(index)).getPosition();
        if (position != this.mPreviousCentral) {
            for (OnScrollListener listener : this.mOnScrollListeners) {
                listener.onCentralPositionChanged(position);
            }
            this.mPreviousCentral = position;
        }
    }

    private int getAdjustedHeight() {
        return getAdjustedHeight(this);
    }

    private static int getAdjustedHeight(View v) {
        return (v.getHeight() - v.getPaddingBottom()) - v.getPaddingTop();
    }

    private void computeTapRegions(float[] tapRegions) {
        Log.d("WearableListView", "computeTapRegions");
        this.mLocation[1] = 0;
        this.mLocation[0] = 0;
        getLocationOnScreen(this.mLocation);
        int mScreenTop = this.mLocation[1];
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        tapRegions[0] = ((float) mScreenTop) + (((float) (metrics.heightPixels - mScreenTop)) * 0.3333333f);
        tapRegions[1] = ((float) mScreenTop) + (((float) (metrics.heightPixels - mScreenTop)) * 0.6666666f);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
