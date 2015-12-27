package com.xpg.hssy.cmdparse;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

public class DataUtil {

	public static double roundDouble(double val, int precision) {
		double ret = 0;

		double factor = Math.pow(10, precision);

		ret = Math.floor(val * factor + 0.5) / factor;
		return ret;
	}

	public static double roundOneDecimal(double val) {
		BigDecimal bigDecimal = new BigDecimal(val).setScale(1,
				BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}

	public static double roundTwoDecimal(double val) {
		BigDecimal bigDecimal = new BigDecimal(val).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}

	public static double convertMphToKmh(double mphVal) {
		return 1.609344 * mphVal;
	}

	public static double convertMphToMs(double mphVal) {
		return 0.44704 * mphVal;
	}

	/**
	 * float to byte[]
	 *
	 * @param f
	 * @return
	 */
	public static byte[] float2byte(float f) {

		// float to byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// reverse the byte array
		int len = b.length;
		// build up an array with the same type
		byte[] dest = new byte[len];
		// to avoid editing the original array, make a copy for that
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// exchange the first ones with the last ones
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	/**
	 * bytes to float
	 *
	 * @param b
	 *            bytes (at lease 4 bytes)
	 * @param index
	 *            start index
	 * @return
	 */
	public static float byte2float(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	/**
	 * short 转 byte
	 *
	 * @param s
	 * @param asc
	 *            大小端
	 * @return
	 */
	public final static byte[] getBytes(short s, boolean asc) {
		byte[] buf = new byte[2];
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x00ff);
				s >>= 8;
			}
		return buf;
	}

	public final static byte[] getBytes(int s, boolean asc) {
		byte[] buf = new byte[4];
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x000000ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x000000ff);
				s >>= 8;
			}
		return buf;
	}

	public final static byte[] getBytes(long s, boolean asc) {
		byte[] buf = new byte[8];
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				buf[i] = (byte) (s & 0x00000000000000ff);
				s >>= 8;
			}
		else
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) (s & 0x00000000000000ff);
				s >>= 8;
			}
		return buf;
	}

	public final static short getShort(byte[] buf, boolean asc) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 2) {
			throw new IllegalArgumentException("byte array size > 2 !");
		}
		short r = 0;
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		else
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x00ff);
			}
		return r;
	}

	public final static int getInt(byte[] buf, boolean asc) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 4) {
			throw new IllegalArgumentException("byte array size > 4 !");
		}
		int r = 0;
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x000000ff);
			}
		else
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x000000ff);
			}
		return r;
	}

	public final static long getLong(byte[] buf, boolean asc) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 8) {
			throw new IllegalArgumentException("byte array size > 8 !");
		}
		long r = 0;
		if (asc)
			for (int i = buf.length - 1; i >= 0; i--) {
				r <<= 8;
				r |= (buf[i] & 0x00000000000000ff);
			}
		else
			for (int i = 0; i < buf.length; i++) {
				r <<= 8;
				r |= (buf[i] & 0x00000000000000ff);
			}
		return r;
	}

	public final static long getLongByBCD(byte[] buf) {
		if (buf == null) {
			throw new IllegalArgumentException("byte array is null!");
		}
		if (buf.length > 8) {
			throw new IllegalArgumentException("byte array size > 8 !");
		}

		int m;
		int n;
		StringBuilder strb = new StringBuilder(buf.length * 2);
		for (byte b : buf) {
			m = b & 0xf;
			n = (b >>> 4)&0xf;
			strb.append(n).append(m);
		}
		return Long.valueOf(strb.toString());
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		// 得到画布
		Canvas canvas = new Canvas(output);

		// 将画布的四角圆化
		final int color = Color.RED;
		final Paint paint = new Paint();
		// 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		// final Rect rect = new Rect(0, 0, 300, 300);
		final RectF rectF = new RectF(rect);
		// 值越大角度越明显
		final float roundPx = 180;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// java 合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	// java 合并一个byte数组和一个byte
	public static byte[] byteMerger(byte[] byte_1, byte byte_2) {
		byte[] byte_3 = new byte[byte_1.length + 1];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		byte_3[byte_1.length] = byte_2;
		return byte_3;
	}

	// java 合并arraylist 中的byte数组
	public static byte[] byteMerger(ArrayList<byte[]> byteArrayList) {
		int totalLength = 0;
		for (int i = 0; i < byteArrayList.size(); i++) {
			totalLength += byteArrayList.get(i).length;
		}
		byte[] retBytes = new byte[totalLength], bytes_1;
		int index = 0;
		for (int i = 0; i < byteArrayList.size(); i++) {
			bytes_1 = byteArrayList.get(i);
			System.arraycopy(bytes_1, 0, retBytes, index, bytes_1.length);
			index += bytes_1.length;
		}
		return retBytes;
	}

	public static String getStringFromBytes(byte[] bytes) {
		StringBuffer thebyteStr = new StringBuffer();
		int theSplite = 50;
		for (int i = 0; i < bytes.length; i++) {
			if (i < theSplite)
				thebyteStr.append(String.format(" 0x%02x", bytes[i]));
			else if (i > theSplite && bytes.length - i < theSplite)
				thebyteStr.append(String.format(" 0x%02x", bytes[i]));
			else if (i == theSplite) {
				if (bytes.length - 1 == theSplite)
					thebyteStr.append(String.format(" 0x%02x", bytes[i]));
				else
					thebyteStr.append(" ... ");
			}
		}
		return thebyteStr.toString();
	}

	/**
	 * 判断某一位是否为1
	 * */
	public static boolean isHightBit(byte b, int index) {
		return ((b >> index) & 0x01) == 0x01;
	}

	/*
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 *
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 *
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static String convertByteAryToStr(byte[] imgByte) {
		if (imgByte != null) {
			return Base64.encodeToString(imgByte, Base64.DEFAULT);
		} else {
			return null;
		}
	}

	public static byte[] revertBytes(byte[] bytes) {
		byte[] newbytes = new byte[bytes.length];
		int byteLength = bytes.length;
		for (int i = 0; i < newbytes.length; i++) {
			newbytes[byteLength - i - 1] = bytes[i];
		}
		return newbytes;
	}

}
