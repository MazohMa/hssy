package com.xpg.hssy.view;

import java.util.List;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.popwindow.PileInfoMapPop;
import com.xpg.hssy.view.PileInfoItemInMapView.MyViewClick;
import com.xpg.hssychargingpole.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年6月25日
 * @version 2.0.0
 */
public class PilesShowInfoView extends LinearLayout {

	private List<Pile> piles;
	private Context context;
	private MyOnItemClickListener myOnItemClickListener;
	private LinearLayout listlinear;
	private Bitmap[] bitMapStrings;
	private PileInfoMapPop pileInfoMapPop ; 

	public PileInfoMapPop getPileInfoMapPop() {
		return pileInfoMapPop;
	}

	public void setPileInfoMapPop(PileInfoMapPop pileInfoMapPop) {
		this.pileInfoMapPop = pileInfoMapPop;
	}

	public Bitmap[] getBitMapStrings() {
		return bitMapStrings;
	}

	public void setBitMapStrings(Bitmap[] bitMapStrings) {
		this.bitMapStrings = bitMapStrings;
	}

	public MyOnItemClickListener getMyOnItemClickListener() {
		return myOnItemClickListener;
	}

	public void setMyOnItemClickListener(
			MyOnItemClickListener myOnItemClickListener) {
		this.myOnItemClickListener = myOnItemClickListener;
	}

	public interface MyOnItemClickListener {
		public void myOnItemClick(int index ,Pile pile);
	}

	public PilesShowInfoView(Context context) {
		super(context);
		this.context = context;
	}

	public List<Pile> getPiles() {
		return piles;

	}

	public void setPiles(List<Pile> piles) {
		this.piles = piles;
	}

	public PilesShowInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public PilesShowInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void init() {
		listlinear = (LinearLayout) findViewById(R.id.listlinear);
		listlinear.removeAllViews();
		if (piles != null && piles.size() > 0) {
			for (int i = 0; i < piles.size(); i++) {
				PileInfoItemInMapView tsv = new PileInfoItemInMapView(pileInfoMapPop,i,context,
						piles.get(i));
				tsv.setBitMapStrings(bitMapStrings);
				tsv.init();
				listlinear.addView(tsv);
			}
			for (int i = 0; i < listlinear.getChildCount(); i++) {
				PileInfoItemInMapView pileInfoItemInMapView  = (PileInfoItemInMapView) listlinear.getChildAt(i) ; 
				pileInfoItemInMapView.setMyViewClick(new MyViewClick() {
					
					@Override
					public void onClickMyView(int index ,Pile pile) {
						if (myOnItemClickListener != null) {
							
							myOnItemClickListener.myOnItemClick(index ,pile);

						}
					}
				}) ;
			}
		}
	}

}
