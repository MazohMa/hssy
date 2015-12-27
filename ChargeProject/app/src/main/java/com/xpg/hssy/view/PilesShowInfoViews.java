package com.xpg.hssy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.popwindow.PileInfoMapPop;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年6月25日
 * @version 2.0.0
 */
public class PilesShowInfoViews extends LinearLayout {

	private List<Pile> piles;
	private Context context;
	private MyOnItemClickListener myOnItemClickListener;
	private ListView lv;
	private Bitmap[] bitMapStrings;
	private PileInfoMapPop pileInfoMapPop;
	private PileInfoAdapters pileInfoAdapters;

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
		public void myOnItemClick(int index, Pile pile);
	}

	public PilesShowInfoViews(Context context) {
		super(context);
		this.context = context;
		pileInfoAdapters = new PileInfoAdapters(context);
		pileInfoAdapters.setMode(EasyAdapter.MODE_RADIO_GROUP);

	}

	public List<Pile> getPiles() {
		return piles;
	}

	public void setPiles(List<Pile> piles) {
		this.piles = piles;
	}

	public PilesShowInfoViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		pileInfoAdapters = new PileInfoAdapters(context);
		pileInfoAdapters.setMode(EasyAdapter.MODE_RADIO_GROUP);

	}

	public PilesShowInfoViews(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		pileInfoAdapters = new PileInfoAdapters(context);
		pileInfoAdapters.setMode(EasyAdapter.MODE_RADIO_GROUP);

	}

	public void init() {
		lv = (ListView) findViewById(R.id.lv);
		lv.setAdapter(pileInfoAdapters);
		pileInfoAdapters.add(piles);
		pileInfoAdapters.select(0);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pileInfoAdapters.select(position);
				if (myOnItemClickListener != null) {
					myOnItemClickListener.myOnItemClick(position,
							(Pile) pileInfoAdapters.getItem(position));
				}
			}
		});
	}

	class PileInfoAdapters extends EasyAdapter<Pile> {
		private Context context;
		private Pile pile;
		private Bitmap bitmap;

		public PileInfoAdapters(Context context, List<Pile> items) {
			super(context, items);
			this.context = context;
		}

		public PileInfoAdapters(Context context) {
			super(context);
			this.context = context;
		}

		public PileInfoAdapters(Context context,
				CenterPointSearchCondition searchCondition) {
			super(context);
			this.context = context;
		}

		@Override
		protected ViewHolder newHolder() {
			return new ViewHolder() {
				private RelativeLayout ll;
				private TextView tv;
				private ImageView img_pile;

				@Override
				protected void update() {
					pile = get(position);
					tv.setText(pile.getPileNameAsString() + "");
					Log.i("tag", position + ":" + pile.getPileNameAsString()
							+ "");
					bitmap = getSingleIcon(pile);
					img_pile.setImageBitmap(bitmap);
					if (isSelected) {
						ll.setEnabled(true);
					} else {
						ll.setEnabled(false);

					}
				}

				@Override
				protected View init(android.view.LayoutInflater arg0) {
					View view = arg0.inflate(
							R.layout.map_piles_adapter_pop_item, null);
					ll = (RelativeLayout) view.findViewById(R.id.ll);
					tv = (TextView) view.findViewById(R.id.tv);
					img_pile = (ImageView) view.findViewById(R.id.img_pile);
					return view;
				}
			};
		}

		public Bitmap getSingleIcon(Pile pile) {
			// 单个桩
			if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
				// 私人桩
				if(pile.getGprsType() == Pile.TYPE_BLUETOOTH){
					return bitMapStrings[1];
				}else{
					return bitMapStrings[0];
				}
//				if (pile.getType() == Pile.TYPE_AC) {
//					// 私人交流
//					if (pile.getShareState() == null
//							|| pile.getShareState() == Pile.SHARE_STATUS_NO
//							|| pile.getIsIdle() == Pile.UNIDLE) {
//						// 私人交流未分享
//						return bitMapStrings[1];
//					} else {
//						// 私人交流已分享
//						return bitMapStrings[0];
//
//					}
//				} else {
//					// 私人直流
//					if (pile.getShareState() == null
//							|| pile.getShareState() == Pile.SHARE_STATUS_NO
//							|| pile.getIsIdle() == Pile.UNIDLE) {
//						// 私人直流未分享
//						return bitMapStrings[3];
//
//					} else {
//						// 私人直流已分享
//						return bitMapStrings[2];
//
//					}
//				}
			} else {
				// 公共桩
				if(pile.getGprsType() == PileStation.TYPE_GPRS_YES){
					return bitMapStrings[2];
				}else{
					return bitMapStrings[3];
				}

			}
		}

	}

}
