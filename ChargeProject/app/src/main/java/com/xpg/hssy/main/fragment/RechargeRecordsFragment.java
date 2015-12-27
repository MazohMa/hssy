package com.xpg.hssy.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.xpg.hssy.adapter.RechargeRecordAdapter;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.RechargeRecord;
import com.xpg.hssy.view.DropDownListView;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 */
public class RechargeRecordsFragment extends BaseFragment {

	private RefreshListView rlv_recharge_records;
	private RechargeRecordAdapter adapter;
	private SPFile sp;
	private String userid;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.fragment_recharge_records, null);
		rlv_recharge_records = (RefreshListView) view.findViewById(R.id.rlv_recharge_records);
		initData();
		rlv_recharge_records.setAdapter(adapter);
		initEvent();
		refreshRechargeRecords();
//		test(true);
		return view;
	}
	private void initData(){
		sp = new SPFile(getActivity(), "config");
		userid = sp.getString("user_id", null);
		adapter = new RechargeRecordAdapter(getActivity(), new ArrayList<RechargeRecord>());
	}

	private void initEvent(){
		rlv_recharge_records.setOnDropDownListener(new DropDownListView.OnDropDownListener() {
			@Override
			public void onDropDown() {
				refreshRechargeRecords();
//				test(true);
			}
		});

		rlv_recharge_records.setOnBottomListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loadRechargeRecords(adapter.getCount(), false);
//				test(false);
			}
		});
	}

	private void refreshRechargeRecords(){
		adapter.clear();
		rlv_recharge_records.setLoadable(false);
		rlv_recharge_records.showRefreshing(true);
		loadRechargeRecords(0, true);
	}

	int count = 21;
	private void test(boolean isRefresh){
		List<RechargeRecord> list = new ArrayList<>();
		if(isRefresh){
			for(int i=0;i<count;i++){
				RechargeRecord record = new RechargeRecord();
				record.setRechargeMoney(2);
				record.setRechargeTime(System.currentTimeMillis());
				record.setRechargeType(1);
				list.add(record);
			}

		}else{
			for(int i=0;i<count;i++){
				RechargeRecord record = new RechargeRecord();
				record.setRechargeMoney(2);
				record.setRechargeTime(System.currentTimeMillis());
				record.setRechargeType(1);
				list.add(record);
			}
		}
		if (list.size() < MyConstant.PAGE_SIZE) {
			rlv_recharge_records.showNoMore();
		} else {
			rlv_recharge_records.prepareLoad();
		}
		if (isRefresh) {
			rlv_recharge_records.completeRefresh();
		}
		adapter.add(list);
		count--;
	}

	private void loadRechargeRecords(int start, final boolean isRefresh){
		WebAPIManager.getInstance().getRechargeRecords(userid, start, MyConstant.PAGE_SIZE, new WebResponseHandler<List<RechargeRecord>>() {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (isRefresh) {
					rlv_recharge_records.showRefreshFail();
				} else {
					rlv_recharge_records.showLoadFail();
				}
			}

			@Override
			public void onFailure(WebResponse<List<RechargeRecord>> response) {
				super.onFailure(response);
				if (isRefresh) {
					rlv_recharge_records.showRefreshFail();
				} else {
					rlv_recharge_records.showLoadFail();
				}
			}

			@Override
			public void onSuccess(WebResponse<List<RechargeRecord>> response) {
				super.onSuccess(response);
				List<RechargeRecord> list = response.getResultObj();
				if (EmptyUtil.notEmpty(list)) {
					adapter.add(list);
					if (list.size() < MyConstant.PAGE_SIZE) {
						rlv_recharge_records.showNoMore();
					} else {
						rlv_recharge_records.prepareLoad();
					}
				} else {
					rlv_recharge_records.showNoMore();
				}


			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (isRefresh) {
					rlv_recharge_records.completeRefresh();
				}
//				else{
//					rlv_recharge_records.completeLoad();
//				}

			}
		});
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
}
