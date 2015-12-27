package com.xpg.hssy.main.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xpg.hssy.adapter.PileManageAdapter;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.PileSortUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * @author Mazoh
 * @Description 电桩管理
 */
public class PileManageActivity extends BaseActivity {
	private ListView lv_pile_manage_list;
	private PileManageAdapter pileManageAdapter;
	private String user_id;
	private SharedPreferences sp;
	private LinearLayout lv_pile_noneexist_payout;
	private LinearLayout lv_pile_exist_payout;
    private LoadingDialog loadingDialog = null ;
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		pileManageAdapter = new PileManageAdapter(PileManageActivity.this);
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.pile_manage_list_layout);
		setTitle("电桩管理");
		lv_pile_exist_payout = (LinearLayout) findViewById(R.id.lv_pile_exist_payout);
		lv_pile_noneexist_payout = (LinearLayout) findViewById(R.id.lv_pile_noneexist_payout);
		lv_pile_manage_list = (ListView) findViewById(R.id.lv_pile_manage_list);
		findViewById(R.id.btn_find_pile).setOnClickListener(this);
		lv_pile_manage_list.setAdapter(pileManageAdapter);
	}

	private void nonePileUi() {
		lv_pile_noneexist_payout.setVisibility(View.VISIBLE);
		lv_pile_exist_payout.setVisibility(View.GONE);
	}

	private void havePileUi() {
		lv_pile_noneexist_payout.setVisibility(View.GONE);
		lv_pile_exist_payout.setVisibility(View.VISIBLE);
	}

	private void getRequests() {
		List<Pile> piles = DbHelper.getInstance(this).getPileByUserId(user_id);
		if (piles != null && piles.size() > 0) {
			piles = PileSortUtil.sortByPileAuthority(self, piles, user_id);
		}
		if (piles.size() == 0) {// 没有桩发布
			nonePileUi();
		} else {// 存在桩
			havePileUi();
			pileManageAdapter.clear();
			pileManageAdapter.add(piles);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btn_find_pile) {
			if (MainActivity.instance != null) {
				MainActivity.instance.sendMessage(MainActivity.MSG_SWITCH_SEARCH_FRAGMENT, null);
			}
			finish();
			return;
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkConnected()) {

			WebAPIManager.getInstance().getOwnPile(user_id, new WebResponseHandler<List<Pile>>(this) {

				@Override
				public void onStart() {
					super.onStart();
					loadingDialog = new LoadingDialog(PileManageActivity.this,R.string.loading) ;
					loadingDialog.showDialog();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					loadingDialog.dismiss();
					getRequests();// 本地数据库中获取
				}

				@Override
				public void onFailure(WebResponse<List<Pile>> response) {
					super.onFailure(response);
					loadingDialog.dismiss();
					getRequests();// 本地数据库中获取
				}

				@Override
				public void onSuccess(WebResponse<List<Pile>> response) {
					super.onSuccess(response);
					loadingDialog.dismiss();
					List<Pile> ownPiles = response.getResultObj();
					if (ownPiles != null && ownPiles.size() > 0) {
						ownPiles = PileSortUtil.sortByPileAuthority(self, ownPiles, user_id);
						DbHelper.getInstance(PileManageActivity.this).insertInTxPile(ownPiles);
						havePileUi();
						pileManageAdapter.clear();
						pileManageAdapter.add(ownPiles);
					} else {
						nonePileUi();
					}
				}
			});
		} else {
			getRequests();// 本地数据库中获取
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// if (pileManageAdapter.getDrawables() != null
		// && pileManageAdapter.getDrawables().length > 0) {
		// for (int i = 0; i < pileManageAdapter.getDrawables().length; i++) {
		// BitmapUtil.recycle(((BitmapDrawable) pileManageAdapter
		// .getDrawables()[i]).getBitmap());
		// }
		// pileManageAdapter.setDrawables(null);
		// System.gc();
		//
		// }

	}

}
