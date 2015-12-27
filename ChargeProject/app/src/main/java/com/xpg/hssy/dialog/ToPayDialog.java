package com.xpg.hssy.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.main.activity.callbackinterface.ISelectItemOperator;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description
 * @createDate 2015年12月02日
 */

public class ToPayDialog extends BaseDialog {
	private String title;
	private ImageView img_tip;
	private TextView tv_tip, tv_soso;

	public TextView getTv_tip() {
		return tv_tip;
	}

	public void setTv_tip(TextView tv_tip) {
		this.tv_tip = tv_tip;
	}

	public ImageView getImg_tip() {
		return img_tip;
	}

	public void setImg_tip(ImageView img_tip) {
		this.img_tip = img_tip;
	}

	public TextView getTv_soso() {
		return tv_soso;
	}

	public void setTv_soso(TextView tv_soso) {
		this.tv_soso = tv_soso;
	}

	public ToPayDialog(final Context context) {
		super(context);
		setContentView(R.layout.to_pay_dialog);
		img_tip = (ImageView) findViewById(R.id.img_tip);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_soso = (TextView) findViewById(R.id.tv_soso);
		this.setCanceledOnTouchOutside(false);
	}


}
