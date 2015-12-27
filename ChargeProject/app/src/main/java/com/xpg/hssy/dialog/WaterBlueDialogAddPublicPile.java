package com.xpg.hssy.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.main.activity.callbackinterface.ISelectItemOperator;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description
 * @createDate 2015年9月10日
 */

public class WaterBlueDialogAddPublicPile extends BaseDialog {

	public MyAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(MyAdapter adapter) {
		this.adapter = adapter;
	}

	private MyAdapter adapter;
	private int type = -1;
	private ISelectItemOperator iSelectItemOperator;
	private String title;

	public WaterBlueDialogAddPublicPile(final Context context, final ISelectItemOperator
			iSelectItemOperator, final String title) {
		super(context);
		this.iSelectItemOperator = iSelectItemOperator;
		setContentView(R.layout.water_blue_dialog_add_pile_type_list);
		setTitle(title);
		ListView lv_content = (ListView) findViewById(R.id.lv_content);
		adapter = new MyAdapter(context);
		adapter.setMode(EasyAdapter.MODE_RADIO_GROUP);
		lv_content.setAdapter(adapter);
		this.setCanceledOnTouchOutside(false);
		lv_content.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				adapter.select(position);
				if (iSelectItemOperator != null) {
					iSelectItemOperator.ItemSelected(position, type);
				}
				dismiss();
			}
		});
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	public void setDialogTitle(String title) {
		this.title = title;
		setTitle(title);
	}

	public String getDialogTitle() {
		return title;
	}

	public WaterBlueDialogAddPublicPile(final Context context, final ISelectItemOperator
			iSelectItemOperator, String title, final int type) {
		super(context);
		this.iSelectItemOperator = iSelectItemOperator;
		this.type = type;
		setContentView(R.layout.water_blue_dialog_add_pile_type_list);
		setTitle(title);
		ListView lv_content = (ListView) findViewById(R.id.lv_content);
		adapter = new MyAdapter(context);
		adapter.setMode(EasyAdapter.MODE_RADIO_GROUP);
		lv_content.setAdapter(adapter);
		this.setCanceledOnTouchOutside(false);
		lv_content.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				adapter.select(position);
				if (iSelectItemOperator != null) {
					iSelectItemOperator.ItemSelected(position, type);
				}
				dismiss();
			}
		});
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	public void addDatas(ArrayList<String> datas) {
		adapter.clear();
		adapter.add(datas);
	}

	public class MyAdapter extends EasyAdapter<String> {

		public MyAdapter(Context context) {
			super(context);
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				TextView tv_content;

				@Override
				protected View init(LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.adapter_add_pile, null);
					tv_content = (TextView) v.findViewById(R.id.tv_content);
					return v;
				}

				@Override
				protected void update() {
					String data = (String) getItem(position);
					tv_content.setText(data);
				}
			};
		}
	}

}
