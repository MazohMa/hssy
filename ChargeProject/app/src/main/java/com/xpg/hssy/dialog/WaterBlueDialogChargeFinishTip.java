package com.xpg.hssy.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年7月23日
 * @version 2.0.3
 */

public class WaterBlueDialogChargeFinishTip extends BaseDialog {
	private DismissActivityClick dismissActivityClick ;
	public DismissActivityClick getDismissActivityClick() {
		return dismissActivityClick;
	}

	public void setDismissActivityClick(DismissActivityClick dismissActivityClick) {
		this.dismissActivityClick = dismissActivityClick;
	}

	public WaterBlueDialogChargeFinishTip(final Context context) {
		super(context);
		setContentView(R.layout.water_blue_dialog_charge_finish_tip);
		WaterBlueDialogChargeFinishTip.this.setCanceledOnTouchOutside(false) ;
		Log.i("tag","WaterBlueDialogChargeFinishTip") ;
		setTitle("温馨提示");
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				WaterBlueDialogChargeFinishTip.this.dismiss() ; 
				if (dismissActivityClick != null) {
					dismissActivityClick.dismiss() ; 
				}
			}
		});
	}

	public interface DismissActivityClick{
		public void dismiss() ; 
	}
}
