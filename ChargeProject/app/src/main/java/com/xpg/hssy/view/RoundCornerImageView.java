package com.xpg.hssy.view;

import com.easy.util.ScreenUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年5月17日
 * @version 1.0.0
 */
public class RoundCornerImageView extends ImageView {
	private Path clipPath;

	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (clipPath == null) {
			clipPath = new Path();
			int w = this.getWidth();
			int h = this.getHeight();
			int roundSize = (int)((ScreenUtil.getShortSide(getContext())) / 50f);
			clipPath.addRoundRect(new RectF(0, 0, w, h), roundSize, roundSize,
					Path.Direction.CW);
		}
		canvas.clipPath(clipPath);
		super.onDraw(canvas);
	}

}
