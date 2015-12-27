package com.xpg.hssy.main.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.BadgeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialog;
import com.xpg.hssy.dialog.WaterBlueDialogVersion;
import com.xpg.hssy.main.activity.AboutAppActivity;
import com.xpg.hssy.main.activity.AddPileOrSiteActivity;
import com.xpg.hssy.main.activity.HelpAndSuggestionActivity;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.main.activity.MessageCenterActivity;
import com.xpg.hssy.main.activity.MyCollectionNewActivity;
import com.xpg.hssy.main.activity.MyIntroductionMessageActivity;
import com.xpg.hssy.main.activity.MyOrderFragmentActivity;
import com.xpg.hssy.main.activity.MyWalletActivity;
import com.xpg.hssy.main.activity.PileManageActivity;
import com.xpg.hssy.main.activity.SpecifiedEvaluateAndMomentsActivity;
import com.xpg.hssy.main.activity.SystemSettingActivity;
import com.xpg.hssy.util.BadgeView;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class MeFragment extends BaseFragment implements OnClickListener {

	private ImageView iv_usericon;// 我的图片
	private TextView tv_username;// 用户名
	private LinearLayout ll_userInfo;
	private Button btn_message;
	private BadgeView badge_message_num;
	private SharedPreferences sp;
	private String name;
	private User user;
	private String phone;
	private ImageButton img_login_out;
	private LinearLayout badge_view;
	private int unReadMsgNum = -1;// 未读消息数量
	private DisplayImageOptions option;
	private LoadingDialog loadingDialog = null;
	private LinearLayout ll_my_evaluate;
	private LinearLayout rl_my_setting;
	private LinearLayout about_app__rl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.personal_touxiang).showImageOnFail(R.drawable.personal_touxiang).showImageOnLoading(R.drawable.personal_touxiang).displayer(new
				RoundedBitmapDisplayer((int) getResources().getDimension(R.dimen.h28))).build();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isResumed()) {
			if (!isLogin()) {
				ImageLoaderManager.getInstance().displayImage("", iv_usericon, option);
				tv_username.setText(R.string.please_login);
				badge_message_num.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void unregister2() {
		final WaterBlueDialog waterBlueDialog = new WaterBlueDialog(getActivity());
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

	private void logout2() {
		loadingDialog = new LoadingDialog(getActivity(), R.string.loading);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().logout(1, phone, new WebResponseHandler<User>(getActivity()) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				loadingDialog.dismiss();
				TipsUtil.showTips(getActivity(), e);
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);
				loadingDialog.dismiss();
				TipsUtil.showTips(getActivity(), response);
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
			}
		});
	}

	private void afterLogout() {
		Editor ed = sp.edit();
		ed.putBoolean("isLogin", false);
		ed.putString("user_id", "");
		ed.putString(MyConstant.BT_MAC_LAST, "");
		ed.commit();
		Intent login_intent = new Intent(getActivity(), LoginActivity.class);
		login_intent.putExtra("isMainActivity", true);
		getActivity().startActivity(login_intent);
		getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		BTManager.getInstance().disconnect();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.img_login_out:
				if (isLogin()) {
					unregister2();
				} else {
				}
				break;
			case R.id.ll_userInfo:
				jumpIntoAnotherActivity(MyIntroductionMessageActivity.class, true);
				break;
			case R.id.my_appoint_rl:
				if (isLogin()) {
					getActivity().startActivityForResult(new Intent(getActivity(), MyOrderFragmentActivity.class), MainActivity.REQUEST_MY_ORDER);
					animationForActivity();
				} else {
					toLoginActivity();
				}
				break;
			case R.id.collect_rl:
				jumpIntoAnotherActivity(MyCollectionNewActivity.class, true);
				break;
			case R.id.ll_my_evaluate: {
				Bundle data = new Bundle();
				data.putInt(SpecifiedEvaluateAndMomentsActivity.KEY_SPECIIED_TYPE, SpecifiedEvaluateAndMomentsActivity.SPECIIED_TYPE_PERSION);
				jumpIntoAnotherActivity(SpecifiedEvaluateAndMomentsActivity.class, data, true);
				break;
			}
			case R.id.wallet_rl:
				jumpIntoAnotherActivity(MyWalletActivity.class, true);
				break;
			case R.id.pile_manage_rl:
				jumpIntoAnotherActivity(PileManageActivity.class, true);
				break;
			case R.id.center_rl:
				if (isLogin()) {
					Intent _intent = new Intent(getActivity(), MessageCenterActivity.class);
					_intent.putExtra(KEY.INTENT.IS_FIRST_INTO_ME_CENTER, true);
					_intent.putExtra(KEY.INTENT.UNREAD_MESSAGE_NUMBER, badge_message_num.getBadgeCount() > 0 ? badge_message_num.getBadgeCount() : 0);
					getActivity().startActivityForResult(_intent, MainActivity.REQUEST_FINISH);
					animationForActivity();
				} else {
					toLoginActivity();
				}

				break;

			case R.id.btn_message:
				if (isLogin()) {
					Intent _intent = new Intent(getActivity(), MessageCenterActivity.class);
					_intent.putExtra(KEY.INTENT.IS_FIRST_INTO_ME_CENTER, true);
					_intent.putExtra(KEY.INTENT.UNREAD_MESSAGE_NUMBER, badge_message_num.getBadgeCount() > 0 ? badge_message_num.getBadgeCount() : 0);
					getActivity().startActivityForResult(_intent, MainActivity.REQUEST_FINISH);
					animationForActivity();
				} else {
					toLoginActivity();
				}
				break;

			case R.id.service_rl:// 客服
				break;
			case R.id.suggest_rl:
				jumpIntoAnotherActivity(HelpAndSuggestionActivity.class, false);
				break;
			case R.id.add_public_pile_rl:
				jumpIntoAnotherActivity(AddPileOrSiteActivity.class, true);
				break;
			case R.id.version_rl:
				new WaterBlueDialogVersion(getActivity()).show();
				break;
			case R.id.rl_my_setting: {
				Intent intent = new Intent();
				if (isLogin()) {
					intent.setClass(getActivity(), SystemSettingActivity.class);
				} else {
					intent.setClass(getActivity(), LoginActivity.class);
				}
				startActivity(intent);
				break;
			}
			case R.id.about_app__rl:
				jumpIntoAnotherActivity(AboutAppActivity.class, false);
				break;
			default:
				break;
		}
	}

	private void jumpIntoAnotherActivity(Class sClass, Bundle data, boolean needLogin) {
		if (needLogin) {
			if (isLogin()) {
				Intent _intent = new Intent(getActivity(), sClass);
				if (data != null) {
					_intent.putExtras(data);
				}
				startActivity(_intent);
				animationForActivity();
			} else {
				toLoginActivity();
			}
		} else {
			Intent _intent = new Intent(getActivity(), sClass);
			startActivity(_intent);
			animationForActivity();
		}
	}

	private void jumpIntoAnotherActivity(Class sClass, boolean needLogin) {
		jumpIntoAnotherActivity(sClass, null, needLogin);
	}

	private void animationForActivity() {
		getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	private void toLoginActivity() {
		Intent login_intent = new Intent(getActivity(), LoginActivity.class);
		startActivity(login_intent);
		animationForActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initData();
		View v = initView(inflater);
		return v;
	}

	private void initData() {
		getActivity().registerReceiver(clearUnreadNumber, new IntentFilter(KEY.ACTION.ACTION_CLEAR_UMREAD_MUNBER));
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	private View initView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.me_fragment, null);
		ll_userInfo = (LinearLayout) v.findViewById(R.id.ll_userInfo);
		iv_usericon = (ImageView) ll_userInfo.findViewById(R.id.iv_usericon);
		tv_username = (TextView) ll_userInfo.findViewById(R.id.tv_username);
		badge_view = (LinearLayout) v.findViewById(R.id.badge_view);
		btn_message = (Button) v.findViewById(R.id.btn_message);
		img_login_out = (ImageButton) v.findViewById(R.id.img_login_out);
		badge_message_num = new BadgeView(getActivity());
		badge_message_num.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
		badge_message_num.setTargetView(badge_view);
		ll_my_evaluate = (LinearLayout) v.findViewById(R.id.ll_my_evaluate);
		rl_my_setting = (LinearLayout) v.findViewById(R.id.rl_my_setting);
		about_app__rl = (LinearLayout) v.findViewById(R.id.about_app__rl);
		onEvent(v);
		return v;
	}

	private void onEvent(View v) {
		img_login_out.setOnClickListener(this);
		ll_userInfo.setOnClickListener(this);
		v.findViewById(R.id.version_rl).setOnClickListener(this);
		v.findViewById(R.id.collect_rl).setOnClickListener(this);
		v.findViewById(R.id.pile_manage_rl).setOnClickListener(this);
		v.findViewById(R.id.wallet_rl).setOnClickListener(this);
		v.findViewById(R.id.center_rl).setOnClickListener(this);
		v.findViewById(R.id.my_appoint_rl).setOnClickListener(this);
		v.findViewById(R.id.service_rl).setOnClickListener(this);
		v.findViewById(R.id.suggest_rl).setOnClickListener(this);
		v.findViewById(R.id.add_public_pile_rl).setOnClickListener(this);
		ll_my_evaluate.setOnClickListener(this);
		rl_my_setting.setOnClickListener(this);
		about_app__rl.setOnClickListener(this);
		btn_message.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadUnReadMessageNum();
	}

	public void loadUnReadMessageNum() {
		if (isLogin()) {
			String user_id = sp.getString("user_id", "");
			user = DbHelper.getInstance(getActivity()).getUserDao().load(user_id);
			name = user == null ? "" : user.getName();
			tv_username.setText(user.getName());
			String avatarUrl = user.getAvatarUrl();
			ImageLoaderManager.getInstance().displayImage(avatarUrl, iv_usericon, option, true);
			// 未读消息数量
			if (unReadMsgNum == -1) {
				getUnreadMessage(user_id);
			} else {
				setUnreadMsgNum(unReadMsgNum);
			}
		} else {
			ImageLoaderManager.getInstance().displayImage("", iv_usericon, option);
			tv_username.setText(R.string.please_login);
			badge_message_num.setVisibility(View.INVISIBLE);
			setUnreadMsgNum(0);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			getActivity().unregisterReceiver(clearUnreadNumber);
		} catch (Exception e) {
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private boolean isLogin() {
		if(sp == null)
			sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getBoolean("isLogin",false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
	}

	private void getUnreadMessage(String userId) {
		WebAPIManager.getInstance().getUnreadMessageNum(userId, new WebResponseHandler<String>(getActivity()) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(getActivity(), e);
			}

			@Override
			public void onFailure(WebResponse<String> response) {
				super.onFailure(response);
				TipsUtil.showTips(getActivity(), response);
			}

			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
				LogUtils.e("MeFragment", "response:" + response);
				try {
					setUnreadMsgNum(Integer.parseInt(response.getResult()));
				} catch (Exception e) {
					setUnreadMsgNum(0);
				}
			}

		});
	}


	private void setUnreadMsgNum(int num) {
		badge_message_num.setBadgeCount(num);
		if (num > 0) {
			badge_message_num.setVisibility(View.VISIBLE);
			badge_message_num.measure(0, 0);
			btn_message.setPadding(btn_message.getPaddingLeft(), btn_message.getPaddingTop(), badge_message_num.getMeasuredWidth(), btn_message
					.getPaddingBottom());
			if (getActivity() != null) BadgeUtil.setBadgeCount(getActivity(), num);
		} else {
			badge_message_num.setVisibility(View.INVISIBLE);
			if (getActivity() != null) BadgeUtil.resetBadgeCount(getActivity());
		}
	}

	private BroadcastReceiver clearUnreadNumber = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			unReadMsgNum = 0;
			setUnreadMsgNum(0);
		}
	};
}
