package com.xpg.hssy.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.easy.view.EasyFragmentPager;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class PageView extends EasyFragmentPager {
	private OnPageChangeListener opclCustom;

	private View indicator;
	private int indicatorWidth = 0;
	private int indicatorPadding = 0;
	private int viewPagerWidth = 0;
	private RelativeLayout.LayoutParams layoutParams;
	private int movePixels;
	public android.support.v4.app.Fragment currentFragment;

	private boolean isFirsCaculate = true;

	public HorizontalScrollView getScrollview() {
		return scrollview;
	}

	public void setScrollview(HorizontalScrollView scrollview) {
		this.scrollview = scrollview;
	}

	private HorizontalScrollView scrollview ;

	public void setIndicator(Context context, View view, int pageCount) {
		indicator = view.findViewById(R.id.imageview_tiao);
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		indicatorWidth = screenWidth / pageCount;
		indicatorPadding = indicatorWidth / 6;
		layoutParams = (RelativeLayout.LayoutParams) indicator
				.getLayoutParams();
		layoutParams.width = indicatorWidth - indicatorPadding * 2;
		indicator.setLayoutParams(layoutParams);

	}

	private void indicatorMove(int position, int positionOffsetPixels) {
		movePixels = (int) (indicatorWidth * position + (((double) positionOffsetPixels / viewPagerWidth) * indicatorWidth))
				+ indicatorPadding;
		layoutParams.leftMargin = movePixels;
		indicator.setLayoutParams(layoutParams);
	}

	private OnPageChangeListener opcl = new OnPageChangeListener() {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (opclCustom != null) {
				opclCustom.onPageScrollStateChanged(arg0);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (opclCustom != null) {
				opclCustom.onPageScrolled(arg0, arg1, arg2);
			}
			if (isFirsCaculate) {
				viewPagerWidth = getWidth() + getPageMargin();
				isFirsCaculate = false;
			}
			if (indicator != null)
				indicatorMove(arg0, arg2);
		}

		@Override
		public void onPageSelected(int arg0) {
			if (opclCustom != null) {
				opclCustom.onPageSelected(arg0);
			}//添加监听  by Mazoh
			if (mScrollToFragment != null) {
				currentFragment = PageView.this.getCurrentPage();
				mScrollToFragment.onClickCurrentFragment(currentFragment) ;
			}
		}
	};

	/**
	 * 构造器
	 */
	public PageView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造器
	 */
	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setOnPageChangeListener(opcl);
		setFragmentDestroyable(false);
	}

	public OnPageChangeListener getOpclCustom() {
		return opclCustom;
	}

	public void setOpclCustom(OnPageChangeListener opclCustom) {
		this.opclCustom = opclCustom;
	}

	public ScrollToFragment getmScrollToFragment() {
		return mScrollToFragment;
	}

	public void setmScrollToFragment(ScrollToFragment mScrollToFragment) {
		this.mScrollToFragment = mScrollToFragment;
	}

	private ScrollToFragment mScrollToFragment;

	public interface ScrollToFragment {
		public void onClickCurrentFragment(Fragment currentFragment);
	}

//	public void initAdapter(FragmentManager fm) {
//		this.setAdapter(new PageView.FragmentAdapter(fm));
//	}

//	class FragmentAdapter extends FragmentStatePagerAdapter {
//
//		public FragmentAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		public int getCount() {
//			return PageView.this.ls == null?0:PageView.this.ls.size();
//		}
//
//		public Fragment getItem(int paramInt) {
//			return (Fragment)PageView.this.ls.get(paramInt);
//		}
//
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			if(PageView.this.fragmentDestroyable) {
//				super.destroyItem(container, position, object);
//			}
//
//		}
//	}
}
