package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.util.SPFile;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssychargingpole.R;

/**
 * Created by black-Gizwits on 2015/09/18.
 */
public class MessageNotificationSettingActivity extends BaseActivity {
	private ImageView iv_message_push_switch;
	private ImageView iv_transaction_push_switch;
	private SPFile sp;

	private ImageButton btn_left;
	private boolean messagePush;
	private boolean transactionPush;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_message_notification_setting);
		super.onCreate(savedInstanceState);
		sp = new SPFile(this, "config");
		messagePush = sp.getBoolean(KEY.CONFIG.MESSAGE_PUSH, true);
		transactionPush = sp.getBoolean(KEY.CONFIG.TRANSACTION_PUSH, true);
		init();
	}

	private void init() {
		initView();
		initListener();
	}

	private void initView() {
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		TextView tv_center = (TextView) findViewById(R.id.tv_center);
		iv_message_push_switch = (ImageView) findViewById(R.id.iv_message_push_switch);
		iv_transaction_push_switch = (ImageView) findViewById(R.id.iv_transaction_push_switch);

		tv_center.setText(R.string.message_notification_setting);
		iv_message_push_switch.setSelected(messagePush);
		iv_transaction_push_switch.setSelected(transactionPush);
	}

	private void initListener() {
		btn_left.setOnClickListener(this);
		iv_message_push_switch.setOnClickListener(this);
		iv_transaction_push_switch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left: {
				onBackPressed();
				break;
			}
			case R.id.iv_message_push_switch: {
				messagePush = !messagePush;
				iv_message_push_switch.setSelected(messagePush);
				sp.put(KEY.CONFIG.MESSAGE_PUSH, messagePush);

				break;
			}
			case R.id.iv_transaction_push_switch: {
				transactionPush = !transactionPush;
				iv_transaction_push_switch.setSelected(transactionPush);
				sp.put(KEY.CONFIG.TRANSACTION_PUSH, transactionPush);
				break;
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
