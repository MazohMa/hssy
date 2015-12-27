package com.xpg.hssy.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.crop.Crop;
import com.android.crop.CropImageActivity;
import com.android.crop.CropUtil;
import com.easy.popup.EasyPopup;
import com.easy.util.BitmapUtil;
import com.easy.util.IntentUtil;
import com.easy.util.ScreenUtil;
import com.easy.util.ToastUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.account.ModifyPwdActivity;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Mazoh 个人信息中心
 */
public class MyIntroductionMessageActivity extends BaseActivity {
	private ImageView iv_avatar; // 上传图片
	private TextView username; // 用户名
	private EditText userphone; // 联系电话
	private TextView shouquan_alipay;
	private TextView qrcode; // 二维码
	private Button save; // 保存按钮
	private RelativeLayout qrcode_layout;
	private RelativeLayout username_layout_id;
	private RelativeLayout alipay_layout_id;
	private RelativeLayout passageword_layout_id;
	private RelativeLayout usericon_layout_id;
	private SharedPreferences sp;
	private String user_id;
	private User user;
	private String phone;
	private String name;
	private String alipay;
	private LayoutInflater mInflater;
	private Bitmap update_pic;
	private int gender;
	private EasyPopup photo_popup;
	private TextView tv_take_photo, tv_from_album, tv_cancel;
	private static final int ACTIVITY_PICKLOCAL = 0;
	private static final int ACTIVITY_PICKCAMERA = 1;
	private static final int ACTIVITY_EDITUSERNAME = 2;
	private static final int ACTIVITY_EDITALIPAY = 4;
	private static final int BACHMAIN = 6;
	private static final int ACTIVITY_CROP = 7;
	private String tempImagePath = "";
	private String finalImagePath = "";
	private RadioGroup sex_tab_new;
	private ImageButton btn_right;
	private Map<String, Object> fileKeyAndPath = new HashMap<String, Object>();
	private String avatarUrl;
	private static final String SDKPICPATH = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private DisplayImageOptions option;
	private LoadingDialog loadingDialog = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (loadingDialog != null) {
				loadingDialog.dismiss();
			}
			cropImageNew(tempImagePath);
		}
	};

	@Override
	protected void onRightBtn(View v) {
		super.onRightBtn(v);
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			case R.id.username_layout_id:
				editUser();
				break;
			case R.id.alipay_layout_id:
				editAlipay();
				break;
			case R.id.passageword_layout_id:
				editPwd();
				break;
			case R.id.usericon_layout_id:// 上传图片
				photo_popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
				break;
			case R.id.save:// 点击保存操作
				unregister2();
				break;
			case R.id.erweino:
				createQrCode();
				break;
			case R.id.erweima_layout_id:
				createQrCode();
				break;
		}

	}

	private void editPwd() {
		Intent _intentEditPwd = new Intent(this, ModifyPwdActivity.class);
		startActivity(_intentEditPwd);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	private void editAlipay() {
		Intent _intentEditAlipay = new Intent(this, EditAlipayNameActivity.class);
		_intentEditAlipay.putExtra("alipayname", shouquan_alipay.getText().toString());
		startActivityForResult(_intentEditAlipay, MyIntroductionMessageActivity.ACTIVITY_EDITALIPAY);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	private void editUser() {
		Intent _intentEditUser = new Intent(this, EditUserNameActivity.class);
		_intentEditUser.putExtra("username", username.getText().toString().trim());
		startActivityForResult(_intentEditUser, MyIntroductionMessageActivity.ACTIVITY_EDITUSERNAME);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	private void createQrCode() {
		String s_userphone = userphone.getText().toString();
		if (TextUtils.isEmpty(s_userphone)) {
			ToastUtil.show(this, R.string.phone_no_null);
			return;
		}
		int screenShortSide = ScreenUtil.getShortSide(self);
		int desiredWidth = (int) (screenShortSide * 0.7f);
		int desiredHeight = (int) (screenShortSide * 0.7f);
		Bitmap bitmap = null;
		try {
			bitmap = encodeQRAsBitmap("" + s_userphone, BarcodeFormat.QR_CODE, desiredWidth, desiredHeight, 0x00FFFFFF, 0xFF000000);
		} catch (WriterException e) {
			e.printStackTrace();
		}

		final Dialog dialog = new Dialog(this, R.style.DialogQrCodeStyle);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_create_qr_code_layout2, null);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dialog.dismiss();
				return false;
			}
		});
		ImageView img = (ImageView) view.findViewById(R.id.img_qr_code);
		if (bitmap != null) {
			img.setImageBitmap(bitmap);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		dialog.addContentView(view, params);
		dialog.show();
	}

	private Bitmap encodeQRAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight, int colorBg, int color) throws
			WriterException {
		final int WHITE = colorBg;
		final int BLACK = color;

		Hashtable<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contents);
		if (encoding != null) {
			hints = new Hashtable<EncodeHintType, Object>();
			ErrorCorrectionLevel ecLevel = ErrorCorrectionLevel.L;
			hints.put(EncodeHintType.ERROR_CORRECTION, ecLevel);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		Canvas canvas = new Canvas(bitmap);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bitmap;
	}

	public void initPhotoPopup() {
		View photo = this.getLayoutInflater().inflate(R.layout.take_pic_choose_layout, null);
		photo_popup = new EasyPopup(photo, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		photo_popup.setAnimationStyle(R.style.PopupAnimation);
		tv_cancel = (TextView) photo.findViewById(R.id.tv_cancel);
		tv_from_album = (TextView) photo.findViewById(R.id.from_album_btn);
		tv_take_photo = (TextView) photo.findViewById(R.id.take_photo_btn);
		photo.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
					case MotionEvent.ACTION_DOWN:
						return true;
					case MotionEvent.ACTION_UP:
						photo_popup.dismiss();
						break;
					default:
						break;
				}
				return false;
			}
		});
		tv_from_album.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, ACTIVITY_PICKLOCAL);
				photo_popup.dismiss();
			}
		});
		tv_take_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				tempImagePath = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/" + System.currentTimeMillis() + ".jpg";
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempImagePath)));
				startActivityForResult(intent, ACTIVITY_PICKCAMERA);
				photo_popup.dismiss();
			}
		});
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				photo_popup.dismiss();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.e("", "data requestCode:" + requestCode + "   resultCode:" + resultCode);
		if (data != null) {
			if (requestCode == MyIntroductionMessageActivity.ACTIVITY_EDITUSERNAME) {
				// 修改用户名返回
				String name = data.getStringExtra("username");
				username.setText(name);
			}
			if (requestCode == MyIntroductionMessageActivity.ACTIVITY_EDITALIPAY) {
				try {
					String alipayname = data.getStringExtra("alipayname");
					shouquan_alipay.setText(alipayname);
				} catch (Exception e) {
					e.printStackTrace();
					shouquan_alipay.setText("");
				}
			}
		}
		if (resultCode == RESULT_OK) {
			if (requestCode == ACTIVITY_PICKLOCAL) {
				Cursor cursor;
				if (data != null && IntentUtil.isLocalUri(data.getData())) {
					cursor = getContentResolver().query(data.getData(), null, null, null, null);
				} else {
					ToastUtil.show(this, "无法读取图片");
					return;
				}
				if (cursor == null) {
					ToastUtil.show(this, "无法读取图片");
					return;
				}
				cursor.moveToFirst();
				finalImagePath = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
				cursor.close();
				cropImageNew(finalImagePath);
			}

			if (requestCode == ACTIVITY_PICKCAMERA) {
				System.gc();
				if (tempImagePath == null || tempImagePath.equals("")) return;
				int orientation = 0;
				try {
					ExifInterface exifInterface = new ExifInterface(tempImagePath);
					orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// 压缩图片
				update_pic = BitmapUtil.cropSize(tempImagePath, ScreenUtil.getWidth(self), ScreenUtil.getHeight(self));
				if (orientation == 6) {
					Matrix matrix = new Matrix();
					matrix.reset();
					matrix.postRotate(90);
					update_pic = Bitmap.createBitmap(update_pic, 0, 0, update_pic.getWidth(), update_pic.getHeight(), matrix, true);
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						OutputStream outputStream = null;
						try {
							outputStream = new FileOutputStream(new File(tempImagePath));
							if (outputStream != null) {
								update_pic.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							CropUtil.closeSilently(outputStream);
						}
						mHandler.sendEmptyMessage(0);
					}
				}).start();

			}
			if (requestCode == ACTIVITY_CROP) {
				if (data != null) {
					String path = data.getStringExtra("data");
					fileKeyAndPath.clear();
					fileKeyAndPath.put("avatarUrl", path);
					updateAvatar();
				}
			}

		}
	}

	private void cropImage(Bitmap data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		intent.putExtra("data", data);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 50);
		intent.putExtra("outputY", 50);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, ACTIVITY_CROP);
	}

	private void cropImageNew(String file) {
		Intent intent = new Intent(self, CropImageActivity.class);
		intent.putExtra(Crop.Extra.ASPECT_X, 1);
		intent.putExtra(Crop.Extra.ASPECT_Y, 1);
		intent.putExtra(Crop.Extra.MAX_X, 50);
		intent.putExtra(Crop.Extra.MAX_Y, 50);
		intent.putExtra("data", file);
		startActivityForResult(intent, ACTIVITY_CROP);
	}

	private void updateAvatar() {
		WebAPIManager.getInstance().modifyUserInfoForAvatar(user_id, fileKeyAndPath, new WebResponseHandler<User>(self) {

			@Override
			public void onStart() {
				super.onStart();
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.showDialog();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				User user = DbHelper.getInstance(self).getUserDao().load(user_id) ;
				String refreshToken=   user.getRefreshToken() +"";
				String token = user.getToken()+"" ;
				if (response.getResultObj() != null) {
					String url = "";
					response.getResultObj().setRefreshToken(refreshToken);
					response.getResultObj()
							.setToken(token); // 服务器返回的token为空，手动设置token
					DbHelper.getInstance(self).getUserDao().update(response.getResultObj());
					if (!TextUtils.isEmpty(response.getResultObj().getAvatarUrl())) {
						url = response.getResultObj().getAvatarUrl();
					}
					ImageLoaderManager.getInstance().displayImage(url, iv_avatar, option, true);
				}

			}

		});
	}
	private String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

	private void logout2() {
		loadingDialog = new LoadingDialog(self, R.string.loading);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().logout(1, phone, new WebResponseHandler<User>(MyIntroductionMessageActivity.this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				User user = DbHelper.getInstance(self).getUserDao().load(user_id) ;
				WebAPIManager.getInstance().setAccessToken(null);
				DbHelper.getInstance(self).getUserDao().delete(user);
			}
		});
	}

	private void unregister2() {
		final WaterBlueDialog waterBlueDialog = new WaterBlueDialog(this);
		waterBlueDialog.setContent("你确定要注销当前账号吗？");
		waterBlueDialog.setLeftBtnText("取消");
		waterBlueDialog.setRightBtnText("确认");
		waterBlueDialog.setRightListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				waterBlueDialog.dismiss();
				logout2();
				afterLogout();
			}
		});
		waterBlueDialog.setLeftListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				waterBlueDialog.dismiss();
			}
		});
		waterBlueDialog.show();
	}

	private void afterLogout() {
		Editor ed = sp.edit();
		ed.putBoolean("isLogin", false);
		ed.putString("user_id", "");
		ed.putString(MyConstant.BT_MAC_LAST, "");
		ed.commit();
		finish();
		Intent login_intent = new Intent(MyIntroductionMessageActivity.this, LoginActivity.class);
		login_intent.putExtra("isMainActivity", true);
		MyIntroductionMessageActivity.this.startActivityForResult(login_intent, BACHMAIN);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		BTManager.getInstance().disconnect();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		mInflater = LayoutInflater.from(this);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		user = DbHelper.getInstance(MyIntroductionMessageActivity.this).getUserDao().load(user_id);// 数据库中获取
		phone = user.getPhone();
		name = user.getName();
		alipay = user.getAlipayName();
		avatarUrl = user.getAvatarUrl();
		gender = user.getGender() == null ? 1 : user.getGender();
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h23))).build();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.myintroduction_message_layout_new);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		setTitle("我的资料");
		btn_right.setVisibility(View.INVISIBLE);
		sex_tab_new = (RadioGroup) findViewById(R.id.sex_tab_new);
		if (gender == 1) {
			RadioButton rbButton = (RadioButton) sex_tab_new.getChildAt(0);
			rbButton.setChecked(true);
		} else {
			RadioButton rbButton = (RadioButton) sex_tab_new.getChildAt(1);
			rbButton.setChecked(true);
		}
		qrcode_layout = (RelativeLayout) findViewById(R.id.erweima_layout_id);
		username_layout_id = (RelativeLayout) findViewById(R.id.username_layout_id);
		alipay_layout_id = (RelativeLayout) findViewById(R.id.alipay_layout_id);
		passageword_layout_id = (RelativeLayout) findViewById(R.id.passageword_layout_id);
		usericon_layout_id = (RelativeLayout) findViewById(R.id.usericon_layout_id);
		iv_avatar = (ImageView) findViewById(R.id.update_pic);
		username = (TextView) findViewById(R.id.username);
		username.setText(name);
		shouquan_alipay = (TextView) findViewById(R.id.shouquan_alipay);
		shouquan_alipay.setText(alipay == null ? "" : alipay);
		userphone = (EditText) findViewById(R.id.userphone);
		userphone.setText(phone);
		String url = avatarUrl;
		ImageLoaderManager.getInstance().displayImage(url, iv_avatar, option, true);
		qrcode = (TextView) findViewById(R.id.erweino);
		save = (Button) findViewById(R.id.save);
		initPhotoPopup();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		save.setOnClickListener(this);
		qrcode.setOnClickListener(this);
		usericon_layout_id.setOnClickListener(this);
		qrcode_layout.setOnClickListener(this);
		username_layout_id.setOnClickListener(this);
		alipay_layout_id.setOnClickListener(this);
		passageword_layout_id.setOnClickListener(this);
		sex_tab_new.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				// 根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton) MyIntroductionMessageActivity.this.findViewById(radioButtonId);
				// 更新文本内容，以符合选中项
				gender = rb.getText().toString().equals("男") ? 1 : 2;
				modifyForGender();
			}

			private void modifyForGender() {
				WebAPIManager.getInstance().modifyUserInfoForSex(user_id, (Integer) gender, new WebResponseHandler<User>(MyIntroductionMessageActivity.this) {

					@Override
					public void onStart() {
						super.onStart();
						loadingDialog = new LoadingDialog(self, R.string.loading);
						loadingDialog.showDialog();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
					}

					@Override
					public void onFailure(WebResponse<User> response) {
						super.onFailure(response);
						loadingDialog.dismiss();
						ToastUtil.show(MyIntroductionMessageActivity.this, response.getMessage());
					}

					@Override
					public void onSuccess(WebResponse<User> response) {
						super.onSuccess(response);
						loadingDialog.dismiss();
						user = DbHelper.getInstance(self).getUserDao().load(user_id) ;
						String refreshToken=   user.getRefreshToken() ;
						String token = user.getToken() ;
						if (response.getResultObj() != null) {
							response.getResultObj().setRefreshToken(refreshToken);
							response.getResultObj().setToken(token); // 服务器返回的token为空，手动设置token
							DbHelper.getInstance(MyIntroductionMessageActivity.this).getUserDao().update(response.getResultObj());
						}
					}

				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deleteTempImg();
	}

	private void deleteTempImg() {
		try {
			File imgDir = new File(SDKPICPATH);
			File[] imgs = imgDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String name) {
					return name.endsWith(".jpg") || name.endsWith(".png");
				}
			});
			if (imgs != null && imgs.length > 0) {

				for (File img : imgs) {
					try {
						img.delete();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		}

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

}
