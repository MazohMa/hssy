package com.xpg.hssy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.easy.util.ScreenUtil;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年9月16日
 * @version 2.4.0
 */
public abstract class MyImageView extends ImageView {

		public MyImageView(Context paramContext) {
			super(paramContext);
		}

		public MyImageView(Context paramContext, AttributeSet paramAttributeSet) {
			super(paramContext, paramAttributeSet);
		}

		public MyImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
			super(paramContext, paramAttributeSet, paramInt);
		}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(widthSize, widthSize/2);
	}
}
