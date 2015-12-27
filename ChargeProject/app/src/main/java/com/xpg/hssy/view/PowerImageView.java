package com.xpg.hssy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xpg.hssychargingpole.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * PowerImageView是一个经过扩展的ImageView，它不仅继承了ImageView原生的所有功能，还加入了播放GIF动画的功能。
 * 
 * @author guolin
 */
public class PowerImageView extends ImageView implements OnClickListener {

	/**
	 * 播放GIF动画的关键类
	 */
	private Movie mMovie;


	/**
	 * 记录动画开始的时间
	 */
	private long mMovieStart;

	/**
	 * GIF图片的宽度
	 */
	private int mImageWidth;

	/**
	 * GIF图片的高度
	 */
	private int mImageHeight;

	/**
	 * 图片是否正在播放
	 */
	private boolean isPlaying;
	int measuredHeight;
	int measuredWidth;
	

	/**
	 * PowerImageView构造函数。
	 * 
	 * @param context
	 */
	public PowerImageView(Context context) {
		super(context);
	}

	/**
	 * PowerImageView构造函数。
	 * 
	 * @param context
	 */
	public PowerImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * PowerImageView构造函数，在这里完成所有必要的初始化操作。
	 * 
	 * @param context
	 */
	public PowerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PowerImageView);
		int resourceId = getResourceId(a, context, attrs);
		if (resourceId != 0) {
			// 当资源id不等于0时，就去获取该资源的流
			InputStream is = getResources().openRawResource(resourceId);
			// 使用Movie类对流进行解码
			mMovie = Movie.decodeStream(is);
			if (mMovie != null) {
				// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性

				Bitmap bitmap = BitmapFactory.decodeStream(is);
				mImageWidth = bitmap.getWidth();
				mImageHeight = bitmap.getHeight();
				bitmap.recycle();

			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == getId()) {
			// 当用户点击图片时，开始播放GIF动画
			isPlaying = true;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMovie == null) {
			// mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
			super.onDraw(canvas);
		} else {
			// mMovie不等于null，说明是张GIF图片

			// 如果允许自动播放，就调用playMovie()方法播放GIF动画
			playMovie(canvas);
			invalidate();

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measuredHeight = measureHeight(heightMeasureSpec);  
		  
		measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);  
//		if (mMovie != null) {
//			// 如果是GIF图片则重写设定PowerImageView的大小
//			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//		}
		
	}
	
	private int measureWidth(int measureSpec) {  
		  
		int specMode = MeasureSpec.getMode(measureSpec);  
		int specSize = MeasureSpec.getSize(measureSpec);  
		  
		// Default size if no limits are specified.  
		  
		int result = 500;  
		  
		if (specMode == MeasureSpec.AT_MOST)  
		  
		{  
		  
		// Calculate the ideal size of your control  
		  
		// within this maximum size.  
		  
		// If your control fills the available space  
		  
		// return the outer bound.  
		  
		result = specSize;  
		  
		}   
		  
		else if (specMode == MeasureSpec.EXACTLY)   
		  
		{  
		  
		// If your control can fit within these bounds return that value.  
		  
		result = specSize;  
		  
		}  
		  
		return result;  
		  
		}  
	
	private int measureHeight(int measureSpec) {  
		  
		int specMode = MeasureSpec.getMode(measureSpec);  
		int specSize = MeasureSpec.getSize(measureSpec);  
		  
		// Default size if no limits are specified.  
		  
		int result = 500;  
		  
		if (specMode == MeasureSpec.AT_MOST)   
		  
		{  
		  
		// Calculate the ideal size of your  
		  
		// control within this maximum size.  
		  
		// If your control fills the available  
		  
		// space return the outer bound.  
		  
		result = specSize;  
		  
		}   
		  
		else if (specMode == MeasureSpec.EXACTLY)   
		  
		{  
		  
		// If your control can fit within these bounds return that value.  
		  
		result = specSize;  
		  
		}  
		  
		return result;  
		  
		}  

	/**
	 * 开始播放GIF动画，播放完成返回true，未完成返回false。
	 * 
	 * @param canvas
	 * @return 播放完成返回true，未完成返回false。
	 */
	private boolean playMovie(Canvas canvas) {
		long now = SystemClock.uptimeMillis();
		 float saclex = (float) measuredWidth  / (float) mImageWidth;
         float sacley = (float) measuredHeight / (float) mImageHeight;
         float sameRate = saclex < sacley ? saclex : sacley;
         canvas.scale(sameRate, sameRate);
		if (mMovieStart == 0) {
			mMovieStart = now;
		}
		int duration = mMovie.duration();
		if (duration == 0) {
			duration = 1000;
		}
		int relTime = (int) ((now - mMovieStart) % duration);
		mMovie.setTime(relTime);
		mMovie.draw(canvas, 0, 0);
		if ((now - mMovieStart) >= duration) {
			mMovieStart = 0;
			return true;
		}
		return false;
	}

	/**
	 * 通过Java反射，获取到src指定图片资源所对应的id。
	 * 
	 * @param a
	 * @param context
	 * @param attrs
	 * @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0。
	 */
	private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {
		try {
			Field field = TypedArray.class.getDeclaredField("mValue");
			field.setAccessible(true);
			TypedValue typedValueObject = (TypedValue) field.get(a);
			return typedValueObject.resourceId;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (a != null) {
				a.recycle();
			}
		}
		return 0;
	}

}
