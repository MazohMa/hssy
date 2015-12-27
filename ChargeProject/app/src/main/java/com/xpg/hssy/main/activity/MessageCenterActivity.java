package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.xpg.hssy.adapter.MessageAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.Message;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.popwindow.MessageShowListPop;
import com.xpg.hssy.popwindow.MessageShowListPop.ItemOnClick;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.DropDownListView;
import com.xpg.hssy.view.DropDownListView.OnDropDownListener;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

public class MessageCenterActivity extends BaseActivity implements OnDropDownListener, OnItemClickListener {

	private static final int RESULT_FINISH_MYSELF = 1;
	private static final int UNREAD = 0;
	private static final int READED = 1;
	private static final int ALL = 2;
	private static final int CLEARALL = 3;

	private boolean refreshing;
	private MessageAdapter messageAdapter;
	private DropDownListView mDropDownListView;
	private TextView mTitle;
	private SharedPreferences sp;
	private String user_id;
	private LinearLayout no_message_layout;
	private boolean isFirstIntoMeCenter;
	private ImageButton btn_right;
	private ImageButton btn_left;
	private MessageShowListPop messageShowListPop;
	private int unReadMessageNum;

	private int status = 2;
	private int flag = 1;
	private LoadingDialog loadingDialog = null;

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	protected void onRightBtn(View v) {
		super.onRightBtn(v);
		btn_right.getWidth();
		messageShowListPop.showAsDropDown(btn_right, -2 * (btn_right.getWidth()), 0);
	}

	@SuppressLint("UseValueOf")
	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString(KEY.CONFIG.USER_ID, "");
		Intent intent = getIntent();
		unReadMessageNum = intent.getIntExtra(KEY.INTENT.UNREAD_MESSAGE_NUMBER, 0);
		LogUtils.e("MessageCenterActivity", "count:" + unReadMessageNum);
		isFirstIntoMeCenter = intent.getBooleanExtra(KEY.INTENT.IS_FIRST_INTO_ME_CENTER, false);
		messageShowListPop = new MessageShowListPop(this);
		messageShowListPop.setTextNum(unReadMessageNum + "");
		messageShowListPop.setWidth(LayoutParams.MATCH_PARENT);
		messageShowListPop.setHeight(LayoutParams.MATCH_PARENT);

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.message_center_activity);
		messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
		mDropDownListView = (DropDownListView) findViewById(R.id.message_listview);
		no_message_layout = (LinearLayout) findViewById(R.id.no_message_layout);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setImageResource(R.drawable.xiaoxiicon);
		btn_left.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.tv_center);
		mTitle.setText("消息列表");
		mDropDownListView.setAdapter(messageAdapter);
		mDropDownListView.setOnBottomStyle(false);
		mDropDownListView.setShowFooterWhenNoMore(true);

		refreshMessage();
	}

	@Override
	protected void initEvent() {
		super.initEvent();

		// 加载列表
		OnClickListener onBottomListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMoreMessage();
			}
		};

		mDropDownListView.setOnDropDownListener(this);
		mDropDownListView.setOnBottomListener(onBottomListener);
		mDropDownListView.setOnItemClickListener(this);
		messageShowListPop.setItemOnClick(new ItemOnClick() {

			@Override
			public void click(int index) {
				switch (index) {
					case UNREAD://未读
						status = UNREAD;
						refreshMessage();
						break;
					case READED://已读
						status = READED;
						refreshMessage();
						break;
					case ALL://显示所有
						status = ALL;
						refreshMessage();
						break;
					case CLEARALL://清空所有
						clearMessage();
						break;

					default:
						break;
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Message message = messageAdapter.get(position - 1);
		if (message != null) {
			switch (message.getType()) {
				case Message.MESSAGE_TYPE_ORDER: {
					if (message.getTypeId() != null) {
						Intent intent = new Intent(this, MyOrderDetailActivity.class);
						intent.putExtra("orderId", message.getTypeId());
						startActivityForResult(intent, MainActivity.REQUEST_FINISH);
						MessageCenterActivity.this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
					}
					break;
				}
				case Message.MESSAGE_TYPE_CHARGE_START: {
					//不做处理
					break;
				}
				case Message.MESSAGE_TYPE_CHARGE_TIME_LINE: {
					if (message.getTypeId() != null) {
						Intent intent = new Intent(this, TopicDetailActivity.class);
						intent.putExtra("articleId", message.getTypeId());
						startActivity(intent);
						MessageCenterActivity.this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
					}
					//不做处理
					break;
				}
				default: {
					break;
				}
			}
			if (message.getStatus() == Message.UNREAD) {
				setMessageStatus(message);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		messageShowListPop.setTextNum(unReadMessageNum + "");
	}

	private void setMessageStatus(final Message message) {

		WebAPIManager.getInstance().setMessageStatus(message.getId(), Message.HAVE_READ, new WebResponseHandler<Object>() {
			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				message.setStatus(Message.HAVE_READ);
				messageAdapter.notifyDataSetChanged();
				unReadMessageNum--;
				messageShowListPop.setTextNum(unReadMessageNum + "");
			}
		});
	}

	@Override
	public void onDropDown() {
		refreshMessage();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_FINISH_MYSELF) {
			finish();
		} else {
		}
		if (resultCode == RESULT_OK) {
			if (data == null) {
				return;
			}
			setResult(RESULT_OK, data);
			finish();
		}
	}

	private void clearMessage() {
		if (refreshing) {
			return;
		}
		refreshing = true;
		WebAPIManager.getInstance().clearMessages(user_id, flag, new WebResponseHandler<Integer>(this) {

			@Override
			public void onStart() {
				super.onStart();
				mDropDownListView.setHeaderStatusLoading();
				if (isFirstIntoMeCenter) {
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				mDropDownListView.setHeaderStatusClickToLoad();
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<Integer> response) {
				super.onFailure(response);
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				mDropDownListView.setHeaderStatusClickToLoad();
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<Integer> response) {
				super.onSuccess(response);
				mDropDownListView.onDropDownComplete();
				messageAdapter.clear();
				messageAdapter.notifyDataSetChanged();
				unReadMessageNum = 0;
				messageShowListPop.setTextNum("0");
				no_message_layout.setVisibility(View.VISIBLE);
				mDropDownListView.setVisibility(View.GONE);
				sendBroadcast(new Intent(KEY.ACTION.ACTION_CLEAR_UMREAD_MUNBER));
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				refreshing = false;
			}
		});

	}

	private void refreshMessage() {
		if (refreshing) {
			return;
		}
		refreshing = true;
		WebAPIManager.getInstance().getMessages(user_id, 0, MyConstant.PAGE_SIZE, status, new WebResponseHandler<List<Message>>(this) {

			@Override
			public void onStart() {
				super.onStart();
				mDropDownListView.setHeaderStatusLoading();
				if (isFirstIntoMeCenter) {
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				mDropDownListView.setHeaderStatusClickToLoad();
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<List<Message>> response) {
				super.onFailure(response);
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				mDropDownListView.setHeaderStatusClickToLoad();
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<List<Message>> response) {
				super.onSuccess(response);
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				mDropDownListView.onDropDownComplete();
				List<Message> messages = response.getResultObj();
				if (!EmptyUtil.isEmpty(messages)) {
					messageAdapter.clear();
					messageAdapter.add(messages);
				}
				mDropDownListView.setOnBottomStyle(true);
				if (messageAdapter.getCount() < MyConstant.PAGE_SIZE) {
					mDropDownListView.setHasMore(false);
					mDropDownListView.setAutoLoadOnBottom(false);
				} else {
					mDropDownListView.setHasMore(true);
					mDropDownListView.setAutoLoadOnBottom(true);
				}
				mDropDownListView.onBottomComplete();

				if (!EmptyUtil.isEmpty(messages)) {
					no_message_layout.setVisibility(View.GONE);
					mDropDownListView.setVisibility(View.VISIBLE);
				} else {
					no_message_layout.setVisibility(View.VISIBLE);
					mDropDownListView.setVisibility(View.GONE);

				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (isFirstIntoMeCenter) {
					loadingDialog.dismiss();
					isFirstIntoMeCenter = false;
				}
				refreshing = false;
			}
		});

	}

	private void loadMoreMessage() {
		if (refreshing) {
			return;
		}
		refreshing = true;
		WebAPIManager.getInstance().getMessages(user_id, messageAdapter.getCount(), MyConstant.PAGE_SIZE, status, new WebResponseHandler<List<Message>>(this) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				mDropDownListView.setAutoLoadOnBottom(false);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<List<Message>> response) {
				super.onFailure(response);
				mDropDownListView.setAutoLoadOnBottom(false);
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<List<Message>> response) {
				super.onSuccess(response);
				List<Message> messages = response.getResultObj();
				if (!EmptyUtil.isEmpty(messages)) {
					messageAdapter.add(messages);
				}
				if (messages == null || messages.size() < MyConstant.PAGE_SIZE) {
					mDropDownListView.setHasMore(false);
					mDropDownListView.setAutoLoadOnBottom(false);
				} else {
					mDropDownListView.setHasMore(true);
					mDropDownListView.setAutoLoadOnBottom(true);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mDropDownListView.onBottomComplete();
				refreshing = false;
			}
		});

	}
}
