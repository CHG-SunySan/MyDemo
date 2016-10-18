package com.sunysan.headportrait.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import test.sunysan.com.headportraittools.R;


/**
 * 圆角图片
 * shu
 * 2016/9/8.
 */
public class FilletImageView extends ImageView {

    private Paint mPaint;
    private Paint mPaint2;

    private int leftUp = 0;//左上
    private int rightUp = 0;//右上
    private int leftDown = 0;//左下
    private int rightDown = 0;//右下
    public FilletImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defStyle(context,attrs,defStyle);
        init();
    }

    public FilletImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public FilletImageView(Context context) {
        super(context);
        init();
    }
    private void defStyle(Context context,AttributeSet attrs,int defStyle){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FilletImageView, defStyle, 0);
        leftUp = a.getDimensionPixelSize(R.styleable.FilletImageView_round_leftup, 0);
        rightUp = a.getDimensionPixelSize(R.styleable.FilletImageView_round_rightup, 0);
        leftDown = a.getDimensionPixelSize(R.styleable.FilletImageView_round_leftdown, 0);
        rightDown = a.getDimensionPixelSize(R.styleable.FilletImageView_round_rightdown, 0);
    }
    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        //16种状态
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mPaint2 = new Paint();
        mPaint2.setXfermode(null);
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (getWidth()>0&&getHeight()>0) {
            Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas2 = new Canvas(bitmap);
            super.onDraw(canvas2);
            drawLeftUp(canvas2);
            drawRightUp(canvas2);
            drawLeftDown(canvas2);
            drawRightDown(canvas2);
            canvas.drawBitmap(bitmap, 0, 0, mPaint2);
            bitmap.recycle();
        }
    }

    private void drawLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, leftUp);
        path.lineTo(0, 0);
        path.lineTo(leftUp, 0);
        //arcTo的第二个参数是以多少度为开始点，第三个参数-90度表示逆时针画弧，正数表示顺时针
        path.arcTo(new RectF(0, 0, leftUp * 2, leftUp * 2), -90, -90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawLeftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - leftDown);
        path.lineTo(0, getHeight());
        path.lineTo(leftDown, getHeight());
        path.arcTo(new RectF(0, getHeight() - leftDown * 2, 0 + leftDown * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - rightDown, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - rightDown);
        path.arcTo(new RectF(getWidth() - rightDown * 2, getHeight() - rightDown * 2, getWidth(), getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), rightUp);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - rightUp, 0);
        path.arcTo(new RectF(getWidth() - rightUp * 2, 0, getWidth(), 0 + rightUp * 2), -90, 90);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    /**
     * 设置圆角
     * @param leftUp
     * @param rightUp
     * @param leftDown
     * @param rightDown
     */
    public void setRounded(int leftUp,int rightUp,int leftDown,int rightDown) {
        this.leftUp = leftUp;
        this.rightUp = rightUp;
        this.leftDown = leftDown;
        this.rightDown = rightDown;
    }
}