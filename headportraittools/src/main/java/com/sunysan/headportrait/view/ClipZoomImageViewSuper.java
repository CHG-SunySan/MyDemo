package com.sunysan.headportrait.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.sunysan.headportrait.bean.CustomBitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 缩放、平移、旋转、裁剪 图片的View
 * Created by SunySan on 2016/10/16.
 */
public class ClipZoomImageViewSuper extends ImageView implements
        OnScaleGestureListener, OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private Context context;

    /**
     * 模式 NONE：无 DRAG：拖拽. ZOOM:缩放
     *
     * @author zhangjia
     */
    private enum MODE {
        NONE, DRAG, ZOOM
    }

    private MODE mode = MODE.NONE;//默认模式

    private List<CustomBitmap> _bitmaps;
    private CustomBitmap _curCustomBitmap;//当前操作的图形

    public void addBitmap(CustomBitmap bitmap) {
        _bitmaps.add(bitmap);
    }

    public List<CustomBitmap> getViews() {
        return _bitmaps;
    }


    private PointF mid = new PointF();

    private float oldDist = 1f;
    private float oldRotation = 0;//第二个手指放下时的两点的旋转角度
    private float rotation = 0;//旋转角度差值
    private float newRotation = 0;
//    private float Reset_scale = 1;

    public static float SCALE_MAX = 4.0f;
    private static float SCALE_MID = 2.0f;

    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix savedMatrix = new Matrix();

    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕
     */
    private float initScale = 1.0f;


    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix();

    /**
     * 用于双击放大缩小
     */
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;
    private boolean once = true;

    private int mTouchSlop;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;
    /**
     * 水平方向与View的边
     */
    private int mHorizontalPadding;

    public ClipZoomImageViewSuper(Context context) {
        this(context, null);
        this.context = context;
        _bitmaps = new ArrayList<>();
    }

    public ClipZoomImageViewSuper(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        _bitmaps = new ArrayList<>();
        setScaleType(ScaleType.MATRIX);
        matrix.set(getImageMatrix());
        scaleRun();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }


    private void scaleRun() {
        mGestureDetector = new GestureDetector(context,
                new SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale == true)
                            return true;

                        mid.x = e.getX();
                        mid.y = e.getY();

                        float x = e.getX();
                        float y = e.getY();
                        matrix1.set(mScaleMatrix);
                        if (getScale() < SCALE_MID) {
                            ClipZoomImageViewSuper.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MID, x, y), 16);
                            isAutoScale = true;
                        } else {
                            ClipZoomImageViewSuper.this.postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }
                        matrix.set(matrix1);
                        setImageMatrix(matrix);
                        return true;
                    }
                });
    }


    /**
     * 自动缩放
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;


        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorder();
            matrix.set(mScaleMatrix);
//            setImageMatrix(matrix);
//            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                ClipZoomImageViewSuper.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);//缩放的时候
                checkBorder();
                matrix.set(mScaleMatrix);
                setImageMatrix(matrix);
//                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            matrix.set(mScaleMatrix);
//            setImageMatrix(matrix);
            Log.e("SunySan", "你现在是缩放状态");
//            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {//这里需要注意
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        try {
            //关键操作：双击放大缩小，设置事件给它
            if (mGestureDetector.onTouchEvent(event)) {
                Log.e("SunySan", "双击操作");
                return true;
            }
            mScaleGestureDetector.onTouchEvent(event);

            float x = 0, y = 0;
            // 拿到触摸点的个数
            final int pointerCount = event.getPointerCount();
            // 得到多个触摸点的x与y
            for (int i = 0; i < pointerCount; i++) {
                x += event.getX(i);
                y += event.getY(i);
            }
            x = x / pointerCount;
            y = y / pointerCount;

            /**
             * 每当触摸点发生变化时，重置mLasX , mLastY
             */
            if (pointerCount != lastPointerCount) {
                isCanDrag = false;
                mLastX = x;
                mLastY = y;
            }

            lastPointerCount = pointerCount;
            //单独的event,getAction()是单点触控，加上& MotionEvent.ACTION_MASK 就是多点触控了，详细查略多点触控和单点触控
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = MODE.DRAG;
                    if (_curCustomBitmap == null && _bitmaps.size() > 0) {
                        _curCustomBitmap = _bitmaps.get(_bitmaps.size() - 1);
                    }
                    boolean isChanged = false;//当前操作bitmap是否改变
                    ClipZoomImageViewSuper.this.setDrawingCacheEnabled(true);
                    Bitmap b = ClipZoomImageViewSuper.this.getDrawingCache();
                    ClipZoomImageViewSuper.this.setDrawingCacheEnabled(false);

                    CustomBitmap bitmap = new CustomBitmap(b);
                    float[] values = new float[9];
                    bitmap.matrix.getValues(values);
                    float globalX = values[Matrix.MTRANS_X];
                    float globalY = values[Matrix.MTRANS_Y];
                    float width = values[Matrix.MSCALE_X] * bitmap.getBitmap().getWidth();
                    float height = values[Matrix.MSCALE_Y] * bitmap.getBitmap().getWidth();
                    Log.e("tag", "globalX: " + globalX + " ,globalY: " + globalY + " ,t: " + width + " ,b: " + height);

                    Rect rect = new Rect((int) globalX, (int) globalY, (int) (globalX + width), (int) (globalY + height));
                    Log.e("tag", "l: " + rect.left + " ,r: " + rect.right + " ,t: " + rect.top + " ,b: " + rect.bottom);
                    if (rect.contains((int) event.getX(), (int) event.getY())) {
                        _curCustomBitmap = bitmap;
                        isChanged = true;
                    }
                    //切换操作对象，只要把这个对象添加到栈底就行
                    if (isChanged) {
                        _bitmaps.remove(_curCustomBitmap);
                        _bitmaps.add(_curCustomBitmap);
                    }
                    matrix.set(mScaleMatrix);// 记录ImageView当前的移动位置
                    mScaleMatrix.set(matrix);
                    _curCustomBitmap.startPoint.set(event.getX(), event.getY());
//                    postInvalidate();
//                    setImageMatrix(mScaleMatrix);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE.ZOOM;
                    _curCustomBitmap.oldRotation = rotations(event);
                    _curCustomBitmap.startDis = distance(event);
                    if (_curCustomBitmap.startDis > 10f) {
                        _curCustomBitmap.midPoint = mid(event);
                        matrix.set(mScaleMatrix);// 记录ImageView当前的缩放倍数
                    }
//                    ssssssssssssssssssssssssssssssssss
                    Log.e("SunySan", "现在是 ACTION_POINTER_DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == MODE.DRAG) {
                        float dx = event.getX() - _curCustomBitmap.startPoint.x;// 得到在x轴的移动距离
                        float dy = event.getY() - _curCustomBitmap.startPoint.y;// 得到在y轴的移动距离
                        mScaleMatrix.set(matrix);// 在没有进行移动之前的位置基础上进行移动
                        mScaleMatrix.postTranslate(dx, dy);
                        Log.e("SunySan","位置是：MotionEvent.ACTION_MOVE +++ MODE.DRAG");
                        setImageMatrix(mScaleMatrix);
                    } else if (mode == MODE.ZOOM) {// 缩放与旋转
                        float endDis = distance(event);// 结束距离
                        _curCustomBitmap.rotation = rotations(event) - _curCustomBitmap.oldRotation;
                        if (endDis > 10f) {
                            float scale = endDis / _curCustomBitmap.startDis;// 得到缩放倍数
                            mScaleMatrix.set(matrix);
                            mScaleMatrix.postScale(scale, scale, _curCustomBitmap.midPoint.x, _curCustomBitmap.midPoint.y);
                            mScaleMatrix.postRotate(_curCustomBitmap.rotation, _curCustomBitmap.midPoint.x, _curCustomBitmap.midPoint.y);
                        }
                        Log.e("SunySan","位置是：MotionEvent.ACTION_MOVE +++ MODE.ZOOM");
                        setImageMatrix(mScaleMatrix);
                    }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = MODE.NONE;
//                lastPointerCount = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    lastPointerCount = 0;
                    Log.e("SunySan", "手已经抬起来了");
//                    setImageMatrix(savedMatrix);g
                    break;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
//        invalidate();
        return true;


    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 垂直方向与View的边
     */
    // private int getHVerticalPadding();
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            // 垂直方向的边
            // getHVerticalPadding() = (getHeight() - (getWidth() - 2 *
            // mHorizontalPadding)) / 2;

            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int drawableW = d.getIntrinsicWidth();
            int drawableH = d.getIntrinsicHeight();
            float scale = 1.0f;

            int frameSize = getWidth() - mHorizontalPadding * 2;

            // 大图
            if (drawableW > frameSize && drawableH < frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableH > frameSize && drawableW < frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableW > frameSize && drawableH > frameSize) {
                float scaleW = frameSize * 1.0f / drawableW;
                float scaleH = frameSize * 1.0f / drawableH;
                scale = Math.max(scaleW, scaleH);
            }

            // 太小的图片放大
            if (drawableW < frameSize && drawableH > frameSize) {
                scale = 1.0f * frameSize / drawableW;
            } else if (drawableH < frameSize && drawableW > frameSize) {
                scale = 1.0f * frameSize / drawableH;
            } else if (drawableW < frameSize && drawableH < frameSize) {
                float scaleW = 1.0f * frameSize / drawableW;
                float scaleH = 1.0f * frameSize / drawableH;
                scale = Math.max(scaleW, scaleH);
            }

            initScale = scale;
            SCALE_MID = initScale * 2;
            SCALE_MAX = initScale * 4;
            mScaleMatrix.postTranslate((width - drawableW) / 2,
                    (height - drawableH) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);

            // 图片移动至屏幕中
            matrix.set(mScaleMatrix);
//            setImageMatrix(matrix);
            once = false;
        }
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象
     *
     * @return
     */
    public Bitmap clip() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(bitmap, mHorizontalPadding,
                getHVerticalPadding(), getWidth() - 2 * mHorizontalPadding,
                getWidth() - 2 * mHorizontalPadding);
    }

    /**
     * 剪切图片，返回剪切后的bitmap对象(裁剪圆形)
     *
     * @return
     */
    public Bitmap clipCircle() {
        return toCircleBitmap(clip());
    }


    /**
     * 边界
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围; 这里的.001是因为精度丢失会产生问题，但是误差一般很小，�?��我们直接加了�?��0.01
        if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }

            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }

        if (rect.height() + 0.01 >= height - 2 * getHVerticalPadding()) {
            if (rect.top > getHVerticalPadding()) {
                deltaY = -rect.top + getHVerticalPadding();
            }

            if (rect.bottom < height - getHVerticalPadding()) {
                deltaY = height - getHVerticalPadding() - rect.bottom;
            }
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 是否是拖动
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    private int getHVerticalPadding() {
        return (getHeight() - (getWidth() - 2 * mHorizontalPadding)) / 2;
    }


    // 取旋转角度
    private float rotations(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        /**
         * 反正切函数
         * 计算两个坐标点的正切角度
         */
        double radians = Math.atan2(delta_y, delta_x);
        return (float) (Math.toDegrees(radians));
    }


    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    private Bitmap toCircleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
//        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output;
    }


    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return
     */
    public float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算两点之间的中间点
     *
     * @param event
     * @return
     */
    public PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }


}
