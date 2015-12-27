package com.xpg.hssy.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年4月24日
 * @version 1.0.0
 */

public class AnimationDrawableUtil {
	// public static final int[] IDS_CONNECT_BT = { R.drawable.bluetooth1,
	// R.drawable.bluetooth2, R.drawable.bluetooth3,
	// R.drawable.bluetooth4, R.drawable.bluetooth5,
	// R.drawable.bluetooth6, R.drawable.bluetooth7,
	// R.drawable.bluetooth8, R.drawable.bluetooth9 };
	// public static final int[] IDS_CONNECT_GUN = { R.drawable.connect_power1,
	// R.drawable.connect_power2, R.drawable.connect_power3,
	// R.drawable.connect_power3, R.drawable.connect_power3,
	// R.drawable.connect_power3, R.drawable.connect_power3,
	// R.drawable.connect_power3 };
	// public static final int[] IDS_CONNECTING_BT = { R.drawable.connecting01,
	// R.drawable.connecting02, R.drawable.connecting03,
	// R.drawable.connecting04, R.drawable.connecting05,
	// R.drawable.connecting06, R.drawable.connecting07,
	// R.drawable.connecting08, R.drawable.connecting09 };
	// public static final int[] IDS_ELECTRIC_QUANTITY = {
	// R.drawable.home_circle1, R.drawable.home_circle2,
	// R.drawable.home_circle3, R.drawable.home_circle4,
	// R.drawable.home_circle5, R.drawable.home_circle6,
	// R.drawable.home_circle7, R.drawable.home_circle8,
	// R.drawable.home_circle9 };

	private AnimationDrawableUtil() {
	}

	public static AnimationDrawable create(Resources res, int[] resIds,
			int duration, boolean isOneShot, BitmapFactory.Options option) {
		AnimationDrawable ad = new AnimationDrawable();
		for (int id : resIds) {
			Bitmap bmp = BitmapFactory.decodeResource(res, id, option);
			ad.addFrame(new BitmapDrawable(res, bmp), duration);
		}
		ad.setOneShot(isOneShot);
		return ad;
	}
}
