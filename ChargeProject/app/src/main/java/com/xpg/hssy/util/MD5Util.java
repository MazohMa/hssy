package com.xpg.hssy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/11/20.
 */
public class MD5Util {

	private static String encrypt(byte[] paramArrayOfByte)
	{
		try
		{
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			StringBuilder localStringBuilder = new StringBuilder();
			for (int i = 0; ; i++)
			{
				if (i >= arrayOfByte.length)
				{
					String str2 = localStringBuilder.toString();
					return str2;
				}
				localStringBuilder.append(Integer.toHexString(0xFFFFFF00 | 0xFF & arrayOfByte[i]).substring(6));
			}
		}
		catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
		{
			while (true)
			{
				localNoSuchAlgorithmException.printStackTrace();
				String str1 = null;
			}
		}
		finally
		{
		}
	}

	static MessageDigest getDigest()
	{
		try
		{
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			return localMessageDigest;
		}
		catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
		{
			throw new RuntimeException(localNoSuchAlgorithmException);
		}
	}

	public static byte[] md5(String paramString)
	{
		return md5(paramString.getBytes());
	}

	public static byte[] md5(byte[] paramArrayOfByte)
	{
		return getDigest().digest(paramArrayOfByte);
	}


	public static String toMD5(String paramString)
	{
		if (paramString != null)
			try
			{
				String str = encrypt(paramString.getBytes("GBK"));
				return str;
			}
			catch (UnsupportedEncodingException localUnsupportedEncodingException)
			{
				localUnsupportedEncodingException.printStackTrace();
			}
		return null;
	}
}
