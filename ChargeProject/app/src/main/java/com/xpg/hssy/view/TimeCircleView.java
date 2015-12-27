package com.xpg.hssy.view;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.easy.util.BitmapUtil;
import com.easy.util.ScreenUtil;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年5月12日
 * @version 1.0.0
 */

public class TimeCircleView extends View implements OnTouchListener {
	private static final float TEXT_SCALE = 0.75f;// 字体在画面上的高与字体实际大小的比例
	private static final int MIN_STEP = 30;// 最小步长
	private static final int MAX_TURNS = 1;// 最大圈数
	private static final int MAX_MINUTES = 12 * 60 * (MAX_TURNS + 1);// 最大分钟数
	private static final int MIN_MINUTES = 0;// 最小分钟数
	private static final int MAX_PERIOD = 600;// 最大时间段跨度
	private static final int MIN_PERIOD = 30;// 最小时间段跨度

	private static final int STATUS_IDLE = 0;
	private static final int STATUS_MOVING_BEGIN_DOT = 1;
	private static final int STATUS_MOVING_END_DOT = 2;

	// 用户转动的参数
	private int status;
	private double curDegrees;
	private int curTurns;
	private int turnsBegin;
	private int turnsEnd;

	// 尺寸
	private int dimenTouchSize;
	private int dimenWidth;
	private int dimenHeight;
	private int dimenTextMargin;
	private int dimenTimeMarginTop;
	private int dimenTimeTextSize;
	private int dimenNonTextSize;
	private int dimenDateMarginTop;
	private int dimenDateTextSize;
	private int dimenLineWidth;
	private int dimenCircleRadius;
	private int centerX;
	private int centerY;

	// 画面元素
	private Bitmap bmpCircleBg;
	private TimeCircleDot dotBegin = new TimeCircleDot();
	private TimeCircleDot dotEnd = new TimeCircleDot();
	// private TimeCircleDot dotEndOn = new TimeCircleDot();
	// private TimeCircleDot dotEndOff = new TimeCircleDot();

	// 颜色
	private int colorTextBlue;
	private int colorTextGray;
	private int colorLineGray;
	private int colorLineBegin;
	private int colorLineEnd;

	// 画笔
	private Paint paintTime;
	private Paint paintTo;
	private Paint paintNon;
	private Paint paintDate;
	private Paint paintLine;

	// 时间参数
	// private boolean hasEnd;
	private int beginMinutes = 2 * 60;
	private int endMinutes = beginMinutes + MIN_PERIOD;
	private int month = -1;
	private int day = -1;
	private boolean showTime;

	// 绘图位置
	private Rect rectDotSrc = new Rect();
	private Rect rectDotDst = new Rect();
	private RectF rectLine;

	private TimeCircleListener listener;

	public TimeCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public TimeCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TimeCircleView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		// 画面元素
		bmpCircleBg = BitmapUtil.get(context, R.drawable.zhao_yuan_bg1);
		Matrix m = new Matrix();
		m.setRotate(180);
		Bitmap bmpDotBegin = BitmapUtil.get(context,
				R.drawable.zhao_yuan_icon_xia2);
		Bitmap bmpDotBeginOn = Bitmap.createBitmap(bmpDotBegin, 0, 0,
				bmpDotBegin.getWidth(), bmpDotBegin.getHeight(), m, true);
		bmpDotBegin.recycle();
		bmpDotBegin = BitmapUtil.get(context, R.drawable.zhao_yuan_icon_xia3);
		Bitmap bmpDotBeginOff = Bitmap.createBitmap(bmpDotBegin, 0, 0,
				bmpDotBegin.getWidth(), bmpDotBegin.getHeight(), m, true);
		bmpDotBegin.recycle();
		Bitmap bmpDotEndOn = BitmapUtil.get(context,
				R.drawable.zhao_yuan_icon_xia1);
		Bitmap bmpDotEndOff = BitmapUtil.get(context,
				R.drawable.zhao_yuan_icon_xia3);
		dotBegin.setBmpOn(bmpDotBeginOn);
		dotBegin.setBmpOff(bmpDotBeginOff);
		dotEnd.setBmpOn(bmpDotEndOn);
		dotEnd.setBmpOff(bmpDotEndOff);

		// 颜色
		colorTextBlue = 0xFF00FFF6;
		colorTextGray = 0xFFA3ACAF;
		colorLineGray = 0xFFE8E8E8;
		colorLineBegin = 0xFF00F0E8;
		colorLineEnd = 0xFF05F17C;

		// 尺寸
		float scale = ScreenUtil.getShortSide(context) / 1080f;
		dimenTouchSize = (int) (scale * 60);
		dimenWidth = bmpCircleBg.getWidth() + bmpDotBegin.getWidth();
		dimenHeight = bmpCircleBg.getHeight() + bmpDotBegin.getHeight();
		dimenTextMargin = (int) (scale * 18);
		dimenTimeMarginTop = (int) (scale * 200);
		dimenTimeTextSize = (int) (scale * 130);
		dimenNonTextSize = (int) (scale * 70);
		dimenDateMarginTop = (int) (scale * 70);
		dimenDateTextSize = (int) (scale * 35);
		dimenLineWidth = (int) (scale * 10);
		centerX = dimenWidth / 2;
		centerY = dimenHeight / 2;
		int offset = (int) (scale * 15);
		int left = (dimenWidth - bmpCircleBg.getWidth()) / 2;
		int top = (dimenHeight - bmpCircleBg.getHeight()) / 2;
		dimenCircleRadius = bmpCircleBg.getWidth() / 2 - offset;
		rectLine = new RectF(left + offset, top + offset, left
				+ bmpCircleBg.getWidth() - offset, top
				+ bmpCircleBg.getHeight() - offset);

		// 画笔
		paintTime = new Paint();
		paintTime.setColor(colorTextBlue);
		paintTime.setTextSize(dimenTimeTextSize);
		paintTime.setAntiAlias(true);
		paintNon = new Paint();
		paintNon.setColor(colorTextBlue);
		paintNon.setTextSize(dimenNonTextSize);
		paintNon.setAntiAlias(true);
		paintTo = new Paint();
		paintTo.setColor(colorTextBlue);
		paintTo.setTextSize(dimenDateTextSize);
		paintTo.setAntiAlias(true);
		paintDate = new Paint();
		paintDate.setColor(colorTextGray);
		paintDate.setTextSize(dimenDateTextSize);
		paintDate.setAntiAlias(true);
		paintLine = new Paint();
		paintLine.setStrokeWidth(dimenLineWidth);
		paintLine.setStyle(Paint.Style.STROKE);
		paintLine.setColor(colorLineGray);
		paintLine.setAntiAlias(true);

		setOnTouchListener(this);
		reset();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(dimenWidth, dimenHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 画背景
		drawBg(canvas);

		int textY = 0;

		// 画开始时间
		textY = dimenTimeMarginTop;
		if (dotBegin.isOn()) {
			textY += dimenTimeTextSize * TEXT_SCALE;
			drawTime(canvas, beginMinutes, textY);
		} else {
			drawNon(canvas, textY);
			textY += dimenTimeTextSize * TEXT_SCALE;
		}

		// 画“至”
		textY += dimenTextMargin + dimenDateTextSize;
		drawTo(canvas, textY);

		// 画结束时间
		textY += dimenTextMargin;
		if (dotEnd.isOn()) {
			textY += dimenTimeTextSize * TEXT_SCALE;
			drawTime(canvas, endMinutes, textY);
		} else {
			drawNon(canvas, textY);
			textY += dimenTimeTextSize * TEXT_SCALE;
		}

		// 画日期
		if (showTime) {
			textY += dimenDateMarginTop + dimenDateTextSize;
			drawDate(canvas, textY);
		}

		// 画弧线
		float beginTimeDegress = time2Degrees(beginMinutes) - 90;
		float endTimeDegress = time2Degrees(endMinutes) - 90;
		canvas.rotate(beginTimeDegress, centerX, centerY);
		if (dotBegin.isOn() && dotEnd.isOn()) {
			SweepGradient sweepG = new SweepGradient(dimenWidth / 2,
					dimenHeight / 2, new int[] { colorLineBegin, colorLineEnd,
							0 }, new float[] { 0f,
							(endTimeDegress - beginTimeDegress) / 360f, 1f });
			paintLine.setShader(sweepG);
		} else {
			paintLine.setShader(null);
		}
		canvas.drawArc(rectLine, 0, endTimeDegress - beginTimeDegress, false,
				paintLine);

		// 画开始点
		drawDot(canvas, dotBegin);

		// 画结束点
		canvas.rotate(endTimeDegress - beginTimeDegress, centerX, centerY);
		drawDot(canvas, dotEnd);

		// 计算点的位置用于判断用户是否触碰该点
		int x = (int) (centerX + dimenCircleRadius
				* Math.cos(Math.PI * beginTimeDegress / 180));
		int y = (int) (centerY + dimenCircleRadius
				* Math.sin(Math.PI * beginTimeDegress / 180));
		dotBegin.setX(x);
		dotBegin.setY(y);
		x = (int) (centerX + dimenCircleRadius
				* Math.cos(Math.PI * endTimeDegress / 180));
		y = (int) (centerY + dimenCircleRadius
				* Math.sin(Math.PI * endTimeDegress / 180));
		dotEnd.setX(x);
		dotEnd.setY(y);

		// 还原画布角度
		canvas.rotate(-endTimeDegress, centerX, centerY);
	}

	private void drawBg(Canvas canvas) {
		canvas.drawBitmap(bmpCircleBg, centerX - bmpCircleBg.getWidth() / 2,
				centerY - bmpCircleBg.getHeight() / 2, null);
	}

	private void drawNon(Canvas canvas, int y) {
		canvas.drawText("— —", centerX - dimenNonTextSize * 0.95f, y
				+ dimenNonTextSize, paintNon);
	}

	private void drawTime(Canvas canvas, int minutes, int y) {
		int hour = minutes / 60;
		int minute = minutes % 60;
		String hourStr = hour < 10 ? "0" + hour : "" + hour;
		String minStr = minute < 10 ? "0" + minute : "" + minute;
		canvas.drawText(hourStr + ":" + minStr, centerX - dimenTimeTextSize
				* 1.25f, y, paintTime);
	}

	private void drawTo(Canvas canvas, int y) {
		canvas.drawText("至", centerX - dimenDateTextSize * 0.5f, y, paintTo);
	}

	private void drawDate(Canvas canvas, int y) {
		String monthStr = month < 10 ? "0" + month : "" + month;
		String dayStr = day < 10 ? "0" + day : "" + day;
		canvas.drawText(monthStr + "月" + dayStr + "日", centerX
				- dimenDateTextSize * 2f, y, paintDate);
	}

	private void drawDot(Canvas canvas, TimeCircleDot dot) {
		int dotDstLeft = centerX + dimenCircleRadius - dot.getWidth() / 2;
		int dotDstTop = centerY - dot.getHeight() / 2;
		rectDotSrc.set(0, 0, dot.getWidth(), dot.getHeight());
		rectDotDst.set(dotDstLeft, dotDstTop, dotDstLeft + dot.getWidth(),
				dotDstTop + dot.getHeight());
		canvas.drawBitmap(dot.getBmp(), rectDotSrc, rectDotDst, null);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// LogUtil.e("", "ACTION_DOWN");
			// 调节开始时间
			if (Math.abs(x - dotBegin.getX()) < dimenTouchSize
					&& Math.abs(y - dotBegin.getY()) < dimenTouchSize) {
				status = STATUS_MOVING_BEGIN_DOT;
				curDegrees = time2Degrees(beginMinutes) % 360;
				curTurns = turnsBegin;
				setBeginOn();
				if (listener != null) {
					listener.onStart(beginMinutes, endMinutes);
				}
				break;
			}

			// 调节结束时间
			if (Math.abs(x - dotEnd.getX()) < dimenTouchSize
					&& Math.abs(y - dotEnd.getY()) < dimenTouchSize) {
				status = STATUS_MOVING_END_DOT;
				curDegrees = time2Degrees(endMinutes) % 360;
				curTurns = turnsEnd;
				setEndOn();
				if (listener != null) {
					listener.onStart(beginMinutes, endMinutes);
				}
				break;
			}
			status = STATUS_IDLE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (status == STATUS_IDLE) {
				break;
			}

			// 计算角度
			double nextDegrees = position2Degrees(x, y);
			int nextTurns = curTurns;
			// LogUtil.e("", "degrees: " + curDegrees + "/" + nextDegrees);
			// LogUtil.v("", "nextTurns0: " + nextTurns);
			// 计算圈数
			if (curDegrees > 270 && nextDegrees < 90) {
				// 顺时针跨越0点
				nextTurns++;
			} else if (curDegrees < 90 && nextDegrees > 270) {
				// 逆时针跨越0点
				nextTurns--;
			}
			// LogUtil.v("", "nextTurns1: " + nextTurns);
			// 纠正圈数
			if (nextTurns < -1) {
				nextTurns = -1;
			}
			if (nextTurns > MAX_TURNS + 1) {
				nextTurns = MAX_TURNS + 1;
			}
			// LogUtil.v("", "nextTurns2: " + nextTurns);

			// 计算时间
			int nextMinutes = (int) (12 * 60 * (nextTurns + nextDegrees / 360));
			// LogUtil.v("", "nextMinutes: " + nextMinutes);

			// 设置时间
			switch (status) {
			case STATUS_MOVING_BEGIN_DOT:
				setBeginMinutes(nextMinutes);
				break;
			case STATUS_MOVING_END_DOT:
				setEndMinutes(nextMinutes);
				break;
			}
			// LogUtil.v("", "beginMinutes: " + beginMinutes);
			// LogUtil.v("", "endMinutes: " + endMinutes);

			curDegrees = nextDegrees;
			curTurns = nextTurns;
			break;
		case MotionEvent.ACTION_UP:
			// LogUtil.e("", "ACTION_UP");
			status = STATUS_IDLE;
			// 纠正圈数
			if (turnsBegin < 0) {
				turnsBegin = 0;
			}
			if (turnsBegin > MAX_TURNS) {
				turnsBegin = MAX_TURNS;
			}
			if (turnsEnd < 0) {
				turnsEnd = 0;
			}
			// 当结束点停留在最后一圈的12点时，圈数会比最大圈数多一圈
			if (turnsEnd > MAX_TURNS + 1) {
				turnsEnd = MAX_TURNS + 1;
			}
			if (listener != null) {
				listener.onFinish(beginMinutes, endMinutes);
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将时间转为角度
	 * 
	 * @param minutes
	 * @return 0点为0度，12点为360度，24点为720度
	 */
	private float time2Degrees(int minutes) {
		return 360 * minutes / 12f / 60f;
	}

	/**
	 * 将touch坐标转换为角度，参考的中心点为整个view的中点
	 * 
	 * @param x
	 * @param y
	 * @return 正上方为0度，正右方为90度，正下方为180度，正左方为270度
	 */
	private double position2Degrees(double x, double y) {
		double radius = Math.sqrt(Math.pow(x - centerX, 2)
				+ Math.pow(y - centerY, 2));
		double degrees = 180 * Math.acos((x - centerX) / radius) / Math.PI;
		if (y < centerY) {
			degrees = 360 - degrees;
		}
		degrees += 90;
		degrees %= 360;
		return degrees;
	}

	public void setEndOn() {
		setBeginOn();
		if (!dotEnd.isOn()) {
			dotEnd.setOn(true);
			invalidate();
		}
	}

	public boolean isEndOn() {
		return dotEnd.isOn();
	}

	public void setBeginOn() {
		if (!dotBegin.isOn()) {
			dotBegin.setOn(true);
			invalidate();
		}
	}

	public boolean isBeginOn() {
		return dotBegin.isOn();
	}

	public void reset() {
		Calendar c = Calendar.getInstance();
		int minutes = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
		setBeginMinutes(minutes);
		setEndMinutes(beginMinutes + MIN_PERIOD);
		dotBegin.setOn(false);
		dotEnd.setOn(false);
		invalidate();
	}

	// public boolean isHasEnd() {
	// return hasEnd;
	// }
	//
	// public void setHasEnd(boolean hasEnd) {
	// this.hasEnd = hasEnd;
	// if (!hasEnd) {
	// setEndMinutes(beginMinutes + MIN_PERIOD);
	// }
	// invalidate();
	// }

	public TimeCircleListener getListener() {
		return listener;
	}

	public void setListener(TimeCircleListener listener) {
		this.listener = listener;
	}

	public int getBeginMinutes() {
		return beginMinutes;
	}

	public void setBeginMinutes(int nextBeginMins) {
		int nextEndMins = endMinutes;
		if (nextBeginMins < MIN_MINUTES) {
			// 如果小于最小时间点
			nextBeginMins = MIN_MINUTES;
		} else if (nextBeginMins > MAX_MINUTES - MIN_PERIOD) {
			// 如果大于最大时间点
			nextBeginMins = MAX_MINUTES - MIN_PERIOD;
		}
		if (dotEnd.isOn()) {
			// 如果设置了结束时间
			if (nextEndMins - nextBeginMins < MIN_PERIOD) {
				// 如果小于最小时长
				nextEndMins = nextBeginMins + MIN_PERIOD;
			} else if (nextEndMins - nextBeginMins > MAX_PERIOD) {
				// 如果大于最大时长
				nextEndMins = nextBeginMins + MAX_PERIOD;
			}
		} else {
			// 如果没有设置结束时间
			nextEndMins = nextBeginMins + MIN_PERIOD;
		}
		// 纠正步长
		nextBeginMins = Math.round((float) nextBeginMins / MIN_STEP) * MIN_STEP;
		nextEndMins = Math.round((float) nextEndMins / MIN_STEP) * MIN_STEP;
		// 纠正圈数
		turnsBegin = nextBeginMins / 60 / 12;
		turnsEnd = nextEndMins / 60 / 12;
		// LogUtil.d("", "turns: " + turnsBegin + "/" + turnsEnd);
		// 刷新画面
		if (beginMinutes != nextBeginMins || endMinutes != nextEndMins) {
			beginMinutes = nextBeginMins;
			endMinutes = nextEndMins;
			invalidate();
			if (listener != null) {
				listener.onChanged(beginMinutes, endMinutes);
			}
		}

	}

	public int getEndMinutes() {
		return endMinutes;
	}

	public void setEndMinutes(int nextEndMins) {
		int nextBeginMins = beginMinutes;
		if (nextEndMins < MIN_MINUTES + MIN_PERIOD) {
			// 如果小于最小时间点
			nextEndMins = MIN_MINUTES + MIN_PERIOD;
		} else if (nextEndMins > MAX_MINUTES) {
			// 如果大于最大时间点
			nextEndMins = MAX_MINUTES;
		}
		if (nextEndMins - nextBeginMins < MIN_PERIOD) {
			// 如果小于最小时长
			nextBeginMins = nextEndMins - MIN_PERIOD;
		} else if (nextEndMins - nextBeginMins > MAX_PERIOD) {
			// 如果大于最大时长
			nextBeginMins = nextEndMins - MAX_PERIOD;
		}
		// 纠正步长
		nextBeginMins = Math.round((float) nextBeginMins / MIN_STEP) * MIN_STEP;
		nextEndMins = Math.round((float) nextEndMins / MIN_STEP) * MIN_STEP;
		// 纠正圈数
		turnsBegin = nextBeginMins / 60 / 12;
		turnsEnd = nextEndMins / 60 / 12;
		// LogUtil.d("", "turns: " + turnsBegin + "/" + turnsEnd);
		// 刷新画面
		if (beginMinutes != nextBeginMins || endMinutes != nextEndMins) {
			beginMinutes = nextBeginMins;
			endMinutes = nextEndMins;
			invalidate();
			if (listener != null) {
				listener.onChanged(beginMinutes, endMinutes);
			}
		}
	}

	public void setDate(int month, int day) {
		this.month = month;
		this.day = day;
		invalidate();
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
		invalidate();
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
		invalidate();
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public void showTime() {
		this.showTime = true;
		invalidate();
	}

	public void hideTime() {
		this.showTime = false;
		invalidate();
	}
}
