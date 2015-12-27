package com.xpg.hssy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.popwindow.PileInfoMapPop;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年6月25日
 * @version 2.0.0
 */
public class PileInfoItemInMapView extends RelativeLayout {
	private Context context;
	private TextView tv;
	private ImageView img_pile;
	private Pile pile;
	private RelativeLayout ll;
	private Bitmap[] bitMapStrings;
	private int index;
	private PileInfoMapPop pileInfoMapPop ; 

	public Bitmap[] getBitMapStrings() {
		return bitMapStrings;
	}

	public void setBitMapStrings(Bitmap[] bitMapStrings) {
		this.bitMapStrings = bitMapStrings;
	}

	public PileInfoItemInMapView(PileInfoMapPop pileInfoMapPop,int index, Context context, Pile pile) {
		super(context);
		this.pileInfoMapPop = pileInfoMapPop ; 
		this.index = index;
		this.context = context;
		this.pile = pile;

	}

	@SuppressLint("ResourceAsColor")
	public void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.map_piles_adapter_pop_item, null);
		ll = (RelativeLayout) view.findViewById(R.id.ll);
		tv = (TextView) view.findViewById(R.id.tv);
		img_pile = (ImageView) view.findViewById(R.id.img_pile);
		tv.setText(pile.getPileNameAsString() + "");
		Bitmap bitmap = getSingleIcon(pile);
		img_pile.setImageBitmap(bitmap);
//		if (index == 0) {
//			ll.setPressed(true) ; 	
//			Log.i("=======", "pilesShowInfoView" + index) ;
//
//		}else{
//			ll.setPressed(false) ; 	
//			Log.i("=======", "pilesShowInfoView" + index) ;
//		}
		
		ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				ll.setPressed(isPressed()) ;
				if (myViewClick != null) {
					
					myViewClick.onClickMyView(index, pile);
				}
			}
		});
		addView(view);

	}

	public Bitmap getSingleIcon(Pile pile) {
		// int drawableId = 0;
		// 单个桩
		if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
			// 私人桩
			if (pile.getType() == Pile.TYPE_AC) {
				// 私人交流
				if (pile.getShareState() == null
						|| pile.getShareState() == Pile.SHARE_STATUS_NO
						|| pile.getIsIdle() == Pile.UNIDLE) {
					// 私人交流未分享
					return bitMapStrings[1];
				} else {
					// 私人交流已分享
					return bitMapStrings[0];

				}
			} else {
				// 私人直流
				if (pile.getShareState() == null
						|| pile.getShareState() == Pile.SHARE_STATUS_NO
						|| pile.getIsIdle() == Pile.UNIDLE) {
					// 私人直流未分享
					return bitMapStrings[3];

				} else {
					// 私人直流已分享
					return bitMapStrings[2];

				}
			}
		} else {
			// 公共桩
			return bitMapStrings[4];

		}
	}

	private MyViewClick myViewClick;

	public MyViewClick getMyViewClick() {
		return myViewClick;
	}

	public void setMyViewClick(MyViewClick myViewClick) {
		this.myViewClick = myViewClick;
	}

	public interface MyViewClick {
		public void onClickMyView(int index, Pile pile);
	}
}
