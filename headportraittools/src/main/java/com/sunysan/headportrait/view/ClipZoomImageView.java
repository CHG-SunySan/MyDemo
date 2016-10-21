package com.sunysan.headportrait.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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

/**
 * 缩放、平移、旋转、裁剪 图片的View
 * Created by SunySan on 2016/10/16.
 */
public class ClipZoomImageView extends ImageView implements
        OnScaleGestureListener, OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private Context context;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF mid = new PointF();

    private float oldDist = 1f;
    private float oldRotation = 0;//第二个手指放下时的两点的旋转角度
    private float rotation = 0;//旋转角度差值
    private float newRotation = 0;
    private float Reset_scale = 1;

    public static float SCALE_MAX = 4.0f;
    private static float SCALE_MID = 2.0f;

    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
//    private Matrix savedMatrix = new Matrix();

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

    public ClipZoomImageView(Context context) {
        this(context, null);
        this.context = context;
    }

    public ClipZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
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
                            ClipZoomImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MID, x, y), 16);
                            isAutoScale = true;
                        } else {
                            ClipZoomImageView.this.postDelayed(
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
            setImageMatrix(matrix);
//            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                ClipZoomImageView.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
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
            setImageMatrix(matrix);
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
                    mode = DRAG;
//                    savedMatrix.set(matrix);
                    mScaleMatrix.set(matrix);
                    Log.e("SunySan", "现在是 ACTION_DOWN");
//                setImageMatrix(savedMatrix);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    //第二个手指刚放下时，计算两个手指之间的距离
                    oldDist = spacing(event);
                    /**
                     * 第二个手指刚放下时
                     * 计算两个手指见的旋转角度
                     */
                    oldRotation = rotations(event);
                    mScaleMatrix.set(matrix);
                    /**
                     * 第二个手指刚放下时
                     * 计算两个手指见的中间点坐标，并存在mid中
                     */
                    midPoint(mid, event);
                    setImageMatrix(mScaleMatrix);
                    Log.e("SunySan", "现在是 ACTION_POINTER_DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == ZOOM) {
                        Log.e("SunySan", "现在是 ACTION_MOVE并且是在mode == ZOOM 中");
                        matrix1.set(mScaleMatrix);
                        /**
                         * 两个手指开始移动
                         * 计算移动后旋转角度
                         */
                        newRotation = rotations(event);
                        /**
                         * 两个角度之差
                         * 即是图片的旋转角度
                         */
                        rotation = newRotation - oldRotation;
                        /**
                         * 计算移动后两点间的中间点
                         */
                        float newDist = spacing(event);
                        /**
                         * 两个中间点的商即时放大倍数
                         */
//                        float scale = 1.0f;
                        float scale = newDist / oldDist;

                        /**
                         * 放大倍数的倒数即是还原图片原来大小的倍数
                         */
                        Reset_scale = oldDist / newDist;
//                        matrix1.postScale(scale, scale, mid.x, mid.y);// 缩放
                        matrix1.postRotate(rotation, mid.x, mid.y);// 旋转
                        matrix1.postScale(scale, scale, mid.x,
                                mid.y);

                        matrix.set(matrix1);
                        /**
                         * 调用该方法即可重新图片
                         */
                        setImageMatrix(matrix);
                    } else {
                        Log.e("SunySan", "现在是 ACTION_MOVE并且是在mode不等于ZOOM中");
                        float dx = x - mLastX;
                        float dy = y - mLastY;

                        if (!isCanDrag) {
                            isCanDrag = isCanDrag(dx, dy);
                        }
                        if (isCanDrag) {
                            if (getDrawable() != null) {

                                RectF rectF = getMatrixRectF();
                                // 如果宽度小于屏幕宽度，则禁止左右移动
                                if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
                                    dx = 0;
                                }

                                // 如果高度小雨屏幕高度，则禁止上下移动
                                if (rectF.height() <= getHeight() - getHVerticalPadding()
                                        * 2) {
                                    dy = 0;
                                }
                                //aiaiaiaiai
//                                matrix1.postTranslate(dx, dy);
//                                checkBorder();
//                                setImageMatrix(matrix1);
                                mScaleMatrix.postTranslate(dx, dy);
                                checkBorder();
                                matrix.set(mScaleMatrix);
                                setImageMatrix(matrix);
                            }
                        }
                        mLastX = x;
                        mLastY = y;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (mode == ZOOM) {
                        /**
                         * 双手放开，停止图片的旋转和缩放
                         * Reset_scale还原图片的缩放比例
                         */
                        matrix1.postScale(Reset_scale, Reset_scale, mid.x, mid.y);
                        /**
                         * 双手放开，停止缩放、旋转图片，此时根据已旋转的角度
                         * 计算还原图片的角度，最终的效果是把图片竖直或横平方正。
                         */
                        setRotate();
                        matrix.set(matrix1);
                        /**
                         * 将图片放在屏幕中间位置
                         */
//                    center(true, true);
//                    matrix1.reset();
                        setImageMatrix(matrix);
                    } else if (mode == DRAG)
                        mode = NONE;
//                lastPointerCount = 0;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    lastPointerCount = 0;
                    break;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
            setImageMatrix(matrix);
//            setImageMatrix(mScaleMatrix);
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
     * @return
     */
    public Bitmap clipCircle() {
        return getCircleBitmap();
    }


    /**
     * @param
     * @return
     */
    private Bitmap getCircleBitmap() {
        int targetWidth = 125;
        int targetHeight = 125;
        Bitmap targetBitmap = Bitmap.createBitmap( targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas( targetBitmap);
        Path path = new Path();
        path.addCircle( ((float)targetWidth - 1) / 2, ((float)targetHeight - 1) / 2,
                (Math.min( ((float)targetWidth), ((float)targetHeight)) / 2), Path.Direction.CCW);

        canvas.clipPath( path);
        Bitmap sourceBitmap = clip();
        canvas.drawBitmap( sourceBitmap, new Rect( 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), new Rect( 0, 0,
                targetWidth, targetHeight), null);
        return targetBitmap;
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


    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
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

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 手指松开，确定旋转的角度
     */
    private void setRotate() {
        if (rotation < -315) {
            matrix1.postRotate(-360 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -270) {
            matrix1.postRotate(-270 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -225) {
            matrix1.postRotate(-270 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -180) {
            matrix1.postRotate(-180 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -135) {
            matrix1.postRotate(-180 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -90) {
            matrix1.postRotate(-90 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < -45) {
            matrix1.postRotate(-90 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 0) {
            matrix1.postRotate(0 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 45) {
            matrix1.postRotate(0 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 90) {
            matrix1.postRotate(90 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 135) {
            matrix1.postRotate(90 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 180) {
            matrix1.postRotate(180 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 225) {
            matrix1.postRotate(180 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 270) {
            matrix1.postRotate(270 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 315) {
            matrix1.postRotate(270 - rotation, mid.x, mid.y);// 旋转
        } else if (rotation < 360) {
            matrix1.postRotate(360 - rotation, mid.x, mid.y);// 旋转
        }
    }


}
