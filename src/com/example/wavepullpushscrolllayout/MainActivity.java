package com.example.wavepullpushscrolllayout;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wavepullpushscrolllayout.PullPushScrollView.OnSlideListenre;

public class MainActivity extends Activity {

	private PullPushScrollView mPullPushScrollView;
	private LinearLayout mLl;
	private RelativeLayout mRlBar;
	private View mLine;

	private int[] images = new int[] { R.drawable.tv_450, R.drawable.tv_angry, R.drawable.tv_cheers, R.drawable.tv_doge,
			R.drawable.tv_happy, R.drawable.tv_huaji, R.drawable.tv_ji, R.drawable.tv_kira, R.drawable.tv_mygod,
			R.drawable.tv_pill, R.drawable.tv_sad, R.drawable.tv_shocking, R.drawable.tv_spitting, R.drawable.tv_thinking,
			R.drawable.tv_xuejie, R.drawable.tv_zhuangb };

	private List<Bean> mBeanList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mPullPushScrollView = (PullPushScrollView) findViewById(R.id.svLayout);
		mLl = (LinearLayout) findViewById(R.id.ll);
		mRlBar = (RelativeLayout) findViewById(R.id.rlBar);
		mLine = (View) findViewById(R.id.line);

		mBeanList = new ArrayList<Bean>();

		for (int i = 0; i < images.length - 1; i++) {
			Bean bean = new Bean();
			bean.image = images[i];
			bean.name = "哈哈" + i;
			mBeanList.add(bean);
		}

		MyAdapter adapter = new MyAdapter(this, mBeanList);

		for (int i = 0; i < adapter.getCount(); i++) {
			mLl.addView(adapter.getView(i, null, null));
		}

		mPullPushScrollView.setOnSlideListenre(new OnSlideListenre() {

			@Override
			public void onSlide(float percent) {
				int alpha = (int) (percent * 255);
				mRlBar.setBackgroundColor(Color.argb(alpha, 255, 255, 255));
				mLine.setAlpha(percent);
			}

			@Override
			public void onRefresh() {
				Toast.makeText(MainActivity.this, "刷新完成", Toast.LENGTH_SHORT).show();
			}
		});

	}

}
