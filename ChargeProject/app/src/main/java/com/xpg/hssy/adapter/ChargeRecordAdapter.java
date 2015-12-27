package com.xpg.hssy.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.db.pojo.Record;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

/**
 * @author Joke Huang
 * @version 1.0.0
 * @Description
 * @createDate 2015年2月3日
 */

public class ChargeRecordAdapter extends EasyAdapter<Record> {
    public static final int SUM_MENEY = 1000;
    public static final String TIME_START_FORMAT = "yyyy-MM-dd  HH:mm";
    public static final String TIME_END_FORMAT = "HH:mm";

    private Handler mHandler;

    public ChargeRecordAdapter(Handler mHandler, Context context,
                               List<Record> items) {
        super(context, items);
        this.mHandler = mHandler;
        setMode(EasyAdapter.MODE_NON);// 默认模式

    }

    @Override
    protected ViewHolder newHolder() {
        return new ViewHolder() {
            private RelativeLayout rl_order_id,rl_sequence_id,rl_charge_time,rl_charge_electric,rl_charge_money;
            private TextView tv_order_id;
            private TextView tv_charge_time;
            private TextView tv_charge_electric;
            private TextView tv_charge_money;
            private TextView tv_sequence;
            private LinearLayout check_box_layout;
            private CheckBox choice_edit;

            @Override
            protected void update() {
                Record r = get(position);
                if (mode == EasyAdapter.MODE_CHECK_BOX) {
                    check_box_layout.setVisibility(View.VISIBLE);
                    if (isSelected) {
                        choice_edit.setChecked(true);
                    } else {
                        choice_edit.setChecked(false);
                    }
                } else if (mode == EasyAdapter.MODE_NON) {
                    check_box_layout.setVisibility(View.GONE);
                }
                if (EmptyUtil.isEmpty(r.getOrderId())
                        || r.getOrderId().equals("0")) {
                    //家人桩主
                    rl_order_id.setVisibility(View.GONE);
                    rl_sequence_id.setVisibility(View.VISIBLE);
                    rl_charge_money.setVisibility(View.GONE);
                    tv_sequence.setText(r.getSequence() + "");
                } else {
                    //租户
                    rl_order_id.setVisibility(View.VISIBLE);
                    rl_charge_money.setVisibility(View.VISIBLE);
                    rl_sequence_id.setVisibility(View.GONE);
                    tv_order_id.setText(r.getOrderId() + "");
                }
                if (r.getStartTime() != null && r.getEndTime() != null) {
                    String startTime = TimeUtil.format(r.getStartTime(),
                            TIME_START_FORMAT);
                    String endTime = TimeUtil.format(r.getEndTime(), TIME_END_FORMAT);
                    tv_charge_time.setText(startTime + " — " + endTime);
                }
                tv_charge_electric.setText(String.format("%.2f kWh",
                        r.getQuantity() / 100f));
                tv_charge_money.setText(r.getChargePrice() == null ? "未统计" : "￥ " + String.format("%.2f ",
                        r.getChargePrice()));
                choice_edit
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    select(position);
                                } else {
                                    unselect(position);
                                }
                                mHandler.sendEmptyMessage(SUM_MENEY);
                            }
                        });
            }

            @Override
            protected View init(LayoutInflater arg0) {
                View v = arg0.inflate(R.layout.item_charge_record, null);
                check_box_layout = (LinearLayout) v
                        .findViewById(R.id.check_box_layout);
                choice_edit = (CheckBox) v.findViewById(R.id.choice_edit);
                rl_order_id = (RelativeLayout) v.findViewById(R.id.rl_order_id);
                tv_order_id = (TextView) v.findViewById(R.id.tv_order_id);
                rl_sequence_id = (RelativeLayout) v.findViewById(R.id.rl_sequence_id);
                tv_sequence = (TextView) v.findViewById(R.id.tv_sequence);
                rl_charge_time = (RelativeLayout)v.findViewById(R.id.rl_charge_time) ;
                tv_charge_time = (TextView) v.findViewById(R.id.tv_charge_time);
                rl_charge_electric = (RelativeLayout)v.findViewById(R.id.rl_charge_electric) ;
                tv_charge_electric = (TextView) v
                        .findViewById(R.id.tv_charge_electric);

                rl_charge_money =(RelativeLayout) v.findViewById(R.id.rl_charge_money) ;


                tv_charge_money = (TextView) v
                        .findViewById(R.id.tv_charge_money);
                return v;
            }
        };
    }

}
