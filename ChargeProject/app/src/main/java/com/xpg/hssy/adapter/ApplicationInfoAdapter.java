package com.xpg.hssy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpg.hssy.bean.ApplicationInfo;
import com.xpg.hssychargingpole.R;

public class ApplicationInfoAdapter extends BaseAdapter {
	private Context context = null;
	private List<ApplicationInfo> applicationinfos = new ArrayList<ApplicationInfo>();

	public ApplicationInfoAdapter(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public int getCount() {
		return applicationinfos.size();
	}

	@Override
	public ApplicationInfo getItem(int position) {
		return applicationinfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.map_item, null);
			convertView.setTag(new ListCell((TextView) convertView
					.findViewById(R.id.application_lable),
					(ImageView) convertView.findViewById(R.id.logo)));
		}

		ListCell lc = (ListCell) convertView.getTag();

		ApplicationInfo applicationinfo = getItem(position);

		lc.getAppName().setText(applicationinfo.getApplication());
		lc.getLogo().setImageDrawable(applicationinfo.getIcon());

		return convertView;
	}

	// public String getPackageName(int position){
	// return
	// }

	public void addAll(List<ApplicationInfo> student) {
		this.applicationinfos = student;
		notifyDataSetChanged();
	}

	public void clear() {
		applicationinfos.clear();
		notifyDataSetChanged();
	}

	private static class ListCell {

		public ListCell(TextView appName, ImageView logo) {
			this.appName = appName;
			this.logo = logo;
		}

		private TextView appName;
		private ImageView logo;

		public TextView getAppName() {
			return appName;
		}

		public void setAppName(TextView appName) {
			this.appName = appName;
		}

		public ImageView getLogo() {
			return logo;
		}

		public void setLogo(ImageView logo) {
			this.logo = logo;
		}

	}
}
