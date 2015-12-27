package com.xpg.hssy.popwindow;

import android.content.Context;
import android.view.View.OnClickListener;

import com.easy.popup.EasyPopup;
import com.xpg.hssychargingpole.R;

public class SearchTypeSelectPopWindow extends EasyPopup {
	public SearchTypeSelectPopWindow(Context context) {
		super(context, R.layout.pop_search_type);
	}

	public void setFindPileListener(OnClickListener listener) {
		findViewById(R.id.iv_icon_pile).setOnClickListener(listener);
		findViewById(R.id.tv_find_pile).setOnClickListener(listener);
	}

	public void setFindAddressListener(OnClickListener listener) {
		findViewById(R.id.iv_icon_address).setOnClickListener(listener);
		findViewById(R.id.tv_find_address).setOnClickListener(listener);
	}
}
