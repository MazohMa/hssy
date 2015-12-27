package com.xpg.hssy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @Description
 * @author Black Jack
 * @createDate 2015年7月28日
 * @version 1.0.0
 */
public class SideBarView extends LinearLayout {
	private barItemClickListener listener;

	public SideBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public SideBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SideBarView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		float y = event.getY();// 点击y坐标
		float height = getHeight();
		int childCount = getChildCount();
		int index = (int) (y / height * childCount);

		if (listener != null && index > -1 && index < childCount) {
			listener.onBarItemClick(index);
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		int childCount = getChildCount();
		if (childCount > 0) {
			int itemHeight = getHeight() / childCount;
			for (int i = 0; i < childCount; i++) {
				LayoutParams params = (LayoutParams) getChildAt(i)
						.getLayoutParams();
				params.height = itemHeight;
				getChildAt(i).setLayoutParams(params);
			}
		}
		super.onDraw(canvas);
	}

	public void setOnBarItemClickListener(barItemClickListener listener) {
		this.listener = listener;
	}

	public interface barItemClickListener {
		void onBarItemClick(int index);
	}
}
