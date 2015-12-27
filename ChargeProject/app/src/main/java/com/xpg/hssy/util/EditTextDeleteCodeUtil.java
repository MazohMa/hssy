package com.xpg.hssy.util;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

/**
 * EditTextDeleteCodeUtil
 * 
 * @author Mazoh
 * @createDate 2015年5月14日
 * @version 2.0.0
 * @deprecated 请使用TextViewDrawableClickUtil
 */
public class EditTextDeleteCodeUtil {
	/**
	 * 点击右侧drawableRight图片重置EditText值
	 * 
	 * @param et
	 *            editText
	 * @return
	 */
	public static void delete(final EditText et) {
		et.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// getCompoundDrawables() 可以获取一个长度为4的数组，
				// 存放drawableLeft，Right，Top，Bottom四个图片资源对象
				// index=2 表示的是 drawableRight 图片资源对象
				Drawable drawable = et.getCompoundDrawables()[2];
				if (drawable == null)
					return false;

				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				// drawable.getIntrinsicWidth() 获取drawable资源图片呈现的宽度
				if (event.getX() > et.getWidth() - et.getPaddingRight()
						- drawable.getIntrinsicWidth()) {
					// 进入这表示图片被选中，可以处理相应的逻辑了
					if (!"".equals(et.getText().toString().trim())) {
						et.setText("");
						return false;
					}
				}

				return false;
			}
		});
	}
}
