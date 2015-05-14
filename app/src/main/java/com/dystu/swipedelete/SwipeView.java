package com.dystu.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2015/5/14.
 * <p/>
 * 继承自FrameLayout，它为我们实现了onMeasure()方法
 */
public class SwipeView extends FrameLayout {
    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private ViewDragHelper viewDragHelper;

    private View contentView, deleteView;

    private int contentViewWidth;

    private int deleteViewHeight;

    private int deleteViewWidth;

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 从xml文件中加载完布局，只知道自己有几个子View,并没有进行测量
     * 一般可以初始化子View的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    /**
     * 测量完子View后调用，在这里可以直接获取子View高度
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh getMeasuredWidth():只要在onMeasure方法执行完，调用该方法都能获取到view宽
     *             getWidth():必须在onLayout方法调用之后才可以获取宽，两个方法得到的值是一样的。
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentViewWidth = contentView.getMeasuredWidth();
        deleteViewHeight = deleteView.getMeasuredHeight();
        deleteViewWidth = deleteView.getMeasuredWidth();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        contentView.layout(0, 0, contentViewWidth, deleteViewHeight);
        deleteView.layout(contentViewWidth, 0, contentViewWidth + deleteViewWidth, deleteViewHeight);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private int lastX;

    private int lastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:

                break;

            case MotionEvent.ACTION_MOVE:

                int deltaX = x - lastX;
                int deltxY = y - lastY;
                if (Math.abs(deltaX) > Math.abs(deltxY)){
                    //认为想滑动删除，拦截该事件，不让ListView去处理
                    requestDisallowInterceptTouchEvent(true);//子View不想让他的父View拦截
                }
                break;

            case  MotionEvent.ACTION_UP:

                break;


        }
        lastX = x;
        lastY = y;
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         *
         * Called when the user's input indicates that they want to capture the given child view with the pointer indicated by pointerId.
         * The callback should return true if the user is permitted to drag the given view with the indicated pointer.
         * ViewDragHelper may call this method multiple times for the same view even if the view is already captured; this indicates that a new pointer is trying to take control of the view.
         * If this method returns true, a call to onViewCaptured(android.view.View, int) will follow if the capture is successful.
         * @param child  Child the user is attempting to capture
         * @param pointerId  ID of the pointer attempting the capture
         * @return true if capture should be allowed, false otherwise
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return contentView == child || deleteView == child;
        }

        /**
         * Called when a child view is captured for dragging or settling.
         * The ID of the pointer currently dragging the captured view is supplied.
         * If activePointerId is identified as INVALID_POINTER the capture is programmatic instead of pointer-initiated.
         * @param capturedChild Child view that was captured
         * @param activePointerId Pointer id tracking the child capture
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         *Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * This method should return 0 for views that cannot move horizontally.
         * @param child  Child view to check
         * @return range of horizontal motion in pixels
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteViewWidth;
        }


        /**
         * Restrict the motion of the dragged child view along the horizontal axis.
         * The default implementation does not allow horizontal motion;
         * the extending class must override this method and provide the desired clamping.
         * @param child Child view being dragged
         * @param left Attempted motion along the X axis
         * @param dx  Proposed change in position for left
         * @return The new clamped position for left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0) left = 0;//右边不能滑动，左边可以滑动
                if (left < -deleteViewWidth) left = -deleteViewWidth;//向左滑动有限制
            } else {
                if (left < (contentViewWidth - deleteViewWidth))
                    left = contentViewWidth - deleteViewWidth;
                if (left > contentViewWidth) left = contentViewWidth;
            }

            return left;
        }

        /**
         * Called when the captured view's position changes as the result of a drag or settle.
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.e("SwipeView", "left:" + left + " top:" + top + "  dx:" + dx + "  dy:" + dy);
            Log.e("SwipeView", "deleteView.getLeft()：" + deleteView.getLeft() + "   deleteView.getRight():" + deleteView.getRight());
            if (changedView == contentView) {
                deleteView.layout(deleteView.getLeft() + dx, 0, deleteView.getRight() + dx, deleteView.getBottom());
            } else {
                contentView.layout(contentView.getLeft() + dx, 0, contentView.getRight() + dx, contentView.getBottom());
            }
            //设置状态
            if (contentView.getLeft() == 0 && mSwipeStatus != SwipeStatus.Close){
                mSwipeStatus = SwipeStatus.Close;
                if (onSwipeStatusChangeListener != null){
                    onSwipeStatusChangeListener.onClose(SwipeView.this);
                }
            }else if (contentView.getLeft() == -deleteViewWidth && mSwipeStatus != SwipeStatus.Open){
                mSwipeStatus = SwipeStatus.Open;
                onSwipeStatusChangeListener.onOpen(SwipeView.this);
            }else if (mSwipeStatus != SwipeStatus.Swiping){
                mSwipeStatus = SwipeStatus.Swiping;
                onSwipeStatusChangeListener.onSwiping(SwipeView.this);
            }


        }

        /**
         *Called when the child view is no longer being actively dragged.
         * The fling velocity is also supplied, if relevant. The velocity values may be clamped to system minimums or maximums.
         * Calling code may decide to fling or otherwise release the view to let it settle into place.
         * It should do so using settleCapturedViewAt(int, int) or flingCapturedView(int, int, int, int).
         * If the Callback invokes one of these methods, the ViewDragHelper will enter STATE_SETTLING and the view capture will not fully end until it comes to a complete stop.
         * If neither of these methods is invoked before onViewReleased returns, the view will stop in place and the ViewDragHelper will return to STATE_IDLE.
         * @param releasedChild The captured child view now being released
         * @param xvel X velocity of the pointer as it left the screen in pixels per second.
         * @param yvel Y velocity of the pointer as it left the screen in pixels per second.
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < - deleteViewWidth / 2){
                //open
                //由于其内部封装了Scroller
                open();
            }else{
                //close
                close();
            }
        }
    };

    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView,0,0);
        ViewCompat.postInvalidateOnAnimation(this);//刷新
    }

    public void fastClose(){
        contentView.layout(0,0,contentViewWidth,deleteViewHeight);
        deleteView.layout(contentViewWidth,0,contentViewWidth+deleteViewWidth,deleteViewHeight);
        //手动更新  不会调用onViewPositionChanged
        mSwipeStatus = SwipeStatus.Close;
        if (onSwipeStatusChangeListener != null){
            onSwipeStatusChangeListener.onClose(SwipeView.this);
        }
    }


    public void open() {
        viewDragHelper.smoothSlideViewTo(contentView,-deleteViewWidth,0);
        ViewCompat.postInvalidateOnAnimation(this);//刷新
    }


    /**
     * viewDragHelper封装了Sroller，获取和移动由Sroller完成，我们只需要负责刷新即可
     */
    @Override
    public void computeScroll() {
        //continueSettling返回true表明动画没有结束。
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SwipeView.this);//刷新
        }
    }

    private SwipeStatus mSwipeStatus = SwipeStatus.Close;

    public SwipeStatus getSwipeStatus() {
        return mSwipeStatus;
    }

    public void setSwipeStatus(SwipeStatus mSwipeStatus) {
        this.mSwipeStatus = mSwipeStatus;
    }

    enum SwipeStatus{
        Open,Close,Swiping
    }

    private OnSwipeStatusChangeListener onSwipeStatusChangeListener;

    public OnSwipeStatusChangeListener getOnSwipeStatusChangeListener() {
        return onSwipeStatusChangeListener;
    }

    public void setOnSwipeStatusChangeListener(OnSwipeStatusChangeListener onSwipeStatusChangeListener) {
        this.onSwipeStatusChangeListener = onSwipeStatusChangeListener;
    }

    /**
     * 将滑动状态暴漏给外界
     */
    public interface OnSwipeStatusChangeListener{
        void onOpen(SwipeView openedSwipeView);
        void onClose(SwipeView closedSwipeView);
        void onSwiping(SwipeView swipingSwipeView);
    }


}
