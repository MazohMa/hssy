package com.xpg.hssy.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easy.util.EmptyUtil;
import com.easy.util.SPFile;
import com.xpg.hssy.adapter.ExpenseRecordAdapter;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.ExpenseRecord;
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
public class PayRecordsFragment extends BaseFragment {

	private RefreshListView rlv_pay_records;
	private ExpenseRecordAdapter adapter;
	private SPFile sp;
	private String userid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initData();
		View view = inflater.inflate(R.layout.fragment_recharge_records, null);
		rlv_pay_records = (RefreshListView) view.findViewById(R.id.rlv_recharge_records);
		rlv_pay_records.setAdapter(adapter);
		refreshPayRecords();
		initEvent();
		return view;
	}

	private void initData(){
		sp = new SPFile(getActivity(), "config");
		userid = sp.getString("user_id", null);
		adapter = new ExpenseRecordAdapter(getActivity(), new ArrayList<ExpenseRecord>());
	}

	private void initEvent(){
		rlv_pay_records.setOnDropDownListener(new DropDownListView.OnDropDownListener() {
			@Override
			public void onDropDown() {
				refreshPayRecords();
			}
		});

		rlv_pay_records.setOnBottomListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loadPayRecords(adapter.getCount(), false);
			}
		});
	}

	private void refreshPayRecords(){
		adapter.clear();
		rlv_pay_records.setLoadable(false);
		rlv_pay_records.showRefreshing(true);
		loadPayRecords(0, true);
	}

	private void loadPayRecords(int start, final boolean isRefresh){
		WebAPIManager.getInstance().getExpenseRecords(userid, start, MyConstant.PAGE_SIZE, new WebResponseHandler<List<ExpenseRecord>>() {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if(isRefresh){
					rlv_pay_records.showRefreshFail();
				}else{
					rlv_pay_records.showLoadFail();
				}
			}

			@Override
			public void onFailure(WebResponse<List<ExpenseRecord>> response) {
				super.onFailure(response);
				if(isRefresh){
					rlv_pay_records.showRefreshFail();
				}else{
					rlv_pay_records.showLoadFail();
				}
			}

			@Override
			public void onSuccess(WebResponse<List<ExpenseRecord>> response) {
				super.onSuccess(response);
				List<ExpenseRecord> list = response.getResultObj();
				if(EmptyUtil.notEmpty(list)){
					adapter.add(list);
					if(list.size() < MyConstant.PAGE_SIZE){
						rlv_pay_records.showNoMore();
					}else{
						rlv_pay_records.prepareLoad();
					}
				}else{
					rlv_pay_records.showNoMore();
				}


			}

			@Override
			public void onFinish() {
				super.onFinish();
				if(isRefresh){
					rlv_pay_records.completeRefresh();
				}
//				else{
//					rlv_pay_records.completeLoad();
//				}

			}
		});
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
}
