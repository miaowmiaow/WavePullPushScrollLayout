package com.example.wavepullpushscrolllayout;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class PullPushScrollView extends ScrollView {

	private ViewGroup mChild;
	private ViewGroup mChildHeader;
	private WaveLayout mWaveLayout;

	private int mOriginalHeaderHeight;

	private float mLastX = 0;
	private float deltaY = -1;

	private ObjectAnimator oa;

	private OnSlideListenre mOnSlideListenre;

	public PullPushScrollView(Context context) {
		super(context);
	}

	public PullPushScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullPushScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setVerticalScrollBarEnabled(false);
		initView();
	}

	private void initView() {
		mChild = (ViewGroup) getChildAt(0);
		mChildHeader = (ViewGroup) mChild.getChildAt(0);
		mChildHeader.post(new Runnable() {

			@Override
			public void run() {
				mOriginalHeaderHeight = mChildHeader.getHeight();
			}
		});
		mWaveLayout = (WaveLayout) mChild.getChildAt(1);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = ev.getY();
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (getScrollY() != 0) {
				deltaY = 0;
				mLastX = ev.getY();
			} else {
				deltaY = ev.getY() - mLastX;
				if (deltaY > 5) {
					// 下滑
					setDown((int) -deltaY / 5);
					if (mWaveLayout != null) {
						mWaveLayout.startWave();
					}
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (getScrollY() < mOriginalHeaderHeight) {
				if (deltaY != 0) {
					reset();
				}
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mWaveLayout != null) {
						mWaveLayout.stopWave();
					}
				}
			}, 150);
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (t > mOriginalHeaderHeight) {
			return;
		}
		ViewHelper.setTranslationY(mChildHeader, t / 2);

		float percent = (float) t / mOriginalHeaderHeight;
		if (percent > 1) {
			percent = 1;
		}

		if (mOnSlideListenre != null) {
			mOnSlideListenre.onSlide(percent);
		}
	}

	public void setDown(int t) {
		scrollTo(0, t);
		if (t < 0) {
			mChildHeader.getLayoutParams().height = mOriginalHeaderHeight - t;
			mChild.getLayoutParams().height = mOriginalHeaderHeight - t;
			mChild.requestLayout();
		}
	}

	private void reset() {
		if (oa != null && oa.isRunning()) {
			return;
		}
		oa = ObjectAnimator.ofInt(this, "down", (int) -deltaY / 5, 0);
		oa.setDuration(150);
		oa.start();

		if (mOnSlideListenre != null) {
			mOnSlideListenre.onRefresh();
		}
	}

	public void setOnSlideListenre(OnSlideListenre l) {
		mOnSlideListenre = l;
	}

	public interface OnSlideListenre {
		public void onSlide(float percent);

		public void onRefresh();
	}
}
