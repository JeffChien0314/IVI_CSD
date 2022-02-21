package com.fxc.ev.launcher.utils.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {
    private static final String TAG = "DragGridView";

    private static final int scrollSpeed = 20;
    /**
     * 长按视为拖动图标
     */
    private long dragRespondTime = 1000;

    /**
     * 手指按下的点坐标
     */
    private int mDownX, mDownY;

    /**
     * 手指移动的距离
     */
    private int moveX, moveY;

    /**
     * 手指按下坐标到该item的边缘偏移值
     */
    private int mPoint2ItemTop, mPoint2ItemLeft;

    /**
     * 开始自动滚动的高度
     */
    private int mStartUpScrollHeight, mStartDownScrollHeight;

    /**
     * 状态栏高度
     */
    private int mStatusHeight = 0;

    /**
     * 手指按下坐标到屏幕边框的偏移值
     */
    private int mOffsetTop, mOffsetLeft;

    /**
     * 手指按下的position
     */
    private int mDragPosition = 0;

    /**
     * 手指按下的view
     */
    private View mStartDragItemView;

    /**
     * 镜像view
     */
    private Bitmap mDragBitmap;

    /**
     * 镜像imageview组件
     */
    private ImageView mDragImageView;

    WindowManager.LayoutParams winLayoutParams;

    /**
     * 是否支持拖动界面
     */
    private boolean isDrag = false;

    OnItemChangerListener listener;

    /**
     * 震动
     */
    private Vibrator vibrator;

    private WindowManager windowManager;

    private Handler handler = new Handler();

    private Runnable dragRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true;
            // 隐藏当前点击的item
            mStartDragItemView.setVisibility(View.INVISIBLE);
            // 震动
            vibrator.vibrate(50);
            // 生成镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
        }

    };

    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            int speed = 0;
            if (moveY < mStartDownScrollHeight) {
                speed = -scrollSpeed;
                handler.postDelayed(scrollRunnable, 25);
            } else if (moveY > mStartUpScrollHeight) {
                speed = scrollSpeed;
                handler.postDelayed(scrollRunnable, 25);
            } else {
                speed = 0;
                handler.removeCallbacks(scrollRunnable);
            }

            swapItem(moveX, moveY);
            smoothScrollBy(speed, 10);
        }
    };

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                Log.i(TAG, "手指按下ev.getX---------->" + mDownX + "  ev.getY-------->"
                        + mDownY);

                mDragPosition = pointToPosition(mDownX, mDownY);

                if (mDragPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                Log.i(TAG, "手指按下mDragPosition---------->" + mDragPosition);
                mStartDragItemView = getChildAt(mDragPosition
                        - getFirstVisiblePosition());

                // 1秒长按认为是拖动事件
                handler.postDelayed(dragRunnable, dragRespondTime);

                // item偏移值
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
                Log.i(TAG, "手指按下mPoint2ItemTop---------->" + mPoint2ItemTop
                        + "  mPoint2ItemLeft-------->" + mPoint2ItemLeft);

                //
                mOffsetLeft = (int) ev.getRawX() - mDownX;
                mOffsetTop = (int) ev.getRawY() - mDownY;

                // 到达1/4屏幕高度时，开始向下滚动，使所有item显示，并交换
                mStartDownScrollHeight = getHeight() / 4;
                // 到达3/4屏幕高度时，开始向上滚动，使所有item显示，并交换
                mStartUpScrollHeight = getHeight() * 3 / 4;

                // 绘图缓存
                mStartDragItemView.setDrawingCacheEnabled(true);
                // 创建镜像
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView
                        .getDrawingCache());
                // 释放缓存
                mStartDragItemView.destroyDrawingCache();
            }
            break;

            case MotionEvent.ACTION_UP: {
                handler.removeCallbacks(dragRunnable);
                handler.removeCallbacks(scrollRunnable);
            }

            break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        if (isDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                }
                break;

                case MotionEvent.ACTION_UP: {
                    isDrag = false;
                    onStopDrag();
                }
                break;

                case MotionEvent.ACTION_MOVE: {
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    updateDragImage(moveX, moveY);

                }
                break;
                default:
                    break;
            }
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 拖动时添加镜像
     *
     * @param mDragBitmap
     * @param mDownX
     * @param mDownY
     */
    private void createDragImage(Bitmap mDragBitmap, int mDownX, int mDownY) {
        // TODO Auto-generated method stub
        winLayoutParams = new WindowManager.LayoutParams();
        // 半透明
        winLayoutParams.format = PixelFormat.TRANSLUCENT;
        winLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        Log.i(TAG, "createDragImage ev.getX---------->" + mDownX
                + "  ev.getY-------->" + mDownY);
        winLayoutParams.x = mDownX - mPoint2ItemLeft + mOffsetLeft;
        winLayoutParams.y = mDownY - mPoint2ItemTop + mOffsetTop
                - mStatusHeight;
        winLayoutParams.alpha = 0.55f;
        winLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        winLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        winLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(mDragBitmap);
        // 添加到wm管理中
        windowManager.addView(mDragImageView, winLayoutParams);

    }

    /**
     * 停止拖动
     */
    private void onStopDrag() {
        // 显示隐藏的初始item
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        // 删除当前镜像
        removeDragImage();
    }

    /**
     * 删除镜像
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            windowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 拖动item界面更新
     */
    private void updateDragImage(int moveX, int moveY) {
        winLayoutParams.x = moveX - mPoint2ItemLeft + mOffsetLeft;
        winLayoutParams.y = moveY - mPoint2ItemTop + mOffsetTop - mStatusHeight;
        windowManager.updateViewLayout(mDragImageView, winLayoutParams);

        // 交换当前位置
        swapItem(moveX, moveY);
        handler.post(scrollRunnable);
    }

    /**
     * 拖动到相应位置更换数据
     *
     * @param moveX2
     * @param moveY2
     */
    private void swapItem(int moveX2, int moveY2) {
        // TODO Auto-generated method stub
        int tempPositon = pointToPosition(moveX2, moveY2);
        if (mDragPosition != tempPositon
                && tempPositon != AdapterView.INVALID_POSITION) {

            // 回调，更新数据
            if (listener != null) {
                listener.onChange(mDragPosition, tempPositon);
            }

            getChildAt(mDragPosition - getFirstVisiblePosition())
                    .setVisibility(View.VISIBLE);
            getChildAt(tempPositon - getFirstVisiblePosition()).setVisibility(
                    View.INVISIBLE);

            mDragPosition = tempPositon;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(rect);
        statusHeight = rect.top;
        if (statusHeight == 0) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return statusHeight;
    }

    /**
     * 设置item相互交换接口
     */
    public void setOnItemChangeListener(OnItemChangerListener listener) {
        this.listener = listener;
    }

    /**
     * 数据交换接口
     *
     * @author Administrator
     *
     */
    public interface OnItemChangerListener {
        public void onChange(int from, int to);
    }


}
