package com.xpg.hssy.web;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * WebCache
 * 
 * @author Joke Huang
 * @createDate 2014年3月24日
 * @version 1.0.0
 */

public class WebCacheManager {
	private static String tag = WebCacheManager.class.getSimpleName();

	private volatile static WebCacheManager webCacheManager;

	/** 最大缓存 */
	private static int maxSize;
	/** 缓存集合 */
	private LruCache<String, Object> mLurCache = new LruCache<String, Object>(
			maxSize) {

		@Override
		protected int sizeOf(String key, Object value) {
			if (value instanceof Bitmap) {
				Bitmap bm = (Bitmap) value;
				return bm.getRowBytes() * bm.getHeight() / 8;
			}
			return 1024;
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				Object oldValue, Object newValue) {
			// TODO 将溢出的缓存资源保存到手机存储中
			// 释放资源
			if (evicted) {
				if (oldValue instanceof Bitmap) {
					try {
						Bitmap bm = (Bitmap) oldValue;
						bm.recycle();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	/** 初始化 */
	private WebCacheManager() {
	}

	public static WebCacheManager getInstance() {
		if (webCacheManager == null) {
			synchronized (WebCacheManager.class) {
				if (webCacheManager == null) {
					long maxMemory = Runtime.getRuntime().maxMemory();
					maxSize = (int) (maxMemory / 4);
					webCacheManager = new WebCacheManager();
				}
			}
		}
		return webCacheManager;
	}

	/** 取出缓存的数据 */
	public Object get(String key) {
		// 如果从硬缓存取出了数据
		Object value = mLurCache.remove(key);
		if (value != null) {
			// 将数据提到栈顶
			mLurCache.put(key, value);
		}
		// 返回数据
		return value;
	}

	/** 缓存数据 */
	public void cache(String key, Object value) {
		mLurCache.put(key, value);
		Log.i(tag, "cache size: " + mLurCache.size() / 1024 + "kb/" + maxSize
				/ 1024 + "kb");
	}

	/** 释放资源 */
	public void release() {
		mLurCache.evictAll();
	}
}
