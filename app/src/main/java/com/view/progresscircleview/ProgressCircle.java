package com.view.progresscircleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class ProgressCircle extends View {
	private float mRingBias = 0.15f;
	private float mSectionRatio = 5.0f;
	private RectF mSectionRect = new RectF();
	protected float mSectionHeight;

	protected float mRadius;

	protected int mMaxProgress;
	protected int mProgress;

	protected float mCenterX;
	protected float mCenterY;

	private Paint mPaint;
	private int mColor1;
	private int mColor2;
	private int mInactiveColor;
	private Paint mArcPaint = new Paint();
	private XChartCalc mXChartCalc = new XChartCalc();
	private RectF mArcRectF;

	{
		mMaxProgress = 100;
		mProgress = 0;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);

		mColor1 = Color.parseColor("#ffffff");
		mColor2 = Color.parseColor("#ffffff");
		mInactiveColor = Color.parseColor("#44ffffff");

		mPaint.setColor(mColor1); // Set default
	}

	public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initAttributes(context, attrs);
	}

	public ProgressCircle(Context context, AttributeSet attrs) {
		super(context, attrs);

		initAttributes(context, attrs);
	}

	public ProgressCircle(Context context) {
		super(context);
	}

	private void initAttributes(Context context, AttributeSet attrs) {
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekCircle, 0, 0);
		try {
			// Read and clamp max
			int max = attributes.getInteger(R.styleable.SeekCircle_seekcircle_max, 20);
			mMaxProgress = Math.max(max, 1);

			// Read and clamp progress
			int progress = attributes.getInteger(R.styleable.SeekCircle_seekcircle_progress, 0);
			mProgress = Math.max(Math.min(progress, mMaxProgress), 0);
			// mInactiveColor=context.getResources().getColor(R.color.progress_inactive_color);
		} finally {
			attributes.recycle();
		}
		
		mArcPaint.setAntiAlias(true);
	}

	private void updateDimensions(int width, int height) {
		// Update center position
		mCenterX = width / 2.0f;
		mCenterY = height / 2.0f;

		// Find shortest dimension
		int diameter = Math.min(width, height);

		float outerRadius = diameter / 2;
		float sectionHeight = (float) (1.5 * outerRadius * mRingBias);
		float sectionWidth = (float) (3 * sectionHeight / mSectionRatio);

		mRadius = outerRadius - sectionHeight / 2;
		mSectionRect.set(-sectionWidth / 2, -sectionHeight / 2, sectionWidth / 2, sectionHeight / 2);
		mSectionHeight = sectionHeight;
		
		float arcLeft = mCenterX - mRadius;
		float arcTop = mCenterY - mRadius;
		float arcRight = mCenterX + mRadius;
		float arcBottom = mCenterY + mRadius;
		mArcRectF = new RectF(arcLeft, arcTop, arcRight, arcBottom);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		if (width > height) {
			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		} else {
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		}

		updateDimensions(getWidth(), getHeight());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		updateDimensions(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mArcRectF == null) {
			return;
		}
		
		float percentage = 0.0f;
		float currPer = -90.0f;

		for (int i = 0; i < mMaxProgress / 2; ++i) {
			percentage = (360 - mMaxProgress) / mMaxProgress;
			if (i < mProgress) {
				float bias = (float) i / (float) (mMaxProgress - 1);
				int color = interpolateColor(mColor1, mColor2, bias);
				mPaint.setColor(color);
			} else {
				canvas.scale(1.0f, 1.0f);
				mPaint.setColor(mInactiveColor);
			}
			canvas.drawArc(mArcRectF, currPer, percentage, true, mPaint);
			mXChartCalc.CalcArcEndPointXY(mCenterX, mCenterY, mRadius - mRadius / 2 / 2, currPer + percentage / 2);
			currPer = currPer + percentage + 1;
		}
		for (int i = mMaxProgress / 2; i < mMaxProgress; ++i) {
			percentage = (360 - mMaxProgress) / mMaxProgress;

			if (i < mProgress) {
				float bias = (float) i / (float) (mMaxProgress - 1);
				int color = interpolateColor(mColor2, mColor1, bias);
				mPaint.setColor(color);
			} else {
				canvas.scale(1.0f, 1.0f);
				mPaint.setColor(mInactiveColor);
			}
			canvas.drawArc(mArcRectF, currPer, percentage, true, mPaint);
			mXChartCalc.CalcArcEndPointXY(mCenterX, mCenterY, mRadius - mRadius / 2 / 2, currPer + percentage / 2);
			currPer = currPer + percentage + 1;
		}
		canvas.drawCircle(mCenterX, mCenterY, (int) (mRadius / 2), mArcPaint);

		super.onDraw(canvas);
	}

	// Force to set transparent
	@Override
	public void setBackgroundColor(int color) {
		mArcPaint.setColor(color);
		super.setBackgroundColor(Color.TRANSPARENT);
	}

	private float interpolate(float a, float b, float bias) {
		return (a + ((b - a) * bias));
	}

	private int interpolateColor(int colorA, int colorB, float bias) {
		float[] hsvColorA = new float[3];
		Color.colorToHSV(colorA, hsvColorA);

		float[] hsvColorB = new float[3];
		Color.colorToHSV(colorB, hsvColorB);

		hsvColorB[0] = interpolate(hsvColorA[0], hsvColorB[0], bias);
		hsvColorB[1] = interpolate(hsvColorA[1], hsvColorB[1], bias);
		hsvColorB[2] = interpolate(hsvColorA[2], hsvColorB[2], bias);

		// NOTE For some reason the method HSVToColor fail in edit mode. Just
		// use the start color for now
		if (isInEditMode())
			return colorA;

		return Color.HSVToColor(hsvColorB);
	}

	/**
	 * Get max progress
	 * 
	 * @return Max progress
	 */
	public float getMax() {
		return mMaxProgress;
	}

	/**
	 * Set max progress
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		int newMax = Math.max(max, 1);
		if (newMax != mMaxProgress)
			mMaxProgress = newMax;

		updateProgress(mProgress);
		invalidate();
	}

	/**
	 * Get Progress
	 * 
	 * @return progress
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Set progress
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		updateProgress(progress);
	}

	/**
	 * Update progress internally. Clamp it to a valid range and invalidate the
	 * view if necessary
	 * 
	 * @param progress
	 * @return true if progress was changed and the view needs an update
	 */
	protected boolean updateProgress(int progress) {
		// Clamp progress
		progress = Math.max(0, Math.min(mMaxProgress, progress));

		if (progress != mProgress) {
			mProgress = progress;
			invalidate();

			return true;
		}

		return false;
	}
}