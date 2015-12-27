package com.xpg.hssy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DianzhuangSettingTabWidget extends LinearLayout {
	public DianzhuangSettingTabWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public DianzhuangSettingTabWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DianzhuangSettingTabWidget(Context context) {
		super(context);
	}

	private OnTabSelectedListener mTabListener;

	public interface OnTabSelectedListener {
		void onTabSelected(int index);
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener) {
		this.mTabListener = listener;
	}

}
