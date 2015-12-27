package com.xpg.hssy.main.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.easy.util.BitmapUtil;
import com.easy.util.ToastUtil;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * HelpAndSuggestionActivity
 * 
 * @author Mazoh 帮助与反馈
 * 
 */
public class HelpAndSuggestionSixthActivity extends BaseActivity implements
		OnClickListener {
	private static final String SDKPICPATH = Environment
			.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private static final int TAKE_PICTURE = 0x000001;
	private static final int FEEDBACKINDEX = 10;
	private EditText question_suggestion;
	private Button save;
	private GridView gView;
	private GridAdapters adapter;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private View parentView;
	private String path;
	private String userid;
	private SharedPreferences sp;
    private LoadingDialog loadingDialog = null ;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.update();
		}
	};

	@Override
	protected void initData() {
		super.initData();
		Bimp.tempSelectBitmap.clear();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		userid = sp.getString("user_id", "");
		PublicWay.coverWithIntrols.clear();

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		save.setOnClickListener(this);

	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private void initPopForIntro() {
		pop = new PopupWindow(this);

		View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
				null);

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
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpAndSuggestionSixthActivity.this,
						AlbumActivity.class);
				startActivityForResult(intent, FEEDBACKINDEX);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
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

		parentView = LayoutInflater.from(this).inflate(
				R.layout.help_suggestion_sixth_layout, null);
		setContentView(parentView);
		initPopForIntro();
		setTitle("帮助与反馈");
		question_suggestion = (EditText) findViewById(R.id.question_suggestion);
		gView = (GridView) findViewById(R.id.noScrollgridview_introduct);
		adapter = new GridAdapters(this);
		gView.setAdapter(adapter);
		save = (Button) findViewById(R.id.save);
		gView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg4) {
				InputMethodManager imm = (InputMethodManager) self
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(gView.getWindowToken(), 0);
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							HelpAndSuggestionSixthActivity.this,
							R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(
							HelpAndSuggestionSixthActivity.this,
							GalleryForHelpAndSuggestionScannActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					intent.putExtra("hasCover", false);
					startActivity(intent);
				}
			}
		});

	}

	private void deleteFileForImage() {
		File dir = new File(SDKPICPATH);
		File[] fs = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".jpeg") || arg1.endsWith(".png")
						|| arg1.endsWith(".jpg");
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
			if (Bimp.tempSelectBitmap.size() < 5 && resultCode == RESULT_OK) {
				Bitmap tempbm = (Bitmap) data.getExtras().get("data");
				Bitmap bm = BitmapUtil.limitSize(tempbm, 1080);
				if (tempbm != bm) {
					recycleBitmap(tempbm);
				}
				Log.i("TAKE_PICTURE", "图片大小： " + bm.getByteCount() + "");
				Log.i("TAKE_PICTURE",
						"图片长宽： " + bm.getWidth() + "---" + bm.getHeight());
				path = SDKPICPATH + getNowTime() + ".jpeg";
				path = BitmapUtil.save(path, bm, CompressFormat.JPEG, 90);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(bm);
				takePhoto.setImagePath(path);
				Bimp.tempSelectBitmap.add(takePhoto);
				if (PublicWay.coverWithIntrols != null
						&& PublicWay.coverWithIntrols.size() > 0) {
					Log.i("装载介绍图服务器集合大小", PublicWay.coverWithIntrols.size()
							+ "");
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
		adapter.notifyDataSetChanged();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;
		case R.id.save:
			PublicWay.coverWithIntrols.clear();
			for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {

				List<String> urls = (List<String>) PublicWay.coverWithIntrols
						.get("imgs");
				if (urls == null) {
					urls = new ArrayList<String>();
					PublicWay.coverWithIntrols.put("imgs", urls);
				}
				urls.add(Bimp.tempSelectBitmap.get(j).getImagePath());
				Log.i("path", Bimp.tempSelectBitmap.get(j).getImagePath() + "");
			}
			if (question_suggestion.getText().toString().trim().equals("")) {
				ToastUtil.show(self, "建议反馈输入框不能为空");
				return;
			}
			WebAPIManager.getInstance().feedBack(
					userid,
					1,
					question_suggestion.getText().toString().trim(),
					PublicWay.coverWithIntrols,
					new WebResponseHandler<Object>(
							HelpAndSuggestionSixthActivity.this) {

						@Override
						public void onStart() {
							super.onStart();
							loadingDialog = new LoadingDialog(HelpAndSuggestionSixthActivity.this,R.string.loading) ;
							loadingDialog.showDialog();
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
//							DialogUtil.dismiss();
							loadingDialog.dismiss();
							loadingDialog = null ;
							TipsUtil.showTips(self, e);
						}

						@Override
						public void onFailure(WebResponse<Object> response) {
							super.onFailure(response);
//							DialogUtil.dismiss();
							loadingDialog.dismiss();
							loadingDialog = null ;

							// ToastUtil.show(self, response.getMessage());
							TipsUtil.showTips(self, response);

						}

						@Override
						public void onSuccess(WebResponse<Object> response) {
							super.onSuccess(response);
//							DialogUtil.dismiss();
							loadingDialog.dismiss();
							loadingDialog = null ;
							// ToastUtil.show(self, response.getMessage());
							TipsUtil.showTips(self, response);
							question_suggestion.setText("");
							Bimp.tempSelectBitmap.clear();
							adapter.notifyDataSetChanged();
							finish();

						}

					});
			break;

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
			if (Bimp.tempSelectBitmap.size() == 5) {
				return 5;
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
				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 5) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
						.getBitmap());
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
		Bimp.tempSelectToServerBitmap.clear() ;
		Bimp.tempSelectBitmap.clear() ;
		
	}

}
