package com.xpg.hssy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.xpg.hssy.adapter.CityListAdapter.CityItem;
import com.xpg.hssy.db.pojo.DistrictData;
import com.xpg.hssy.main.activity.callbackinterface.ISelectCityOperator;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Black Jack
 * @createDate 2015年7月28日
 * @version 1.0.0
 */
public class CityListAdapter<T extends Context & ISelectCityOperator> extends
		EasyAdapter<CityItem> implements PinnedSectionListAdapter,
		SectionIndexer {

	private LayoutInflater mInflater;
	private ISelectCityOperator operator;
	private List<CityItem> sections;
	private OnClickListener onItemClick;

	public CityListAdapter(T context) {
		super(context);
		mInflater = LayoutInflater.from(context);
		sections = new ArrayList<>();
		this.operator = context;
	}

	private OnClickListener getOnItemClickListener() {

		if (onItemClick == null) {
			onItemClick = new OnClickListener() {

				@Override
				public void onClick(View v) {
					@SuppressWarnings("unchecked")
					DistrictData cityData = (DistrictData) v.getTag();
					operator.onCitySelected(cityData);
				}

			};
		}
		return onItemClick;
	}

	private View createCitysGirdRow(ViewGroup root, DistrictData[] district) {
		View girdRow = mInflater.inflate(R.layout.layout_citys_item, null);
		TextView[] citys = new TextView[3];
		citys[0] = (TextView) girdRow.findViewById(R.id.city1);
		citys[1] = (TextView) girdRow.findViewById(R.id.city2);
		citys[2] = (TextView) girdRow.findViewById(R.id.city3);
		int i = 0;
		int maxIndex = district.length < citys.length ? district.length
				: citys.length;
		for (; i < maxIndex; i++) {
			citys[i].setTag(district[i]);
			citys[i].setText(district[i].getDistrictName());
			citys[i].setOnClickListener(getOnItemClickListener());
			citys[i].setVisibility(View.VISIBLE);
		}
		for (; i < citys.length; i++) {
			citys[i].setVisibility(View.INVISIBLE);
		}
		return girdRow;
	};

	private void setCitysGirdItem(ViewGroup root, CityItem item) {
		int row = item.getCitys().length / 3;
		for (int i = 0; i < row; i++) {
			DistrictData[] citys = new DistrictData[3];
			System.arraycopy(item.getCitys(), i * 3, citys, 0, 3);
			root.addView(createCitysGirdRow(root, citys));
		}
		int leftCitysCount = item.getCitys().length % 3;
		if (leftCitysCount > 0) {
			DistrictData[] citys = new DistrictData[leftCitysCount];
			System.arraycopy(item.getCitys(), row * 3, citys, 0, leftCitysCount);
			root.addView(createCitysGirdRow(root, citys));
		}
	}

	@Override
	protected ViewHolder newHolder() {
		return new itemHolder();
	}

	private class itemHolder extends ViewHolder {

		private View view;
		private TextView tv_section;
		private LinearLayout ll_hot_citys;
		private LinearLayout ll_city;
		private TextView tv_city;
		private CityItem item;

		@Override
		protected View init(LayoutInflater inflater) {
			view = inflater.inflate(R.layout.layout_city_list_adapter_item,
					null);
			tv_section = (TextView) view.findViewById(R.id.tv_section);
			ll_hot_citys = (LinearLayout) view.findViewById(R.id.ll_hot_citys);
			ll_city = (LinearLayout) view.findViewById(R.id.ll_city);
			tv_city = (TextView) view.findViewById(R.id.tv_city);
			return view;
		}

		@Override
		protected void update() {
			item = get(position);
			switch (item.type) {
			case CityItem.ITEM_TYPE_SECTION: {
				ll_hot_citys.removeAllViewsInLayout();
				ll_hot_citys.setVisibility(View.GONE);
				tv_section.setText(item.getSection());
				tv_section.setVisibility(View.VISIBLE);
				ll_city.setVisibility(View.GONE);
				break;
			}
			case CityItem.ITEM_TYPE_CITY: {
				ll_hot_citys.removeAllViewsInLayout();
				ll_hot_citys.setVisibility(View.GONE);
				tv_city.setText(item.getCitys()[0].getDistrictName());
				tv_city.setTag(item.getCitys()[0]);
				tv_city.setOnClickListener(getOnItemClickListener());
				tv_section.setVisibility(View.GONE);
				ll_city.setVisibility(View.VISIBLE);
				break;
			}
			case CityItem.ITEM_TYPE_HOT_CITY: {
				ll_hot_citys.removeAllViewsInLayout();
				ll_hot_citys.setVisibility(View.VISIBLE);
				tv_section.setVisibility(View.GONE);
				ll_city.setVisibility(View.GONE);
				setCitysGirdItem(ll_hot_citys, item);
				break;
			}
			case CityItem.ITEM_TYPE_LOCATION: {
				ll_hot_citys.removeAllViewsInLayout();
				ll_hot_citys.setVisibility(View.VISIBLE);
				tv_section.setVisibility(View.GONE);
				ll_city.setVisibility(View.GONE);
				setCitysGirdItem(ll_hot_citys, item);
				break;
			}

			}
		}

	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		return get(position).type;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == CityItem.ITEM_TYPE_SECTION;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex > -1 && sectionIndex < sections.size()) {
			return sections.get(sectionIndex).listPosition;
		} else {
			return -1;
		}

	}

	@Override
	public int getSectionForPosition(int position) {
		if (position > -1 && position < getCount()) {
			return get(position).sectionPosition;
		} else {
			return -1;
		}
	}

	@Override
	public Object[] getSections() {
		return sections.toArray();
	}

	public void generateSection() {
		sections.clear();
		int sectionPosition = 0;
		for (int i = 0; i < items.size(); i++) {
			CityItem item = items.get(i);
			switch (item.type) {
			case CityItem.ITEM_TYPE_SECTION: {
				sectionPosition = sections.size();
				item.sectionPosition = sectionPosition;
				item.listPosition = i;
				sections.add(item);
				break;
			}
			default: {
				item.sectionPosition = sectionPosition;
				item.listPosition = i;
				break;
			}
			}
		}
	}

	public static class CityItem {
		public final static int ITEM_TYPE_SECTION = 0x00;
		public final static int ITEM_TYPE_CITY = 0x01;
		public final static int ITEM_TYPE_LOCATION = 0x02;
		public final static int ITEM_TYPE_HOT_CITY = 0x03;

		public final int type;
		private String section;
		private DistrictData[] citys;

		private int sectionPosition;
		private int listPosition;

		public CityItem(int type) {
			this.type = type;
			sectionPosition = -1;
			listPosition = -1;
		}

		public String getSection() {
			return section;
		}

		public void setSection(String section) {
			this.section = section;
		}

		public DistrictData[] getCitys() {
			return citys;
		}

		public void setCitys(DistrictData... citys) {
			this.citys = citys;
		}

		public int getType() {
			return type;
		}

		public int getSectionPosition() {
			return sectionPosition;
		}

		public int getListPosition() {
			return listPosition;
		}

	}

	@Override
	public void notifyDataSetChanged() {
		generateSection();
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		generateSection();
		super.notifyDataSetInvalidated();
	}

}
