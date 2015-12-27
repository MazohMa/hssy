package com.xpg.hssy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年3月26日
 * @version 1.0.0
 */

public class RefreshListView extends DropDownListView {

	private boolean isRefreshing;
	private boolean isLoading;
	private OnDropDownListener oddl;
	private OnClickListener obl;

	private boolean canlayoutChildren = true;

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshListView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setDropDownStyle(true);
		setOnBottomStyle(true);
		// setDropDownStyle(false);
		setOnBottomStyle(false);
		setOnDropDownListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				isRefreshing = true;
				if (oddl != null) {
					oddl.onDropDown();
				}
			}
		});
		setOnBottomListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isLoading = true;
				if (obl != null) {
					obl.onClick(arg0);
				}
			}
		});
	}

	public void showRefreshing(boolean scrollTop) {
		isRefreshing = true;
		setHeaderStatusLoading();
		if (scrollTop) {
			smoothScrollToPosition(0);
		}
	}

	public void showRefreshFail() {
		isRefreshing = false;
		setHeaderStatusLoading();
		setHeaderStatusClickToLoad();
	}

	public void prepareLoad() {
		isLoading = false;
		onBottomComplete();
		setOnBottomStyle(true);
		setAutoLoadOnBottom(true);
		setHasMore(true);
		onBottomBegin();
	}

	public void showLoading() {
		isLoading = true;
		onBottomComplete();
		setOnBottomStyle(true);
		setAutoLoadOnBottom(false);
		setHasMore(true);
		onBottomBegin();
	}

	public void showNoMore() {
		isLoading = false;
		setOnBottomStyle(true);
		setAutoLoadOnBottom(false);
		setShowFooterWhenNoMore(true);
		setHasMore(false);
		onBottomComplete();
	}

	public void showLoadFail() {
		isLoading = false;
		setOnBottomStyle(true);
		setAutoLoadOnBottom(false);
		setHasMore(true);
		onBottomComplete();
	}

	public void completeRefresh() {
		isRefreshing = false;
		onDropDownComplete();
	}

	public void completeLoad() {
		isLoading = false;
		onBottomComplete();
	}

	public void setLoadable(boolean loadable) {
		setOnBottomStyle(loadable);
	}

	public void setRefreshable(boolean refreshable) {
		setDropDownStyle(refreshable);
	}

	public void setOnRefreshListener(OnDropDownListener l) {
		this.oddl = l;
	}

	public void setOnLoadListener(OnClickListener l) {
		this.obl = l;
	}

	public boolean isRefreshing() {
		return isRefreshing;
	}

	public void setRefreshing(boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	@Override
	protected void layoutChildren() {
		if (canlayoutChildren) {
			super.layoutChildren();
		}
	}

	public void setCanLayoutChildren(boolean b) {
		canlayoutChildren = b;
	}

	public boolean isCanLayoutChildren() {
		return canlayoutChildren;
	}

}
