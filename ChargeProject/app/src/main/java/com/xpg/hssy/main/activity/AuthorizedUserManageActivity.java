package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.easy.util.ToastUtil;
import com.google.zxing.Result;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssy.zxing.ZxingManager;
import com.xpg.hssy.zxing.ZxingManager.OnResultListener;
import com.xpg.hssy.zxing.view.ViewfinderView;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthorizedUserManageActivity
 *
 * @author Mazoh
 * @version 2.0.0
 * @Description 授权用户管理
 * @createDate 2015年05月16日
 */
public class AuthorizedUserManageActivity extends BaseActivity implements OnClickListener {
	private ListView lv_shouquanUserManages_list;
	private GrantUserManageAdapter grantUserManageAdapter;
	private Button shoudong_add_bt;
	private Button erweima_add_bt;
	private String pile_id;
	private ZxingManager mZxingManager;
	private SurfaceView preview_view;
	private RelativeLayout rl_qrcode;
	private RelativeLayout rl_userlist;
	private RelativeLayout rl_no_user;
	private Pile pile;
	private SharedPreferences sp;
	private String user_id;
	private LoadingDialog loadingDialog = null ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		pile_id = getIntent().getStringExtra("pile_id");
		pile = DbHelper.getInstance(self).getPileDao().load(pile_id);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", null);
	}

	private void getFamily() {
		loadingDialog = new LoadingDialog(self,R.string.loading) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().getFamily(pile_id, new WebResponseHandler<List<User>>(this) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
				loadingDialog.dismiss();

			}

			@Override
			public void onFailure(WebResponse<List<User>> response) {
				super.onFailure(response);
				TipsUtil.showTips(self, response);
				loadingDialog.dismiss();

			}

			@Override
			public void onSuccess(WebResponse<List<User>> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				if (EmptyUtil.isEmpty(response.getResultObj())) {
					showNoUserView();
				} else {
					grantUserManageAdapter.clear();
					grantUserManageAdapter.add(response.getResultObj());
					showUserListView();
				}
			}

		});
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.authorized_user_manage_list_layout);
		setTitle(R.string.authorized_management);
		rl_qrcode = (RelativeLayout) findViewById(R.id.rl_qrcode);
		rl_userlist = (RelativeLayout) findViewById(R.id.rl_userlist);
		rl_no_user = (RelativeLayout)findViewById(R.id.rl_no_user);
		rl_no_user.setVisibility(View.GONE);
		lv_shouquanUserManages_list = (ListView) findViewById(R.id.lv_shouquanUserManages_list);
		shoudong_add_bt = (Button) findViewById(R.id.shoudong_add_bt);
		erweima_add_bt = (Button) findViewById(R.id.erweima_add_bt);
		preview_view = (SurfaceView) findViewById(R.id.preview_view);
		ViewfinderView viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);
		mZxingManager = new ZxingManager(this, viewfinderView, preview_view);
		mZxingManager.onCreate();
		viewfinderView.setType(ViewfinderView.TYPE_2D_CODE);
//		viewfinderView.setTips(getString(R.string.scan_qr_code_tips1) + "&" + getString(R.string
//				.scan_qr_code_tips2));
		viewfinderView.setTips(getString(R.string.scan_qr_code_tips1) + "&" + getString(R.string
				.scan_qr_code_tips3)+ "&" + getString(R.string
				.scan_qr_code_tips4)+ "&" + getString(R.string
				.scan_qr_code_tips5));
		grantUserManageAdapter = new GrantUserManageAdapter(self, new ArrayList<User>(), pile_id);
		lv_shouquanUserManages_list.setAdapter(grantUserManageAdapter);
		getFamily(); // 获得家人信息数据源
//		showNoUserView();
		hideAllView() ;
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		shoudong_add_bt.setOnClickListener(this);
		erweima_add_bt.setOnClickListener(this);
		findViewById(R.id.btn_manual_add).setOnClickListener(this);
		findViewById(R.id.btn_qr_add).setOnClickListener(this);

		mZxingManager.setOnResultListener(new OnResultListener() {
			@Override
			public void OnResult(Result result, Bitmap barcode) {
				mZxingManager.onPause();
				String code = result.getText().trim();
				Log.v("zxing", code + "");

				if (code == null || code.equals("")) {
					ToastUtil.show(AuthorizedUserManageActivity.this, R.string.format_error);
					mZxingManager.onResume();
					return;
				}
				if (!code.matches("1[0-9]{10}")) {
					ToastUtil.show(AuthorizedUserManageActivity.this, R.string.format_error);
					mZxingManager.onResume();
					return;
				}

				if (!isNetworkConnected()) {
					ToastUtil.show(AuthorizedUserManageActivity.this, R.string
							.network_no_connection);
					return;
				}
				addFamily(code);
			}
		});
	}

	private void hideAllView() {
		rl_qrcode.setVisibility(View.GONE);
		rl_userlist.setVisibility(View.GONE);
		rl_no_user.setVisibility(View.GONE);
		mZxingManager.onPause();
	}

	private void showQrcodeView() {
		hideAllView();
		setTitle(R.string.qr_code_add_user);
		rl_qrcode.setVisibility(View.VISIBLE);
		mZxingManager.onResume();
	}

	private void showNoUserView() {
		hideAllView();
		setTitle(R.string.authorized_management);
		rl_no_user.setVisibility(View.VISIBLE);
	}

	private void showUserListView() {
		hideAllView();
		setTitle(R.string.authorized_management);
		rl_userlist.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (rl_qrcode.getVisibility() == View.VISIBLE) {
			mZxingManager.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (rl_qrcode.getVisibility() == View.VISIBLE) {
			mZxingManager.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mZxingManager.onDestroy();
	}

	@Override
	protected void onLeftBtn(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (rl_qrcode.getVisibility() == View.VISIBLE) {
			if (grantUserManageAdapter.getCount() == 0) {
				showNoUserView();
			} else {
				showUserListView();
			}
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("size", grantUserManageAdapter.getCount() + "");
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
			// 手动添加用户
			case R.id.shoudong_add_bt:
			case R.id.btn_manual_add:
				manualAddFamily();
				break;
			// 二维码添加用户
			case R.id.btn_qr_add:
			case R.id.erweima_add_bt:
				showQrcodeView();
				break;
			default:
				break;
		}

	}

	private void manualAddFamily() {
		Intent manualAddIntent = new Intent(this, AddUserManualActivity.class);
		manualAddIntent.putExtra("pile_id", pile_id);
		startActivityForResult(manualAddIntent, 2);
		this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		if (arg1 != RESULT_OK) {
			return;
		}

		//		User user = (User) data.getSerializableExtra("user");
		//		if (user != null) {
		//			grantUserManageAdapter.add(user);
		//			showUserListView();
		getFamily();
		//		}

	}

	// 获取登录用户拥有的key
	//	private WebResponseHandler<List<Key>> keyHandler = new WebResponseHandler<List<Key>>
	//			("KeyHandler") {
	//
	//		@Override
	//		public void onFailure(WebResponse<List<Key>> response) {
	//			super.onFailure(response);
	//		}
	//
	//		@Override
	//		public void onSuccess(WebResponse<List<Key>> response) {
	//			super.onSuccess(response);
	//			DbHelper.getInstance(self).getKeyDao().deleteAll();
	//			List<Key> keys = response.getResultObj();
	//			if (EmptyUtil.isEmpty(keys)) {
	//				return;
	//			}
	//			// 保存到数据库
	//			DbHelper.getInstance(self).getKeyDao().insertInTx(keys);
	//			grantUserManageAdapter.add(user);
	//
	//		}
	//
	//	};

	private void addFamily(String phone) {
		loadingDialog = new LoadingDialog(self,R.string.loading) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().addFamily(pile_id, phone, new WebResponseHandler<User>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
				TipsUtil.showTips(self, e);
				mZxingManager.onResume();
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);
				loadingDialog.dismiss();

				TipsUtil.showTips(self, response);
				mZxingManager.onResume();
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				ToastUtil.show(AuthorizedUserManageActivity.this, "添加成功");
				if (response.getResultObj() != null) {
					//							user = response.getResultObj();
					//							WebAPIManager.getInstance().getKey(currentUser
					// .getPhone(), 1,
					//									keyHandler);
					pile = DbHelper.getInstance(AuthorizedUserManageActivity.this).getPileDao()
							.load(pile_id);
					pile.setFamilyNumber(pile.getFamilyNumber() + 1);
					DbHelper.getInstance(self).insertPile(pile);
					showUserListView();
					getFamily();
				} else {
					mZxingManager.onResume();
				}
			}

		});
	}

	class GrantUserManageAdapter extends EasyAdapter<User> {

		private final static String TIME_FORMAT = "yyyy/MM/dd";

		public GrantUserManageAdapter(Context context, List<User> users, String pile_id) {
			super(context, users);

		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				private TextView shouquan_usertime;
				private TextView shouquan_userno;
				private TextView shouquan_username;
				private ImageView imageView_delete;

				@Override
				protected View init(android.view.LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.authority_user_manage_item_layout, null);
					shouquan_usertime = (TextView) v.findViewById(R.id.shouquan_usertime);
					shouquan_userno = (TextView) v.findViewById(R.id.shouquan_userno);
					shouquan_username = (TextView) v.findViewById(R.id.shouquan_username);
					imageView_delete = (ImageView) v.findViewById(R.id.imageView_delete);
					return v;
				}

				@Override
				protected void update() {
					final User user = get(position);
					Long time = user.getCreateTime();
					//					List<Key> keys = DbHelper.getInstance(context).getKeyDao()
					//							.loadAll();
					//					// 过滤家人
					//					for (Iterator<Key> iterator = keys.iterator(); iterator
					//							.hasNext();) {
					//						Key key = iterator.next();
					//						if (key.getUserId().equals(user.getUserid())) {
					//							time = key.getAddTime();
					//							break;
					//						}
					//					}
					shouquan_usertime.setText("添加于 " + TimeUtil.format(time, TIME_FORMAT));
					shouquan_userno.setText(user.getPhone());
					shouquan_username.setText(user.getName());
					imageView_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							loadingDialog = new LoadingDialog(self,R.string.loading) ;
							loadingDialog.showDialog();
							WebAPIManager.getInstance().removeFamily(pile_id, user.getPhone(), new
									WebResponseHandler<User>(context) {

								@Override
								public void onError(Throwable e) {
									super.onError(e);
									loadingDialog.dismiss();
									TipsUtil.showTips(self, e);
								}

								@Override
								public void onFailure(WebResponse<User> response) {
									super.onFailure(response);
									loadingDialog.dismiss();
									TipsUtil.showTips(self, response);
								}

								@Override
								public void onSuccess(WebResponse<User> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();
									remove(user);
									pile = DbHelper.getInstance(AuthorizedUserManageActivity.this)
											.getPileDao().load(pile_id);
									pile.setFamilyNumber(pile.getFamilyNumber() - 1);
									DbHelper.getInstance(self).insertPile(pile);
									TipsUtil.showTips(self, response, "删除成功");
									if (getCount() == 0) {
										showNoUserView();
									}
								}
							});

						}
					});
				}
			};
		}

	}

}
