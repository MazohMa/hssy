package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.manager.EasyActivityManager;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class OrderAddSuccessActivity extends BaseActivity {
	TextView tv_order_num;
	TextView tv_pile_name;
	TextView tv_pile_location;
	TextView tv_book_time;
	TextView tv_pile_price;
	ImageView iv_collect_heart;

	SPFile sp;
	Pile pile;
	ChargeOrder order;
	private boolean isCollectedPile = false;


	private final static String TIME_FORMAT_YMDHM = "yyyy/MM/dd HH:mm";
	private final static String TIME_FORMAT_HM = "HH:mm";
	private final static String TIME_FORMAT_KM = "kk:mm";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = new SPFile(this, "config");
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_order_add_success_new);
		setTitle(R.string.title_order_add_success);
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		tv_pile_name = (TextView) findViewById(R.id.tv_pile_name);
		tv_pile_location = (TextView) findViewById(R.id.tv_pile_location);
		tv_book_time = (TextView) findViewById(R.id.tv_book_time);
		tv_pile_price = (TextView) findViewById(R.id.tv_pile_price);
		iv_collect_heart = (ImageView) findViewById(R.id.iv_collect_heart);

		Intent bookingMessage = getIntent();
		pile = (Pile) bookingMessage.getSerializableExtra(KEY.INTENT.PILE);
		order = (ChargeOrder) bookingMessage.getSerializableExtra(KEY.INTENT.CHARGE_ORDER);
		isCollectedPile = getIntent().getBooleanExtra(KEY.INTENT.IS_PILE_COLLECTED, false);

		iv_collect_heart.setSelected(isCollectedPile);
		tv_order_num.setText(order.getOrderId());
		tv_pile_name.setText(pile.getPileName());
		tv_pile_location.setText(pile.getLocation());
		tv_book_time.setText(createTimeString());
//		tv_pile_price.setText(getResources().getString(R.string.rmb_symbol) + " " + pile.getPrice() + getResources().getString(R.string.charge_unit));
		CalculateUtil.infusePrice(tv_pile_price, pile.getPrice());
	}

	@NonNull
	private String createTimeString() {
		String startTime = TimeUtil.format(order.getStartTime(), TIME_FORMAT_YMDHM);
		String endTime = TimeUtil.format(order.getEndTime(), TIME_FORMAT_HM);
		endTime = endTime.equals("00:00") ? "24:00" : endTime;
		return startTime + "-" + endTime;
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		findViewById(R.id.btn_check_order).setOnClickListener(this);
		iv_collect_heart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.btn_check_order:
				EasyActivityManager.getInstance().finish(PileInfoNewActivity.class);
				startActivity(MyOrderFragmentActivity.class);
				finish();
				break;
			case R.id.iv_collect_heart: {
				if (iv_collect_heart.isSelected()) {
					cancelCollect();
				} else {
					collect();
				}
				break;
			}
		}
	}

	private void collect() {
		if (pile == null) {
			return;
		}

		String userid = sp.getString("user_id", null);
		if (userid == null) {
			Intent intentLogin = new Intent(this, LoginActivity.class);
			startActivity(intentLogin);
			return;
		}

		WebAPIManager.getInstance().addFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(self) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(self, R.string.collect_fail);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				TipsUtil.showLoinAgain(OrderAddSuccessActivity.this, response);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(self, R.string.collect_success);
				iv_collect_heart.setSelected(true);
				isCollectedPile = true;
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void cancelCollect() {
		if (pile == null) {
			return;
		}

		String userid = sp.getString("user_id", null);
		if (userid == null) {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.putExtra("isMainActivity", false);
			startActivityForResult(loginIntent, 9);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			return;
		}

		WebAPIManager.getInstance().removeFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(self) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				TipsUtil.showLoinAgain(OrderAddSuccessActivity.this, response);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(self, R.string.cancel_collect_success);
				iv_collect_heart.setSelected(false);
				isCollectedPile = false;
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}
}
