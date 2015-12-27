package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xpg.hssy.adapter.PileMessageCommandAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.Command;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * PileMessageCommandActivity 桩评价中心
 *
 * @author Mazoh
 */
public class PileOrStationAllEvaluateActivity extends BaseActivity {
	private TextView pile_name;
	private TextView point;
	private ListView id_listview_commands;
	private PileMessageCommandAdapter pileMessageCommandAdapter;
	private List<Command> commands;
	private ImageButton btn_left;
	private TextView tv_center;
	private String pilename;
	private String phone;
	private float evaluate;
	private String pileId;
	private EvaluateColumn eva_star_point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		pilename = intent.getStringExtra(KEY.INTENT.PILE_NAME);
		evaluate = intent.getFloatExtra(KEY.INTENT.PILE_EVALUATE, 5);
		commands = (List<Command>) intent.getSerializableExtra(KEY.INTENT.PILE_COMMANDS);
		pileId = intent.getStringExtra(KEY.INTENT.PILE_ID);
		pileMessageCommandAdapter = new PileMessageCommandAdapter(this);
		if (commands == null) {
			commands = new ArrayList<>();
		}
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.specified_all_evaluate, null);
		setContentView(view);
		btn_left = (ImageButton) view.findViewById(R.id.btn_left);
		tv_center = (TextView) view.findViewById(R.id.tv_center);
		pile_name = (TextView) view.findViewById(R.id.pile_name);
		point = (TextView) view.findViewById(R.id.point);
		eva_star_point = (EvaluateColumn) findViewById(R.id.eva_star_point);
		eva_star_point.setEvaluate(evaluate);
		id_listview_commands = (ListView) view.findViewById(R.id.id_listview_commands);
		pileMessageCommandAdapter.setCommands(commands);
		id_listview_commands.setAdapter(pileMessageCommandAdapter);
		tv_center.setText("所有评价");
		pile_name.setText(pilename);
		point.setText(String.format("%.1f", evaluate) + "分");
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			default:
				break;
		}
	}

}
