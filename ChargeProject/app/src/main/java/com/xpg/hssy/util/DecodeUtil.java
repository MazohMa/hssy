package com.xpg.hssy.util;

import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class DecodeUtil {
	public static String decode(Result result) {
		if (TextUtils.isEmpty(result.getText())) {
			return null;
		}
		if (result.getBarcodeFormat() == BarcodeFormat.QR_CODE) {
			// 二维码解析
			String[] messages = result.getText().split("\n");
			for (String line : messages) {
				if (line == null) {
					continue;
				}
				line = line.trim();
				String[] s = line.split(":");
				if (s.length <= 1) {
					s = line.split("：");
				}
				if (s.length <= 1) {
					continue;
				}
				String key = s[0];
				String value = s[1];
				if (key == null || value == null) {
					continue;
				}
				if (key.contains("phonenumber")) {
					return value.trim();
				}
			}
		}
		return null;
	}
}
