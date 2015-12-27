package com.xpg.hssy.util;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TimeUtil
 *
 * @author Joke Huang
 * @version 1.0.0
 * @createDate 2014年3月22日
 */

public class TimeUtil {
	/**
	 * 解析时间
	 *
	 * @param time    时间字符串
	 * @param pattern 时间表达式
	 * @return
	 */
	public static Date parse(String time, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
			return sdf.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String format(Long time, String pattern, String defaultValue) {
		return time == null ? defaultValue : format(new Date(time), pattern);
	}

	public static String format(Long time, String pattern) {
		return format(time, pattern, "");
	}

	public static String format(long time, String pattern) {
		return format(new Date(time), pattern);
	}

	/**
	 * 返回时间
	 *
	 * @param duration 经过的毫秒数
	 * @return String
	 * HH小时mm分
	 */
	public static String formatDuration(long duration) {
		long sum_min = duration / 1000 / 60;
		long hour = sum_min / 60;
		long min = sum_min % 60;
		return hour + "小时" + min + "分";
	}

	/**
	 * 返回时间
	 *
	 * @param time 获取的毫秒数
	 * @return String
	 * HH小时mm分
	 */
	public static String formatDate(long time, String pattern) {
		long cur = System.currentTimeMillis();
		long duration = cur - time;
		long sum_min = duration / 1000 / 60;
		long hour = sum_min / 60;
		long min = sum_min % 60;
		if (min == 0 && hour == 0) {
			return "刚刚";
		} else if (min > 0 && hour == 0) {
			return min + "分钟前";
		} else if (hour < 24 && hour > 0) {
			return hour + "小时前";

		} else {
			return format(time, pattern);
		}
	}

	/**
	 * 格式化时间
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
			return sdf.format(date);
		} catch (Exception e) {
			return "";
		}
	}

	public static String initTime(long time_period) {
		if (time_period < 60) {
			return time_period + "秒";
		}
		int MM = (int) time_period / 60; // 共计分钟数
		if (MM < 60) {
			if (MM < 10) {
				return "0" + MM + "分";

			} else {
				return MM + "分";

			}
		} else {
			int hh = (int) MM / 60; // 共计小时数
			int min = (int) MM % 60; // 截取分钟
			if (min < 10) {
				return hh + "小时" + "0" + min + "分" + "";
			} else {
				return hh + "小时" + min + "分" + "";
			}

		}
	}
	public static  void initTime(long time_period, View view) {
		if (time_period < 60) {
			if (view instanceof TextView) {
				((TextView) view).setText(time_period + "秒");
			}
			return;
		}
		int MM = (int) time_period / 60; // 共计分钟数
		if (MM < 60) {
			if (MM < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText("0" + MM + "分");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(MM + "分");
				}
			}
		} else {
			int hh = (int) MM / 60; // 共计小时数
			int min = (int) MM % 60; // 截取分钟
			if (min < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + "0" + min + "分" + "");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + min + "分" + "");
				}
			}

		}
	}


	/**
	 * 描述之前的某个时刻距离现在多久，一般用于检查版本更新
	 *
	 * @param currentTime
	 * @param lastTime
	 * @return
	 */
	public static String toLastTimeString(long currentTime, long lastTime) {
		if (currentTime < lastTime) {
			return "";
		}
		if (lastTime == 0) {
			return "";
		}

		long subTime = currentTime - lastTime;
		subTime /= 1000 * 60;
		if (subTime == 0) {
			return "刚刚";
		}
		if (subTime < 60) {
			return subTime + "分钟前";
		}
		subTime /= 60;
		if (subTime < 24) {
			return subTime + "小时前";
		}
		subTime /= 24;
		if (subTime < 4) {
			return subTime + "天前";
		}
		Calendar currentCalendar = Calendar.getInstance();
		Calendar lastCalendar = Calendar.getInstance();
		currentCalendar.setTimeInMillis(currentTime);
		lastCalendar.setTimeInMillis(lastTime);
		if (currentCalendar.get(Calendar.YEAR) == lastCalendar.get(Calendar.YEAR)) {
			return (lastCalendar.get(Calendar.MONTH) + 1) + "月" + lastCalendar.get(Calendar.DATE) + "日";
		}
		return lastCalendar.get(Calendar.YEAR) + "年" + (lastCalendar.get(Calendar.MONTH) + 1) + "月" + lastCalendar.get(Calendar.DATE) + "日";
	}

	/**
	 * 检查两个时间段之间是否相互独立,没有相交或包含的部分
	 *
	 * @param firstTimeStart
	 * @param firstTimeEnd
	 * @param secondTimeStart
	 * @param secondTimeEnd
	 * @return 如果独立则返回true, 否则返回false
	 */
	public static boolean checkShareTimeIndependence(long firstTimeStart, long firstTimeEnd, long secondTimeStart, long secondTimeEnd) {
		/*
		 * 判断时间是否相互独立,设A,B,C,D分别为时间段1,2的开始时间,结束时间,则AB代表时间段1,CD代表时间段2
		 * 已知A<=B,C<=D;所以当A>D或B<C时,AB,CD没有重合部分,相互独立
		 */
		if (firstTimeStart >= secondTimeEnd || firstTimeEnd <= secondTimeStart) {
			return true;
		} else {
			return false;
		}
	}
}
