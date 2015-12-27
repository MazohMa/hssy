package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.easy.activity.EasyActivity;
import com.easy.util.BitmapUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by black-Gizwits on 2015/09/11.
 */
public class MultipleEvaluateActivity extends EasyActivity implements View.OnClickListener, BDLocationListener {

	private static final String SDKPICPATH = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private static final int TAKE_PICTURE = 0x000001;
	private static final int FEEDBACKINDEX = 10;
	private static final int MAX_PHOTO_NUM = 6;
	private static final int MINI_EVALUATE_CHAR_COUNT = 10;

	public static final int EVALUATE_TYPE_STATION = 1;
	public static final int EVALUATE_TYPE_PILE = 2;


	private Pile pile;
	private LoadingDialog loadingDialog;
	private EvaluateHandler evaluateHandler;
	private String pileId;
	private String pileName;
	private String cityName = "";
	private int evaluateType;
	private String userId;
	private String orderId;
	private SPFile sp;

	private TextView tv_center;
	private Button btn_right;
	private ImageButton btn_left;
	private LinearLayout ll_user_evaluate_line;
	private EvaluateColumn eva_user_evaluate;
	private EvaluateColumn eva_environment;
	private EvaluateColumn eva_exterior;
	private EvaluateColumn eva_performance;
	private EvaluateColumn eva_service;//目前已屏蔽此评价,默认5
	private EditText et_evaluate_detail;
	private TextView tv_photo_num;
	private GridView grv_photos;
	private ViewGroup selectedUserEvaluate;

	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private View rootView;
	private GridAdapters adapter;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.update();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		loadingDialog = new LoadingDialog(this, R.id.loading);
		evaluateHandler = new EvaluateHandler(this);
		sp = new SPFile(this, "config");
		Intent intent = getIntent();
		orderId = intent.getStringExtra(KEY.INTENT.ORDER_ID) == null ? "" : intent.getStringExtra(KEY.INTENT.ORDER_ID);
		pileId = intent.getStringExtra(KEY.INTENT.PILE_ID) == null ? "" : intent.getStringExtra(KEY.INTENT.PILE_ID);
		pileName = intent.getStringExtra(KEY.INTENT.PILE_NAME) == null ? "" : intent.getStringExtra(KEY.INTENT.PILE_NAME);
		evaluateType = intent.getIntExtra(KEY.INTENT.EVALUATE_TYPE, EVALUATE_TYPE_STATION);
		userId = intent.getStringExtra(KEY.INTENT.USER_ID);//从intent中读取
		userId = userId == null ? sp.getString(KEY.CONFIG.USER_ID, null) : userId;//如果intent中没找到,再从sp中读取
	}

	private void init() {
		rootView = LayoutInflater.from(this).inflate(R.layout.activity_multiple_evaluate, null);
		setContentView(rootView);
		tv_center = (TextView) findViewById(R.id.tv_center);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_text_right);
		ll_user_evaluate_line = (LinearLayout) findViewById(R.id.ll_user_evaluate_line);
		eva_user_evaluate = (EvaluateColumn) findViewById(R.id.eva_user_evaluate);
		eva_environment = (EvaluateColumn) findViewById(R.id.eva_environment);
		eva_exterior = (EvaluateColumn) findViewById(R.id.eva_exterior);
		eva_performance = (EvaluateColumn) findViewById(R.id.eva_performance);
		eva_service = (EvaluateColumn) findViewById(R.id.eva_service);
		et_evaluate_detail = (EditText) findViewById(R.id.et_evaluate_detail);
		tv_photo_num = (TextView) findViewById(R.id.tv_photo_num);
		grv_photos = (GridView) findViewById(R.id.grv_photos);

		//初始化界面文字和按钮
		findViewById(R.id.btn_right).setVisibility(View.GONE);//这个是ImageButton,要消失掉
		tv_center.setText("评价");
		btn_right.setText(R.string.publish_evaluate);
		btn_right.setVisibility(View.VISIBLE);
		tv_photo_num.setText(getString(R.string.photo_pick_num, 0, MAX_PHOTO_NUM));
		adapter = new GridAdapters(this);
		grv_photos.setAdapter(adapter);
		adapter.registerDataSetObserver(new PhotoDataObserver());
		initListener();
		initEvaluateLine();
	}

	private void initListener() {
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		grv_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg4) {
				InputMethodManager imm = (InputMethodManager) self.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(grv_photos.getWindowToken(), 0);
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(MultipleEvaluateActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(MultipleEvaluateActivity.this, GalleryForHelpAndSuggestionScannActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					intent.putExtra("hasCover", false);
					startActivity(intent);
				}
			}
		});
		initPopForIntro();
		EvaluateColumn.OnItemClickListener onItemClickListener = new EvaluateColumn.OnItemClickListener() {
			@Override
			public void onItemClick(View v, int index) {
				String evaluateDetail = createEvaluteDetail();
				et_evaluate_detail.setHint(evaluateDetail);
			}
		};
		eva_environment.setOnItemClickListener(onItemClickListener);
		eva_exterior.setOnItemClickListener(onItemClickListener);
		eva_performance.setOnItemClickListener(onItemClickListener);
	}

	private String createEvaluteDetail() {
		StringBuilder strb = new StringBuilder();
		int userEvaluate = (int) eva_user_evaluate.getEvaluate();
		if (userEvaluate > 0) {
			String[] userEvaluateArray = getResources().getStringArray(R.array.total_evaluate_detail);
			int index = userEvaluate - 1;
			if (index >= userEvaluateArray.length) {
				index = userEvaluateArray.length - 1;
			}
			strb.append(userEvaluateArray[index]);

		}
		int environmentEvaluate = (int) eva_environment.getEvaluate();
		if (environmentEvaluate > 0) {
			String[] environmentEvaluateArray = getResources().getStringArray(R.array.env_evaluate_detail);
			int index = environmentEvaluate - 1;
			if (index >= environmentEvaluateArray.length) {
				index = environmentEvaluateArray.length - 1;
			}
			strb.append(environmentEvaluateArray[index]);

		}
		int exteriorEvaluate = (int) eva_exterior.getEvaluate();
		if (exteriorEvaluate > 0) {
			String[] exteriorEvaluateArray = getResources().getStringArray(R.array.dev_evaluate_detail);
			int index = exteriorEvaluate - 1;
			if (index >= exteriorEvaluateArray.length) {
				index = exteriorEvaluateArray.length - 1;
			}
			strb.append(exteriorEvaluateArray[index]);

		}
		int performanceEvaluate = (int) eva_performance.getEvaluate();
		if (performanceEvaluate > 0) {
			String[] performanceEvaluateArray = getResources().getStringArray(R.array.per_evaluate_detail);
			int index = performanceEvaluate - 1;
			if (index >= performanceEvaluateArray.length) {
				index = performanceEvaluateArray.length - 1;
			}
			strb.append(performanceEvaluateArray[index]);

		}
		return strb.toString();
	}

	public void takePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private void initPopForIntro() {
		pop = new PopupWindow(this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

		pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt4 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MultipleEvaluateActivity.this, AlbumActivity.class);
				intent.putExtra(KEY.INTENT.PICK_SIZE, MAX_PHOTO_NUM);//只允许选择6个
				startActivityForResult(intent, FEEDBACKINDEX);
				overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
	}

	private void initEvaluateLine() {
		int childCount = ll_user_evaluate_line.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = ll_user_evaluate_line.getChildAt(i);
			view.setTag(i);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectedUserEvaluate != null) {
						unselectView(selectedUserEvaluate);
					}
					selectView((ViewGroup) v);
					Integer index = (Integer) v.getTag();
					Integer evaluateLevel = index + 1;
					eva_user_evaluate.setEvaluate(evaluateLevel);
					selectedUserEvaluate = (ViewGroup) v;
					String evaluateDetail = createEvaluteDetail();
					et_evaluate_detail.setHint(evaluateDetail);
				}
			});
		}
	}

	private void selectView(ViewGroup viewGroup) {
		viewGroup.setSelected(true);
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setSelected(true);
		}
	}

	private void unselectView(ViewGroup viewGroup) {
		viewGroup.setSelected(false);
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setSelected(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_text_right: {
				submit();
				break;
			}
			case R.id.btn_left: {
				onBackPressed();
				break;
			}
		}
	}

	private void submit() {
		PublicWay.coverWithIntrols.clear();
		List<String> urls = (List<String>) PublicWay.coverWithIntrols.get("imgs");
		for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {
			if (urls == null) {
				urls = new ArrayList<String>();
				PublicWay.coverWithIntrols.put("imgs", urls);
			}
			urls.add(Bimp.tempSelectBitmap.get(j).getImagePath());
			Log.i("path", Bimp.tempSelectBitmap.get(j).getImagePath() + "");
		}
		int userEvaluate = (int) eva_user_evaluate.getEvaluate();
		int environmentLevel = (int) eva_environment.getEvaluate();
		int exteriorLevel = (int) eva_exterior.getEvaluate();
		int performanceLevel = (int) eva_performance.getEvaluate();
		int serviceLevel = (int) eva_service.getEvaluate();
		String evaluateDetail = et_evaluate_detail.getText().toString();
		if (TextUtils.isEmpty(evaluateDetail)) {
			evaluateDetail = et_evaluate_detail.getHint().toString();
		}
		if (userEvaluate == 0) {
			ToastUtil.show(this, getString(R.string.please_make_subjectivity_evaluate));
			return;
		}
		if (environmentLevel == 0 || exteriorLevel == 0 || performanceLevel == 0 || serviceLevel == 0) {
			ToastUtil.show(this, getString(R.string.please_make_objective_evaluate));
			return;
		}
		if (evaluateDetail.length() < MINI_EVALUATE_CHAR_COUNT) {
			ToastUtil.show(this, getString(R.string.evaluate_must_over_to_15_word));
			return;
		}
		WebAPIManager.getInstance().postEvaluate(pileId, pileName, cityName, evaluateType, userId, orderId, evaluateDetail, userEvaluate, environmentLevel,
				exteriorLevel, performanceLevel, serviceLevel, PublicWay.coverWithIntrols, evaluateHandler);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		LbsManager.getInstance().getLocation(this);
		super.onResume();
		adapter.update();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {

			case TAKE_PICTURE:
				if (Bimp.tempSelectBitmap.size() < MAX_PHOTO_NUM && resultCode == RESULT_OK) {
					Bitmap tempbm = (Bitmap) data.getExtras().get("data");
					Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
					if (tempbm != bm) {
						recycleBitmap(tempbm);
					}
					Log.i("TAKE_PICTURE", "图片大小： " + bm.getByteCount() + "");
					Log.i("TAKE_PICTURE", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
					String path = SDKPICPATH + getNowTime() + ".jpeg";
					path = BitmapUtil.save(path, bm, Bitmap.CompressFormat.JPEG, 90);
					ImageItem takePhoto = new ImageItem();
					takePhoto.setBitmap(bm);
					takePhoto.setImagePath(path);
					Bimp.tempSelectBitmap.add(takePhoto);
					if (PublicWay.coverWithIntrols != null && PublicWay.coverWithIntrols.size() > 0) {
						Log.i("装载介绍图服务器集合大小", PublicWay.coverWithIntrols.size() + "");
						handler.sendEmptyMessage(0);
					}
				}
				break;
			case FEEDBACKINDEX:
				handler.sendEmptyMessage(0);
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		PublicWay.coverWithIntrols.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Bimp.tempSelectToServerBitmap.clear();
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		if (Bimp.tempSelectBitmap != null) {
			Bimp.tempSelectBitmap.clear();
		}
		setResult(RESULT_OK);
		finish();
	}


	private void recycleBitmap(final Bitmap bmp) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				bmp.recycle();
			}
		});
	}

	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		if (bdLocation != null) {
			cityName = bdLocation.getCity();
		}
	}

	private class PileHandler extends WebResponseHandler<Pile> {
		public PileHandler(Context context) {
			super(context);
		}

		@Override
		public void onSuccess(WebResponse<Pile> response) {
			super.onSuccess(response);
			pile = response.getResultObj();
			if (pile != null) {
				//TODO
			}
		}
	}

	private class EvaluateHandler extends WebResponseHandler<ChargeOrder> {
		public EvaluateHandler(Context context) {
			super(context);
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
			TipsUtil.showTips(self, e);
			loadingDialog.dismiss();
		}

		@Override
		public void onFailure(WebResponse<ChargeOrder> response) {
			super.onFailure(response);
			TipsUtil.showTips(self, response);
			loadingDialog.dismiss();
		}

		@Override
		public void onSuccess(WebResponse<ChargeOrder> response) {
			super.onSuccess(response);
			TipsUtil.showTips(self, response);
			loadingDialog.dismiss();
			if (Bimp.tempSelectBitmap != null) {
				Bimp.tempSelectBitmap.clear();
			}
			setResult(RESULT_OK);
			finish(); // 返回
		}

	}

	public class GridAdapters extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapters(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == MAX_PHOTO_NUM) {
				return MAX_PHOTO_NUM;
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
				convertView = inflater.inflate(R.layout.item_published_grida_middle, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
				if (position == MAX_PHOTO_NUM) {
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

	}

	private class PhotoDataObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			tv_photo_num.setText(getString(R.string.photo_pick_num, Bimp.tempSelectBitmap.size(), MAX_PHOTO_NUM));
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			tv_photo_num.setText(getString(R.string.photo_pick_num, Bimp.tempSelectBitmap.size(), MAX_PHOTO_NUM));
		}
	}

}
