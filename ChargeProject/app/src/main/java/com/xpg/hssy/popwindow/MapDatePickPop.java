package com.xpg.hssy.popwindow;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;

import com.easy.popup.EasyPopup;
import com.xpg.hssy.adapter.DateWheelAdapter;
import com.xpg.hssychargingpole.R;

@Deprecated
public class MapDatePickPop extends EasyPopup implements OnWheelChangedListener {
	private Context context;

	private WheelView wv_date;
	private DateWheelAdapter dateWheelAdapter;

	private OnWheelChangedListener onWheelChangedListener;

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (onWheelChangedListener == null)
			return;
		onWheelChangedListener.onChanged(wheel, oldValue, newValue);
	}

	public MapDatePickPop(Context context) {
		super(context, R.layout.map_date_pick_pop, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		this.context = context;

		init(context);
	}

	private void init(Context context) {
		this.setAnimationStyle(R.style.PopupAnimationSlide);

		dateWheelAdapter = new DateWheelAdapter(context);
		dateWheelAdapter.setItemResource(R.layout.wheel_text_item_small);
		// dateWheelAdapter.setItemTextResource(R.string.default_date);
		dateWheelAdapter.setTextColor(context.getResources().getColor(
				R.color.black));
		dateWheelAdapter.setTextColorUnselect(context.getResources().getColor(
				R.color.wheel_view_text_default));

		wv_date = (WheelView) findViewById(R.id.wv_date);
		wv_date.setViewAdapter(dateWheelAdapter);
		wv_date.setVisibleItems(5);
		wv_date.setBackgroundVisible(false);
		wv_date.setForegroundVisible(false);
		wv_date.setDrawShadows(false);
		wv_date.setCyclic(true);
		wv_date.addChangingListener(this);

	}

	public OnWheelChangedListener getOnChangedListener() {
		return onWheelChangedListener;
	}

	public void setOnChangedListener(
			OnWheelChangedListener onWheelChangedListener) {
		this.onWheelChangedListener = onWheelChangedListener;
	}
}
