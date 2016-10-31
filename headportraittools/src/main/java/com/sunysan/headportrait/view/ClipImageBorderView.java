package com.sunysan.headportrait.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import test.sunysan.com.headportraittools.R;

/**
 * @ClassName: ClipImageBorderView
 * Created by SunySan on 2016/10/16.
 */

public class ClipImageBorderView extends View {
    private Context context;
    private boolean isRound;
    /**
     * 水平方向与View的边
     */
    private int mHorizontalPadding;
    /**
     * 垂直方向与View的边
     */
    private int mVerticalPadding;
    /**
     * 绘制的矩形的宽度
     */
    private int mWidth;
    /**
     * 边框的颜色，默认为白
     */
    private int mBorderColor = Color.parseColor("#FFFFFF");
    /**
     * 边框的宽
     */
    private int mBorderWidth = 1;

    private int highlightColor;
    private static final int DEFAULT_HIGHLIGHT_COLOR = 0xFF33B5E5;

    private Paint mPaint;

    public ClipImageBorderView(Context context, boolean isRound) {
        this(context, null);
        this.isRound = isRound;
        this.context = context;
    }

    public ClipImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBorderWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
                        .getDisplayMetrics());

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.cropImageStyle, outValue, true);
        TypedArray attributes = context.obtainStyledAttributes(outValue.resourceId, R.styleable.CropImageView);

        highlightColor = attributes.getColor(R.styleable.CropImageView_highlightColor,
                DEFAULT_HIGHLIGHT_COLOR);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRound) {
            // 计算矩形区域的宽
            mWidth = getWidth() - 2 * mHorizontalPadding;
            // 计算距离屏幕垂直边界 的边
            mVerticalPadding = (getHeight() - mWidth) / 2;
            mPaint.setColor(Color.parseColor("#aa000000"));
            mPaint.setStyle(Style.FILL);
            // 绘制左边1
            canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
            // 绘制右边2
            canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
                    getHeight(), mPaint);
            // 绘制上边3
            canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
                    mVerticalPadding, mPaint);
            // 绘制下边4
            canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
                    getWidth() - mHorizontalPadding, getHeight(), mPaint);

            // 绘制外边
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setStyle(Style.STROKE);
            canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
                    - mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);
        } else {
//			int x = getWidth()/2;
//			int y = getHeight()/2;
//			int innerCircle = x; //设置内圆半径
////			int innerCircle = dip2px(context, 83); //设置内圆半径
//			int ringWidth = dip2px(context, 5); //设置圆环宽度
//
//			canvas.drawARGB(Color.parseColor("#aa000000"),0,0,0);
//
//			//绘制内圆
//			mPaint.setColor(Color.parseColor("#aaC90C0F"));
//			mPaint.setStrokeWidth(2);
//			mPaint.setStyle(Style.STROKE);
//			canvas.drawCircle(x,y, innerCircle, mPaint);

//////////////////////////////////////////////////////////////////////////////////
            int x = getWidth() / 2;
            int y = getHeight() / 2;

            Paint outlinePaint = new Paint();//圆环
            Paint outsidePaint = new Paint();//背景

            outsidePaint.setARGB(125, 50, 50, 50);
            outlinePaint.setStyle(Style.STROKE);
            outlinePaint.setAntiAlias(true);


            Rect viewDrawingRect = new Rect();
            Path path = new Path();
            getDrawingRect(viewDrawingRect);

            //已裁剪框drawRect，算出圆的半径
            float radius = x;
            //添加一个圆形
            path.addCircle(x, y, radius, Path.Direction.CW);
            outlinePaint.setColor(Color.parseColor("#aa000000"));

            /**
             * 这里有个坑，在使用canvas.clipPath的时候，高版本默认是开启硬件加速的
             * 但是在低版本手机如果开启硬件加速的话，就会使其高亮部分加载不出来
             * 所以在这里调用setLayerType方法来关闭硬件加速
             */
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            //裁剪画布，path之外的区域，以outsidePaint填充
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            canvas.drawRect(viewDrawingRect, outsidePaint);

//			canvas.restore();
//			//绘制圆上高亮线，这里outlinePaint定义的Paint.Style.STROKE：表示只绘制几何图形的轮廓。
//			canvas.drawPath( path,outlinePaint);

            //当modifyMode为grow时，绘制handles,也就是那四个小圆
//			if(handleMode == HighlightView.HandleMode.Always
//					|| (view.handleMode == HighlightView.HandleMode.Changing
//					&& view.modifyMode == HighlightView.ModifyMode.Grow)) {
//				drawHandles( canvas);
//			}


            //////////////////////////////////这是一条美丽的分隔线////////////////////////////////////////////////////////////

//			//绘制圆环
//			this.mPaint.setARGB(255, 212 ,225, 233);
//			this.mPaint.setStrokeWidth(ringWidth);
//			canvas.drawCircle(center,center, innerCircle+1+ringWidth/2, this.mPaint);
//
//			//绘制外圆
//			this.mPaint.setARGB(155, 167, 190, 206);
//			this.mPaint.setStrokeWidth(2);
//			canvas.drawCircle(center,center, innerCircle+ringWidth, this.mPaint);


        }

    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
