package com.xpg.hssy.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class UIUtil {

	public static void hideSoftInput(final Activity context, View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(context);
					return false;
				}

			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				View innerView = ((ViewGroup) view).getChildAt(i);

				hideSoftInput(context, innerView);
			}
		}
	}

	private static void hideSoftKeyboard(Activity context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = context.getCurrentFocus();
		if (currentFocus != null) {
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		}
	}
	public static void setImageWithAndHeight(ImageView imageView,int width,int height) {
		imageView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
	}

}
