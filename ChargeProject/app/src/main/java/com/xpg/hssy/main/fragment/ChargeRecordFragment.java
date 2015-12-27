package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.easy.util.MeasureUtil;
import com.easy.util.NetWorkUtil;
import com.easy.util.SPFile;
import com.easy.util.ScreenUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.adapter.ChargeRecordAdapter;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.db.pojo.Record;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.DropDownListView;
import com.xpg.hssy.view.DropDownListView.OnDropDownListener;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年2月3日
 */

@SuppressLint({"ValidFragment", "InflateParams"})
public class ChargeRecordFragment extends Fragment implements OnCheckedChangeListener, OnClickListener, OnDismissListener, OnItemClickListener {
	private static final int PROGRESSBAR1INDEX = 121;
	public String tag = ChargeRecordFragment.class.getSimpleName();

	private String pileId;
	private String userid;
	private RadioGroup rg_filter;
	private RadioButton rb_year;
	private RadioButton rb_month;
	private RadioButton rb_user_type;
	private DropDownListView lv_record;
	private Button btn_sum;
	private TextView sum_free;
	private TextView sum_money;
	private ChargeRecordAdapter recordAdapter;
	private LinearLayout yuyue_charge_layout;

	private PopupWindow yearPop;
	private PopupWindow monthPop;
	private PopupWindow userTypePop;
	private ListView lv_year;
	private ListView lv_month;
	private ListView lv_user_type;
	private FilterAdapter yearAdapter;
	private FilterAdapter monthAdapter;
	private FilterAdapter userTypeAdapter;
	private float sum = 0.00f;
	private int count = 0;
	private Button btn_cancel;
	private RelativeLayout cancel_layout, bt_sum_layout, rl_top_content;
	private TextView tv_year_top, tv_month_top, tv_user_top;
	private Button tv_cancel_top;
	private boolean isCheckModel;

	private ProgressBar progressBar1;
	private TextView percent_tv;
	private LinearLayout cache_record_layout, progress_record_layout, finished_record_layout;
	private TextView num_unload;
	private Button update;
	private int intCounter;
	private int position = -1;//0代表点击全部用户，1代表桩主和家人，2代表租户

	List<String> months = new ArrayList<String>();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case ChargeRecordAdapter.SUM_MENEY:
					if (recordAdapter != null) {
						List<Record> records = recordAdapter.getSelectedItems();
						float sum = 0.00f;
						for (Record record : records) {
							sum += record.getChargePrice();
						}
						sum_money.setText(String.format("%.2f", sum));
					}
					break;
				case PROGRESSBAR1INDEX:
					percent_tv.setText(progressBar1.getProgress() + "%");
					if (progressBar1.getProgress() == 100) {
						cache_record_layout.setVisibility(View.GONE);
						progress_record_layout.setVisibility(View.GONE);
						finished_record_layout.setVisibility(View.VISIBLE);
					}
					break;
				default:
					break;
			}
		}
	};

	public ChargeRecordFragment(String pileId) {
		this.pileId = pileId;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void showUiForRlTopContent() {

		rl_top_content.setVisibility(View.VISIBLE);
		rg_filter.setVisibility(View.GONE);
	}

	public void showUiForRgFilter() {
		rl_top_content.setVisibility(View.GONE);
		rg_filter.setVisibility(View.VISIBLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_charge_record, null);
		yuyue_charge_layout = (LinearLayout) v.findViewById(R.id.yuyue_charge_layout);

		rl_top_content = (RelativeLayout) v.findViewById(R.id.rl_top_content);
		tv_year_top = (TextView) v.findViewById(R.id.tv_year_top);
		tv_month_top = (TextView) v.findViewById(R.id.tv_month_top);
		tv_user_top = (TextView) v.findViewById(R.id.tv_user_top);
		btn_cancel = (Button) v.findViewById(R.id.btn_cancel);

		tv_year_top.setText("全部年份");
		tv_month_top.setText("全部月份");
		tv_user_top.setText("全部用户");

		sum_free = (TextView) v.findViewById(R.id.sum_free);
		sum_money = (TextView) v.findViewById(R.id.sum_money);
		yuyue_charge_layout.setVisibility(View.GONE);
		rg_filter = (RadioGroup) v.findViewById(R.id.rg_filter);
		rb_year = (RadioButton) v.findViewById(R.id.rb_year);
		rb_month = (RadioButton) v.findViewById(R.id.rb_month);
		rb_user_type = (RadioButton) v.findViewById(R.id.rb_user_type);
		lv_record = (DropDownListView) v.findViewById(R.id.lv_record);
		cancel_layout = (RelativeLayout) v.findViewById(R.id.cancel_layout);
		bt_sum_layout = (RelativeLayout) v.findViewById(R.id.bt_sum_layout);
		btn_sum = (Button) v.findViewById(R.id.btn_sum);

		cache_record_layout = (LinearLayout) v.findViewById(R.id.cache_record_layout);
		progress_record_layout = (LinearLayout) v.findViewById(R.id.progress_record_layout);
		finished_record_layout = (LinearLayout) v.findViewById(R.id.finished_record_layout);
		showUiForRgFilter();
		bt_sum_layout.setVisibility(View.GONE);

		// TODO 暂时屏蔽 2015 06 26 Mazoh
		cache_record_layout.setVisibility(View.GONE);
		// END

		progress_record_layout.setVisibility(View.GONE);
		finished_record_layout.setVisibility(View.GONE);
		num_unload = (TextView) v.findViewById(R.id.num_unload);
		update = (Button) v.findViewById(R.id.update);
		update.setOnClickListener(this);

		progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar1);
		percent_tv = (TextView) v.findViewById(R.id.percent_tv);
		progressBar1.setMax(100);
		percent_tv.setText(progressBar1.getProgress() + "%");

		recordAdapter = new ChargeRecordAdapter(mHandler, getActivity(), new ArrayList<Record>());
		lv_record.setAdapter(recordAdapter);
		lv_record.setDropDownStyle(true);
		lv_record.setOnBottomStyle(false);
		lv_record.setShowFooterWhenNoMore(true);

		initPop(inflater);

		rg_filter.clearCheck();
		rg_filter.setOnCheckedChangeListener(this);

		btn_sum.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);

		if (NetWorkUtil.isNetworkConnected(getActivity())) {
			lv_record.setOnDropDownListener(new OnDropDownListener() {
				@Override
				public void onDropDown() {
					getChargeRecord(true);
				}
			});
			lv_record.setOnBottomListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getChargeRecord(false);
				}
			});
		} else {
			lv_record.setDropDownStyle(false);
			recordAdapter.clear();
			getChargeRecordWithoutNet();
		}
		userid = new SPFile(getActivity(), "config").getString("user_id", null);
		lv_record.onDropDownBegin();
		getChargeRecord(true);
		lv_record.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// ChargeRecord chargeRecord = (ChargeRecord)
				// recordAdapter.getItem(position) ;
				// Intent intent = new
				// Intent(getActivity(),ChargeRecordDetailActivity.class) ;
				// intent.putExtra("chargeRecord", (Serializable)chargeRecord) ;
				// getActivity().startActivity(intent) ;
			}
		});
		months.add("全部月份");
		for (int i = 1; i <= Calendar.getInstance().get(Calendar.MONTH); i++) {
			months.add(i + "月");
		}
		return v;
	}

	/**
	 * 初始化筛选窗口
	 */
	@SuppressWarnings("deprecation")
	private void initPop(LayoutInflater inflater) {
		// 初始化筛选数据
		List<String> year = new ArrayList<String>();
		List<String> month = new ArrayList<String>();
		List<String> userType = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		while (c.get(Calendar.YEAR) >= 2014) {
			year.add(c.get(Calendar.YEAR) + "年");
			c.add(Calendar.YEAR, -1);
		}
		year.add("全部年份");
		Collections.reverse(year);
		month.add("全部月份");
		for (int i = 1; i <= 12; i++) {
			month.add(i + "月");
		}
		userType.add("全部用户");
		userType.add("桩主及家人");
		userType.add("租赁用户");

		yearAdapter = new FilterAdapter(getActivity(), year);
		monthAdapter = new FilterAdapter(getActivity(), month);
		userTypeAdapter = new FilterAdapter(getActivity(), userType);

		//注册数据监听器
		yearAdapter.registerDataSetObserver(new yearObserver());
		monthAdapter.registerDataSetObserver(new monthObserver());
		userTypeAdapter.registerDataSetObserver(new userTypebserver());

		// 初始化列表
		lv_year = (ListView) inflater.inflate(R.layout.filter_list_gray, null);
		lv_month = (ListView) inflater.inflate(R.layout.filter_list_gray, null);
		lv_user_type = (ListView) inflater.inflate(R.layout.filter_list_gray, null);
		lv_year.setAdapter(yearAdapter);
		lv_month.setAdapter(monthAdapter);
		lv_user_type.setAdapter(userTypeAdapter);
		lv_year.setOnItemClickListener(this);
		lv_month.setOnItemClickListener(this);
		lv_user_type.setOnItemClickListener(this);

		//设定为当前年份
		yearAdapter.select(yearAdapter.getCount() - 1);


		// 初始化窗口
		int screenWidth = MeasureUtil.getScreenWidth(getActivity());
		int height = ScreenUtil.getLongSide(getActivity()) * 2 / 5;
		yearPop = new PopupWindow(lv_year, screenWidth, LayoutParams.WRAP_CONTENT);
		monthPop = new PopupWindow(lv_month, screenWidth, height);
		userTypePop = new PopupWindow(lv_user_type, screenWidth, LayoutParams.WRAP_CONTENT);
		yearPop.setFocusable(true);
		monthPop.setFocusable(true);
		userTypePop.setFocusable(true);
		yearPop.setOutsideTouchable(true);
		monthPop.setOutsideTouchable(true);
		userTypePop.setOutsideTouchable(true);
		yearPop.setBackgroundDrawable(new BitmapDrawable());
		monthPop.setBackgroundDrawable(new BitmapDrawable());
		userTypePop.setBackgroundDrawable(new BitmapDrawable());
		yearPop.setOnDismissListener(this);
		monthPop.setOnDismissListener(this);
		userTypePop.setOnDismissListener(this);
	}

	/**
	 * 显示筛选窗口
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rb_year:
				showPop(yearPop);
				break;
			case R.id.rb_month:
				if (yearAdapter.getSelection() == 0) {
					rg_filter.setOnCheckedChangeListener(null);
					rg_filter.clearCheck();
					rg_filter.setOnCheckedChangeListener(this);
					ToastUtil.show(getActivity(), "请先选择年份");
					return;
				}
				showPop(monthPop);
				break;
			case R.id.rb_user_type:
				showPop(userTypePop);
				break;
			default:
				break;
		}
	}

	private void showPop(PopupWindow pop) {// 显示在rb_year的左下方
		pop.showAsDropDown(rb_year);
	}

	@Override
	public void onDismiss() {
		rg_filter.setOnCheckedChangeListener(null);
		rg_filter.clearCheck();
		rg_filter.setOnCheckedChangeListener(this);
	}

	/**
	 * 选择筛选条件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == lv_year) {
			yearAdapter.select(position);
			sum = 0.00f;
			yearPop.dismiss();// 关闭popwindow
		}
		if (parent == lv_month) {
			sum = 0.00f;
			monthAdapter.select(position);
			monthPop.dismiss();
		}
		if (parent == lv_user_type) {
			this.position = position;
//			Log.i("taglv_user_type:", position + "");
			if (this.position == 2) {//租户才显示统计金额
				bt_sum_layout.setVisibility(View.VISIBLE);
			} else {
				bt_sum_layout.setVisibility(View.GONE);
			}
			sum = 0.00f;
			userTypeAdapter.select(position);
			userTypePop.dismiss();
		}
		if (NetWorkUtil.isNetworkConnected(getActivity())) {
			lv_record.onDropDownBegin();
			getChargeRecord(true);
		} else {
			recordAdapter.clear();
			lv_record.setDropDownStyle(false);
			getChargeRecordWithoutNet();
		}
	}

	/**
	 * 获取充电记录
	 */
	private List<ChargeRecordCache> localRecords;

	private void getChargeRecord(boolean isRefresh) {
		if (isCheckModel && recordAdapter != null) {
			recordAdapter.setMode(EasyAdapter.MODE_CHECK_BOX);
			bt_sum_layout.setVisibility(View.GONE);
			cancel_layout.setVisibility(View.VISIBLE);
		} else {
			recordAdapter.setMode(EasyAdapter.MODE_NON);
			if (position == 2) {//租户才能统计金额
				bt_sum_layout.setVisibility(View.VISIBLE);
			} else {
				bt_sum_layout.setVisibility(View.GONE);
			}
			cancel_layout.setVisibility(View.GONE);
		}
		if (isRefresh) {
			recordAdapter.clear();
			localRecords = null;
			lv_record.onBottomComplete();
			lv_record.setOnBottomStyle(false);
			lv_record.smoothScrollToPosition(0);// 自动滑动到顶部
		} else {
			lv_record.onDropDownComplete();
		}

		if (rrh != null) {
			rrh.abandon();
		}

		Long startTime = null;
		Long endTime = null;
		Calendar sc = null;
		Calendar ec = null;
		// 获取年
		String yearStr = yearAdapter.get(yearAdapter.getSelection());
		if (!yearStr.contains("全部")) {
			int year = Integer.parseInt(yearStr.replace("年", ""));
			sc = Calendar.getInstance();
			ec = Calendar.getInstance();
			sc.set(Calendar.YEAR, year);
			ec.set(Calendar.YEAR, year);
			// 获取月
			String monthStr = monthAdapter.get(monthAdapter.getSelection());
			if (!monthStr.contains("全部")) {
				// 某月
				int month = Integer.parseInt(monthStr.replace("月", ""));
				sc.set(Calendar.MONTH, month - 1);
				sc.set(Calendar.DAY_OF_MONTH, 1);
				ec.set(Calendar.MONTH, month);
				ec.set(Calendar.DAY_OF_MONTH, 1);
				ec.add(Calendar.DAY_OF_YEAR, -1);
			} else {
				// 从1月1日到12月31日
				sc.set(Calendar.MONTH, 0);
				sc.set(Calendar.DAY_OF_MONTH, 1);
				ec.set(Calendar.MONTH, 11);
				ec.set(Calendar.DAY_OF_MONTH, 31);
			}
			// 设置时分秒
			sc.set(Calendar.HOUR_OF_DAY, 0);
			sc.set(Calendar.MINUTE, 0);
			sc.set(Calendar.SECOND, 0);
			sc.set(Calendar.MILLISECOND, 0);
			ec.set(Calendar.HOUR_OF_DAY, 23);
			ec.set(Calendar.MINUTE, 59);
			ec.set(Calendar.SECOND, 59);
			ec.set(Calendar.MILLISECOND, 999);

			startTime = sc.getTimeInMillis();
			endTime = ec.getTimeInMillis();

			Log.e(tag, TimeUtil.format(sc.getTime(), "yyyy-MM-dd HH:mm:ss"));
			Log.e(tag, TimeUtil.format(ec.getTime(), "yyyy-MM-dd HH:mm:ss"));
		}
		int type = userTypeAdapter.getSelection() + 1;

		// 先筛选本地历史记录
		List<ChargeRecordCache> crcs = DbHelper.getInstance(getActivity()).getChargeRecordCacheByPileId(pileId);
		localRecords = new ArrayList<>();
		for (ChargeRecordCache crc : crcs) {
			// 桩的充电时间不明，过滤掉
			if (crc.getStartTime() == null || crc.getEndTime() == null) {
				continue;
			}
			// 如果超出筛选的时间，过滤掉
			if (sc != null && ec != null) {
				if (crc.getStartTime() < sc.getTimeInMillis() || crc.getEndTime() > ec.getTimeInMillis()) {
					continue;
				}
			}

			// 如果不是筛选的用户类别，过滤掉
			if (type != 1) {
				// 桩柱及家人是没有订单号的
				if (type == 2 && !EmptyUtil.isEmpty(crc.getOrderId())) {
					continue;
				}
				// 租户是一定有订单号的
				if (type == 3 && EmptyUtil.isEmpty(crc.getOrderId())) {
					continue;
				}
			}

			// 将符合条件的加入集合
			localRecords.add(crc);
			recordAdapter.add(localRecords);
			count += 1;

		}

		// 筛选服务器上的历史记录
		rrh = new RecordResponseHandler(isRefresh);
		WebAPIManager.getInstance().getChargeRecord(pileId, userid, startTime, endTime, type, isRefresh ? 0 : (recordAdapter.getCount() - count), MyConstant
				.PAGE_SIZE, rrh);
	}

	private void getChargeRecordWithoutNet() {

		String startTime = null;
		String endTime = null;
		Calendar sc = null;
		Calendar ec = null;
		// 获取年
		String yearStr = yearAdapter.get(yearAdapter.getSelection());
		if (!yearStr.contains("全部")) {
			int year = Integer.parseInt(yearStr.replace("年", ""));
			sc = Calendar.getInstance();
			ec = Calendar.getInstance();
			sc.set(Calendar.YEAR, year);
			ec.set(Calendar.YEAR, year);
			// 获取月
			String monthStr = monthAdapter.get(monthAdapter.getSelection());
			if (!monthStr.contains("全部")) {
				// 某月
				int month = Integer.parseInt(monthStr.replace("月", ""));
				sc.set(Calendar.MONTH, month - 1);
				sc.set(Calendar.DAY_OF_MONTH, 1);
				ec.set(Calendar.MONTH, month);
				ec.set(Calendar.DAY_OF_MONTH, 1);
				ec.add(Calendar.DAY_OF_YEAR, -1);
			} else {
				// 从1月1日到12月31日
				sc.set(Calendar.MONTH, 0);
				sc.set(Calendar.DAY_OF_MONTH, 1);
				ec.set(Calendar.MONTH, 11);
				ec.set(Calendar.DAY_OF_MONTH, 31);
			}
			// 设置时分秒
			sc.set(Calendar.HOUR_OF_DAY, 0);
			sc.set(Calendar.MINUTE, 0);
			sc.set(Calendar.SECOND, 0);
			sc.set(Calendar.MILLISECOND, 0);
			ec.set(Calendar.HOUR_OF_DAY, 23);
			ec.set(Calendar.MINUTE, 59);
			ec.set(Calendar.SECOND, 59);
			ec.set(Calendar.MILLISECOND, 999);

			startTime = TimeUtil.format(sc.getTime(), "yyyy-MM-dd HH:mm:ss");
			endTime = TimeUtil.format(ec.getTime(), "yyyy-MM-dd HH:mm:ss");

			Log.e(tag, startTime);
			Log.e(tag, endTime);
		}
		int type = userTypeAdapter.getSelection() + 1;

		// 先筛选本地历史记录
		List<ChargeRecordCache> crcs = DbHelper.getInstance(getActivity()).getChargeRecordCacheByPileId(pileId);
		localRecords = new ArrayList<ChargeRecordCache>();
		for (ChargeRecordCache crc : crcs) {
			// 桩的充电时间不明，过滤掉
			if (crc.getStartTime() == null || crc.getEndTime() == null) {
				continue;
			}
			// 如果超出筛选的时间，过滤掉
			if (sc != null && ec != null) {
				if (crc.getStartTime() < sc.getTimeInMillis() || crc.getEndTime() > sc.getTimeInMillis()) {
					continue;
				}
			}

			// 如果不是筛选的用户类别，过滤掉
			if (type != 1) {
				// 桩柱及家人是没有订单号的
				if (type == 2 && !EmptyUtil.isEmpty(crc.getOrderId())) {
					continue;
				}
				// 租户是一定有订单号的
				if (type == 3 && EmptyUtil.isEmpty(crc.getOrderId())) {
					continue;
				}
			}

			// 将符合条件的加入集合
			localRecords.add(crc);
		}
		recordAdapter.add(localRecords);
		List<ChargeRecord> crss = DbHelper.getInstance(getActivity()).getChargeRecordByPileId(pileId);
		List<ChargeRecord> crssdb = new ArrayList<ChargeRecord>();
		for (ChargeRecord crcs1 : crss) {
			// 桩的充电时间不明，过滤掉
			if (crcs1.getStartTime() == null || crcs1.getEndTime() == null) {
				continue;
			}
			// 如果超出筛选的时间，过滤掉
			if (sc != null && ec != null) {
				if (crcs1.getStartTime() < sc.getTimeInMillis() || crcs1.getEndTime() > ec.getTimeInMillis()) {
					continue;
				}
			}

			// 如果不是筛选的用户类别，过滤掉
			if (type != 1) {
				// 桩柱及家人是没有订单号的
				if (type == 2 && !EmptyUtil.isEmpty(crcs1.getOrderId())) {
					continue;
				}
				// 租户是一定有订单号的
				if (type == 3 && EmptyUtil.isEmpty(crcs1.getOrderId())) {
					continue;
				}
			}
			crssdb.add(crcs1);
			lv_record.smoothScrollToPosition(0);
		}

		recordAdapter.add(crssdb);
		lv_record.setOnBottomStyle(true);
		lv_record.setHasMore(false);
		lv_record.setAutoLoadOnBottom(false);
		lv_record.onBottomComplete();
	}

	private RecordResponseHandler rrh;

	class RecordResponseHandler extends WebResponseHandler<List<ChargeRecord>> {

		private boolean isRefresh;

		public RecordResponseHandler(boolean isRefresh) {
			this.tag = ChargeRecordFragment.this.tag;
			this.isRefresh = isRefresh;
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
			TipsUtil.showTips(getActivity(), e);
			if (isRefresh) {
				lv_record.setHeaderStatusClickToLoad();
			} else {
				lv_record.setAutoLoadOnBottom(false);
				lv_record.onBottomComplete();
			}
		}

		@Override
		public void onFailure(WebResponse<List<ChargeRecord>> response) {
			super.onFailure(response);
			TipsUtil.showTips(getActivity(), response);
			if (isRefresh) {
				lv_record.setHeaderStatusClickToLoad();
			} else {
				lv_record.setAutoLoadOnBottom(false);
				lv_record.onBottomComplete();
			}
		}

		@Override
		public void onSuccess(WebResponse<List<ChargeRecord>> response) {
			super.onSuccess(response);
			if (!isVisible()) {
				return;
			}
			List<ChargeRecord> crs = response.getResultObj();
			if (crs != null) {
				showHasRecord();
				DbHelper.getInstance(getActivity()).getChargeRecordDao().insertOrReplaceInTx(crs);
				if (isRefresh) {// 刷新清理data
					recordAdapter.clear();
					// 先显示本地缓存的历史记录
					if (localRecords != null) {
						recordAdapter.add(localRecords);
					}
					lv_record.setOnBottomStyle(true);
				}
				if (crs.size() < MyConstant.PAGE_SIZE) {
					lv_record.setHasMore(false);
					lv_record.setAutoLoadOnBottom(false);
				} else {
					lv_record.setHasMore(true);
					lv_record.setAutoLoadOnBottom(true);
				}
				// TODO begin fix repeat data bug by Mazoh 20150522
				if (!EmptyUtil.isEmpty(crs)) {
					yuyue_charge_layout.setVisibility(View.GONE);
					recordAdapter.add(crs);
					lv_record.onBottomComplete();
				} else {
				}
				// TODO end
			} else {
				lv_record.setHasMore(false);
				lv_record.setAutoLoadOnBottom(false);
				// //暂时屏蔽 2015 06 26 Mazoh
				//open 2015 08 26  by mazoh
				showNoneRecord();
			}
			lv_record.onDropDownComplete();
			lv_record.onBottomComplete();
		}
	}

	public void showNoneRecord() {
		yuyue_charge_layout.setVisibility(View.VISIBLE);
		lv_record.setVisibility(View.GONE);
		if (this.position == 2 && !isCheckModel) {//租户以及可选模式
			bt_sum_layout.setVisibility(View.VISIBLE);
		}
		btn_sum.setEnabled(false);
	}

	public void showHasRecord() {
		lv_record.setVisibility(View.VISIBLE);
		yuyue_charge_layout.setVisibility(View.GONE);
		if (this.position == 2 && !isCheckModel) {//租户以及可选模式
			bt_sum_layout.setVisibility(View.VISIBLE);
		}
		btn_sum.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.btn_sum:
				isCheckModel = true;
				recordAdapter.setMode(EasyAdapter.MODE_CHECK_BOX);
				bt_sum_layout.setVisibility(View.GONE);
				showUiForRlTopContent();
				cancel_layout.setVisibility(View.VISIBLE);
				break;
			case R.id.btn_cancel:
				isCheckModel = false;
				recordAdapter.setMode(EasyAdapter.MODE_NON);
				sum_money.setText("0.00");
				showUiForRgFilter();
				bt_sum_layout.setVisibility(View.VISIBLE);
				cancel_layout.setVisibility(View.GONE);
				break;
			case R.id.update:
				cache_record_layout.setVisibility(View.GONE);
				progress_record_layout.setVisibility(View.VISIBLE);

			/* 开始一个进程 */
				new Thread(new Runnable() {
					@Override
					public void run() {
					/* 默认0至9，共运行10次的循环语句 */
						for (int i = 0; i < 10; i++) {
							try {
							/* 成员变量，用以识别加载进度 */
								intCounter = (i + 1) * 10;
								progressBar1.setProgress(intCounter);
							/* 每运行一次循环，即暂停1秒 */
								Thread.sleep(1000);

								Message m = new Message();
								m.what = PROGRESSBAR1INDEX;
								m.obj = intCounter;
								mHandler.sendMessage(m);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}).start();

				break;
			default:
				break;
		}
	}

	/**
	 * 筛选项
	 *
	 * @author Joke
	 */
	class FilterAdapter extends EasyAdapter<String> {
		public FilterAdapter(Context context, List<String> items) {
			super(context, items);
			setMode(EasyAdapter.MODE_RADIO_GROUP);
			select(0);
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				TextView tv;
				ImageView iv_check;

				@Override
				protected View init(android.view.LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.filter_adapter_water_blue, null);
					tv = (TextView) v.findViewById(R.id.tv);
					iv_check = (ImageView) v.findViewById(R.id.iv_check);
					return v;
				}

				@Override
				protected void update() {
					tv.setText(get(position));
					if (position == getSelection()) {
						iv_check.setVisibility(View.VISIBLE);
					} else {
						iv_check.setVisibility(View.INVISIBLE);
					}
				}
			};
		}
	}

	class yearObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			onYearSelected();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			onYearSelected();
		}

		void onYearSelected() {

			String yearStr = yearAdapter.getSelectedItem();
			String monthStr = monthAdapter.getSelectedItem();
			int index = monthAdapter.indexOf(monthStr);//获得选择月份的索引值
			rb_year.setText(yearStr);
			tv_year_top.setText(yearStr);
			List<String> months = new ArrayList<String>();
			months.add("全部月份");
			for (int i = 1; i <= 12; i++) {
				months.add(i + "月");
			}
			monthAdapter.clear();
			monthAdapter.add(months);
			monthAdapter.select(index);
			monthStr = monthAdapter.getSelectedItem();//重新获取月份值
			index = monthAdapter.indexOf(monthStr);//重新获得选择月份的索引值


			//TODO 年份选择为当前年份，要判断月份 by Mazoh
			if (yearStr.equals(Calendar.getInstance().get(Calendar.YEAR) + "年")) {
				List<String> monthss = new ArrayList<String>();
				monthss.add("全部月份");
				for (int i = 1; i <= Calendar.getInstance().get(Calendar.MONTH) + 1; i++) {
					monthss.add(i + "月");
				}
				monthAdapter.clear();
				monthAdapter.add(monthss);
				if (!monthStr.equals("全部月份")) {
					int month = Integer.parseInt(monthStr.substring(0, monthStr.length() - 1));
					if (month > Calendar.getInstance().get(Calendar.MONTH)) {
						monthAdapter.select(0);
						rb_month.setText(monthAdapter.getSelectedItem());
						tv_month_top.setText(monthAdapter.getSelectedItem());
					} else {
						monthAdapter.select(index);
						rb_month.setText(monthAdapter.getSelectedItem());
						tv_month_top.setText(monthAdapter.getSelectedItem());
					}
				} else {
					//当年的当前月份小于上次选择的月份，选中第一个
					monthAdapter.select(0);
				}
			} else if (yearStr.equals("全部年份")) {
				monthAdapter.select(0);
				rb_month.setText(monthAdapter.getSelectedItem());
				tv_month_top.setText(monthAdapter.getSelectedItem());
			} else {
			}
		}
	}

	class monthObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			monthSelected();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			monthSelected();
		}

		void monthSelected() {
			rb_month.setText(monthAdapter.getSelectedItem());
			tv_month_top.setText(monthAdapter.get(monthAdapter.getSelection()));
		}
	}

	class userTypebserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			userTypeSelected();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			userTypeSelected();
		}

		void userTypeSelected() {
			rb_user_type.setText(userTypeAdapter.get(userTypeAdapter.getSelection()));
			tv_user_top.setText(userTypeAdapter.get(userTypeAdapter.getSelection()));
		}
	}
}
