package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.easy.util.BitmapUtil;
import com.easy.util.IntentUtil;
import com.easy.util.ToastUtil;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Article;
import com.xpg.hssy.bean.ThemeTopic;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description 优易圈话题发表
 * @createDate 2015年10月14日
 */
public class ThemeDisplayTopicActivity extends BaseActivity implements OnClickListener, BDLocationListener {
	private static final String SDKPICPATH = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private static final int TAKE_PICTURE = 0x000001;
	private static final int FEEDBACKINDEX = 10;
	private static final int PIC_NUM = 6;
	private EditText ed_content;
	private GridView gView;
	private GridAdapters adapter;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private View parentView;
	private String path;
	private String userid;
	private String cityName = "";
	private SharedPreferences sp;
	private LoadingDialog loadingDialog = null;
	private TextView tv_left, tv_right, tv_num_count;
	private ThemeTopic themeTopic = new ThemeTopic();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.update();
		}
	};
	public TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			tv_num_count.setText(temp.length() + "/140");
		}
	};

	@Override
	protected void initData() {
		super.initData();
		Bimp.tempSelectBitmap.clear();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		userid = sp.getString("user_id", "");
		themeTopic.setUserid(userid);
		themeTopic.setType(3);//1.电站评论，2桩（订单）评论，3优易圈话题
		PublicWay.coverWithIntrols.clear();
		Intent intent = getIntent();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		tv_left.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		ed_content.addTextChangedListener(mTextWatcher);
		gView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg4) {
				InputMethodManager imm = (InputMethodManager) self.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(gView.getWindowToken(), 0);
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(ThemeDisplayTopicActivity.this, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(ThemeDisplayTopicActivity.this, GalleryForHelpAndSuggestionScannActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					intent.putExtra("hasCover", false);
					startActivity(intent);
				}
			}
		});


	}

	public void takePhoto() {
		path = SDKPICPATH + getNowTime() + ".jpeg";
		IntentUtil.takePhoto(this,TAKE_PICTURE,path);
//		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private void initPop() {
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
				Intent intent = new Intent(ThemeDisplayTopicActivity.this, AlbumActivity.class);
				intent.putExtra(KEY.INTENT.PICK_SIZE, PIC_NUM);
				startActivityForResult(intent, FEEDBACKINDEX);
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
	protected void initUI() {
		super.initUI();

		deleteFileForImage();

		parentView = LayoutInflater.from(this).inflate(R.layout.theme_display_topic_layout, null);
		setContentView(parentView);
		initPop();
		setTitle("优易圈");
		tv_left = (TextView) parentView.findViewById(R.id.tv_left);
		tv_right = (TextView) parentView.findViewById(R.id.tv_right);
		tv_right.setText("发布");
		tv_num_count = (TextView) findViewById(R.id.tv_num_count);
		ed_content = (EditText) findViewById(R.id.ed_content);
		gView = (GridView) findViewById(R.id.gView);
		adapter = new GridAdapters(this);
		gView.setAdapter(adapter);

	}

	private void deleteFileForImage() {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {

			case TAKE_PICTURE:
				if (Bimp.tempSelectBitmap.size() < PIC_NUM && resultCode == RESULT_OK) {
//					IntentUtil.getPath(this,data.getData());
					Bitmap tempbm =  BitmapUtil.get(path);
					float scale =  BitmapUtil.measureScale(tempbm,1080);
					Log.i("TAKE_PICTURE", "scaleSize： " + scale + "");
					Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
					if (tempbm != bm) {
						recycleBitmap(tempbm);
					}
					Log.i("TAKE_PICTURE", "图片大小： " + bm.getByteCount() + "");
					Log.i("TAKE_PICTURE", "图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
					path = BitmapUtil.save(path, bm, Bitmap.CompressFormat.JPEG, 100);
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
	protected void onResume() {
		super.onResume();
		LbsManager.getInstance().getLocation(this);
		adapter.notifyDataSetChanged();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_left:
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case R.id.tv_right:
				PublicWay.coverWithIntrols.clear();
				for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {
					List<String> urls = (List<String>) PublicWay.coverWithIntrols.get("imgs");
					if (urls == null) {
						urls = new ArrayList<String>();
						PublicWay.coverWithIntrols.put("imgs", urls);
					}
					urls.add(Bimp.tempSelectBitmap.get(j).getImagePath());
					Log.i("path", Bimp.tempSelectBitmap.get(j).getImagePath() + "");
				}
				if (ed_content.getText().toString().trim().equals("")) {
					ToastUtil.show(self, "输入内容不能为空");
					return;
				}
				themeTopic.setLocation(cityName);
				themeTopic.setContent(ed_content.getText().toString().trim());
				displayTopic(themeTopic);    //发表话题
				break;
		}
	}

	//发表话题
	public void displayTopic(ThemeTopic themeTopic) {
		WebAPIManager.getInstance().displayTopic(themeTopic, PublicWay.coverWithIntrols, new WebResponseHandler<Article>() {
			@Override
			public void onStart() {
				super.onStart();
				loadingDialog = new LoadingDialog(ThemeDisplayTopicActivity.this, R.string.loading);
				loadingDialog.showDialog();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					loadingDialog = null;
				}
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<Article> response) {
				super.onFailure(response);
				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					loadingDialog = null;
				}
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<Article> response) {
				super.onSuccess(response);
//				Article article =response.getResultObj() ;
				if(loadingDialog!=null) {
					loadingDialog.dismiss();
					loadingDialog = null;
				}
				TipsUtil.showTips(self, response);
				ed_content.setText("");
				Bimp.tempSelectBitmap.clear();
				adapter.notifyDataSetChanged();
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		if (bdLocation != null) {
			cityName = bdLocation.getCity();
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
			adapter.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == PIC_NUM) {
				return PIC_NUM;
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
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
				if (position == PIC_NUM) {
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deleteFileForImage();
		Bimp.tempSelectToServerBitmap.clear();
		Bimp.tempSelectBitmap.clear();
		System.gc();

	}

}
