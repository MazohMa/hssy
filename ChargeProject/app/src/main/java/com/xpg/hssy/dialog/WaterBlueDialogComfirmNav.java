package com.xpg.hssy.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.easy.adapter.EasyAdapter;
import com.easy.util.ToastUtil;
import com.xpg.hssy.util.NavigationUtil;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class WaterBlueDialogComfirmNav extends BaseDialog {

	private NavigationAdapter adapter;

	private double myLat;
	private double myLon;
	private double desLat;
	private double desLon;

	private class Navigation {
		String name;
		int iconId;

		Navigation(String name, int iconId) {
			super();
			this.name = name;
			this.iconId = iconId;
		}
	}

	private final Navigation BAIDU = new Navigation("百度地图",
			R.drawable.app_icon2);
	private final Navigation GAODE = new Navigation("高德地图",
			R.drawable.app_icon1);

	public WaterBlueDialogComfirmNav(final Context context) {
		super(context);
		setContentView(R.layout.water_blue_dialog_list);
		setTitle("地图导航");
		ListView lv_content = (ListView) findViewById(R.id.lv_content);
		adapter = new NavigationAdapter(context);
		adapter.setMode(EasyAdapter.MODE_RADIO_GROUP);
		if (NavigationUtil.isInstalledBaidu()) {
			adapter.add(BAIDU);
		}
		if (NavigationUtil.isInstalledGaode()) {
			adapter.add(GAODE);
		}
		if (adapter.getCount() == 1) {
			adapter.select(0);
		}
		lv_content.setAdapter(adapter);
		lv_content.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				adapter.select(position);
			}
		});
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (adapter.getSelectedItem() == null) {
					ToastUtil.show(context, "请选择地图");
				} else if (adapter.getSelectedItem() == BAIDU) {
					NavigationUtil.startBaidu(context, myLat, myLon, desLat,
							desLon);
					dismiss();
				} else if (adapter.getSelectedItem() == GAODE) {
					LatLng ll = NavigationUtil.baidu2Gaode(new LatLng(desLat,
							desLon));
					NavigationUtil.startGaode(context, ll.latitude,
							ll.longitude);
					dismiss();
				}
			}
		});
	}

	private class NavigationAdapter extends EasyAdapter<Navigation> {

		public NavigationAdapter(Context context) {
			super(context);
		}

		@Override
		protected EasyAdapter<Navigation>.ViewHolder newHolder() {
			return new ViewHolder() {
				ImageView iv_icon;
				TextView tv_name;
				ImageView iv_ok;

				@Override
				protected View init(LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.adapter_navi, null);
					iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
					tv_name = (TextView) v.findViewById(R.id.tv_name);
					iv_ok = (ImageView) v.findViewById(R.id.iv_ok);
					return v;
				}

				@Override
				protected void update() {
					Navigation navi = get(position);
					iv_icon.setImageResource(navi.iconId);
					tv_name.setText(navi.name);
					if (isSelected) {
						iv_ok.setVisibility(View.VISIBLE);
						;
					} else {
						iv_ok.setVisibility(View.INVISIBLE);
						;
					}
				}
			};
		}
	}

	public void show(double myLat, double myLon, double desLat, double desLon) {
		if (adapter.getCount() == 0) {
			ToastUtil.show(getContext(), "没有安装导航软件");
			return;
		}
		super.show();
		this.myLat = myLat;
		this.myLon = myLon;
		this.desLat = desLat;
		this.desLon = desLon;
	}
}
