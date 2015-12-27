package com.xpg.hssy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.activity.EasyActivity;
import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.adapter.MyPileFindAdapterNews;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.view.DropDownListView.OnDropDownListener;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh 收藏
 */
public class MyCollectionNewActivity extends EasyActivity implements OnClickListener, OnDropDownListener {

	private static final int REQUEST_INFO = 2;
	private static final int RESULT_FINISH_MYSELF = 1;
	private MyPileFindAdapterNews myPileOrSiteCollectionAdapter;
	private RefreshListView mDropDownListView;
	private TextView mTitle;
	private SharedPreferences sp;
	private String user_id;
	private ImageButton btn_left;
	private ImageButton btn_right;
	private List<Pile> piles = new ArrayList<Pile>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(this).inflate(R.layout.activity_collection, null);
		setContentView(view);
		initData();
		initView(view);
		initListener();
	}

	protected void initData() {
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
	}

	private void initView(View view) {
		mDropDownListView = (RefreshListView) view.findViewById(R.id.pole_listview);
		btn_left = (ImageButton) view.findViewById(R.id.btn_left);
		btn_right = (ImageButton) view.findViewById(R.id.btn_right);
		btn_right.setImageResource(R.drawable.icon_tab_edit);
		btn_right.setVisibility(View.INVISIBLE);
		mTitle = (TextView) view.findViewById(R.id.tv_center);
		mTitle.setText("我的收藏");
		myPileOrSiteCollectionAdapter = new MyPileFindAdapterNews(this, piles, true);
		mDropDownListView.setAdapter(myPileOrSiteCollectionAdapter);
		myPileOrSiteCollectionAdapter.setMode(EasyAdapter.MODE_SINGLE_SELECT);// 默认模式
		myPileOrSiteCollectionAdapter.setFromActivity(true);
		refreshPile();
	}

	private void initListener() {
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		mDropDownListView.setOnDropDownListener(this);
		mDropDownListView.setOnBottomListener(onBottomListener);
		mDropDownListView.setOnItemClickListener(new MyItemClick());
	}

	// 加载列表
	private OnClickListener onBottomListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			loadPile();
		}
	};

	private void loadPile() {
		WebAPIManager.getInstance().getFavoritesPiles(user_id, myPileOrSiteCollectionAdapter.getCount(), MyConstant.PAGE_SIZE, new
				WebResponseHandler<List<Pile>>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				mDropDownListView.showLoadFail();
			}

			@Override
			public void onFailure(WebResponse<List<Pile>> response) {
				super.onFailure(response);
				mDropDownListView.showLoadFail();
			}

			@Override
			public void onSuccess(WebResponse<List<Pile>> response) {
				super.onSuccess(response);
				List<Pile> piles = response.getResultObj();
				if (!EmptyUtil.isEmpty(piles)) {
					for(Pile pile :piles)
					{
						pile.setFavor(Pile.FAVOR_YES);
					}
					myPileOrSiteCollectionAdapter.add(piles);
				}

				if (piles == null || piles.size() < MyConstant.PAGE_SIZE) {
					mDropDownListView.showNoMore();
				} else {
					mDropDownListView.prepareLoad();
				}
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_FINISH_MYSELF) {
			finish();
		} else {
		}
	}

	@Override
	public void onDropDown() {
		refreshPile();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshPile();
	}

	private void refreshPile() {
		mDropDownListView.setLoadable(false);
		mDropDownListView.showRefreshing(true);
		WebAPIManager.getInstance().getFavoritesPiles(user_id, 0, MyConstant.PAGE_SIZE, new WebResponseHandler<List<Pile>>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				mDropDownListView.showRefreshFail();
			}

			@Override
			public void onFailure(WebResponse<List<Pile>> response) {
				super.onFailure(response);
				mDropDownListView.showRefreshFail();
			}

			@Override
			public void onSuccess(WebResponse<List<Pile>> response) {
				super.onSuccess(response);
				List<Pile> piles = response.getResultObj();
				for(Pile pile :piles)
				{
					pile.setFavor(Pile.FAVOR_YES);
				}
				myPileOrSiteCollectionAdapter.clear();
				if (!EmptyUtil.isEmpty(piles)) {
					myPileOrSiteCollectionAdapter.add(piles);
				}
				if (myPileOrSiteCollectionAdapter.getCount() < MyConstant.PAGE_SIZE) {
					mDropDownListView.showNoMore();
				} else {
					mDropDownListView.prepareLoad();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mDropDownListView.completeRefresh();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case R.id.btn_right:
				break;
		}
	}

	class MyItemClick implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Pile pile = myPileOrSiteCollectionAdapter.get(position - mDropDownListView.getHeaderViewsCount());
			Intent intent = null;
			if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
				intent = new Intent(self, PileInfoNewActivity.class);
				intent.putExtra(KEY.INTENT.GPRS_TYPE, pile.getGprsType());
			} else {
				intent = new Intent(self, PileStationInfoActivity.class);
			}
			intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
			startActivityForResult(intent, REQUEST_INFO);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}

	}

}
