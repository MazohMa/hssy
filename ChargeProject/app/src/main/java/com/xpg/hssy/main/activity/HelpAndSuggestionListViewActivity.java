package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.xpg.hssy.adapter.HelpAndSuggestionAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.bean.HelpAndSuggestion;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * HelpAndSuggestionListViewActivity
 * 
 * @author Mazoh 帮助与反馈
 * 
 */
public class HelpAndSuggestionListViewActivity extends BaseActivity implements
		OnClickListener {
	private List<HelpAndSuggestion> helpAndSuggestions = new ArrayList<HelpAndSuggestion>();
	private ListView lv;
	private HelpAndSuggestionAdapter helpAndSuggestionAdapter;
	private int index;

	@Override
	protected void initData() {
		super.initData();
		helpAndSuggestions
				.add(new HelpAndSuggestion("1. 为什么我收不到验证手机号码的短信？",
						"\t\t\t网络通讯出现异常情况可能会造成丢失或延时收到，需要您耐心等待，如果您始终无法收到，您可以致电客服咨询。"));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"2. App上显示的充电价格是什么费用？",
						"\t\t\tApp上显示的充电价格是由“电费+服务费”组成，也是充电电量结算单价，价格设置权归该充电点的业主所有。如在预约过程中遇到价格变化，以预约订单中的充电价格为结算依据。"));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"3. 在哪里可以快速找到充电点？",
						"\t\t\t您可以通过“寻找电桩”的“地图/列表”页面上通过筛选来寻找您要的充电点，点击按钮“导航”即可跳转导航应用来为您导航路线。"));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"4. 为什么我不能同时预约两个电桩？",
						"\t\t\t目前一个用户在8小时内只能提交一个预约订单，若想新增/重新预约，需把前一订单取消或完成支付才能预约。"));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"5. 充电连接方式有哪些？",
						"\t\t\t目前有三种类型充电桩，通过优易充可以实现扫码充电、蓝牙连接充电两种方式，用户可根据现场的充电桩来选择何种方式启动。\n" +
								"\t\t\t扫码充电：点击“充电-扫码充电”页面，扫描充电桩上的二维码，获得充电桩当前信息，如状态是空闲的，即可启动充电。\n" +
								"\t\t\t蓝牙充电：点击“充电-蓝牙充电”页面，如您已在App上通过预约了充电桩，可在预约充电时段内通过蓝牙连接充电桩来启动充电。"));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"6. 我是否只能在预约的充电时段内连接充电桩启动充电？",
						"\t\t\t如您预约的是蓝牙连接启动的充电桩，您可以在预约的充电时段内连接已授权的充电桩去启动或停止充电；当时段过后，如您有没有停止充电的订单，您可以在预约的充电时段后3天内连接该订单的充电桩进行停止。"
				));
		helpAndSuggestions
				.add(new HelpAndSuggestion(
						"7. 为什么我充电完成后订单状态没有改变？",
						"\t\t\t如您预约的是蓝牙连接启动的充电桩，它会存在因没有网络导致不能上传记录至云端的情况，当您充电完成，并预约充电时段过后，云端没有同步到桩的充电记录，您的订单状态需等待业主连接充电桩并把记录上传才会改变状态，如您有疑问可致电客户咨询或与自行业主沟通。"
				));
		helpAndSuggestionAdapter = new HelpAndSuggestionAdapter(this,
				helpAndSuggestions);
		index = getIntent().getIntExtra("index", 0);

	}

	@Override
	protected void initEvent() {
		super.initEvent();

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.help_suggestion_list);
		setTitle("常见问题");
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(helpAndSuggestionAdapter);
		lv.setSelection(index);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;

		}
	}

}
