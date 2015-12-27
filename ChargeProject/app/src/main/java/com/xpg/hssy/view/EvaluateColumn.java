package com.xpg.hssy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xpg.hssychargingpole.R;

import java.math.BigDecimal;

/**
 * Created by black-Gizwits on 2015/09/06.
 */
public class EvaluateColumn extends LinearLayout {

	private static final int DEFAULT_WEIGHT = 1;

	private static final int DEFAULT_MAX_CHILD_COUNT = 5;
	private static final int DEFAULT_EVALUATE = 0;

	private static final int DEFAULT_EVALUATED_SRC = R.drawable.booking_info_favourite;
	private static final int DEFAULT_UNEVALUATED_SRC = R.drawable.booking_info_favourite_3;
	private static final int DEFAULT_HALF_EVALUATED_SRC = R.drawable.booking_info_favourite_2;

	private boolean fromLayoutSrc = false;
	private int maxChildCount = DEFAULT_MAX_CHILD_COUNT;
	private double evaluate = DEFAULT_EVALUATE;
	private boolean showUnevaluated = true;
	private boolean editable = false;
	private int evaluatedSrc = DEFAULT_EVALUATED_SRC;
	private int halfEvaluatedSrc = DEFAULT_HALF_EVALUATED_SRC;
	private int unevaluatedSrc = DEFAULT_UNEVALUATED_SRC;

	private boolean balanceWeight = true;
	private float itemMargin;
	private float itemMarginLeft;
	private float itemMarginRight;
	private float itemMarginTop;
	private float itemMarginBottom;

	private EvaluateSelectedListener evaluateSelectedListener;

	private OnItemClickListener onItemClickListener;

	public EvaluateColumn(Context context) {
		this(context, null);
	}

	public EvaluateColumn(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EvaluateColumn(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttribute(context, attrs);
	}

	/**
	 * api21以上才能使用
	 *
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 * @param defStyleRes
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public EvaluateColumn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initAttribute(context, attrs);
	}

	private void initAttribute(Context context, AttributeSet attrs) {
		if (null != attrs && attrs.getAttributeCount() > 0) {
			TypedArray mAttrs = context.obtainStyledAttributes(attrs, R.styleable.EvaluateColumn);

			for (int i = 0; i < mAttrs.getIndexCount(); i++) {
				int attr = mAttrs.getIndex(i);
				switch (attr) {
					case R.styleable.EvaluateColumn_evaluate: {
						evaluate = mAttrs.getFloat(R.styleable.EvaluateColumn_evaluate, DEFAULT_EVALUATE);
						break;
					}
					case R.styleable.EvaluateColumn_maxCount: {
						maxChildCount = mAttrs.getInt(R.styleable.EvaluateColumn_maxCount, DEFAULT_MAX_CHILD_COUNT);
						break;
					}
					case R.styleable.EvaluateColumn_editable: {
						editable = mAttrs.getBoolean(R.styleable.EvaluateColumn_editable, false);
						if (editable) {
							evaluateSelectedListener = new EvaluateSelectedListener();
						}
						break;
					}
					case R.styleable.EvaluateColumn_balanceWeight: {
						balanceWeight = mAttrs.getBoolean(R.styleable.EvaluateColumn_balanceWeight, true);
						break;
					}
					case R.styleable.EvaluateColumn_showUnevaluated: {
						showUnevaluated = mAttrs.getBoolean(R.styleable.EvaluateColumn_showUnevaluated, true);
						break;
					}

					case R.styleable.EvaluateColumn_evaluateLayout: {
						fromLayoutSrc = true;
						evaluatedSrc = mAttrs.getResourceId(R.styleable.EvaluateColumn_evaluateLayout, -1);
						break;
					}
					case R.styleable.EvaluateColumn_evaluatedImage: {
						if (!fromLayoutSrc) {
							evaluatedSrc = mAttrs.getResourceId(R.styleable.EvaluateColumn_evaluatedImage, DEFAULT_EVALUATED_SRC);
							halfEvaluatedSrc = mAttrs.getResourceId(R.styleable.EvaluateColumn_halfEvaluatedImage, halfEvaluatedSrc);
							unevaluatedSrc = mAttrs.getResourceId(R.styleable.EvaluateColumn_unevaluatedImage, unevaluatedSrc);
							if (evaluatedSrc != DEFAULT_EVALUATED_SRC) {
								if (halfEvaluatedSrc == DEFAULT_HALF_EVALUATED_SRC) {
									halfEvaluatedSrc = -1;
								}
								if (unevaluatedSrc == DEFAULT_UNEVALUATED_SRC) {
									unevaluatedSrc = -1;
								}
							}
							if (unevaluatedSrc == DEFAULT_UNEVALUATED_SRC) {
								unevaluatedSrc = -1;
								showUnevaluated = false;
							}
						}
						break;
					}


					case R.styleable.EvaluateColumn_itemMarginLeft: {
						itemMarginLeft = mAttrs.getDimension(R.styleable.EvaluateColumn_itemMarginLeft, 0);
						break;
					}
					case R.styleable.EvaluateColumn_itemMarginTop: {
						itemMarginTop = mAttrs.getDimension(R.styleable.EvaluateColumn_itemMarginTop, 0);
						break;
					}
					case R.styleable.EvaluateColumn_itemMarginRight: {
						itemMarginRight = mAttrs.getDimension(R.styleable.EvaluateColumn_itemMarginRight, 0);
						break;
					}
					case R.styleable.EvaluateColumn_itemMarginBottom: {
						itemMarginBottom = mAttrs.getDimension(R.styleable.EvaluateColumn_itemMarginBottom, 0);
						break;
					}
					case R.styleable.EvaluateColumn_itemMargin: {
						itemMargin = mAttrs.getDimension(R.styleable.EvaluateColumn_itemMargin, 0);
						itemMarginLeft = itemMarginTop = itemMarginRight = itemMarginBottom = itemMargin;
						break;
					}
				}
			}
			mAttrs.recycle();
		}
		initChild();
		setEvaluate(evaluate);
	}

	private void initChild() {
		for (int i = 0; i < maxChildCount; i++) {
			View childView = createChildView(evaluatedSrc);
			if (editable) {
				childView.setOnClickListener(evaluateSelectedListener);
			}
			addView(childView);
		}
	}

	private View createChildView(int srcId) throws RuntimeException {
		View v = null;
		if (true == fromLayoutSrc) {
			v = createViewFromLayout(srcId);
		} else {
			v = createViewFromDrawable(srcId);
		}
		if (null == v) {
			throw new RuntimeException("unSupportSrcId: " + srcId);
		}
		return v;
	}

	/**
	 * @param srcId
	 * @return
	 */
	@Nullable
	private View createViewFromDrawable(int srcId) {
		try {
			Drawable dr = getContext().getResources().getDrawable(srcId);
			ImageView iv = new ImageView(getContext());
			iv.setImageDrawable(dr);
			return iv;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param srcId
	 * @return
	 */
	@Nullable
	private View createViewFromLayout(int srcId) {
		try {
			XmlResourceParser mLayout = getContext().getResources().getLayout(srcId);
			View v = LayoutInflater.from(getContext()).inflate(mLayout, null);
			return v;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) childView.getLayoutParams();
			if (balanceWeight) {
				params.weight = DEFAULT_WEIGHT;
			}
			params.leftMargin = (int) itemMarginLeft;
			params.topMargin = (int) itemMarginTop;
			params.rightMargin = (int) itemMarginRight;
			params.bottomMargin = (int) itemMarginBottom;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public double getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(double eva) {
		evaluate = eva;
		if (!fromLayoutSrc) {
			setImageEvaluate();
		} else {
			setLayoutEvaluate();
		}
	}

	private void setImageEvaluate() {
		int integerPart;
		double decimalPart;
		if (evaluate > getChildCount()) {
			evaluate = integerPart = getChildCount();
			decimalPart = 0;
		} else {
			BigDecimal decimal = new BigDecimal(evaluate).setScale(1, BigDecimal.ROUND_HALF_UP);//四舍五入,保留小数点后一位
			integerPart = decimal.setScale(0, BigDecimal.ROUND_DOWN).intValue();//取整
			decimalPart = decimal.subtract(new BigDecimal(integerPart)).doubleValue();//取小数
		}
		int i = 0;
		for (; i < integerPart; i++) {
			ImageView item = (ImageView) getChildAt(i);
			item.setImageResource(evaluatedSrc);
		}
		if (decimalPart >= 0.5 && halfEvaluatedSrc != -1) {
			ImageView item = (ImageView) getChildAt(i);
			item.setImageResource(halfEvaluatedSrc);
			i++;
		}
		// 把最后的星星置空或消失
		for (; i < getChildCount(); i++) {
			ImageView item = (ImageView) getChildAt(i);
			if (showUnevaluated) {
				item.setVisibility(VISIBLE);
				item.setImageResource(unevaluatedSrc);
			} else {
				item.setVisibility(INVISIBLE);
			}
		}
	}

	private void setLayoutEvaluate() {
		evaluate = BigDecimal.valueOf(evaluate).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		int childCount = getChildCount();
		int i = 0;
		for (; i < evaluate; i++) {
			getChildAt(i).setSelected(true);
		}
		for (; i < childCount; i++) {
			getChildAt(i).setSelected(false);
		}
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	private class EvaluateSelectedListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (child.equals(v)) {
					setEvaluate(i + 1);
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(v, i);
					}
					break;
				}
			}

		}
	}

	public interface OnItemClickListener {
		public void onItemClick(View v, int index);
	}
}
