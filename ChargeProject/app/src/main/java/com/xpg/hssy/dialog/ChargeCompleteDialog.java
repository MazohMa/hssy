package com.xpg.hssy.dialog;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description
 * @createDate 2015年12月10日
 */

public class ChargeCompleteDialog extends BaseDialog {
	private TextView tv_tip;
	public TextView getTv_tip() {
		return tv_tip;
	}

	public void setTv_tip(TextView tv_tip) {
		this.tv_tip = tv_tip;
	}
	public ChargeCompleteDialog(final Context context) {
		super(context);
		setContentView(R.layout.charge_complete_dialog);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		this.setCanceledOnTouchOutside(false);
		Log.i("tag","ChargeCompleteDialog构造") ;
	}


}
