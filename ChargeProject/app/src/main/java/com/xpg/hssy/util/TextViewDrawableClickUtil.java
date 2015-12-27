package com.xpg.hssy.util;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * TextViewDrawableClickUtil
 *
 * @author BlackJack
 * @version 1.0.0
 * @createDate 2015年8月19日
 */
public class TextViewDrawableClickUtil {
	/**
	 * 点击右侧drawableRight图片重置TextView值
	 *
	 * @param et editText
	 * @return
	 */
	public static void setRightDrawableDelete(final EditText et) {
		setRightDrawableClick(et, new onDrawableClickListener() {
			@Override
			public void onDrawableClick(View v) {
				if (!TextUtils.isEmpty(et.getText())) {
					et.setText("");
				}
			}
		});
	}

	/**
	 * 仅设定topDrawable
	 *
	 * @param tv
	 * @param listener
	 */
	public static void setTopDrawableClick(final TextView tv, final onDrawableClickListener listener) {
		setTextViewDrawableClick(tv, listener, null, null, null);
	}

	/**
	 * 仅设定leftDrawable
	 *
	 * @param tv
	 * @param listener
	 */
	public static void setLeftDrawableClick(final TextView tv, final onDrawableClickListener listener) {
		setTextViewDrawableClick(tv, null, listener, null, null);
	}

	/**
	 * 仅设定rightDrawable
	 *
	 * @param tv
	 * @param listener
	 */
	public static void setRightDrawableClick(final TextView tv, final onDrawableClickListener listener) {
		setTextViewDrawableClick(tv, null, null, listener, null);
	}

	/**
	 * 仅设定bottomDrawable
	 *
	 * @param tv
	 * @param listener
	 */
	public static void setBottomDrawableClick(final TextView tv, final onDrawableClickListener listener) {
		setTextViewDrawableClick(tv, null, null, null, listener);
	}

	/**
	 * 一次性设定多个drawableOnClickListener
	 *
	 * @param tv
	 * @param leftListener
	 * @param topListener
	 * @param rightListener
	 * @param bottomListener
	 */
	public static void setTextViewDrawableClick(final TextView tv, final onDrawableClickListener leftListener, final onDrawableClickListener topListener,
	                                            final onDrawableClickListener rightListener, final onDrawableClickListener bottomListener) {
		tv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// getCompoundDrawables() 可以获取一个长度为4的数组，
				// 存放drawableLeft，Right，Top，Bottom四个图片资源对象
				// index=2 表示的是 drawableRight 图片资源对象
				Drawable leftDrawable = tv.getCompoundDrawables()[0];
				Drawable topDrawable = tv.getCompoundDrawables()[1];
				Drawable rightDrawable = tv.getCompoundDrawables()[2];
				Drawable bottomDrawable = tv.getCompoundDrawables()[3];

				if (event.getAction() != MotionEvent.ACTION_UP) return false;

				// Drawable.getIntrinsicWidth() 获取drawable资源图片呈现的宽度

				if (leftDrawable != null && leftListener != null && event.getX() <tv.getPaddingLeft() + leftDrawable.getIntrinsicWidth()) {
					leftListener.onDrawableClick(v);
				} else if (topDrawable != null && topListener != null && event.getY() < tv.getPaddingTop() + topDrawable.getIntrinsicHeight()) {
					topListener.onDrawableClick(v);
				} else if (rightDrawable != null && rightListener != null && event.getX() > tv.getWidth() - tv.getPaddingRight() - rightDrawable
						.getIntrinsicWidth()) {
					rightListener.onDrawableClick(v);
				} else if (bottomDrawable != null && bottomListener != null && event.getY() > tv.getHeight() - tv.getPaddingBottom() - bottomDrawable
						.getIntrinsicHeight()) {
					bottomListener.onDrawableClick(v);
				}
				return false;
			}
		});
	}

	public interface onDrawableClickListener {
		void onDrawableClick(View v);
	}
}
