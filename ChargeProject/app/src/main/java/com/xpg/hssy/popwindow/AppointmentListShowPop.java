package com.xpg.hssy.popwindow;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.easy.adapter.EasyAdapter;
import com.easy.popup.EasyPopup;
import com.xpg.hssy.adapter.AppointmentAdapter;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;

public class AppointmentListShowPop extends EasyPopup {
	private Context context;
	private final static String TIME_FORMAT = "MM月dd日";


	private ListView lv_appoint_time_pop;
	private AppointmentAdapter adapter;

	public AppointmentListShowPop(Context context) {
		super(context, R.layout.appointment_time_pop,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.context = context;
		initView();
		initListener();

	}

	private void initListener() {
		lv_appoint_time_pop.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (itemOnClick != null) {

                    AppointmentListShowPop.this.dismiss();
                    itemOnClick.click(position);
                }
                adapter.select(position);
            }
        });
	}



    private void initView() {
		lv_appoint_time_pop = (ListView) findViewById(R.id.lv_appoint_time_pop);
		ArrayList<String> mList = new ArrayList<>();
		mList.add("显示所有");
		mList.add("最近3天");
		mList.add("最近14天");
		mList.add("最近30天");
		mList.add("最近90天");
		adapter = new AppointmentAdapter(context, mList);
		adapter.setMode(EasyAdapter.MODE_SINGLE_SELECT);
		adapter.select(0);
		lv_appoint_time_pop.setAdapter(adapter);
		LinearLayout parent = (LinearLayout) findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppointmentListShowPop.this.dismiss();
			}
		});
	}
	private ItemOnClick itemOnClick;

	public ItemOnClick getItemOnClick() {
		return itemOnClick;
	}

	public void setItemOnClick(ItemOnClick itemOnClick) {
		this.itemOnClick = itemOnClick;
	}

	public void unselectAll()
	{
		adapter.unselectAll();
	}

    public void select(int position) {
        adapter.select(position);
    }
	public interface ItemOnClick {
		public void click(int index);
	}
}
