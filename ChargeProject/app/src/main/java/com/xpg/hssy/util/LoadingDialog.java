package com.xpg.hssy.util;

import android.app.ProgressDialog;
import android.content.Context;

class LoadingDialog {

	ProgressDialog mypDialog;
	Context context;

	public boolean isShowing() {
		if (mypDialog == null)
			return false;
		return mypDialog.isShowing();
	}

	public void show(Context context, String text, boolean cancelable) {
		this.context = context;
		this.dismiss();
		try {
			mypDialog = null;
			mypDialog = new ProgressDialog(context);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage(text);
			mypDialog.setIndeterminate(false);
			mypDialog.setCancelable(cancelable);
			mypDialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dismiss() {
		if (mypDialog != null && mypDialog.isShowing()) {
			mypDialog.cancel();
			mypDialog.dismiss();
			mypDialog = null;
		}
	}

}
