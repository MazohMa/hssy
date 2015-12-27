package com.xpg.hssy.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.util.AppInfo;
import com.xpg.hssy.main.fragment.callbackinterface.GprsBleChargeOperater;
import com.xpg.hssy.util.UpdateUtil;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 * @version 2.4.0
 * @Description
 * @createDate 2015年9月22日
 */

public class WaterBlueDialogChargeForTwoSide extends BaseDialog {

	private String titleStr;
	private String contentStr;
	private String leftBtnTextStr;
	private String rightBtnTextStr;
	private boolean isGprsAndBle, isOwner;
	private GprsBleChargeOperater gprsBleChargeOperater;
	private TextView tv_pileName, tv_ownerName, tv_price, tv_ble_id;
	private String pileName;
	private String ownerName;
	private String price;
	private String ble_id ;
	private LinearLayout ll_ble_id;
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
		if(tv_price != null){
			tv_price.setText(price);
		}
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
		if(tv_ownerName != null){
			tv_ownerName.setText(ownerName);
		}
	}

	public String getPileName() {
		return pileName;
	}

	public void setPileName(String pileName) {
		this.pileName = pileName;
		if(tv_pileName != null){
			tv_pileName.setText(pileName);
		}
	}

	public String getBle_id() {
		return ble_id;
	}

	public void setBle_id(String ble_id) {
		this.ble_id = ble_id;
		if(tv_ble_id != null){
			tv_ble_id.setText(ble_id);
		}
	}

	public String getTitleStr() {
		return titleStr;
	}

	public void setTitleStr(String titleStr) {
		this.titleStr = titleStr;
		setTitle(titleStr);
	}

	public void setTitleStr(int titleId) {
		setTitle(titleId);
	}

	public String getContentStr() {
		return contentStr;
	}

	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
		setContent(contentStr);
	}

	public void setContentStr(int contentID) {
		setContent(contentID);
	}
	public String getLeftBtnTextStr() {
		return leftBtnTextStr;
	}
	public void setLeftBtnTextStr(String leftBtnTextStr) {
		this.leftBtnTextStr = leftBtnTextStr;
		setLeftBtnText(leftBtnTextStr);
	}

	public void setLeftBtnTextStr(int leftBtnTextID) {
		setLeftBtnText(leftBtnTextID);
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
	public void setBleIdVisibily(boolean visibily) {
		if(visibily){
			ll_ble_id.setVisibility(View.VISIBLE);
		}else {
			ll_ble_id.setVisibility(View.GONE);
		}
	}
	public WaterBlueDialogChargeForTwoSide(GprsBleChargeOperater gprsBleChargeOperater,
	                                       final Context context, boolean isGprsAndBle, boolean isOwner) {
		super(context);
		this.gprsBleChargeOperater = gprsBleChargeOperater;
		this.isGprsAndBle = isGprsAndBle;
		this.isOwner = isOwner;
		if (isOwner) {
			setContentView(R.layout.water_blue_dialog_title);
		} else {
			setContentView(R.layout.water_blue_dialog_for_qrc);
			ll_ble_id = (LinearLayout) findViewById(R.id.ll_ble_id);
			tv_pileName = (TextView) findViewById(R.id.tv_pileName);
			tv_ownerName = (TextView) findViewById(R.id.tv_ownerName);
			tv_price = (TextView) findViewById(R.id.tv_price);
			tv_ble_id = (TextView) findViewById(R.id.tv_ble_id);
		}
		setCanceledOnTouchOutside(false);
		setRightListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (WaterBlueDialogChargeForTwoSide.this.gprsBleChargeOperater != null) {
					WaterBlueDialogChargeForTwoSide.this.gprsBleChargeOperater.clickDialog(WaterBlueDialogChargeForTwoSide.this.isGprsAndBle,
							WaterBlueDialogChargeForTwoSide.this.isOwner, true);
				}
				dismiss();
			}
		});
		setLeftListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (WaterBlueDialogChargeForTwoSide.this.gprsBleChargeOperater != null) {
					WaterBlueDialogChargeForTwoSide.this.gprsBleChargeOperater.clickDialog(WaterBlueDialogChargeForTwoSide.this.isGprsAndBle,
							WaterBlueDialogChargeForTwoSide.this.isOwner, false);
				}
				dismiss();
			}
		});

	}
}
