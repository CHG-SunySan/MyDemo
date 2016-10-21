package com.sunysan.headportrait.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * 裁剪的区域
 * @ClassName: ClipImageLayout
 * Created by SunySan on 2016/10/16.
 */
public class ClipImageLayout extends RelativeLayout {
	private ClipZoomImageView mZoomImageView;
	private ClipImageBorderView mClipImageView;
	private int mHorizontalPadding = 0;// 框左右的边距，这里左右边距为0

	public static boolean isCcircle = false;//剪裁是否为圆形，true为圆形，false为矩形

	public ClipImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mZoomImageView = new ClipZoomImageView(context);
		mClipImageView = new ClipImageBorderView(context,isCcircle);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}

	public void setImageDrawable(Drawable drawable) {
		mZoomImageView.setImageDrawable(drawable);
	}

	public void setImageBitmap(Bitmap bitmap) {
		mZoomImageView.setImageBitmap(bitmap);
	}

	/**
	 * 对外公布设置边距的方法，单位为dp
	 * 
	 * @param mHorizontalPadding
	 */
	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;
	}

	/**
	 * 裁切图片
	 * 
	 * @return
	 */
	public Bitmap clip() {
		return mZoomImageView.clip();
	}

	/**
	 * 裁切图片:圆形
	 *
	 * @return
	 */
	public Bitmap clipCircle() {
		return mZoomImageView.clipCircle();
	}
}
