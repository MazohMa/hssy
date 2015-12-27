package com.xpg.hssy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class TextUtil {

	/**
	 * @param orgString
	 * @param values
	 * @return
	 */
	public static String insertValues(String orgString, String[] values) {

		if (null == values) {
			return orgString;
		}
		String sign;
		String result = orgString;
		for (int i = 0; i < values.length; i++) {
			sign = "${" + i + "}";
			result = replaceAll(result, sign, values[i]);
		}

		return result;
	}

	/**
	 * @param orgString
	 * @param values
	 * @return
	 */
	public static String insertValue0(String orgString, String value) {

		if (null == value) {
			return orgString;
		}
		String sign;
		String result = orgString;
		sign = "${0}";
		result = replaceAll(orgString, sign, value);
		return result;
	}

	/**
	 * @param orgString
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static String replaceAll(String orgString, String regex,
			String replacement) {

		String org = null;
		String replaced = orgString;
		while (org != replaced) {
			org = replaced;
			replaced = replaceFirst(replaced, regex, replacement);
		}
		return replaced;
	}

	/**
	 * @param orgString
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static String replaceFirst(String orgString, String regex,
			String replacement) {

		if (null == orgString || null == regex || null == replacement) {
			return orgString;
		}
		if (orgString.length() == 0 || regex.length() == 0) {
			return orgString;
		}
		int index = orgString.indexOf(regex);
		if (index < 0) {
			return orgString;
		}
		char[] chars = orgString.toCharArray();
		StringBuffer sb = new StringBuffer();
		sb.append(chars, 0, index);
		sb.append(replacement);
		sb.append(chars, index + regex.length(),
				chars.length - index - regex.length());

		return sb.toString();

	}

	public static boolean isChinese(String name) {
		boolean isResult = false;
		try {
			String comp = "[\\u4e00-\\u9fa5]+";
			Pattern pattern = Pattern.compile(comp);
			Matcher matcher = pattern.matcher(name);
			isResult = matcher.find();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("___________isResult = " + isResult);
		return isResult;
	}

	public static String getPinyin(String str) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		char[] chars = str.toCharArray();
		StringBuilder strb = new StringBuilder();
		for (char c : chars) {
			String[] pinyin;
			try {
				pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
				if (pinyin != null) {
					if (pinyin != null) {
						strb.append(pinyin[0]);// 只要第一个发音
					}
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				strb.append(c);
				e.printStackTrace();
			}

		}
		return strb.toString();
	}

	public static String getFirstPinyin(String str) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		char[] chars = str.toCharArray();
		StringBuilder strb = new StringBuilder();
		for (char c : chars) {
			String[] pinyin;
			try {
				pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
				if (pinyin != null) {
					strb.append(pinyin[0].substring(0, 1));// 只要第一个发音的首字母
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				strb.append(c);
				e.printStackTrace();
			}
		}
		return strb.toString();
	}
}
