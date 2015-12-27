package com.xpg.hssy.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年3月26日
 */

public abstract class LazyLoadFragment extends BaseFragment {
	protected boolean isFirst;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		isFirst = true;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisiable = true;
			onVisible();
			if (isFirst) {
				lazyLoad();
				isFirst = false;
			}
		} else {
			isVisiable = false;
			onInvisible();
		}
	}

	protected abstract void lazyLoad();
}
