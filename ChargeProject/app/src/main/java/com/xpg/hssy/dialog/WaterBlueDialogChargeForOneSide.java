package com.xpg.hssy.dialog;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.easy.util.IntentUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.main.fragment.callbackinterface.GprsBleChargeOperater;
import com.xpg.hssy.util.UpdateUtil;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description
 * @createDate 2015年9月22日
 */

public class WaterBlueDialogChargeForOneSide extends BaseDialog {

	private String titleStr;
	private String contentStr;
	private String rightBtnTextStr;
	private GprsBleChargeOperater gprsBleChargeOperater;
	private boolean contentCanClick = false;
	private Context context;

	public boolean isContentCanClick() {
		return contentCanClick;
	}

	public void setContentCanClick(boolean contentCanClick) {
		this.contentCanClick = contentCanClick;
	}

	public String getTitleStr() {
		return titleStr;
	}

	public void setTitleStr(String titleStr) {
		this.titleStr = titleStr;
		setTitle(this.titleStr);
	}

	public void setTitleStr(int titleId) {
		setTitle(titleId);
	}

	public String getContentStr() {
		return contentStr;
	}

	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
		if (isContentCanClick()) {
			content.setClickable(true);
			content.setText(getClickableSpan(contentStr));
			content.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			setContent(contentStr);
		}
	}

	public void setContentStr(int contentId) {
		this.contentStr = content.getResources().getString(contentId);
		if (isContentCanClick()) {
			content.setClickable(true);
			content.setText(getClickableSpan(contentStr));
			content.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			setContent(contentStr);
		}
	}

	public String getRightBtnTextStr() {
		return rightBtnTextStr;
	}

	public void setRightBtnTextStr(String rightBtnTextStr) {
		this.rightBtnTextStr = rightBtnTextStr;
		setRightBtnText(rightBtnTextStr);

	}

	public void setRightBtnTextStr(int rightBtnTextId) {
		setRightBtnText(rightBtnTextId);
	}

	public WaterBlueDialogChargeForOneSide(GprsBleChargeOperater gprsBleChargeOperater, final Context context) {
		super(context);
		this.context = context;
		this.gprsBleChargeOperater = gprsBleChargeOperater;
		setContentView(R.layout.water_blue_dialog_comfirm);
		setCanceledOnTouchOutside(false);
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (WaterBlueDialogChargeForOneSide.this.gprsBleChargeOperater != null) {
					WaterBlueDialogChargeForOneSide.this.gprsBleChargeOperater.clickDialog();
				}
				dismiss();
			}
		});

//		content.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (isContentCanClick()) {//可点击
//					ToastUtil.show(context, "可点击 isContentCanClick()");
//				}
//			}
//		});

	}

	private SpannableString getClickableSpan(String contentStr) {
		View.OnClickListener l = new View.OnClickListener() {
			//如下定义自己的动作
			public void onClick(View v) {
				IntentUtil.openTelephone(context, "4006556620") ;
				WaterBlueDialogChargeForOneSide.this.dismiss();
			}
		};
		SpannableString spanableInfo = new SpannableString(contentStr);
		int count = contentStr.length();
		int start = 0;
		if (count > 12) {
			start = count - 12;
		}
		int end = count;
		spanableInfo.setSpan(new Clickable(l), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanableInfo;
	}
}

class Clickable extends ClickableSpan implements View.OnClickListener {
	private final View.OnClickListener mListener;

	public Clickable(View.OnClickListener l) {
		mListener = l;
	}

	@Override
	public void onClick(View v) {
		mListener.onClick(v);
	}
}
