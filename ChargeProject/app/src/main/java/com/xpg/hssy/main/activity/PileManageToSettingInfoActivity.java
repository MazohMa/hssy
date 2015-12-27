package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.easy.util.BitmapUtil;
import com.easy.util.EmptyUtil;
import com.easy.util.IntentUtil;
import com.easy.util.ToastUtil;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.activity.GalleryActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PileManageToSettingInfoActivity
 *
 * @author Mazoh 电桩设置--电桩信息
 */
public class PileManageToSettingInfoActivity extends BaseActivity implements OnClickListener {
	private ImageButton btn_left;
	private TextView tv_center;
	private Pile pile;
	private String pile_id;
	private String pile_num;
	private String pile_type;
	private String ratedPower;
	private String ratevoltage;
	private String rateCurrent;
	private String location;
	private float score;
	private TextView dianzhuangno_tvs;
	private TextView dianzhuangtype_tvs;
	private TextView edinggonglv_tvs;
	private TextView edingdianya_tvs;
	private TextView edingdianliu_tvs;
	private TextView localaddr_tvs;
	private String type_pile;
	private TextView command_point;
	private TextView sum_num_points;
	private TextView pilepoint_tv;
	private EvaluateColumn eva_star_point;
	private ImageView comment_next_bt;
	private ImageView iv_comfirm;
	private EditText et_detail_description;
	private PopupWindow pop = null;
	private PopupWindow pop2 = null;
	private LinearLayout ll_popup;
	private GridView noScrollgridview_introduct;
	private ImageView image_back;
	private GridAdapter adapter;
	private View parentView;
	private ImageView pile_type_pic;
	private RelativeLayout pilepoint_layout_id;
	public static Bitmap bimap;
	private static final int TAKE_PICTURE = 0x000001;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 6;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private static final int UPDATE_FXID = 4;// 结果
	private static final int UPDATE_NICK = 5;// 结果
	private static final String SDKPICPATH = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private String coverCropName; // 封面剪切
	private String coverName; // 封面原图
	private String userid;
	private SharedPreferences sp;
	private String path;
	private List<String> intros;
	private ImageItem imageItem = new ImageItem();
	private Bitmap defaultBitmap;
	private LoadingDialog loadingDialog = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			adapter.update();

		}
	};

	/**
	 * 定位
	 */
	private void startLbs() {

		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String proStr = location.getProvince();// 省份,目前没有用上
				String cityStr = location.getCity();
				String addressStr = "";
				if (cityStr != null) {
					if (location.getDistrict() != null) {
						addressStr += location.getDistrict();
					}
					if (location.getStreet() != null) {
						addressStr += location.getStreet();
					}
					if (location.getStreetNumber() != null) {
						addressStr += location.getStreetNumber();
					}
					setAddress(addressStr);

				} else {
					ToastUtil.show(PileManageToSettingInfoActivity.this, "定位失败");
				}
			}

		});
	}

	protected void setAddress(String addressStr) {
		localaddr_tvs.setText(addressStr);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_left: {
				finish();
				break;
			}
			case R.id.image_back: {
				// showPhotoBackDialog();
				if (imageItem.getImagePath() == null) {

					ll_popup.startAnimation(AnimationUtils.loadAnimation(PileManageToSettingInfoActivity.this, R.anim.activity_translate_in));
					pop2.showAtLocation(parentView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(PileManageToSettingInfoActivity.this, GalleryActivity.class);
					intent.putExtra("pile_id", pile_id);
					intent.putExtra("position", "1");
					intent.putExtra("ID", 0);
					intent.putExtra("hasCover", true);

					startActivity(intent);
				}
				break;
			}
			case R.id.pilepoint_layout_id: {
				if (!isNetworkConnected()) {
					return;
				}
				if (pile.getAcount()>0) {
					Intent intent = new Intent(this, SpecifiedEvaluateAndMomentsActivity.class);
					intent.putExtra(SpecifiedEvaluateAndMomentsActivity.KEY_SPECIIED_TYPE, SpecifiedEvaluateAndMomentsActivity.SPECIIED_TYPE_PILE_OR_STATION);
					intent.putExtra(KEY.INTENT.PILE_NAME, pile.getPileName());
					intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
					startActivity(intent);
				}
				break;
			}
			case R.id.iv_comfirm: {
				if (et_detail_description.isEnabled()) {
					modifyDetailDescription();
				} else {
					iv_comfirm.setImageResource(R.drawable.icon_ok);
					et_detail_description.setEnabled(true);
				}
				break;
			}
			default:
				break;
		}
	}

	private void modifyDetailDescription() {
		pile.setDesp(et_detail_description.getText().toString().trim());
		WebAPIManager.getInstance().modifyPile(pile, new WebResponseHandler(this) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse response) {
				super.onFailure(response);
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse response) {
				super.onSuccess(response);
				DbHelper.getInstance(self).insertPile(pile);
				ToastUtil.show(self, "修改成功");
				iv_comfirm.setImageResource(R.drawable.icon_edit);
				et_detail_description.setEnabled(false);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("TAG", "onCreate");
		clearBitmapFile();
	}

	@Override
	protected void initData() {
		super.initData();

		pile_id = getIntent().getStringExtra("pile_id");// 获得桩对象id ;
		pile = DbHelper.getInstance(this).getPileDao().load(pile_id); // 数据库获取个人桩
		pile_num = pile.getPileId() == null ? "" : pile.getPileId() + "";
		pile_type = pile.getType() + "";
		ratedPower = pile.getRatedPower() == null ? "" : pile.getRatedPower() + "";
		ratevoltage = pile.getRatedVoltage() == null ? "" : pile.getRatedVoltage() + "";
		rateCurrent = pile.getRatedCurrent() == null ? "" : pile.getRatedCurrent() + "";
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		userid = sp.getString("user_id", "");
	}

	@Override
	protected void initUI() {
		super.initUI();
		parentView = getLayoutInflater().inflate(R.layout.pile_info_layout, null);
		setContentView(parentView);
		PublicWay.activityList.add(this);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		tv_center = (TextView) findViewById(R.id.tv_center);
		eva_star_point = (EvaluateColumn) findViewById(R.id.eva_star_point);
		dianzhuangno_tvs = (TextView) findViewById(R.id.dianzhuangno_tvs);
		dianzhuangtype_tvs = (TextView) findViewById(R.id.dianzhuangtype_tvs);
		pile_type_pic = (ImageView) findViewById(R.id.pile_type_pic);
		edingdianya_tvs = (TextView) findViewById(R.id.edingdianya_tvs);
		edingdianliu_tvs = (TextView) findViewById(R.id.edingdianliu_tvs);
		localaddr_tvs = (TextView) findViewById(R.id.localaddr_tvs);
		pilepoint_tv = (TextView) findViewById(R.id.pilepoint_tv);
		edinggonglv_tvs = (TextView) findViewById(R.id.edinggonglv_tvs);
		command_point = (TextView) findViewById(R.id.command_point);
		sum_num_points = (TextView) findViewById(R.id.sum_num_points);
		comment_next_bt = (ImageView) findViewById(R.id.comment_next_bt);
		iv_comfirm = (ImageView) findViewById(R.id.iv_comfirm);
		et_detail_description = (EditText) findViewById(R.id.et_detail_description);
		image_back = (ImageView) findViewById(R.id.image_back);

		pilepoint_layout_id = (RelativeLayout) findViewById(R.id.pilepoint_layout_id);
		tv_center.setText("电桩信息");
		dianzhuangno_tvs.setText(pile_num);
		pile_type_pic.setImageResource("1".equals(pile_type) ? R.drawable.icon_direct : R.drawable.icon_communication);
		edinggonglv_tvs.setText(ratedPower + "");
		edingdianya_tvs.setText(ratevoltage + "");
		edingdianliu_tvs.setText(rateCurrent + "");
		if (pile.getAvgLevel() == null || pile.getAvgLevel() == 0) {
			command_point.setText("暂无评分" + "");
			sum_num_points.setText("");
			comment_next_bt.setImageBitmap(null);
		} else {
			score = pile.getAvgLevel();
			eva_star_point.setEvaluate(score);
			sum_num_points.setText("(" + pile.getAcount() + "人)");
			command_point.setText(pile.getAvgLevelAsString());
			comment_next_bt.setVisibility(View.VISIBLE);
		}
		et_detail_description.setText(pile.getDesp());
		et_detail_description.setEnabled(false);
		// 定位获取当前位置
		startLbs();
		initPopForIntro();
		initPopForCover();
		noScrollgridview_introduct = (GridView) findViewById(R.id.noScrollgridview_introduct);
		noScrollgridview_introduct.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		noScrollgridview_introduct.setAdapter(adapter);
		Resources res = getResources();
		defaultBitmap = BitmapFactory.decodeResource(res, R.drawable.default_img);
		imageItem.setImagePath("123");// 占位符，防止点击
		image_back.setImageBitmap(defaultBitmap);
		noScrollgridview_introduct.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg4) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(PileManageToSettingInfoActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(PileManageToSettingInfoActivity.this, GalleryActivity.class);
					intent.putExtra("pile_id", pile_id);
					intent.putExtra("position", "1");
					if (imageItem.getImagePath() == null) {
						intent.putExtra("ID", arg2);
						intent.putExtra("hasCover", false);
					} else {
						intent.putExtra("ID", arg2 + 1);
						intent.putExtra("hasCover", true);
					}
					startActivity(intent);
				}
			}
		});
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
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			Log.i("tag", "已经保存");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initPopForCover() {
		pop2 = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop2.setWidth(LayoutParams.MATCH_PARENT);
		pop2.setHeight(LayoutParams.WRAP_CONTENT);
		pop2.setBackgroundDrawable(new BitmapDrawable());
		pop2.setFocusable(true);
		pop2.setOutsideTouchable(true);
		pop2.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt4 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop2.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// takePhoto();
				coverName = getNowTime() + ".jpeg";
				coverCropName = "crop" + coverName;
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(SDKPICPATH, coverName)));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop2.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getNowTime();
				coverName = getNowTime() + ".jpeg";
				coverCropName = "crop" + coverName;
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop2.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop2.dismiss();
				ll_popup.clearAnimation();
			}
		});
	}

	private void initPopForIntro() {
		pop = new PopupWindow(this);
		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt4 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PileManageToSettingInfoActivity.this, AlbumActivity.class);
				intent.putExtra("pile_id", pile_id);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		pilepoint_layout_id.setOnClickListener(this);
		image_back.setOnClickListener(this);
		iv_comfirm.setOnClickListener(this);
		et_detail_description.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPileRepeat();
	}

	private void getPileRepeat() {
		Bimp.tempSelectBitmap.clear();
		Bimp.tempSelectToServerBitmap.clear();
		Bimp.tempItemsForViewPage.clear();
		handler.sendEmptyMessage(0);
		getPile();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("tag", "onDestroy");
		if (defaultBitmap != null && !defaultBitmap.isRecycled()) {

			BitmapUtil.recycle(defaultBitmap);
		}
		clearBitmapFile();
		Bimp.tempSelectBitmap.clear();
		System.gc();
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;
		private List<ViewHolder> holders = new ArrayList<PileManageToSettingInfoActivity.GridAdapter.ViewHolder>();

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 4) {
				return 4;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
				holders.add(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 4) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		public void recycle() {
			for (ViewHolder holder : holders) {
				BitmapUtil.recycle(holder.image);
			}
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null) return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	@Override
	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public void takePhoto() {
		path = SDKPICPATH + getNowTime() + ".jpeg";
		IntentUtil.takePhoto(this, TAKE_PICTURE, path);
	}

	@SuppressLint("SdCardPath")
	private void startPhotoZoom(Uri uri1, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri1, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(SDKPICPATH, coverCropName)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * Try to return the absolute file path from the given Uri
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null) data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaColumns.DATA}, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(MediaColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {

			case TAKE_PICTURE:
				if (Bimp.tempSelectBitmap.size() < 4 && resultCode == RESULT_OK) {
					Bitmap tempbm = BitmapUtil.get(path);
					Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
					if (tempbm != bm) {
						recycleBitmap(tempbm);
					}
					Log.i("TAKE_PICTURE", "图片大小： " + bm.getByteCount() + "");
					Log.i("TAKE_PICTURE", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
					path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 100);
					ImageItem takePhoto = new ImageItem();
					takePhoto.setBitmap(bm);
					Bimp.tempSelectBitmap.add(takePhoto);
					handler.sendEmptyMessage(0);
					PublicWay.coverWithIntrols.clear();
					PublicWay.coverWithIntrols.put("intros", path);
					if (PublicWay.coverWithIntrols != null && PublicWay.coverWithIntrols.size() > 0) {
						Log.i("装载介绍图服务器集合大小", PublicWay.coverWithIntrols.size() + "");
						uploadToServer();
					}

				}
				break;
			case PHOTO_REQUEST_TAKEPHOTO:
				Uri uri2 = Uri.fromFile(new File(SDKPICPATH, coverName));
				try {
					Bitmap tempbm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri2);
					Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
					if (tempbm != bm) {
						recycleBitmap(tempbm);
					}
					Log.i("PHOTO_REQUEST_TAKEPHOTO", "图片大小： " + bm.getByteCount() + "");
					Log.i("PHOTO_REQUEST_TAKEPHOTO", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
					path = SDKPICPATH + getNowTime() + ".jpeg";
					path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 100);
					PublicWay.coverWithIntrols.clear();
					PublicWay.coverWithIntrols.put("cover", path);
					startPhotoZoom(uri2, 480);
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				break;

			case PHOTO_REQUEST_GALLERY:
				if (data != null) {
					PublicWay.coverWithIntrols.clear();
					Uri uri = data.getData();
					try {
						String srcPath = IntentUtil.getPath(self, uri);
						Bitmap bm = BitmapUtil.limitSize(srcPath, 1080);
						// if (tempbm != bm) {
						// recycleBitmap(tempbm);
						// }
						Log.i("PHOTO_REQUEST_GALLERY", "图片大小： " + bm.getByteCount() + "");
						Log.i("PHOTO_REQUEST_GALLERY", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
						path = SDKPICPATH + getNowTime() + ".jpeg";
						path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 100);
						if (path != null) {
							PublicWay.coverWithIntrols.put("cover", path);
							Log.i("tag", path);
							startPhotoZoom(uri, 480);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				break;

			case PHOTO_REQUEST_CUT:
				// BitmapFactory.Options options = new BitmapFactory.Options();

				/**
				 * 最关键在此，把options.inJustDecodeBounds = true;
				 * 这里再decodeFile()，返回的bitmap为空 ，但此时调用options.outHeight时，已经包含了图片的高了
				 */
				// options.inJustDecodeBounds = true;
				Bitmap bm = BitmapUtil.limitSize(SDKPICPATH + coverCropName, 1080);
				path = SDKPICPATH + getNowTime() + ".jpeg";
				path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 100);
				Log.i("PHOTO_REQUEST_CUT", "图片大小： " + bm.getByteCount() + "");
				Log.i("PHOTO_REQUEST_CUT", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
				if (path != null) {
					// 装载服务器参数
					PublicWay.coverWithIntrols.put("coverCrop", path);
					Log.i("本地路径", path);
				}

				if (PublicWay.coverWithIntrols != null && PublicWay.coverWithIntrols.size() > 0) {
					Log.i("装载服务器集合大小", PublicWay.coverWithIntrols.size() + "");

					uploadToServer();
				}
				break;

		}
	}

	private void recycleBitmap(final Bitmap bmp) {
		bmp.recycle();
	}

	private void uploadToServer() {
		WebAPIManager.getInstance().updateImagesInServer(userid, pile_id, PublicWay.coverWithIntrols, new WebResponseHandler<Object>(this) {

			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(PileManageToSettingInfoActivity.this, R.string.loading);
				loadingDialog.showDialog();
				image_back.setEnabled(false);
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				Log.i("tag", "访问出错");
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				Log.i("tag", response.getMessage());
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				Log.i("tag", response.getMessage());
				getPileRepeat();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();
				image_back.setEnabled(true);

			}
		});

	}

	private void getPile() {
		WebAPIManager.getInstance().getPileById(pile_id, new WebResponseHandler<Pile>(self) {
			@Override
			public void onStart() {
				super.onStart();
				Bimp.tempItemsForViewPage.clear();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(PileManageToSettingInfoActivity.this, R.string.loading);
				loadingDialog.showDialog();
				imageItem.setImagePath("123");// 占位符，防止点击
				image_back.setImageBitmap(defaultBitmap);
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
			}

			@Override
			public void onFailure(WebResponse<Pile> response) {
				super.onFailure(response);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadingDialog.dismiss();
			}

			@Override
			public void onSuccess(WebResponse<Pile> response) {
				super.onSuccess(response);
				pile = response.getResultObj();
				DbHelper.getInstance(self).getPileDao().insertOrReplace(pile);
				Bimp.tempItemsForViewPage.clear();

				final String pathString = pile.getCoverCropImg();
				if (EmptyUtil.isEmpty(pathString)) {
					Log.i("tag===== nullpathString", pathString + "");
					image_back.setImageResource(R.drawable.icon_addpic_unfocused);
					imageItem.setImagePath(null);
					getIntros();
					return;
				}
				Log.i("tag== notnullpathString", pathString + "");

				final String url =  pathString;
				Log.i("封面图", url);
				ImageLoaderManager.getInstance().loadImage(url, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						imageItem.setImagePath("123");// 占位符，防止点击
						image_back.setImageBitmap(defaultBitmap);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						imageItem.setImagePath("123");// 占位符，防止点击
						image_back.setImageBitmap(defaultBitmap);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (loadedImage == null) {
							return;
						}
						image_back.setImageBitmap(loadedImage);
						imageItem.setBitmap(loadedImage);
						imageItem.setImagePath(url);
						imageItem.setImageRelativePath(pathString);
						imageItem.setCover_url(pile.getCoverImg());
						Bimp.tempItemsForViewPage.add(0, imageItem);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						imageItem.setImagePath("123");// 占位符，防止点击
						image_back.setImageBitmap(defaultBitmap);
					}
				});

				getIntros();
			}

			private void getIntros() {

				Bimp.tempSelectBitmap.clear();
				handler.sendEmptyMessage(0);

				intros = pile.getIntroImgs();
				if (intros != null && intros.size() > 0) {
					for (int i = 0; i < intros.size(); i++) {
						final int j = i;
						String tempUrl = intros.get(i);
						if (EmptyUtil.isEmpty(tempUrl)) {
							continue;
						}

						final String urlStr = tempUrl;
						final ImageItem mImageItem = new ImageItem();
						mImageItem.setBitmap(defaultBitmap);
						mImageItem.setImagePath(urlStr);
						// 获取服务器拿到的url,放进集合，方便删除的时候提交
						mImageItem.setImageRelativePath(intros.get(j));
						Bimp.tempSelectBitmap.add(mImageItem);
						Bimp.tempItemsForViewPage.add(mImageItem);
						ImageLoaderManager.getInstance().loadImage(urlStr, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri, View view) {
								// stub

							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								// stub

							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								// stub
								if (loadedImage == null) {
									return;
								}

								mImageItem.setBitmap(loadedImage);
								handler.sendEmptyMessage(0);
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {
								// stub

							}
						});

					}
					handler.sendEmptyMessage(0);
				}
			}

		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			for (int i = 0; i < PublicWay.activityList.size(); i++) {
				if (null != PublicWay.activityList.get(i)) {
					PublicWay.activityList.get(i).finish();
				}
			}
		}
		return true;
	}

	@Override
	protected void onLeftBtn(View v) {
		for (int i = 0; i < PublicWay.activityList.size(); i++) {
			if (null != PublicWay.activityList.get(i)) {
				PublicWay.activityList.get(i).finish();
			}
		}
	}

	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
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
