package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easy.util.IntentUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssychargingpole.R;

/**
 * HelpAndSuggestionActivity
 * 
 * @author Mazoh 帮助与反馈
 * 
 */
public class HelpAndSuggestionActivity extends BaseActivity implements
		OnClickListener {

	private ImageView call_image;
	private RelativeLayout first_rl, second_rl, third_rl, fouth_rl, fifth_rl,
			sixth_rl,seventh_rl,eighth_rl;
	private String phone = "4006556620";

	@Override
	protected void initData() {
		super.initData();

	}

	
	@Override
	protected void initEvent() {
		super.initEvent();
		call_image.setOnClickListener(this);
		first_rl.setOnClickListener(this);
		second_rl.setOnClickListener(this);
		third_rl.setOnClickListener(this);
		fouth_rl.setOnClickListener(this);
		fifth_rl.setOnClickListener(this);
		sixth_rl.setOnClickListener(this);
		seventh_rl.setOnClickListener(this);
		eighth_rl.setOnClickListener(this);

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.help_suggestion_layout);
		setTitle("帮助与反馈");
		call_image = (ImageView) findViewById(R.id.call_image);
		first_rl = (RelativeLayout) findViewById(R.id.first_rl);
		second_rl = (RelativeLayout) findViewById(R.id.second_rl);
		third_rl = (RelativeLayout) findViewById(R.id.third_rl);
		fouth_rl = (RelativeLayout) findViewById(R.id.fouth_rl);
		fifth_rl = (RelativeLayout) findViewById(R.id.fifth_rl);
		sixth_rl = (RelativeLayout) findViewById(R.id.sixth_rl);
		seventh_rl = (RelativeLayout) findViewById(R.id.seventh_rl);
		eighth_rl = (RelativeLayout) findViewById(R.id.eighth_rl);
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
		case R.id.call_image:
			IntentUtil.openTelephone(this, phone) ;
			break;
		case R.id.first_rl:
			Intent intentFirst = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			intentFirst.putExtra("index", 0);
			startActivity(intentFirst);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.second_rl:
			Intent second_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			second_rl.putExtra("index", 1);
			startActivity(second_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.third_rl:
			Intent third_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			third_rl.putExtra("index", 2);
			startActivity(third_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.fouth_rl:
			Intent fouth_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			fouth_rl.putExtra("index", 3);
			startActivity(fouth_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.fifth_rl:
			Intent fifth_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			fifth_rl.putExtra("index", 4);
			startActivity(fifth_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.sixth_rl:
			Intent sixth_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			sixth_rl.putExtra("index", 5);
			startActivity(sixth_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.seventh_rl:
			Intent seventh_rl = new Intent(this,
					HelpAndSuggestionListViewActivity.class);
			seventh_rl.putExtra("index", 6);
			startActivity(seventh_rl);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;
		case R.id.eighth_rl:
			Intent eightIntent = new Intent(this,
					HelpAndSuggestionSixthActivity.class);
			startActivity(eightIntent);
			overridePendingTransition(R.anim.slide_left_in,
					R.anim.slide_right_out);
			break;

		}
	}

}
