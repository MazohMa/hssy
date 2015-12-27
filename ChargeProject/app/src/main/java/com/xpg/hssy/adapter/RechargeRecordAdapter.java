package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.db.pojo.RechargeRecord;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class RechargeRecordAdapter extends EasyAdapter<RechargeRecord> {

	private final static String TIME_FORMAT = "yyyy-MM-dd HH:mm";

	public RechargeRecordAdapter(Context context, List<RechargeRecord> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {


		return new ViewHolder() {

			private TextView tv_recharge_count;
			private TextView tv_recharge_time;
			private TextView tv_recharge_type;
			private RechargeRecord rechargeRecord;

			@Override
			protected View init(LayoutInflater layoutInflater) {
				View view = layoutInflater.inflate(R.layout.adapter_recharge_record, null);
				tv_recharge_count = (TextView) view.findViewById(R.id.tv_recharge_count);
				tv_recharge_time = (TextView) view.findViewById(R.id.tv_recharge_time);
				tv_recharge_type = (TextView) view.findViewById(R.id.tv_recharge_type);
				return view;
			}

			@Override
			protected void update() {
				rechargeRecord = get(position);
				if (rechargeRecord == null) return;
				tv_recharge_count.setText(CalculateUtil.formatRechargeMoney(rechargeRecord.getRechargeMoney()));
				tv_recharge_time.setText(TimeUtil.format(rechargeRecord.getRechargeTime(), TIME_FORMAT));
				tv_recharge_type.setText(rechargeRecord.getRechargeTypeToStringId());
			}
		};
	}
}
