package com.king.photo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.easy.util.BitmapUtil;
import com.easy.util.ToastUtil;
import com.king.photo.adapter.AlbumGridViewAdapter;
import com.king.photo.util.AlbumHelper;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageBucket;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.xpg.hssy.dialog.DialogUtil;

/**
 * @author Mazoh 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends Activity {
	// 显示手机里的所有图片的列表控件
	private GridView gridView;
	// 当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	// gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private Button okButton;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	private Button preview;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	private String pile_id;
	private String userid;
	private SharedPreferences sp;
	private String coverName;
	private String path;
	volatile static boolean isOpen;
	private static final String SDKPICPATH = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private ArrayList<ImageItem> tempItems = new ArrayList<ImageItem>();
	private LoadingDialog loadingDialog = null;
	private int pickSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		userid = sp.getString("user_id", "");
		PublicWay.activityList.add(this);
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
			tempItems.add(Bimp.tempSelectBitmap.get(i));
		}
		Bimp.tempSelectToServerBitmap.clear(); // 进入activity，清理向服务器发送的url集合
		pile_id = getIntent().getStringExtra("pile_id");
		pickSize = getIntent().getIntExtra(KEY.INTENT.PICK_SIZE, 0);
		if (pickSize > 0) {
			PublicWay.num = pickSize;
		} else {
			if (pile_id == null || pile_id.equals("")) {
				// 建议反馈中的图片选择最多选择5张图片
				PublicWay.num = pickSize = 5;
			} else {
				// 桩设置中图片上传介绍图最多只能选择4张
				PublicWay.num = pickSize = 4;
			}
		}
		mContext = this;
		// 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plugin_camera_no_pictures);
		init();
		initListener();
		// 这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// mContext.unregisterReceiver(this);
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
//		clearBitmapFile() ;

	}

	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.putExtra("pile_id", pile_id);
				intent.putExtra("coverDeleteBt", true);
				intent.setClass(AlbumActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		}

	}

	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		Log.e("tag", "保存图片");
		path = Environment.getExternalStorageDirectory() + "/" + bitName + ".jpeg";
		File f = new File(Environment.getExternalStorageDirectory() + "/" + bitName + ".jpeg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			Log.i("tag", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		@Override
		@SuppressWarnings("unchecked")
		public void onClick(View v) {
			ArrayList<ImageItem> tempSelectToServerBitmap = Bimp.tempSelectToServerBitmap;
			PublicWay.coverWithIntrols.clear();
			if (tempSelectToServerBitmap != null && tempSelectToServerBitmap.size() > 0) {

				for (int i = 0; i < tempSelectToServerBitmap.size(); i++) {
					Bitmap tempbm = tempSelectToServerBitmap.get(i).getBitmap();

					Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
					if (bm != tempbm) {
						tempbm.recycle();
					}
					path = SDKPICPATH + getNowTime() + ".jpeg";
					path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 90);

					// saveMyBitmap(coverName, bm);// 保存bitmap到本地，设置对应的path
					tempSelectToServerBitmap.get(i).setImagePath(path);
					List<String> urls = (List<String>) PublicWay.coverWithIntrols.get("intros");
					if (urls == null) {
						urls = new ArrayList<String>();
						PublicWay.coverWithIntrols.put("intros", urls);
					}
					urls.add(tempSelectToServerBitmap.get(i).getImagePath());
					Log.i("path", tempSelectToServerBitmap.get(i).getImagePath() + "========");

				}
				if (pile_id != null) {
					WebAPIManager.getInstance().updateImagesInServer(userid, pile_id, PublicWay.coverWithIntrols, new WebResponseHandler<Object>(AlbumActivity
							.this) {

						@Override
						public void onStart() {
							super.onStart();
							loadingDialog = new LoadingDialog(AlbumActivity.this, R.string.loading);
							loadingDialog.showDialog();

						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							Log.i("tag", "访问出错");
							loadingDialog.dismiss();
							finish();

						}

						@Override
						public void onFailure(WebResponse<Object> response) {
							super.onFailure(response);
							Log.i("tag", response.getMessage());
							loadingDialog.dismiss();
							finish();

						}

						@Override
						public void onSuccess(WebResponse<Object> response) {
							super.onSuccess(response);
							Log.i("tag", "已完成");
							Bimp.tempSelectToServerBitmap.clear();
							Bimp.tempSelectBitmap.clear();
							loadingDialog.dismiss();
							finish();
						}

					});
				} else {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		}

	}

	// 获取指定路径的图片
	public static Bitmap getImage(String urlpath) throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		Bitmap bitmap = null;
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
		}
		return bitmap;
	}

	// 返回按钮监听
	private class BackListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			intent.putExtra("pile_id", pile_id);
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Bimp.tempSelectBitmap = tempItems;
			Log.i("tempItems", tempItems.size() + "");
			finish();
		}
	}

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < contentList.size(); i++) {
			dataList.addAll(contentList.get(i).imageList);
		}

		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, boolean isChecked, Button chooseBt) {
				Log.i("tag", "isChecked:===" + isChecked);
				Log.i("tag", "bimp.size:===" + Bimp.tempSelectBitmap.size() + "    " + "publicway.num===" + PublicWay.num);
				Log.i("tag", "isChecked2:===" + isChecked);
				if (isChecked) {
					if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
						toggleButton.setChecked(false);
						chooseBt.setVisibility(View.GONE);
						if (!removeOneData(dataList.get(position))) {
							Toast.makeText(AlbumActivity.this, getString(R.string.only_can_choice, PublicWay.num), Toast.LENGTH_SHORT).show();
						}
						return;
					}
					Log.i("tag", "isChecked3:===" + isChecked);
					// Bimp.tempSelectToServerBitmap.clear() ;
					if (dataList.get(position).getBitmap() == null) {
						ToastUtil.show(AlbumActivity.this, "图片已损坏，请重新选择");
						toggleButton.setChecked(false);
						return;
					}
					chooseBt.setVisibility(View.VISIBLE);
					Bimp.tempSelectToServerBitmap.add(dataList.get(position));
					Bimp.tempSelectBitmap.add(dataList.get(position));
					okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
					Log.i("tag", "确认" + Bimp.tempSelectToServerBitmap.size());
				} else {
					Log.i("tag", "isChecked4:===" + isChecked);
					Bimp.tempSelectToServerBitmap.remove(dataList.get(position));
					Bimp.tempSelectBitmap.remove(dataList.get(position));
					chooseBt.setVisibility(View.GONE);
					okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
					Log.i("tag", "取消" + Bimp.tempSelectToServerBitmap.size());
				}
				isShowOkBt();
			}
		});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			return true;
		}
		return false;
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("tempItems", tempItems.size() + "");
		finish();
		return true;

	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}

	private void clearBitmapFile() {
		File dir = new File(SDKPICPATH);
		File[] fs = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".jpeg") || arg1.endsWith(".png") || arg1.endsWith(".jpg");
			}
		});
		if (fs != null && fs.length > 0) {
			for (File file : fs) {
				try {
					file.delete();
				} catch (Exception e) {
				}
			}
		}

	}
}
