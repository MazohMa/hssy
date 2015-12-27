package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.adapter.SettingTimeAdapter;
import com.xpg.hssy.adapter.SettingTimeAdapter.AdapterItem;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssy.dialog.WaterBlueDialog;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mazoh 时间设置
 */
public class SettingTimeActivity extends BaseActivity implements OnClickListener {
	private static final int REQUEST_ADD_SHARE_TIME = 0x00;
	public static final int REQUEST_MODIFY_SHARE_TIME = 0x01;

	private static final int MAX_SHARE_TIME_COUNT = 10;

	private ListView listView;
	private Pile pile;
	private List<ShareTime> shareTimes;
	private SettingTimeAdapter settingTimeAdapter;
	private Button add, delete;
	private String time_begins;
	private String time_ends;
	private List<Integer> weeks;
	private ImageButton btn_left;
	private boolean timeConflict;
	private StringBuilder conflictTipsBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshShareTime();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// Intent intent = new Intent();
	// intent.putExtra("shareTimes", (Serializable) shareTimes);
	// setResult(Activity.RESULT_OK, intent);
	// finish();
	// return true;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case REQUEST_ADD_SHARE_TIME: {
				// 添加时间ITEM请求码
				time_begins = data.getStringExtra("time_begins");
				time_ends = data.getStringExtra("time_ends");
				weeks = (List<Integer>) data.getSerializableExtra("weeks");
				ShareTime sharetime = new ShareTime();
				sharetime.setStartTime(time_begins);
				sharetime.setEndTime(time_ends);
				sharetime.setWeeks(weeks);
				shareTimes.add(sharetime);
				refreshShareTime();
				break;
			}
			case REQUEST_MODIFY_SHARE_TIME: {
				// 修改时间ITEM请求码
				time_begins = data.getStringExtra("time_begins");
				time_ends = data.getStringExtra("time_ends");
				weeks = (List<Integer>) data.getSerializableExtra("weeks");
				ShareTime sharetime = new ShareTime();
				sharetime.setStartTime(time_begins);
				sharetime.setEndTime(time_ends);
				sharetime.setWeeks(weeks);
				synchronized (settingTimeAdapter) {
					int modifyIndex = settingTimeAdapter.getModifyIndex();
					shareTimes.remove(modifyIndex);
					shareTimes.add(modifyIndex, sharetime);
					settingTimeAdapter.notifyDataSetChanged();
				}
				refreshShareTime();
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		super.initData();
		shareTimes = (List<ShareTime>) getIntent().getSerializableExtra("shareTimes");
		if (settingTimeAdapter != null) {
			settingTimeAdapter = null;
		}
		settingTimeAdapter = new SettingTimeAdapter(this);
		settingTimeAdapter.registerDataSetObserver(new settingTimeItemObserver());
		conflictTipsBuilder = new StringBuilder();
	}

	private void refreshShareTime() {
		timeConflict = false;
		settingTimeAdapter.clear();
		List<AdapterItem> itemList = new ArrayList<>(shareTimes.size());
		if (shareTimes != null) {
			int size = shareTimes.size();
			for (int i = 0; i < size; i++) {
				boolean isConflict = false;
				conflictTipsBuilder.setLength(0);
				conflictTipsBuilder.append("与");
				ShareTime shareTime = shareTimes.get(i);// 要显示的分享时间
				Set<Integer> weeksSet = shareTime.createWeeksSet();
				for (int j = 0; j < i; j++) {
					ShareTime compareTime = shareTimes.get(j);// 用于对比的分享时间
					Set<Integer> compareWeeksSet = compareTime.createWeeksSet();
					compareWeeksSet.retainAll(weeksSet);// 求交集
					if (compareWeeksSet.size() > 0) {// 如果两时间段存在交集
						// 判断时间段之间的独立性
						boolean isIndependence = TimeUtil.checkShareTimeIndependence(shareTime
								.getStartTime(), shareTime.getEndTime(), compareTime.getStartTime
								(), compareTime.getEndTime());

						if (!isIndependence) {// 如果不独立,则证明两个时间段冲突
							int sn = j + 1;
							conflictTipsBuilder.append("时间段").append(sn).append("、");
						}

						isConflict = isConflict || !isIndependence;// 更新本时间段的冲突状态
					} else {// 没有存在交集,不存在冲突
						isConflict = isConflict || false;
					}
				}
				timeConflict = timeConflict || isConflict;// 更新整体冲突状态

				conflictTipsBuilder.setLength(conflictTipsBuilder.length() - 1);// 移除最后一个顿号
				conflictTipsBuilder.append("冲突");
				AdapterItem item = new AdapterItem(shareTime, isConflict, conflictTipsBuilder
						.toString());
				itemList.add(item);
			}
			settingTimeAdapter.add(itemList);
		}
		updateAddAndDeleteButtonState();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.setting_time_perid_item);
		listView = (ListView) findViewById(R.id.list_view);
		add = (Button) findViewById(R.id.add);
		delete = (Button) findViewById(R.id.delete);
		setTitle("设置时间段");
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		listView.setAdapter(settingTimeAdapter);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		add.setOnClickListener(this);
		delete.setOnClickListener(this);
		btn_left.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		switch (settingTimeAdapter.getMode()) {
			case SettingTimeAdapter.MODE_NON: {
				if (timeConflict) {
					WaterBlueDialog timeConflictDialog = new WaterBlueDialog(this);
					timeConflictDialog.setContent(R.string.setting_time_activity_timeConflict);
					timeConflictDialog.show();
					timeConflictDialog.setCanceledOnTouchOutside(true);
				} else {
					Intent intent = new Intent();
					intent.putExtra("shareTimes", (Serializable) shareTimes);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				break;
			}
			case SettingTimeAdapter.MODE_MULTIPLE_SELECT: {
				settingTimeAdapter.setMode(EasyAdapter.MODE_NON);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				onBackPressed();
				break;
			case R.id.add:
				Intent intentAdd = new Intent(this, AddTimeActivity.class);
				startActivityForResult(intentAdd, REQUEST_ADD_SHARE_TIME);
				break;
			case R.id.delete:
				deleteButtonClick();
				break;
			default:
				break;
		}
	}

	private void deleteButtonClick() {
		switch (settingTimeAdapter.getMode()) {
			case EasyAdapter.MODE_MULTIPLE_SELECT: {
				if (settingTimeAdapter.getSelectedItems().size() > 0) {
					for (AdapterItem adapterItem : settingTimeAdapter.getSelectedItems()) {
						shareTimes.remove(adapterItem.getShareTime());
					}
					refreshShareTime();
				}
				settingTimeAdapter.setMode(EasyAdapter.MODE_NON);
				break;
			}
			case EasyAdapter.MODE_NON: {
				settingTimeAdapter.setMode(EasyAdapter.MODE_MULTIPLE_SELECT);
				break;
			}
		}
	}

	// 在这里统一更新add和delete button的状态
	private void updateAddAndDeleteButtonState() {
		switch (settingTimeAdapter.getMode()) {
			case EasyAdapter.MODE_MULTIPLE_SELECT: {
				delete.setEnabled(true);
				if (settingTimeAdapter.getSelectedItems().size() > 0) {
					delete.setText(getResources().getString(R.string.btn_delete_with_count,
							settingTimeAdapter.getSelectedItems().size()));
				} else {
					delete.setText(R.string.cancel);
				}
				add.setVisibility(View.INVISIBLE);
				break;
			}
			case EasyAdapter.MODE_NON: {
				delete.setText(R.string.btn_delete);
				if (settingTimeAdapter.getCount() > 0) {
					delete.setEnabled(true);
				} else {
					delete.setEnabled(false);
				}
				add.setVisibility(View.VISIBLE);
				break;
			}
		}
		updateAddButtonEnableState();
	}

	// 当分享时段数量超过10或者存在时间冲突的时候停止使能添加按钮
	private void updateAddButtonEnableState() {
		add.setEnabled(settingTimeAdapter.getCount() < MAX_SHARE_TIME_COUNT && !timeConflict);
	}

	// adapter的数据变更监听器,监听adapter状态的变化回调updateAddAndDeleteButtonState()更新button
	private class settingTimeItemObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			super.onChanged();
			updateAddAndDeleteButtonState();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			updateAddAndDeleteButtonState();
		}

	}
}
