package com.xpg.hssy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.easy.view.EasyViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.main.activity.PilePhotoActivity;
import com.xpg.hssy.main.activity.WebViewNewsActivity;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssychargingpole.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年6月4日
 */

public class PilePhotoPager extends EasyViewPager implements View.OnClickListener, OnViewTapListener {
	public static final int JUMP_TYPE_TO_PHOTO = 0x01;
	public static final int JUMP_TYPE_TO_WEBVIEW = 0x02;
	private boolean canOperate;
	private List<View> imageViews;
	private List<String> imageUrls;
	private Map<String, String> imgExtra;
	private int jumpType;
	private DisplayImageOptions option;

	{
		imageViews = new ArrayList<>();
		imageUrls = new ArrayList<>();
		setPages(imageViews);
		option = ImageLoaderManager.createDisplayOptionsWtichImageResurces(R.drawable.find_sanyoubg, R.drawable.find_sanyoubg, R.drawable.find_sanyoubg);
	}

	public PilePhotoPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		jumpType = JUMP_TYPE_TO_PHOTO;
	}

	public PilePhotoPager(Context context) {
		this(context, null);
	}

	public void loadPhoto(String url) {
		final ImageView iv = new ImageView(getContext());
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		synchronized (getAdapter()) {
			imageViews.add(iv);
			imageUrls.add(url);
			ImageLoaderManager.getInstance().displayImage(url, iv, option, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					if (canOperate) {
						new PhotoViewAttacher(iv).setOnViewTapListener(PilePhotoPager.this);
					} else {
						iv.setOnClickListener(PilePhotoPager.this);
					}
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
		}
		notifyDataSetChanged();
	}

	public void loadPhoto(String url, int maxWidth, int maxHeight, ScaleType scaleType) {
		final ImageView iv = new ImageView(getContext());
		iv.setScaleType(scaleType);
		iv.setLayoutParams(new ViewGroup.LayoutParams(maxWidth, maxHeight));
		synchronized (getAdapter()) {
			imageViews.add(iv);
			imageUrls.add(url);
			ImageLoaderManager.getInstance().displayImage(url, iv, option, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					if (canOperate) {
						new PhotoViewAttacher(iv).setOnViewTapListener(PilePhotoPager.this);
					} else {
						iv.setOnClickListener(PilePhotoPager.this);
					}
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
		}
		notifyDataSetChanged();
	}

	/**
	 * 当图片需要携带链接时可以调用此方法
	 *
	 * @param imgUrl 图片url
	 * @param link   图片对应的链接地址link
	 */
	public void loadPhoto(String imgUrl, String link) {
		if (imgExtra == null) {
			imgExtra = new HashMap<>();
		}
		final ImageView iv = new ImageView(getContext());
		iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		iv.setLayoutParams(new ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		synchronized (getAdapter()) {
			imageViews.add(iv);
			imageUrls.add(imgUrl);
			imgExtra.put(imgUrl, link);
			ImageLoaderManager.getInstance().displayImage(imgUrl, iv, option, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {

				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					if (canOperate) {
						new PhotoViewAttacher(iv).setOnViewTapListener(PilePhotoPager.this);
					} else {
						iv.setOnClickListener(PilePhotoPager.this);
					}
				}

				@Override
				public void onLoadingCancelled(String s, View view) {

				}
			});
		}
		notifyDataSetChanged();
	}

	@Override
	public void onViewTap(View arg0, float arg1, float arg2) {
		((Activity) getContext()).finish();
	}

	@Override
	public void onClick(View arg0) {
		switch (jumpType) {
			case JUMP_TYPE_TO_PHOTO: {
				Intent intent = new Intent(getContext(), PilePhotoActivity.class);
				intent.putExtra(KEY.INTENT.IMAGE_INDEX, imageViews.indexOf(arg0));
				intent.putExtra(KEY.INTENT.IMAGE_URLS, (Serializable) imageUrls);
				getContext().startActivity(intent);
				break;
			}
			case JUMP_TYPE_TO_WEBVIEW: {
				Intent data = new Intent(getContext(), WebViewNewsActivity.class);
				data.putExtra(KEY.INTENT.WEB_LINK, imgExtra.get(imageUrls.get(imageViews.indexOf(arg0))));
				getContext().startActivity(data);
				break;
			}
		}
	}

	public int getCount() {
		return imageViews == null ? 0 : imageViews.size();
	}

	public boolean isCanOperate() {
		return canOperate;
	}

	public void setCanOperate(boolean canOperate) {
		this.canOperate = canOperate;
	}

	public int getJumpType() {
		return jumpType;
	}

	public void setJumpType(int jumpType) {
		this.jumpType = jumpType;
	}
	//重写viewPage,重写以下两个方法，解决google API关于appoint out of range 异常 by Mazoh

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public void clearItems() {
		synchronized (getAdapter()) {
			if (null != imageUrls) imageUrls.clear();
			if (null != imageViews) imageViews.clear();
			if (null != imgExtra) imgExtra.clear();
		}
		notifyDataSetChanged();
	}
}
