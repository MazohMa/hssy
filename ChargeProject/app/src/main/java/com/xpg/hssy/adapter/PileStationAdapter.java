package com.xpg.hssy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class PileStationAdapter extends EasyAdapter<Pile> {


	public PileStationAdapter(Context context, List<Pile> items) {
		super(context, items);

	}

	@Override
	protected ViewHolder newHolder() {


		return new ViewHolder() {

			private TextView tv_num;
			private TextView tv_name;
			private TextView tv_type;
			private ImageView iv_pile_imag;
			private LinearLayout ll_electricity;
			private TextView tv_electricity_percent;
			private SeekBar sb_soc;
			private Pile pile;

			@Override
			protected View init(LayoutInflater layoutInflater) {
				View view = layoutInflater.inflate(R.layout.adapter_item_station_more_piles, null);
				tv_num = (TextView) view.findViewById(R.id.tv_num);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				tv_type = (TextView) view.findViewById(R.id.tv_type);
				iv_pile_imag = (ImageView) view.findViewById(R.id.iv_pile_image);
				ll_electricity = (LinearLayout) view.findViewById(R.id.ll_electricity);
				tv_electricity_percent = (TextView) view.findViewById(R.id.tv_electricity_percent);
				sb_soc = (SeekBar) view.findViewById(R.id.sb_soc);
				sb_soc.setEnabled(false);
				return view;
			}

			@Override
			protected void update() {
				pile = get(position);
				if (pile == null) return;
				tv_num.setText(position + 1 + "");
				tv_type.setText(pile.getPileTypeAsString());
				tv_name.setText(pile.getPileName());
				if (pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile.STATE_PERSION_CHARGE_FINISHED) {
					if (pile.getPileType() == Pile.TYPE_DC) {
						ll_electricity.setVisibility(View.VISIBLE);
					} else {
						ll_electricity.setVisibility(View.GONE);
					}
					sb_soc.setProgress(pile.getChargingProgress());
					tv_electricity_percent.setText(pile.getChargingProgress() + "%");
				} else {
					ll_electricity.setVisibility(View.GONE);
				}

				if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_free);
				} else if (pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() == Pile.STATE_PERSION_CHARGE_FINISHED) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_charging);
				} else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_occupancy);
				} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_BOOK) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_free);
				} else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_repair);
				} else if (pile.getRunStatus() == Pile.STATE_PERSION_GPRS_DISCONNECT) {
					iv_pile_imag.setImageResource(R.drawable.pile_status_offline);
				} else {
					iv_pile_imag.setImageResource(R.drawable.pile_status_offline);
				}


//				if (pile.getRunStatus() == Pile.STATE_PERSION_FREE) iv_pile_imag.setImageResource(R.drawable.pile_status_free);
//				else if (pile.getRunStatus() == Pile.STATE_PERSION_IN_MAINTAIN || pile.getRunStatus() == Pile.STATE_PERSION_FAULT || pile.getRunStatus() ==
//						Pile.STATE_PERSION_GPRS_DISCONNECT)
//					iv_pile_imag.setImageResource(R.drawable.pile_status_service);
//				else if (pile.getRunStatus() == Pile.STATE_PERSION_CONNECT || pile.getRunStatus() == Pile.STATE_PERSION_CHARGING || pile.getRunStatus() ==
//						Pile.STATE_PERSION_CHARGE_FINISHED)
//					iv_pile_imag.setImageResource(R.drawable.pile_status_occupancy);
//				else iv_pile_imag.setImageResource(R.drawable.pile_status_service);
			}
		};
	}
}
