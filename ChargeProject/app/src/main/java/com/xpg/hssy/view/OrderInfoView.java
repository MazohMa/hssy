package com.xpg.hssy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;

public class OrderInfoView extends RelativeLayout {

	private TextView tv_order_number;
	private TextView tv_big_title;
	private TextView tv_order_status_message;
	private LinearLayout ll_order_info;

	private EvaluateColumn evaluateColumn;

	private Button btn_left;
	private Button btn_right;
	private ChargeOrder order;
	private TextView tv_order_info_date;
	private TextView tv_right;
	private LinearLayout check_box_layout;
	private CheckBox choice_edit;

	public OrderInfoView(Context context) {
		this(context, null, 0);
	}

	public OrderInfoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public OrderInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public OrderInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.order_info_view, this, true);
		tv_order_number = (TextView) findViewById(R.id.tv_order_number);
		tv_big_title = (TextView) findViewById(R.id.tv_big_title);
		tv_order_status_message = (TextView) findViewById(R.id.tv_order_status_message);
		ll_order_info = (LinearLayout) findViewById(R.id.ll_order_info);
		evaluateColumn = (EvaluateColumn) findViewById(R.id.eva_star_point);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		tv_order_info_date = (TextView) findViewById(R.id.tv_order_info_date);
		tv_right = (TextView) findViewById(R.id.tv_right);
		check_box_layout = (LinearLayout) findViewById(R.id.check_box_layout);
		choice_edit = (CheckBox) findViewById(R.id.choice_edit);
	}

	public void setOrderNumber(String number) {
		tv_order_number.setText(number);
	}

	public void setDateVisibity(int visibility) {
		tv_order_info_date.setVisibility(visibility);
	}

	public void setNoComment(int visibility) {
		tv_right.setVisibility(visibility);
		evaluateColumn.setVisibility(View.GONE);
	}

	public void setDate(String date) {
		tv_order_info_date.setText(date);
	}

	public void setOrderStatusMessage(String statusMessage) {
		setOrderStatusMessage(Color.GRAY, statusMessage);
	}

	public void setOrderStatusMessage(int color, String statusMessage) {
		tv_order_status_message.setTextColor(color);
		tv_order_status_message.setText(statusMessage);
	}

	public void setBigTitle(String userName) {
		tv_big_title.setText(userName);
	}

	public void addOrderInfo(View view) {
		ll_order_info.addView(view);
	}

	public void clearOrderInfo() {
		ll_order_info.removeAllViews();
	}

	public void showEvaluateStar(boolean isShow) {
		if (isShow) {
			evaluateColumn.setVisibility(View.VISIBLE);
			btn_left.setVisibility(View.GONE);
			btn_right.setVisibility(View.GONE);
			tv_right.setVisibility(View.GONE);
		} else {
			evaluateColumn.setVisibility(View.GONE);
		}
	}

	public void showChoiceEdit(boolean isShow) {
		if (isShow) {
			check_box_layout.setVisibility(VISIBLE);
		} else {
			check_box_layout.setVisibility(GONE);
		}
	}

	public void showLeftButton(boolean isShow) {
		if (isShow) {
			evaluateColumn.setVisibility(View.GONE);
			btn_left.setVisibility(View.VISIBLE);
		} else {
			btn_left.setVisibility(View.GONE);
		}
	}

	public void showRightButton(boolean isShow) {
		if (isShow) {
			evaluateColumn.setVisibility(View.GONE);
			btn_right.setVisibility(View.VISIBLE);
		} else {
			btn_right.setVisibility(View.GONE);
		}
	}

	public void setChecked(boolean checked) {
		choice_edit.setChecked(checked);
	}

	public void setChoiceEdit(CompoundButton.OnCheckedChangeListener listener) {
		choice_edit.setOnCheckedChangeListener(listener);
	}

	public void setLeftButton(int backgroundColor, String text, View.OnClickListener listener) {
		setLeftButton(backgroundColor, text, Color.WHITE, listener);
	}

	public void setLeftButton(int backgroundResource, String text, int textColor, View.OnClickListener listener) {
		btn_left.setOnClickListener(listener);
		btn_left.setBackgroundResource(backgroundResource);
		btn_left.setText(text);
		btn_left.setTextColor(textColor);
	}

	public void setRightButton(int backgroundColor, String text, View.OnClickListener listener) {
		setRightButton(backgroundColor, text, Color.WHITE, listener);
	}

	public void setRightButton(int backgroundResource, String text, int textColor, View.OnClickListener listener) {
		btn_right.setOnClickListener(listener);
		btn_right.setBackgroundResource(backgroundResource);
		btn_right.setText(text);
		btn_right.setTextColor(textColor);
	}

	public void setEvaluate(double eva) {
		evaluateColumn.setEvaluate(eva);
	}

	public ChargeOrder getOrder() {
		return order;
	}

	public void setOrder(ChargeOrder order) {
		this.order = order;
	}

	public static View createTextItem(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_text, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_name.setText(data_name);
		tv_data.setText(data);
		if (data_name.equals("充电费用") || data_name.equals("已付费用") || data_name.equals("总\t费\t用")) {
			tv_data.setTextColor(view.getContext().getResources().getColor(R.color.red));
		}
		return view;
	}

	public static View createTextItemForOrderManage(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_text, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_name.setText(data_name);
		tv_data.setText(data);
		if (data_name.equals("充电费用") || data_name.equals("已付费用") || data_name.equals("总 费 用")) {
			tv_data.setTextColor(view.getContext().getResources().getColor(R.color.red));
		}
		return view;
	}

	public static View createTextLine(LayoutInflater mInflater) {
		View view = mInflater.inflate(R.layout.line_gray, null);
		return view;
	}

	public static View createTextItemForRecord(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_text, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);
		tv_tip.setVisibility(View.VISIBLE);
		tv_tip.setText("未付款");
		tv_name.setText(data_name);
		tv_data.setText(data);
		return view;
	}

	public static View createTextItemMid(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_text_mid, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_name.setText(data_name);
		tv_data.setText(data);
		if (data_name.equals("充电费用") || data_name.equals("已付费用")) {
			tv_data.setTextColor(view.getContext().getResources().getColor(R.color.red));
		}
		return view;
	}

	public static View createTextItemTitle(LayoutInflater mInflater, String data_name) {
		View view = mInflater.inflate(R.layout.order_info_item_text_title, null);
		TextView tv_order_info_item_title = (TextView) view.findViewById(R.id.tv_order_info_item_title);
		tv_order_info_item_title.setText(data_name);
		return view;
	}

	public static View createTextItemBill(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_bill, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_name.setText(data_name);
		tv_data.setText(data);
		return view;
	}

	public static View createTextItemBillForPileOwner(LayoutInflater mInflater, String data_name, String data) {
		View view = mInflater.inflate(R.layout.order_info_item_bill_for_owner, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_data_name);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_name.setText(data_name);
		tv_data.setText(data);
		return view;
	}

	public static View createLine(LayoutInflater mInflater) {
		View view = mInflater.inflate(R.layout.line_gray, null);
		return view;
	}

	public static View createChargeDetailItem(LayoutInflater mInflater, int index, String... arg) {
		ArrayList<TextView> dataViews = new ArrayList<>();
		View view = mInflater.inflate(R.layout.order_info_item_charge_detail, null);
		TextView tv_charge_record_number = (TextView) view.findViewById(R.id.tv_charge_record_number);
		TextView tv_charge_time_data = (TextView) view.findViewById(R.id.tv_charge_time_data);
		TextView tv_charge_power_data = (TextView) view.findViewById(R.id.tv_charge_power_data);
		TextView tv_charge_bill_data = (TextView) view.findViewById(R.id.tv_charge_bill_data);
		TextView tv_pay_time_data = (TextView) view.findViewById(R.id.tv_pay_time_data);
		TextView tv_charge_bill_tip = (TextView) view.findViewById(R.id.tv_charge_bill_tip);
		tv_charge_bill_tip.setVisibility(View.VISIBLE);
		tv_charge_bill_tip.setText(R.string.status_wait_to_play);
		dataViews.add(tv_charge_time_data);
		dataViews.add(tv_charge_power_data);
		dataViews.add(tv_charge_bill_data);
		dataViews.add(tv_pay_time_data);

		LinearLayout ll_pay_time = (LinearLayout) view.findViewById(R.id.ll_pay_time);
		tv_charge_record_number.setText(index + "");

		for (int i = 0; i < arg.length; i++) {
			dataViews.get(i).setText(arg[i]);
		}
		if (arg.length < dataViews.size()) {
			ll_pay_time.setVisibility(View.GONE);
		}
		return view;
	}

	public static View createChargeDetailItemForTanent(LayoutInflater mInflater, int index, String... arg) {
		ArrayList<TextView> dataViews = new ArrayList<>();
		View view = mInflater.inflate(R.layout.order_info_item_charge_detail, null);
		TextView tv_charge_record_number = (TextView) view.findViewById(R.id.tv_charge_record_number);
		TextView tv_charge_time_data = (TextView) view.findViewById(R.id.tv_charge_time_data);
		TextView tv_charge_power_data = (TextView) view.findViewById(R.id.tv_charge_power_data);
		TextView tv_charge_bill_data = (TextView) view.findViewById(R.id.tv_charge_bill_data);
		TextView tv_pay_time_data = (TextView) view.findViewById(R.id.tv_pay_time_data);
		TextView tv_charge_bill_tip = (TextView) view.findViewById(R.id.tv_charge_bill_tip);
		tv_charge_bill_tip.setVisibility(View.GONE);
		dataViews.add(tv_charge_time_data);
		dataViews.add(tv_charge_power_data);
		dataViews.add(tv_charge_bill_data);
		dataViews.add(tv_pay_time_data);

		LinearLayout ll_pay_time = (LinearLayout) view.findViewById(R.id.ll_pay_time);
		tv_charge_record_number.setText(index + "");

		for (int i = 0; i < arg.length; i++) {
			dataViews.get(i).setText(arg[i]);
		}
		if (arg.length < dataViews.size()) {
			ll_pay_time.setVisibility(View.GONE);
		}
		return view;
	}

	public static View createChargeDetailItemForOrder(LayoutInflater mInflater, int index, String... arg) {
		ArrayList<TextView> dataViews = new ArrayList<>();
		View view = mInflater.inflate(R.layout.order_info_item_charge_detail_order, null);
		TextView tv_charge_record_number = (TextView) view.findViewById(R.id.tv_charge_record_number);
		TextView tv_charge_time_data = (TextView) view.findViewById(R.id.tv_charge_time_data);
		TextView tv_charge_power_data = (TextView) view.findViewById(R.id.tv_charge_power_data);
		TextView tv_charge_bill_data = (TextView) view.findViewById(R.id.tv_charge_bill_data);
		TextView tv_pay_time_data = (TextView) view.findViewById(R.id.tv_pay_time_data);
		TextView tv_charge_bill_tip = (TextView) view.findViewById(R.id.tv_charge_bill_tip);
		tv_charge_bill_tip.setVisibility(View.GONE);
		dataViews.add(tv_charge_time_data);
		dataViews.add(tv_charge_power_data);
		dataViews.add(tv_charge_bill_data);
		dataViews.add(tv_pay_time_data);

		LinearLayout ll_pay_time = (LinearLayout) view.findViewById(R.id.ll_pay_time);
		tv_charge_record_number.setText(index + "");

		for (int i = 0; i < arg.length; i++) {
			dataViews.get(i).setText(arg[i]);
		}
		if (arg.length < dataViews.size()) {
			ll_pay_time.setVisibility(View.GONE);
		}
		return view;
	}

	public static View createChargeDetailItemForTanentRecord(LayoutInflater mInflater, int index, String... arg) {
		ArrayList<TextView> dataViews = new ArrayList<>();
		View view = mInflater.inflate(R.layout.order_info_item_charge_detail, null);
		TextView tv_charge_record_number = (TextView) view.findViewById(R.id.tv_charge_record_number);
		TextView tv_charge_time_data = (TextView) view.findViewById(R.id.tv_charge_time_data);
		TextView tv_charge_power_data = (TextView) view.findViewById(R.id.tv_charge_power_data);
		TextView tv_charge_bill_data = (TextView) view.findViewById(R.id.tv_charge_bill_data);
		TextView tv_pay_time_data = (TextView) view.findViewById(R.id.tv_pay_time_data);
		TextView tv_charge_bill_tip = (TextView) view.findViewById(R.id.tv_charge_bill_tip);
		tv_charge_bill_tip.setVisibility(View.VISIBLE);
		tv_charge_bill_tip.setText("待付款");

		dataViews.add(tv_charge_time_data);
		dataViews.add(tv_charge_power_data);
		dataViews.add(tv_charge_bill_data);
		dataViews.add(tv_pay_time_data);

		LinearLayout ll_pay_time = (LinearLayout) view.findViewById(R.id.ll_pay_time);
		tv_charge_record_number.setText(index + "");

		for (int i = 0; i < arg.length; i++) {
			dataViews.get(i).setText(arg[i]);
		}
		if (arg.length < dataViews.size()) {
			ll_pay_time.setVisibility(View.GONE);
		}
		return view;
	}

	public static View createDetailItemTop(boolean isUser, Context context, String... arg) {
		ArrayList<TextView> dataViews = new ArrayList<>();
		if (isUser) {
			View view = LayoutInflater.from(context).inflate(R.layout.orderdetailtopfortanent, null);
			TextView tv_pile_location = (TextView) view.findViewById(R.id.tv_pile_location);
			TextView tv_tel_num = (TextView) view.findViewById(R.id.tv_tel_num);
			TextView tv_pile_name = (TextView) view.findViewById(R.id.tv_pile_name);
			dataViews.add(tv_pile_name);
			dataViews.add(tv_tel_num);
			dataViews.add(tv_pile_location);
			for (int i = 0; i < arg.length; i++) {
				dataViews.get(i).setText(arg[i]);
			}
			return view;
		} else {
			View view = LayoutInflater.from(context).inflate(R.layout.orderdetailtop, null);
			TextView tv_pile_name = (TextView) view.findViewById(R.id.tv_pile_name);
			dataViews.add(tv_pile_name);
			dataViews.add(new TextView(context));
			for (int i = 0; i < arg.length; i++) {
				if (dataViews.get(i) != null) {
					dataViews.get(i).setText(arg[i]);
				}
			}
			return view;
		}
	}

	public static View createCommand(LayoutInflater mInflater, String title, int avgLevel, String content) {
		View view = mInflater.inflate(R.layout.orderdetailcommand, null);
		TextView command_title = (TextView) view.findViewById(R.id.command_title);
		EvaluateColumn eva_star_point1 = (EvaluateColumn) view.findViewById(R.id.eva_star_point);
		TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
		command_title.setText(title + "");
		eva_star_point1.setEvaluate(avgLevel);
		Log.i("tag content", content + "");
		tv_content.setText(content + "");
		return view;
	}
}
