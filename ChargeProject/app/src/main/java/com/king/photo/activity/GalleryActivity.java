package com.king.photo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.util.NetWorkUtil;
import com.easy.util.ToastUtil;
import com.king.photo.util.Bimp;
import com.king.photo.util.PublicWay;
import com.king.photo.zoom.PhotoView;
import com.king.photo.zoom.ViewPagerFixed;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh 这个是用于进行图片浏览时的界面
 */
public class GalleryActivity extends Activity {
	private Intent intent;
	// 返回按钮
	private Button back_bt;
	// 发送按钮
	private Button send_bt;
	// 删除按钮
	private Button del_bt;
	// 顶部显示预览图片位置的textview
	private TextView positionTextView;
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
	private RelativeLayout photo_relativeLayout;
	private String pile_id;
	private String userid;
	private SharedPreferences sp;
	private String coverName;
	private String path;
	private boolean coverDeleteBt;
	private boolean hasCover;
    private LoadingDialog loadingDialog = null ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_gallery);// 切屏到主界面
		PublicWay.activityList.add(this);
		mContext = this;
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		userid = sp.getString("user_id", "");
		back_bt = (Button) findViewById(R.id.gallery_back);
		send_bt = (Button) findViewById(R.id.send_button);
		del_bt = (Button) findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new BackListener());
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		coverDeleteBt = intent.getBooleanExtra("coverDeleteBt", false);
		pile_id = intent.getStringExtra("pile_id");
		hasCover = intent.getBooleanExtra("hasCover", false);
		Bundle bundle = intent.getExtras();
		position = Integer.parseInt(intent.getStringExtra("position"));
		if (coverDeleteBt) {
			del_bt.setVisibility(View.GONE);
		} else {
			del_bt.setVisibility(View.VISIBLE);

		}
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		Log.i("Bimp.tempItemsForViewPage.size()",
				Bimp.tempItemsForViewPage.size() + "");
		for (int i = 0; i < Bimp.tempItemsForViewPage.size(); i++) {
			initListViews(Bimp.tempItemsForViewPage.get(i).getBitmap());
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

	private void delImgs(ArrayList<String> imgs) {
		loadingDialog = new LoadingDialog(GalleryActivity.this,R.string.loading) ;
		loadingDialog.showDialog();
		int item = pager.getCurrentItem();
		if (hasCover) {
				if(Bimp.tempItemsForViewPage.get(item).getCover_url() != null ){
					imgs.add(Bimp.tempItemsForViewPage.get(item).getCover_url());
					hasCover = false ;
				}
		}
		WebAPIManager.getInstance().delImgs(userid, pile_id, imgs,
				new WebResponseHandler<Object>(GalleryActivity.this) {

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						loadingDialog.dismiss();
						ToastUtil.show(GalleryActivity.this, "删除失败");

					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);
						ToastUtil.show(GalleryActivity.this,
								"删除" + response.getMessage());
						loadingDialog.dismiss();

					}

				});

	}

	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(GalleryActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if(listViews==null) return;
			if (listViews.size() == 1) {
				Bimp.imgs.clear();
				Bimp.tempSelectBitmap.size();
				Log.i("tag", Bimp.tempSelectBitmap.size() + "");
				String path = Bimp.tempItemsForViewPage.get(location)
						.getImageRelativePath();
				if (NetWorkUtil.isNetworkConnected(GalleryActivity.this)) {
					Bimp.imgs.add(path);
					delImgs(Bimp.imgs);
					send_bt.setText("完成" + "("
							+ Bimp.tempItemsForViewPage.size() + "/"
							+ PublicWay.num + ")");
					Intent intent = new Intent("data.broadcast.action");
					sendBroadcast(intent);
				}

				finish();
			} else {
				Bimp.imgs.clear();
				String path = Bimp.tempItemsForViewPage.get(location)
						.getImageRelativePath();
				if (NetWorkUtil.isNetworkConnected(GalleryActivity.this)) {
					Bimp.imgs.add(path);
					delImgs(Bimp.imgs);
					Bimp.tempItemsForViewPage.remove(location);
					pager.removeAllViews();
					listViews.remove(location);
					adapter.setListViews(listViews);
					send_bt.setText("完成" + "("
							+ Bimp.tempItemsForViewPage.size() + "/"
							+ PublicWay.num + ")");
					adapter.notifyDataSetChanged();

				}
			}
		}
	}


	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			send_bt.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/"
					+ PublicWay.num + ")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
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
}
