package com.xpg.hssy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.easy.util.NetWorkUtil;
import com.easy.util.SPFile;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssychargingpole.R;

import java.io.File;

public final class ImageLoaderManager {

	private static ImageLoaderManager instance;
	private ImageLoader loader;

	private DisplayImageOptions defaultDisplayOptions;
	private Context context;
	private SPFile sp;

	public boolean isSaveTraffic() {
		return isSaveTraffic;
	}

	public void setIsSaveTraffic(boolean isSaveTraffic) {
		this.isSaveTraffic = isSaveTraffic;
	}

	private boolean isSaveTraffic;

	public void init(Context context) {
		this.context = context;
		loader = ImageLoader.getInstance();
		loader.init(new ImageLoaderConfiguration.Builder(context).diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder
				(QueueProcessingType.LIFO).build());

		defaultDisplayOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.find_sanyoubg2).showImageOnLoading(R.drawable
				.find_sanyoubg2).showImageOnFail(R.drawable.find_sanyoubg2).bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.cacheInMemory(true).cacheOnDisk(true).build();
		sp = new SPFile(context, "config");
		isSaveTraffic = sp.getBoolean(KEY.CONFIG.SAVE_TRAFFIC, false);
	}

	public static ImageLoaderManager getInstance() {
		if (instance == null) {
			instance = new ImageLoaderManager();
		}
		return instance;
	}

	/**
	 * 依次载入图片资源id,分别是图片加载失败,图片加载中,url为空时的默认图片
	 *
	 * @param imageResourceId 图片资源ID,最多为3,超过3张取前3张
	 * @return
	 */
	public static DisplayImageOptions createDisplayOptionsWtichImageResurces(int... imageResourceId) {
		DisplayImageOptions option = null;
		Builder optionBuilder = new DisplayImageOptions.Builder();
		optionBuilder.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.cacheInMemory(true).cacheOnDisk(true);
		switch (imageResourceId.length) {
			case 0:
				break;
			default:
			case 3:
				optionBuilder.showImageForEmptyUri(imageResourceId[2]);
			case 2:
				optionBuilder.showImageOnLoading(imageResourceId[1]);
			case 1:
				optionBuilder.showImageOnFail(imageResourceId[0]);
				break;
		}
		option = optionBuilder.build();
		return option;
	}

	public DisplayImageOptions getDefaultDisplayOptions() {
		return defaultDisplayOptions;
	}

	public void displayImage(String uri, ImageView imageView) {
		displayImage(uri, imageView, defaultDisplayOptions, null, null, false);
	}

	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		displayImage(uri, imageView, defaultDisplayOptions, listener, null, false);
	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
		displayImage(uri, imageView, options, null, null, false);
	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
		displayImage(uri, imageView, options, listener, null, false);
	}

	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		displayImage(uri, imageView, defaultDisplayOptions, listener, progressListener, false);
	}

	public void displayImage(String uri, ImageView imageView, boolean ignoreSaveTrafficSetting) {
		displayImage(uri, imageView, defaultDisplayOptions, null, null, ignoreSaveTrafficSetting);

	}

	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener, boolean ignoreSaveTrafficSetting) {
		displayImage(uri, imageView, defaultDisplayOptions, listener, null, ignoreSaveTrafficSetting);

	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, boolean ignoreSaveTrafficSetting) {
		displayImage(uri, imageView, options, null, null, ignoreSaveTrafficSetting);

	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, boolean ignoreSaveTrafficSetting) {
		displayImage(uri, imageView, options, listener, null, ignoreSaveTrafficSetting);
	}

	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener, ImageLoadingProgressListener progressListener, boolean
			ignoreSaveTrafficSetting) {
		displayImage(uri, imageView, defaultDisplayOptions, listener, progressListener, ignoreSaveTrafficSetting);
	}

	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener
			progressListener, boolean ignoreSaveTrafficSetting) {
		uri = repairImageUrl(uri);
		if (!ignoreSaveTrafficSetting && needSaveTraffic(uri)) {
			uri = "";
		}
		loader.displayImage(uri, new ImageViewAware(imageView), options, listener, progressListener);

	}

	/*loadImage方法不适用省流量模式*/

	public void loadImage(String uri, ImageLoadingListener listener) {
		loadImage(uri, null, defaultDisplayOptions, listener, null);
	}

	public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener) {
		loadImage(uri, targetImageSize, defaultDisplayOptions, listener, null);
	}

	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener) {
		loadImage(uri, targetImageSize, options, listener, null);
	}

	public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
		loadImage(uri, targetImageSize, defaultDisplayOptions, listener, progressListener);
	}

	public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener
			progressListener) {
		uri = repairImageUrl(uri);
		loader.loadImage(uri, targetImageSize, options, listener, progressListener);
	}

	public void loadImageNoCache(String uri, ImageLoadingListener listener) {
		loadImage(uri, null, null, listener, null);

	}

	public boolean chackCache(String uri) {
		Bitmap cacheBitmap = loader.getMemoryCache().get(uri);
		if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
			return true;
		} else {
			File cacheFile = loader.getDiskCache().get(uri);
			return cacheFile != null && cacheFile.exists();
		}
	}

	public File getCacheFile(String uri){
		File cacheFile = loader.getDiskCache().get(uri);
		return cacheFile;
	}

	private boolean needSaveTraffic(String uri) {
		return isSaveTraffic && !chackCache(uri) && !NetWorkUtil.isWifiConnected(context);
	}

	public long getCacheSize() {
		File cache = loader.getDiskCache().getDirectory();
		LogUtils.e("cache", cache.getAbsolutePath());
		return FileUtils.calculateFileSize(cache);
	}

	public void clearCache() {
		loader.clearDiskCache();
	}

	public String repairImageUrl(String url) {
		if (null == url) {
			url = "";
		} else if (!url.contains("http")) {
			url = WebAPI.BASE_URL + "/" + url;
		}
		return url;
	}
}
