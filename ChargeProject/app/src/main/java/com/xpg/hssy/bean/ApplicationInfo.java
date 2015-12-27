package com.xpg.hssy.bean;

import android.graphics.drawable.Drawable;

public class ApplicationInfo {
	String Application;
	Drawable icon;
	String packageName;

	public ApplicationInfo(String Application, Drawable icon) {
		this.Application = Application;
		this.icon = icon;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public ApplicationInfo() {
	}

	public String getApplication() {
		return Application;
	}

	public void setApplication(String application) {
		Application = application;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
