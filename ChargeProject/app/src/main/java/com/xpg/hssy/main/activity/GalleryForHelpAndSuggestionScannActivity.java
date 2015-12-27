package com.xpg.hssy.main.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.king.photo.util.Bimp;
import com.king.photo.util.PublicWay;
import com.king.photo.zoom.PhotoView;
import com.king.photo.zoom.ViewPagerFixed;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh 这个是用于进行图片浏览时的界面
 */
public class GalleryForHelpAndSuggestionScannActivity extends Activity {
	private Intent intent;
	// 返回按钮
	private Button back_bt;
	// 删除按钮
	private Button del_bt;
	// 获取前一个activity传过来的position
	private int position;
	// 当前的位置
	private int location = 0;

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();

	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_gallery_forhelp_and_suggestion);// 切屏到主界面
		PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(R.id.gallery_back);
		del_bt = (Button) findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new BackListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		position = Integer.parseInt(intent.getStringExtra("position"));
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			initListViews(Bimp.tempSelectBitmap.get(i).getBitmap());
		}
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			location = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}
	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			finish() ; 
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.remove(location);
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return true;
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	private void recycleBitmap(final Bitmap bmp) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				bmp.recycle();
			}
		}) ;
	}
}
