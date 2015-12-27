package com.xpg.hssy.popwindow;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.popup.EasyPopup;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年6月19日
 * @version 2.0.0
 */

public class MapPilesShowListPop extends EasyPopup implements
		OnItemClickListener {
	private Context context;
	private ListView lv;
	private PileListAdapter adapter;

	public MapPilesShowListPop(Context context, List<Pile> items) {
		super(context, R.layout.map_piles_list_pop, 600,
				LayoutParams.WRAP_CONTENT);
		this.context = context;
		adapter = new PileListAdapter(context, items);
		lv = (ListView) getContentView();
		lv.addHeaderView(new View(context));
		lv.setOnItemClickListener(this);
		lv.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
		adapter.select(position - 1);
		dismiss();
		// if (myOnItemClickListener != null) {
		// myOnItemClickListener.myOnItemClick((Pile) getSelectedItem());
		// }
	}

	public Object getItem(int position) {
		Object id = adapter.get(position);
		return id == null ? -1 : id;
	}

	public Object getSelectedItem() {
		Object object = adapter.getSelectedItem();
		return object == null ? -1 : object;
	}

	public void select(Integer item) {
		adapter.select(item);
	}

	static class PileListAdapter extends EasyAdapter<Pile> {
		public PileListAdapter(Context context, List<Pile> items) {
			super(context, items);
			setMode(EasyAdapter.MODE_RADIO_GROUP);
			select(0);
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				TextView tv;
				ImageView img_pile;

				@Override
				protected View init(android.view.LayoutInflater arg0) {
					View v = arg0.inflate(R.layout.map_piles_adapter_pop_item,
							null);
					tv = (TextView) v.findViewById(R.id.tv);
					img_pile = (ImageView) v.findViewById(R.id.img_pile);
					return v;
				}

				@Override
				protected void update() {
					Pile pile = get(position);
					tv.setText(pile.getPileNameAsString() + "");
					if (isSelected) {
					} else {
					}
				}
			};
		}
	}

	// private MyOnItemClickListener myOnItemClickListener;
	//
	// public MyOnItemClickListener getMyOnItemClickListener() {
	// return myOnItemClickListener;
	// }
	//
	// public void setMyOnItemClickListener(
	// MyOnItemClickListener myOnItemClickListener) {
	// this.myOnItemClickListener = myOnItemClickListener;
	// }
	//
	// public interface MyOnItemClickListener {
	// public void myOnItemClick(Pile pile);
	// }
}
