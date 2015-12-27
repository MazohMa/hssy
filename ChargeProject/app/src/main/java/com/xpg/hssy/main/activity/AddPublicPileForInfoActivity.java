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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.util.BitmapUtil;
import com.king.photo.activity.AlbumActivity;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialogAddPublicPile;
import com.xpg.hssy.main.activity.callbackinterface.ISelectItemOperator;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mazoh
 * @version 2.0.3
 * @description
 * @email 471977848@qq.com
 * @create 2015年7月21日
 */

public class AddPublicPileForInfoActivity extends BaseActivity implements ISelectItemOperator {

	private static final String SDKPICPATH = Environment
			.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/";
	private static final int FEEDBACKINDEX = 10;
	private static final int TAKE_PICTURE = 0x000001;
	private static final int FROMMAP = 0x000002;

	//桩标识
	private int CHARGE_TYPEPARAM = -1;
	private int OPERATORCONDITION = -1;
	private Float power = null;
	private Float voltage = null;
	private Float current = null;
	private String address = null;
	private ImageButton btn_right;
	Double longitude = null;
	Double latitude = null;
	private WaterBlueDialogAddPublicPile waterBlueDialogAddPublicPile = null;
	private WaterBlueDialogAddPublicPile waterBlueDialogAddPublicCondition = null;
	private WaterBlueDialogAddPublicPile waterBlueDialogAddPublicPileType = null;
	private WaterBlueDialogAddPublicPile waterBlueDialogAddPublicConditionForState = null;
	private WaterBlueDialogAddPublicPile waterBlueDialogAddPublicPileTypeForState = null;

	private RelativeLayout rl_pile_type, rl_device_condition, rl_type, rl_current_voltage_power_layout;
	private ImageView img_to_map;
	private TextView tv_location, tv_pile_type, tv_device_condition, tv_type, tv_current_voltage_power;
	private EditText ed_cd_num, et_ad_num, ed_current,ed_voltage,ed_power, ed_owner_name, ed_desp;
	private LinearLayout ll_pile_num;
	private RadioGroup rg_condition2;
	private ArrayList<String> datasrl_pile_type;
	private ArrayList<String> dataRl_device_conditionForPile;
	private ArrayList<String> dataRl_typeForPile;

	private ArrayList<String> dataRl_device_conditionForState;
	private ArrayList<String> dataRl_typeForForState;
	private boolean isPileView = true;
	private GridView gridview_introduct;
	private Button upload;
	private PopupWindow pop = null;

	private LinearLayout ll_popup;
	private String path;
	private View parentView;
	private Bitmap defaultBitmap;
	private GridAdapters gridAdapters;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			gridAdapters.update();
		}
	};
	private String user_id;
	private SharedPreferences sp;
	private LoadingDialog loadingDialog = null;

	public AddPublicPileForInfoActivity() {
	}

	@Override
	protected void initData() {
		super.initData();
		Bimp.tempSelectBitmap.clear();
		PublicWay.coverWithIntrols.clear();
		sp = getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
		user_id = sp.getString("user_id", null);
		Intent intent = getIntent();
		defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);

		if (intent != null) {
			longitude = intent.getDoubleExtra(KEY.INTENT.LONGITUDE, -1);
			latitude = intent.getDoubleExtra(KEY.INTENT.LATITUDE, -1);
			address = intent.getStringExtra(KEY.INTENT.ADDRESS);
		}
		datasrl_pile_type = new ArrayList<String>();
		datasrl_pile_type.add("私人电桩");
		datasrl_pile_type.add("充电站");

		dataRl_device_conditionForPile = new ArrayList<String>();
		dataRl_device_conditionForPile.add("正常情况");
		dataRl_device_conditionForPile.add("正常运行");

		dataRl_typeForPile = new ArrayList<String>();
		dataRl_typeForPile.add("快速充电桩");
		dataRl_typeForPile.add("慢速充电桩");

		dataRl_device_conditionForState = new ArrayList<String>();
		dataRl_device_conditionForState.add("正投运");
		dataRl_device_conditionForState.add("未投运");
		dataRl_device_conditionForState.add("建设中");
		dataRl_device_conditionForState.add("规划中");
		dataRl_device_conditionForState.add("其他");


		dataRl_typeForForState = new ArrayList<String>();
		dataRl_typeForForState.add("公共开放站");
		dataRl_typeForForState.add("专用站");
		dataRl_typeForForState.add("其他");

	}

	private void showUiForPile() {
		ll_pile_num.setVisibility(View.GONE);
		rg_condition2.setVisibility(View.VISIBLE);
		rl_current_voltage_power_layout.setVisibility(View.VISIBLE);
	}

	private void showUiForState() {
		ll_pile_num.setVisibility(View.VISIBLE);
		rg_condition2.setVisibility(View.GONE);
		rl_current_voltage_power_layout.setVisibility(View.GONE);
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
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddPublicPileForInfoActivity.this,
						AlbumActivity.class);
				startActivityForResult(intent, FEEDBACKINDEX);
				overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
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

	@Override
	protected void onResume() {
		super.onResume();
		gridAdapters.notifyDataSetChanged();

	}

	@Override
	protected void initUI() {
		super.initUI();
		parentView = LayoutInflater.from(this).inflate(
				R.layout.add_public_pile_info_layout, null);
		setContentView(parentView);

		initPopForIntro();
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		setTitle("填写信息");
		btn_right.setVisibility(View.INVISIBLE);
		rl_pile_type = (RelativeLayout) findViewById(R.id.rl_pile_type);
		rl_device_condition = (RelativeLayout) findViewById(R.id.rl_device_condition);
		rl_type = (RelativeLayout) findViewById(R.id.rl_type);
		img_to_map = (ImageView) findViewById(R.id.img_to_map);
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_location.setText(address);
		tv_pile_type = (TextView) findViewById(R.id.tv_pile_type);
		tv_device_condition = (TextView) findViewById(R.id.tv_device_condition);
		tv_type = (TextView) findViewById(R.id.tv_type);


		ll_pile_num = (LinearLayout) findViewById(R.id.ll_pile_num);
		rg_condition2 = (RadioGroup) findViewById(R.id.rg_condition2);
		rl_current_voltage_power_layout = (RelativeLayout) findViewById(R.id.rl_current_voltage_power_layout);

		tv_current_voltage_power = (TextView) findViewById(R.id.tv_current_voltage_power);
		ed_cd_num = (EditText) findViewById(R.id.ed_cd_num);
		et_ad_num = (EditText) findViewById(R.id.et_ad_num);

		ed_power = (EditText) findViewById(R.id.ed_power);
		ed_voltage = (EditText) findViewById(R.id.ed_voltage);
		ed_current = (EditText) findViewById(R.id.ed_current);

		ed_power.setVisibility(View.VISIBLE);
		ed_voltage.setVisibility(View.GONE);
		ed_current.setVisibility(View.GONE);

		ed_owner_name = (EditText) findViewById(R.id.ed_owner_name);
		ed_desp = (EditText) findViewById(R.id.ed_desp);

		gridview_introduct = (GridView) findViewById(R.id.gridview_introduct);
		gridAdapters = new GridAdapters(this);
		gridview_introduct.setAdapter(gridAdapters);
		upload = (Button) findViewById(R.id.upload);
		img_to_map.setOnClickListener(this);
		rl_pile_type.setOnClickListener(this);
		rl_device_condition.setOnClickListener(this);
		rl_type.setOnClickListener(this);
		upload.setOnClickListener(this);
		rg_condition2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				switch (i) {
					case R.id.rb_power:
						ed_voltage.setVisibility(View.GONE);
						ed_current.setVisibility(View.GONE);
						ed_power.setVisibility(View.VISIBLE);

						tv_current_voltage_power.setText("kWh");
						if (ed_power.getText().toString().trim().equals("")) {
						} else {
							power = Float.parseFloat(ed_power.getText().toString().trim());
						}
						break;
					case R.id.rb_voltage:
						ed_voltage.setVisibility(View.VISIBLE);
						ed_current.setVisibility(View.GONE);
						ed_power.setVisibility(View.GONE);

						tv_current_voltage_power.setText("V");
						if (ed_voltage.getText().toString().trim().equals("")) {
						} else {
							voltage = Float.parseFloat(ed_voltage.getText().toString().trim());
						}
						break;
					case R.id.rb_current:
						ed_voltage.setVisibility(View.GONE);
						ed_current.setVisibility(View.VISIBLE);
						ed_power.setVisibility(View.GONE);
						tv_current_voltage_power.setText("A");
						if (ed_current.getText().toString().trim().equals("")) {
						} else {
							current = Float.parseFloat(ed_current.getText().toString().trim());
						}
						break;
				}
			}
		});

		gridview_introduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			                        long arg4) {
				InputMethodManager imm = (InputMethodManager) self
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(gridview_introduct.getWindowToken(), 0);
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							AddPublicPileForInfoActivity.this,
							R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.CENTER, 0, 0);
				} else {
					Intent intent = new Intent(
							AddPublicPileForInfoActivity.this,
							GalleryForAddPileAndSiteActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					intent.putExtra("hasCover", false);
					startActivity(intent);
				}
			}
		});
		initDialog();
		showUiForPile();
		waterBlueDialogAddPublicPile.getAdapter().select(0);//初始化时默认选中桩
		waterBlueDialogAddPublicCondition.getAdapter().select(0);
		waterBlueDialogAddPublicPileType.getAdapter().select(0);
		waterBlueDialogAddPublicConditionForState.getAdapter().select(0);
		waterBlueDialogAddPublicPileTypeForState.getAdapter().select(0);
	}

	@Override
	protected void onLeftBtn(View v) {
		Intent intent = new Intent(this, AddPublicPileBaiduMapNewActivity.class);
		intent.putExtra("isFromDetail", true);
		this.startActivityForResult(intent, FROMMAP);
	}

	private void initDialog() {

		waterBlueDialogAddPublicPile = new WaterBlueDialogAddPublicPile(this, this, "电桩类型", 0);
		waterBlueDialogAddPublicCondition = new WaterBlueDialogAddPublicPile(this, this, "运行情况", 1);
		waterBlueDialogAddPublicPileType = new WaterBlueDialogAddPublicPile(this, this, "快充类型", 2);

		waterBlueDialogAddPublicConditionForState = new WaterBlueDialogAddPublicPile(this, this, "站点状态", 1);
		waterBlueDialogAddPublicPileTypeForState = new WaterBlueDialogAddPublicPile(this, this, "站点类型", 2);

		waterBlueDialogAddPublicCondition.setDialogTitle("运行情况");
		waterBlueDialogAddPublicPileType.setDialogTitle("快充类型");
		waterBlueDialogAddPublicConditionForState.setDialogTitle("站点状态");
		waterBlueDialogAddPublicPileTypeForState.setDialogTitle("站点类型");


		waterBlueDialogAddPublicPile.addDatas(datasrl_pile_type);

		waterBlueDialogAddPublicCondition.addDatas(dataRl_device_conditionForPile);
		waterBlueDialogAddPublicPileType.addDatas(dataRl_typeForPile);

		waterBlueDialogAddPublicConditionForState.addDatas(dataRl_device_conditionForState);
		waterBlueDialogAddPublicPileTypeForState.addDatas(dataRl_typeForForState);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.img_to_map:
				Intent intent = new Intent(this, AddPublicPileBaiduMapNewActivity.class);
				intent.putExtra("isFromDetail", true);
				this.startActivityForResult(intent, FROMMAP);
				break;
			case R.id.rl_pile_type:
				waterBlueDialogAddPublicPile.show();
				break;
			case R.id.rl_device_condition:
				if (isPileView) {
					waterBlueDialogAddPublicCondition.show();
				} else {
					waterBlueDialogAddPublicConditionForState.show();
				}
				break;
			case R.id.rl_type:
				if (isPileView) {
					waterBlueDialogAddPublicPileType.show();
				} else {
					waterBlueDialogAddPublicPileTypeForState.show();
				}
				break;
			case R.id.upload:
				PublicWay.coverWithIntrols.clear();
				for (int j = 0; j < Bimp.tempSelectBitmap.size(); j++) {
					List<String> urls = (List<String>) PublicWay.coverWithIntrols
							.get("imgs");
					if (urls == null) {
						urls = new ArrayList<String>();
						PublicWay.coverWithIntrols.put("imgs", urls);
					}
					urls.add(Bimp.tempSelectBitmap.get(j).getImagePath());
					Log.i("pathurls", Bimp.tempSelectBitmap.get(j).getImagePath() + "");
				}
				if(PublicWay.coverWithIntrols.size() == 0){
					return ;
				}
//				if (isPileView) {
//					Log.i("user_id", user_id + "");
//					Log.i("longitude", longitude + "");
//					Log.i("latitude", latitude + "");
//					Log.i("OPERATORCONDITION", OPERATORCONDITION + "");
//					Log.i("CHARGE_TYPEPARAM", CHARGE_TYPEPARAM + "");
//					Log.i("power", power + "");
//					Log.i("voltage", voltage + "");
//					Log.i("current", CHARGE_TYPEPARAM + "");
//
//
//					WebAPIManager.getInstance().addPile(user_id, longitude, latitude, OPERATORCONDITION, CHARGE_TYPEPARAM, power, voltage, current,
//							ed_owner_name.getText().toString().trim() + "", ed_desp.getText().toString().trim() + "", PublicWay.coverWithIntrols, new WebResponseHandler<Object>(
//									this) {
//								@Override
//								public void onStart() {
//									super.onStart();
//									loadingDialog = new LoadingDialog(AddPublicPileForInfoActivity.this, R.string.loading);
//									loadingDialog.showDialog();
//								}
//
//								@Override
//								public void onError(Throwable e) {
//									super.onError(e);
//									loadingDialog.dismiss();
//									loadingDialog = null;
//									TipsUtil.showTips(self, e);
//								}
//
//								@Override
//								public void onFailure(WebResponse<Object> response) {
//									super.onFailure(response);
//									loadingDialog.dismiss();
//									loadingDialog = null;
//									TipsUtil.showTips(self, response);
//
//								}
//
//								@Override
//								public void onSuccess(WebResponse<Object> response) {
//									super.onSuccess(response);
//									loadingDialog.dismiss();
//									loadingDialog = null;
//									TipsUtil.showTips(self, response);
////					Bimp.tempSelectBitmap.clear();
////					adapter.notifyDataSetChanged();
////					finish();
//
//								}
//
//							});
//				}else{
//					String cdNum = ed_cd_num.getText().toString().trim() ;
//					String adNum = et_ad_num.getText().toString().trim() ;
//					int cd = Integer.parseInt(cdNum) ;
//                    int ad = Integer.parseInt(adNum) ;
//
//						WebAPIManager.getInstance().addSite(user_id, longitude, latitude, OPERATORCONDITION, CHARGE_TYPEPARAM,cd,ad,
//								ed_owner_name.getText().toString().trim() + "", ed_desp.getText().toString().trim() + "",
//								PublicWay.coverWithIntrols, new WebResponseHandler<Object>(
//										this) {
//									@Override
//									public void onStart() {
//										super.onStart();
//										loadingDialog = new LoadingDialog(AddPublicPileForInfoActivity.this, R.string.loading);
//										loadingDialog.showDialog();
//									}
//
//									@Override
//									public void onError(Throwable e) {
//										super.onError(e);
//										loadingDialog.dismiss();
//										loadingDialog = null;
//										TipsUtil.showTips(self, e);
//									}
//
//									@Override
//									public void onFailure(WebResponse<Object> response) {
//										super.onFailure(response);
//										loadingDialog.dismiss();
//										loadingDialog = null;
//										TipsUtil.showTips(self, response);
//
//									}
//
//									@Override
//									public void onSuccess(WebResponse<Object> response) {
//										super.onSuccess(response);
//										loadingDialog.dismiss();
//										loadingDialog = null;
//										TipsUtil.showTips(self, response);
////					Bimp.tempSelectBitmap.clear();
////					adapter.notifyDataSetChanged();
////					finish();
//
//									}
//
//								});
//					}
				break;
			default:
				break;
		}
	}

	@Override
	public void ItemSelected(int type, int index) {
		if (index == 0) {
			//桩或者电站dialog回调
			tv_pile_type.setText(waterBlueDialogAddPublicPile.getAdapter().getSelectedItem());
			if (type == 0) {
				//桩
				showUiForPile();
				isPileView = true;
				tv_device_condition.setText("运行情况");
				tv_type.setText("快充类型");
			} else {
				//站
				showUiForState();
				isPileView = false;
				tv_device_condition.setText("站点状态");
				tv_type.setText("站点类型");
			}
		} else if (index == 1) {
			CHARGE_TYPEPARAM = type;
			Log.i("CHARGE_TYPEPARAM",CHARGE_TYPEPARAM + "") ;
			if (isPileView) {
				tv_device_condition.setText(waterBlueDialogAddPublicCondition.getAdapter().getSelectedItem());
			} else {
				tv_device_condition.setText(waterBlueDialogAddPublicConditionForState.getAdapter().getSelectedItem());
			}
		} else if (index == 2) {
			OPERATORCONDITION = type;
			Log.i("OPERATORCONDITION",OPERATORCONDITION + "") ;
			if (isPileView) {
				tv_type.setText(waterBlueDialogAddPublicPileType.getAdapter().getSelectedItem());
			} else {
				tv_type.setText(waterBlueDialogAddPublicPileTypeForState.getAdapter().getSelectedItem());
			}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(this, AddPublicPileBaiduMapNewActivity.class);
		intent.putExtra("isFromDetail", true);
		this.startActivityForResult(intent, FROMMAP);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deleteFileForImage();
		Bimp.tempSelectToServerBitmap.clear();
		Bimp.tempSelectBitmap.clear();

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
					path = BitmapUtil.save(path, bm, Bitmap.CompressFormat.JPEG, 90);
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
				if (PublicWay.coverWithIntrols != null
						&& PublicWay.coverWithIntrols.size() > 0) {
					Log.i("装载介绍图服务器集合大小", PublicWay.coverWithIntrols.size()
							+ "");
				}
				handler.sendEmptyMessage(0);
				break;
			case FROMMAP:
				String address = data.getStringExtra(KEY.INTENT.ADDRESS);
				boolean isFinish = data.getBooleanExtra("isFinish", false);
				if (address != null) {
					tv_location.setText(address);
				}
				if (isFinish) {
					this.finish();
				}
				break;

		}


	}
}
