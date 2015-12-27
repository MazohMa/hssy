package com.xpg.hssy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreUtil {

	private SharedPreferences sp;
	Editor et;
	private static SharePreUtil instance;

	private SharePreUtil(Context context) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

	}

	public static SharePreUtil getInstance(Context context) {
		if (instance == null)
			return new SharePreUtil(context);
		return instance;
	}

	public void setSp(String str0, Object result) {
		et = sp.edit();
		et.putString(str0, result.toString());
		et.commit();

	}

	public String getSp(String str0) {
		return sp.getString(str0, "");
	}
}
