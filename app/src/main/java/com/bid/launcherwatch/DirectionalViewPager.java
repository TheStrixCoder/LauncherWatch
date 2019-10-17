package com.bid.launcherwatch;


import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.Scroller;
import java.util.ArrayList;

public class DirectionalViewPager extends ViewPager {
    private int mActivePointerId = -1;
    private PagerAdapter mAdapter;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCurItem;
    private boolean mInLayout;
    private float mInitialMotion;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems = new ArrayList<>();
    private float mLastMotionX;
    private float mLastMotionY;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private PagerObserver mObserver;
    private OnPageChangeListener mOnPageChangeListener;
    private int mOrientation = 0;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private int mRestoredCurItem = -1;
    private int mScrollState = 0;
    private Scroller mScroller;
    private boolean mScrolling;
    private boolean mScrollingCacheEnabled;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;

        ItemInfo() {
        }
    }

    private class PagerObserver extends DataSetObserver {
        /* synthetic */ PagerObserver(DirectionalViewPager this$02, PagerObserver pagerObserver) {
            this();
        }

        private PagerObserver() {
        }

        public void onChanged() {
            DirectionalViewPager.this.dataSetChanged();
        }

        public void onInvalidated() {
            DirectionalViewPager.this.dataSetChanged();
        }
    }

    public static class SavedState extends BaseSavedState {
        Parcelable adapterState;
        ClassLoader loader;
        int position;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.position);
            out.writeParcelable(this.adapterState, flags);
        }

        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }
    }

    public DirectionalViewPager(Context context) {
        super(context);
        initViewPager();
    }

    public DirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager();
        int orientation = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "orientation", -1);
        if (orientation != -1) {
            setOrientation(orientation);
        }
    }

    /* access modifiers changed from: 0000 */
    public void initViewPager() {
        setWillNotDraw(false);
        this.mScroller = new Scroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private void setScrollState(int newState) {
        if (this.mScrollState != newState) {
            this.mScrollState = newState;
            if (this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageScrollStateChanged(newState);
            }
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        int mlastItem = 0;
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
            this.mAdapter.startUpdate((ViewGroup) this);
            for (int i = 0; i < this.mItems.size(); i++) {
                ItemInfo ii = (ItemInfo) this.mItems.get(i);
                this.mAdapter.destroyItem((ViewGroup) this, ii.position, ii.object);
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
            this.mItems.clear();
            mlastItem = this.mCurItem;
            this.mCurItem = 0;
            scrollTo(0, 0);
        }
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver(this, null);
            }
            this.mAdapter.registerDataSetObserver(this.mObserver);
            if (mlastItem < this.mAdapter.getCount()) {
                this.mCurItem = mlastItem;
                if (this.mOrientation == 0) {
                    scrollTo(this.mCurItem * getWidth(), 0);
                } else {
                    scrollTo(0, this.mCurItem * getHeight());
                }
            }
            this.mPopulatePending = false;
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
                return;
            }
            populate();
        }
    }

    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setCurrentItem(int item) {
        this.mPopulatePending = false;
        setCurrentItemInternal(item, true, false);
    }

    /* access modifiers changed from: 0000 */
    public void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
        } else if (always || this.mCurItem != item || this.mItems.size() == 0) {
            if (item < 0) {
                item = 0;
            } else if (item >= this.mAdapter.getCount()) {
                item = this.mAdapter.getCount() - 1;
            }
            if (item > this.mCurItem + 1 || item < this.mCurItem - 1) {
                for (int i = 0; i < this.mItems.size(); i++) {
                    ((ItemInfo) this.mItems.get(i)).scrolling = true;
                }
            }
            boolean dispatchSelected = this.mCurItem != item;
            this.mCurItem = item;
            populate();
            if (smoothScroll) {
                if (this.mOrientation == 0) {
                    smoothScrollTo(getWidth() * item, 0);
                } else {
                    smoothScrollTo(0, getHeight() * item);
                }
                if (dispatchSelected && this.mOnPageChangeListener != null) {
                    this.mOnPageChangeListener.onPageSelected(item);
                }
            } else {
                if (dispatchSelected && this.mOnPageChangeListener != null) {
                    this.mOnPageChangeListener.onPageSelected(item);
                }
                completeScroll();
                if (this.mOrientation == 0) {
                    scrollTo(getWidth() * item, 0);
                } else {
                    scrollTo(0, getHeight() * item);
                }
            }
        } else {
            setScrollingCacheEnabled(false);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    /* access modifiers changed from: 0000 */
    public void smoothScrollTo(int x, int y) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll();
            return;
        }
        setScrollingCacheEnabled(true);
        this.mScrolling = true;
        setScrollState(2);
        this.mScroller.startScroll(sx, sy, dx, dy);
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = this.mAdapter.instantiateItem((ViewGroup) this, position);
        if (index < 0) {
            this.mItems.add(ii);
        } else {
            this.mItems.add(index, ii);
        }
    }

    /* access modifiers changed from: 0000 */
    public void dataSetChanged() {
        boolean needPopulate = this.mItems.isEmpty() && this.mAdapter.getCount() > 0;
        int newCurrItem = -1;
        int i = 0;
        while (i < this.mItems.size()) {
            ItemInfo ii = (ItemInfo) this.mItems.get(i);
            int newPos = this.mAdapter.getItemPosition(ii.object);
            if (newPos != -1) {
                if (newPos == -2) {
                    this.mItems.remove(i);
                    i--;
                    this.mAdapter.destroyItem((ViewGroup) this, ii.position, ii.object);
                    needPopulate = true;
                    if (this.mCurItem == ii.position) {
                        newCurrItem = Math.max(0, Math.min(this.mCurItem, this.mAdapter.getCount() - 1));
                    }
                } else if (ii.position != newPos) {
                    if (ii.position == this.mCurItem) {
                        newCurrItem = newPos;
                    }
                    ii.position = newPos;
                    needPopulate = true;
                }
            }
            i++;
        }
        if (newCurrItem >= 0) {
            setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }
        if (needPopulate) {
            populate();
            requestLayout();
        }
    }

    /* access modifiers changed from: 0000 */
    public void populate() {
        int lastPos;
        if (this.mAdapter != null && !this.mPopulatePending && getWindowToken() != null) {
            this.mAdapter.startUpdate((ViewGroup) this);
            int startPos = this.mCurItem > 0 ? this.mCurItem - 1 : this.mCurItem;
            int count = this.mAdapter.getCount();
            int endPos = this.mCurItem < count + -1 ? this.mCurItem + 1 : count - 1;
            int lastPos2 = -1;
            int i = 0;
            while (i < this.mItems.size()) {
                ItemInfo ii = (ItemInfo) this.mItems.get(i);
                if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
                    this.mItems.remove(i);
                    i--;
                    this.mAdapter.destroyItem((ViewGroup) this, ii.position, ii.object);
                } else if (lastPos2 < endPos && ii.position > startPos) {
                    int lastPos3 = lastPos2 + 1;
                    if (lastPos3 < startPos) {
                        lastPos3 = startPos;
                    }
                    while (lastPos3 <= endPos && lastPos3 < ii.position) {
                        addNewItem(lastPos3, i);
                        lastPos3++;
                        i++;
                    }
                }
                lastPos2 = ii.position;
                i++;
            }
            if (this.mItems.size() > 0) {
                lastPos = ((ItemInfo) this.mItems.get(this.mItems.size() - 1)).position;
            } else {
                lastPos = -1;
            }
            if (lastPos < endPos) {
                int lastPos4 = lastPos + 1;
                if (lastPos4 <= startPos) {
                    lastPos4 = startPos;
                }
                while (lastPos4 <= endPos) {
                    addNewItem(lastPos4, -1);
                    lastPos4++;
                }
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.position = this.mCurItem;
        ss.adapterState = this.mAdapter.saveState();
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (this.mAdapter != null) {
            this.mAdapter.restoreState(ss.adapterState, ss.loader);
            setCurrentItemInternal(ss.position, false, true);
        } else {
            this.mRestoredCurItem = ss.position;
            this.mRestoredAdapterState = ss.adapterState;
            this.mRestoredClassLoader = ss.loader;
        }
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case 0:
            case 1:
                if (orientation != this.mOrientation) {
                    completeScroll();
                    this.mInitialMotion = 0.0f;
                    this.mLastMotionX = 0.0f;
                    this.mLastMotionY = 0.0f;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.clear();
                    }
                    this.mOrientation = orientation;
                    if (this.mOrientation == 0) {
                        scrollTo(this.mCurItem * getWidth(), 0);
                    } else {
                        scrollTo(0, this.mCurItem * getHeight());
                    }
                    requestLayout();
                    return;
                }
                return;
            default:
                throw new IllegalArgumentException("Only HORIZONTAL and VERTICAL are valid orientations.");
        }
    }

    public void addView(View child, int index, LayoutParams params) {
        if (this.mInLayout) {
            addViewInLayout(child, index, params);
            child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
            return;
        }
        super.addView(child, index, params);
    }

    /* access modifiers changed from: 0000 */
    public ItemInfo infoForChild(View child) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo ii = (ItemInfo) this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAdapter != null) {
            populate();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        this.mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), MeasureSpec.EXACTLY);
        this.mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), MeasureSpec.EXACTLY);
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mOrientation == 0) {
            int scrollPos = this.mCurItem * w;
            if (scrollPos != getScrollX()) {
                completeScroll();
                scrollTo(scrollPos, getScrollY());
                return;
            }
            return;
        }
        int scrollPos2 = this.mCurItem * h;
        if (scrollPos2 != getScrollY()) {
            completeScroll();
            scrollTo(getScrollX(), scrollPos2);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int count = getChildCount();
        int size = this.mOrientation == 0 ? r - l : b - t;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                ItemInfo ii = infoForChild(child);
                if (ii != null) {
                    int off = size * ii.position;
                    int childLeft = getPaddingLeft();
                    int childTop = getPaddingTop();
                    if (this.mOrientation == 0) {
                        childLeft += off;
                    } else {
                        childTop += off;
                    }
                    child.layout(childLeft, childTop, child.getMeasuredWidth() + childLeft, child.getMeasuredHeight() + childTop);
                }
            }
        }
    }

    public void computeScroll() {
        int size;
        int value;
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll();
            return;
        }
        int oldX = getScrollX();
        int oldY = getScrollY();
        int x = this.mScroller.getCurrX();
        int y = this.mScroller.getCurrY();
        if (!(oldX == x && oldY == y)) {
            scrollTo(x, y);
        }
        if (this.mOnPageChangeListener != null) {
            if (this.mOrientation == 0) {
                size = getWidth();
                value = x;
            } else {
                size = getHeight();
                value = y;
            }
            int offsetPixels = value % size;
            this.mOnPageChangeListener.onPageScrolled(value / size, ((float) offsetPixels) / ((float) size), offsetPixels);
        }
        invalidate();
    }

    private void completeScroll() {
        boolean needPopulate = this.mScrolling;
        if (needPopulate) {
            setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (!(oldX == x && oldY == y)) {
                scrollTo(x, y);
            }
            setScrollState(0);
        }
        this.mPopulatePending = false;
        this.mScrolling = false;
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo ii = (ItemInfo) this.mItems.get(i);
            if (ii.scrolling) {
                needPopulate = true;
                ii.scrolling = false;
            }
        }
        if (needPopulate) {
            populate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float primaryDiff;
        float secondaryDiff;
        int action = ev.getAction() & 255;
        if (action == 3 || action == 1) {
            this.mIsBeingDragged = false;
            this.mIsUnableToDrag = false;
            this.mActivePointerId = -1;
            return false;
        }
        if (action != 0) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        switch (action) {
            case 0:
                if (this.mOrientation == 0) {
                    float x = ev.getX();
                    this.mInitialMotion = x;
                    this.mLastMotionX = x;
                    this.mLastMotionY = ev.getY();
                } else {
                    this.mLastMotionX = ev.getX();
                    float y = ev.getY();
                    this.mInitialMotion = y;
                    this.mLastMotionY = y;
                }
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                if (this.mScrollState != 2) {
                    completeScroll();
                    this.mIsBeingDragged = false;
                    this.mIsUnableToDrag = false;
                    break;
                } else {
                    this.mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    this.mIsUnableToDrag = false;
                    setScrollState(1);
                    break;
                }
            case 2:
                int activePointerId = this.mActivePointerId;
                if (activePointerId != -1 || VERSION.SDK_INT <= 4) {
                    int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                    float x2 = MotionEventCompat.getX(ev, pointerIndex);
                    float y2 = MotionEventCompat.getY(ev, pointerIndex);
                    float xDiff = Math.abs(x2 - this.mLastMotionX);
                    float yDiff = Math.abs(y2 - this.mLastMotionY);
                    if (this.mOrientation == 0) {
                        primaryDiff = xDiff;
                        secondaryDiff = yDiff;
                    } else {
                        primaryDiff = yDiff;
                        secondaryDiff = xDiff;
                    }
                    if (primaryDiff > ((float) this.mTouchSlop) && primaryDiff > secondaryDiff) {
                        this.mIsBeingDragged = true;
                        setScrollState(1);
                        requestParentDisallowInterceptTouchEvent(true);
                        if (this.mOrientation == 0) {
                            this.mLastMotionX = x2;
                        } else {
                            this.mLastMotionY = y2;
                        }
                        setScrollingCacheEnabled(true);
                        return true;
                    } else if (secondaryDiff > ((float) this.mTouchSlop)) {
                        this.mIsUnableToDrag = true;
                        return false;
                    }
                }
                break;
            case 6:
                onSecondaryPointerUp(ev);
                break;
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int initialVelocity;
        float lastMotion;
        int sizeOverThree;
        int size;
        float scroll;
        float primaryDiff;
        float secondaryDiff;
        if (ev.getAction() == 0 && ev.getEdgeFlags() != 0) {
            return false;
        }
        if (this.mAdapter == null || this.mAdapter.getCount() == 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (ev.getAction() & 255) {
            case 0:
                completeScroll();
                if (this.mOrientation == 0) {
                    float x = ev.getX();
                    this.mInitialMotion = x;
                    this.mLastMotionX = x;
                } else {
                    float y = ev.getY();
                    this.mInitialMotion = y;
                    this.mLastMotionY = y;
                }
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            case 1:
                if (this.mIsBeingDragged) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                    if (this.mOrientation == 0) {
                        initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
                        lastMotion = this.mLastMotionX;
                        sizeOverThree = getWidth() / 3;
                    } else {
                        initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker, this.mActivePointerId);
                        lastMotion = this.mLastMotionY;
                        sizeOverThree = getHeight() / 3;
                    }
                    this.mPopulatePending = true;
                    if (Math.abs(initialVelocity) <= this.mMinimumVelocity && Math.abs(this.mInitialMotion - lastMotion) < ((float) sizeOverThree)) {
                        setCurrentItemInternal(this.mCurItem, true, true);
                    } else if (lastMotion > this.mInitialMotion) {
                        setCurrentItemInternal(this.mCurItem - 1, true, true);
                    } else {
                        setCurrentItemInternal(this.mCurItem + 1, true, true);
                    }
                    this.mActivePointerId = -1;
                    endDrag();
                    break;
                }
                break;
            case 2:
                if (!this.mIsBeingDragged) {
                    int pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                    float x2 = MotionEventCompat.getX(ev, pointerIndex);
                    float y2 = MotionEventCompat.getY(ev, pointerIndex);
                    float xDiff = Math.abs(x2 - this.mLastMotionX);
                    float yDiff = Math.abs(y2 - this.mLastMotionY);
                    if (this.mOrientation == 0) {
                        primaryDiff = xDiff;
                        secondaryDiff = yDiff;
                    } else {
                        primaryDiff = yDiff;
                        secondaryDiff = xDiff;
                    }
                    if (primaryDiff > ((float) this.mTouchSlop) && primaryDiff > secondaryDiff) {
                        this.mIsBeingDragged = true;
                        requestParentDisallowInterceptTouchEvent(true);
                        if (this.mOrientation == 0) {
                            this.mLastMotionX = x2;
                        } else {
                            this.mLastMotionY = y2;
                        }
                        setScrollState(1);
                        setScrollingCacheEnabled(true);
                    }
                }
                if (this.mIsBeingDragged) {
                    int activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                    float x3 = MotionEventCompat.getX(ev, activePointerIndex);
                    float y3 = MotionEventCompat.getY(ev, activePointerIndex);
                    if (this.mOrientation == 0) {
                        size = getWidth();
                        scroll = ((float) getScrollX()) + (this.mLastMotionX - x3);
                        this.mLastMotionX = x3;
                    } else {
                        size = getHeight();
                        scroll = ((float) getScrollY()) + (this.mLastMotionY - y3);
                        this.mLastMotionY = y3;
                    }
                    float lowerBound = (float) Math.max(0, (this.mCurItem - 1) * size);
                    float upperBound = (float) (Math.min(this.mCurItem + 1, this.mAdapter.getCount() - 1) * size);
                    if (scroll < lowerBound) {
                        scroll = lowerBound;
                    } else if (scroll > upperBound) {
                        scroll = upperBound;
                    }
                    if (this.mOrientation == 0) {
                        this.mLastMotionX += scroll - ((float) ((int) scroll));
                        scrollTo((int) scroll, getScrollY());
                    } else {
                        this.mLastMotionY += scroll - ((float) ((int) scroll));
                        scrollTo(getScrollX(), (int) scroll);
                    }
                    if (this.mOnPageChangeListener != null) {
                        int positionOffsetPixels = ((int) scroll) % size;
                        this.mOnPageChangeListener.onPageScrolled(((int) scroll) / size, ((float) positionOffsetPixels) / ((float) size), positionOffsetPixels);
                        break;
                    }
                }
                break;
            case 3:
                if (this.mIsBeingDragged) {
                    setCurrentItemInternal(this.mCurItem, true, true);
                    this.mActivePointerId = -1;
                    endDrag();
                    break;
                }
                break;
            case 5:
                int index = MotionEventCompat.getActionIndex(ev);
                if (this.mOrientation == 0) {
                    this.mLastMotionX = MotionEventCompat.getX(ev, index);
                } else {
                    this.mLastMotionY = MotionEventCompat.getY(ev, index);
                }
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case 6:
                onSecondaryPointerUp(ev);
                int index2 = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (this.mOrientation != 0) {
                    this.mLastMotionY = MotionEventCompat.getY(ev, index2);
                    break;
                } else {
                    this.mLastMotionX = MotionEventCompat.getX(ev, index2);
                    break;
                }
        }
        return true;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (MotionEventCompat.getPointerId(ev, pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            if (this.mOrientation == 0) {
                this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            } else {
                this.mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            }
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled != enabled) {
            this.mScrollingCacheEnabled = enabled;
        }
    }
}

