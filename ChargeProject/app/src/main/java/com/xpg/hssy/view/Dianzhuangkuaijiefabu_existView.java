package com.xpg.hssy.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class Dianzhuangkuaijiefabu_existView extends LinearLayout {
	private Activity context;
	private ListView id_listview_msgs;
	private List<Pile> piles;
	private View view;
	private LinearLayout.LayoutParams params;
	private LinearLayout pile_none_exist_layout_id;
	private TextView statue;
	private SharedPreferences sp;
	private String user_id;
	private LoadingDialog loadingDialog = null ;
	public Dianzhuangkuaijiefabu_existView(Activity context) {
		super(context);
		this.context = context;
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		view = LayoutInflater
				.from(Dianzhuangkuaijiefabu_existView.this.context).inflate(
						R.layout.pile_display_quick_exist_layout, null);
		params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		id_listview_msgs = (ListView) view.findViewById(R.id.id_listview_msgs);
		pile_none_exist_layout_id = (LinearLayout) view
				.findViewById(R.id.pile_none_exist_layout_id);
		statue = (TextView) view.findViewById(R.id.statue_tv);
		getOwnPile();
		initLinstener();
	}

	private void initLinstener() {

	}

	private void getOwnPile() {
		loadingDialog = new LoadingDialog(context,R.string.please_wait) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().getOwnPile(user_id,
				new WebResponseHandler<List<Pile>>(context) {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(getContext(), e);
					}

					@Override
					public void onFailure(WebResponse<List<Pile>> response) {
						super.onFailure(response);
						TipsUtil.showTips(getContext(), response);
					}

					@Override
					public void onSuccess(WebResponse<List<Pile>> response) {
						super.onSuccess(response);
						piles = response.getResultObj();
						if (piles.size() == 0) {// 不存在可发布的桩
							if (view != null) {
								removeView(view);
							}
							id_listview_msgs.setVisibility(View.GONE);
							pile_none_exist_layout_id
									.setVisibility(View.VISIBLE);
							statue.setVisibility(View.GONE);

							addView(view, params);
						}

						else {// 存在可发布桩
							if (view != null) {
								removeView(view);
							}
							id_listview_msgs.setVisibility(View.VISIBLE);
							pile_none_exist_layout_id.setVisibility(View.GONE);
							statue.setVisibility(View.VISIBLE);

						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
					loadingDialog.dismiss();
					}

				});
	}

}
