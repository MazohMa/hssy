package com.xpg.hssy.view;

import android.graphics.Bitmap;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年5月13日
 * @version 1.0.0
 */

public class TimeCircleDot {
	private boolean isOn;
	private Bitmap bmpOn;
	private Bitmap bmpOff;
	private int x;
	private int y;

	public int getHeight() {
		return isOn ? bmpOn.getHeight() : bmpOff.getHeight();
	}

	public int getWidth() {
		return isOn ? bmpOn.getWidth() : bmpOff.getWidth();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Bitmap getBmp() {
		return isOn ? bmpOn : bmpOff;
	}

	public Bitmap getBmpOff() {
		return bmpOff;
	}

	public void setBmpOff(Bitmap bmpOff) {
		this.bmpOff = bmpOff;
	}

	public Bitmap getBmpOn() {
		return bmpOn;
	}

	public void setBmpOn(Bitmap bmpOn) {
		this.bmpOn = bmpOn;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

}
