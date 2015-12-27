package com.xpg.hssy.util;

public class NumToAphabetUtil {
	/**
	 * numToEnglishUtil
	 * 
	 * @param num
	 * @author Mazoh 数字转换英文字母，目前支持26位字母
	 */

	public static String numToEnglishUtil(int num) {
		String alphabet = null;
		switch (num) {
		case 0:
			alphabet = "A";
			break;
		case 1:
			alphabet = "B";
			break;
		case 2:
			alphabet = "C";
			break;
		case 3:
			alphabet = "D";
			break;
		case 4:
			alphabet = "E";
			break;
		case 5:
			alphabet = "F";
			break;
		case 6:
			alphabet = "G";
			break;
		case 7:
			alphabet = "H";
			break;
		case 8:
			alphabet = "I";
			break;
		case 9:
			alphabet = "J";
			break;
		case 10:
			alphabet = "K";
			break;
		case 11:
			alphabet = "L";
			break;
		case 12:
			alphabet = "M";
			break;
		case 13:
			alphabet = "N";
			break;
		case 14:
			alphabet = "O";
			break;
		case 15:
			alphabet = "P";
			break;
		case 16:
			alphabet = "Q";
			break;
		case 17:
			alphabet = "R";
			break;
		case 18:
			alphabet = "S";
			break;
		case 19:
			alphabet = "T";
			break;
		case 20:
			alphabet = "U";
			break;
		case 21:
			alphabet = "V";
			break;
		case 22:
			alphabet = "W";
			break;
		case 23:
			alphabet = "X";
			break;
		case 24:
			alphabet = "Y";
			break;
		case 25:
			alphabet = "Z";
			break;
		default:
			alphabet = "Z+";
			break;
		}
		return alphabet;
	}

}
