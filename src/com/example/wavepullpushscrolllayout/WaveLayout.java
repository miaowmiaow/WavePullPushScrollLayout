package com.example.wavepullpushscrolllayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/*
 * 参考https://github.com/john990/WaveView.git
 */

public class WaveLayout extends RelativeLayout {

	public final int DEFAULT_ABOVE_WAVE_ALPHA = 255;
	public final int DEFAULT_BLOW_WAVE_ALPHA = 255;

	private final float X_SPACE = 10;
	private final double PI2 = 2 * Math.PI;

	private Path mAboveWavePath = new Path();
	private Path mBlowWavePath = new Path();

	private Paint mAboveWavePaint = new Paint();
	private Paint mBlowWavePaint = new Paint();

	private int mAboveWaveColor = 0xFFFFFF;
	private int mBlowWaveColor = 0xE0E0E0;

	private float mAboveWaveMultiple = 1.5f;
	private float mBlowWaveMultiple = 1.725f;
	private float mAboveWaveLength;
	private float mBlowWaveLength;
	private int mWaveHeight = 32;
	private float mMaxRight;
	private float mWaveHz = 0.16f;

	private float mAboveOffset = 0.0f;
	private float mBlowOffset = 3.75f;

	private RefreshProgressRunnable mRefreshProgressRunnable;

	private int left, right, bottom;
	// ω
	private double oAboveMega, oBlowMega;

	public WaveLayout(Context context) {
		this(context, null);
	}

	public WaveLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(Color.TRANSPARENT);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
		setLayoutParams(params);
		mAboveWavePaint.setColor(mAboveWaveColor);
		mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
		mAboveWavePaint.setStyle(Paint.Style.FILL);
		mAboveWavePaint.setAntiAlias(true);

		mBlowWavePaint.setColor(mBlowWaveColor);
		mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
		mBlowWavePaint.setStyle(Paint.Style.FILL);
		mBlowWavePaint.setAntiAlias(true);
	}

	/**
	 * calculate wave track
	 */
	private void calculatePath() {
		mAboveWavePath.reset();
		mBlowWavePath.reset();

		getWaveOffset();

		float y;
		mAboveWavePath.moveTo(left, bottom);
		for (float x = 0; x <= mMaxRight; x += X_SPACE) {
			y = (float) (mWaveHeight * Math.sin(oAboveMega * x + mAboveOffset) + mWaveHeight);
			mAboveWavePath.lineTo(x, y);
		}
		mAboveWavePath.lineTo(right, bottom);

		mBlowWavePath.moveTo(left, mWaveHeight * 2);
		for (float x = 0; x <= mMaxRight; x += X_SPACE) {
			y = (float) (mWaveHeight * Math.sin(oBlowMega * x + mBlowOffset) + mWaveHeight);
			mBlowWavePath.lineTo(x, y);
		}
		mBlowWavePath.lineTo(right, mWaveHeight * 2);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			if (mAboveWaveLength == 0) {
				initWave();
				mAboveWavePath.addRect(new RectF(0, 0, right, bottom), Direction.CW);
				mBlowWavePath.addRect(new RectF(0, 0, right, mWaveHeight * 2), Direction.CW);
				invalidate();
			}
		}
	}

	private void initWave() {
		if (getWidth() != 0) {
			int width = getWidth();
			mAboveWaveLength = width * mAboveWaveMultiple;
			mBlowWaveLength = width * mBlowWaveMultiple;
			oAboveMega = PI2 / mAboveWaveLength;
			oBlowMega = PI2 / mBlowWaveLength;
			left = getLeft();
			right = getRight();
			bottom = getBottom() + 2;
			mMaxRight = right + X_SPACE;
		}
	}

	private void getWaveOffset() {
		if (mBlowOffset > Float.MAX_VALUE - 100) {
			mBlowOffset = 0;
		} else {
			mBlowOffset += mWaveHz;
		}

		if (mAboveOffset > Float.MAX_VALUE - 100) {
			mAboveOffset = 0;
		} else {
			mAboveOffset += mWaveHz;
		}
	}

	public void startWave() {
		mWaveHeight = 32;
		removeCallbacks(mRefreshProgressRunnable);
		mRefreshProgressRunnable = new RefreshProgressRunnable();
		post(mRefreshProgressRunnable);
	}

	public void stopWave() {
		isStopWave = true;
	}

	private boolean isStopWave;

	private class RefreshProgressRunnable implements Runnable {
		public void run() {
			synchronized (WaveLayout.this) {
				long start = System.currentTimeMillis();
				calculatePath();
				invalidate();
				long gap = 16 - (System.currentTimeMillis() - start);
				postDelayed(this, gap < 0 ? 0 : gap);
				if (isStopWave) {
					mWaveHeight -= 2;
					if (mWaveHeight <= 0) {
						removeCallbacks(mRefreshProgressRunnable);
						isStopWave = false;
					}
				}
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(mBlowWavePath, mBlowWavePaint);
		canvas.drawPath(mAboveWavePath, mAboveWavePaint);
		super.draw(canvas);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		canvas.clipPath(mAboveWavePath);
		return super.drawChild(canvas, child, drawingTime);
	}
}
