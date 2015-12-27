package com.xpg.hssy.popwindow;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.easy.popup.EasyPopup;
import com.xpg.hssychargingpole.R;

@Deprecated
public class MapPileTypePop extends EasyPopup implements OnClickListener {

	private Context context;

	private OnClickListener onClickListener;

	private RelativeLayout rlyt_ac;
	private RelativeLayout rlyt_dc;
	private RelativeLayout rlyt_public;

	private ImageView iv_ac_ok;
	private ImageView iv_dc_ok;
	private ImageView iv_public_ok;

	public MapPileTypePop(Context context) {
		super(context, R.layout.map_pile_type_pop, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		this.context = context;

		initView();
		initData();
		initListener();
	}

	private void initData() {
		rlyt_ac.setSelected(true);
		rlyt_dc.setSelected(true);
		rlyt_public.setSelected(true);

		this.setAnimationStyle(R.style.PopupAnimationSlide);
	}

	private void initView() {
		rlyt_ac = (RelativeLayout) findViewById(R.id.rlyt_ac);
		rlyt_dc = (RelativeLayout) findViewById(R.id.rlyt_dc);
		rlyt_public = (RelativeLayout) findViewById(R.id.rlyt_public);

		iv_ac_ok = (ImageView) findViewById(R.id.iv_ac_ok);
		iv_dc_ok = (ImageView) findViewById(R.id.iv_dc_ok);
		iv_public_ok = (ImageView) findViewById(R.id.iv_public_ok);
	}

	private void initListener() {
		rlyt_ac.setOnClickListener(this);
		rlyt_dc.setOnClickListener(this);
		rlyt_public.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		select(v);
		if (onClickListener != null) {
			onClickListener.onClick(v);
		}
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	private void select(View v) {
		switch (v.getId()) {
		case R.id.rlyt_ac:
			onClickTypeAC();
			break;
		case R.id.rlyt_dc:
			onClickTypeDC();
			break;
		case R.id.rlyt_public:
			onClickTypePublic();
			break;

		default:
			break;
		}
	}

	private void onClickTypeAC() {
		if (rlyt_ac.isSelected()) {
			rlyt_ac.setSelected(false);
			iv_ac_ok.setVisibility(View.INVISIBLE);
		} else {
			rlyt_ac.setSelected(true);
			iv_ac_ok.setVisibility(View.VISIBLE);
		}
	}

	private void onClickTypeDC() {
		if (rlyt_dc.isSelected()) {
			rlyt_dc.setSelected(false);
			iv_dc_ok.setVisibility(View.INVISIBLE);
		} else {
			rlyt_dc.setSelected(true);
			iv_dc_ok.setVisibility(View.VISIBLE);
		}
	}

	private void onClickTypePublic() {
		if (rlyt_public.isSelected()) {
			rlyt_public.setSelected(false);
			iv_public_ok.setVisibility(View.INVISIBLE);
		} else {
			rlyt_public.setSelected(true);
			iv_public_ok.setVisibility(View.VISIBLE);
		}
	}

}
