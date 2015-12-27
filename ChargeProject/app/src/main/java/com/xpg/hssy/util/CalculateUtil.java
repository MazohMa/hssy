package com.xpg.hssy.util;

import android.content.Context;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.easy.util.SPFile;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssychargingpole.R;

/**
 * Created by black-Gizwits on 2015/08/21.
 * 封装了一些应用中通用的计算方法
 */
public final class CalculateUtil {

	private CalculateUtil() {
		throw new Error("don't instantiation util !");
	}

	private static final String DISTANCE_FORMAT_M = "%.1fm";
	private static final String DISTANCE_FORMAT_KM = "%.1fkm";
	private static final String PRICE_FORMAT = "￥ %.2f/kWh";
	private static final String QUANTITY_FORMAT = "%.2f kWh";
	private static final String BILL_FORMAT = "￥ %.2f";
	private static final String DEFAULT_NUMBER_FORMAT = "%.2f";
	private static final String RECHARGE_MONEY = "￥ %.2f";

	public enum DistanceType {
		baseMapCenter,
		baseUserLocation
	}

	private static DistanceType defaultDistanceType = DistanceType.baseUserLocation;

	public static double calculateDistance(Context context, LatLng location) {
		return calculateDistance(context, defaultDistanceType, location);
	}

	/**
	 * 根据输入和距离类型计算距离
	 *
	 * @param context
	 * @param distanceType
	 * @param location
	 * @return
	 */
	public static double calculateDistance(Context context, DistanceType distanceType, LatLng location) {
		SPFile sp = new SPFile(context, "config");
		double latitude;
		double longitude;
		switch (distanceType) {
			case baseMapCenter: {
				latitude = Double.valueOf(sp.getString(KEY.CONFIG.LATITUDE, "-1"));
				longitude = Double.valueOf(sp.getString(KEY.CONFIG.LONGITUDE, "-1"));
				break;
			}
			case baseUserLocation: {
				latitude = Double.valueOf(sp.getString(KEY.CONFIG.MY_LATITUDE, "-1"));
				longitude = Double.valueOf(sp.getString(KEY.CONFIG.MY_LONGITUDE, "-1"));
				break;
			}
			default: {
				latitude = -1;
				longitude = -1;
			}
		}
		if (latitude == -1 || longitude == -1) return -1;
		LatLng sLatLng = new LatLng(latitude, longitude);
		double distance = DistanceUtil.getDistance(sLatLng, location);
		return distance;
	}

	public static void infuseDistance(Context context, TextView textView, LatLng location) {
		infuseDistance(context, textView, defaultDistanceType, location);
	}

	public static void infuseDistance(Context context, TextView textView, DistanceType distanceType, LatLng location) {
		double distance = calculateDistance(context, distanceType, location);
		infuseDistance(textView, distance);
	}

	/**
	 * 根据输入的距离确定填充的字符串格式和单位
	 * 小于0m,显示距离未知,大于0小于1000m,单位是m,大于1000m,转换成km
	 *
	 * @param textView
	 * @param distance
	 */
	public static void infuseDistance(TextView textView, double distance) {
		if (distance > 0) {
			if (distance < 1000) {

				textView.setText(String.format(DISTANCE_FORMAT_M, distance));
			} else {
				distance = distance / 1000;
				textView.setText(String.format(DISTANCE_FORMAT_KM, distance));
			}
		} else {
			textView.setText(R.string.distance_unknow);
		}
	}

	/**
	 * 格式化输出的价格,并注入到textView中,例子: ￥ 1.20/kWh
	 *
	 * @param textView
	 * @param price
	 */
	public static void infusePrice(TextView textView, double price) {
		textView.setText(String.format(PRICE_FORMAT, price));
	}

	public static String formatPirce(double price) {
		return String.format(PRICE_FORMAT, price);
	}

	public static String formatQuantity(double quantity) {
		return String.format(QUANTITY_FORMAT, quantity);
	}

	public static String formatBill(double bill) {
		return String.format(BILL_FORMAT, bill);
	}

	public static String formatDefaultNumber(double number) {
		return String.format(DEFAULT_NUMBER_FORMAT, number);
	}

	public static String formatRechargeMoney(float money){
		return String.format(RECHARGE_MONEY, money);
	}

}
