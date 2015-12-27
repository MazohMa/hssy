package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.db.pojo.ExpenseRecord;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class ExpenseRecordAdapter extends EasyAdapter<ExpenseRecord> {

	private final static String TIME_FORMAT = "yyyy-MM-dd HH:mm";

	public ExpenseRecordAdapter(Context context, List<ExpenseRecord> items) {
		super(context, items);
	}

	@Override
	protected ViewHolder newHolder() {


		return new ViewHolder() {

			private TextView tv_expense_count;
			private TextView tv_expense_time;
			private TextView tv_pay_order_id;
			private ExpenseRecord expenseRecord;

			@Override
			protected View init(LayoutInflater layoutInflater) {
				View view = layoutInflater.inflate(R.layout.adapter_expense_record, null);
				tv_expense_count = (TextView) view.findViewById(R.id.tv_expense_count);
				tv_expense_time = (TextView) view.findViewById(R.id.tv_expense_time);
				tv_pay_order_id = (TextView) view.findViewById(R.id.tv_pay_order_id);
				return view;
			}

			@Override
			protected void update() {
				expenseRecord = get(position);
				if (expenseRecord == null) return;
				tv_expense_count.setText(CalculateUtil.formatDefaultNumber(expenseRecord.getPayMoney()) + "个");
				tv_expense_time.setText(TimeUtil.format(expenseRecord.getPayTime(), TIME_FORMAT));
				tv_pay_order_id.setText(expenseRecord.getOrderNo());
			}
		};
	}
}
