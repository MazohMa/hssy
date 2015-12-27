package com.king.photo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;

/**
 * 存放所有的list在最后退出时一起关闭
 * 
 */
public class PublicWay {
	public static List<Activity> activityList = new ArrayList<Activity>();
	public static Map<String, Object> coverWithIntrols = new HashMap<String, Object>();
	public static int num = 4;

}
