package com.king.photo.zoom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerFixed extends android.support.v4.view.ViewPager {

	public ViewPagerFixed(Context context) {
		super(context);
	}

	public ViewPagerFixed(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
//重写viewPage,重写以下两个方法，解决google API关于appoint out of range 异常
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
