/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.crop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import com.easy.util.BitmapUtil;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CropImageActivity extends MonitoredActivity {

	private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;
	private static final int SIZE_DEFAULT = 2048;
	private static final int SIZE_LIMIT = 4096;

	private final Handler handler = new Handler();

	private int aspectX;
	private int aspectY;

	private int maxX;
	private int maxY;
	private int exifRotation;
	private String path;

	private boolean isSaving;

	private int sampleSize;
	private RotateBitmap rotateBitmap;
	private CropImageView imageView;
	private HighlightView cropView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crop__activity_crop);
		initViews();

		setupFromIntent();
		if (rotateBitmap == null) {
			finish();
			return;
		}
		startCrop();
	}

	private void initViews() {
		imageView = (CropImageView) findViewById(R.id.crop_image);
		imageView.context = this;
		imageView.setRecycler(new ImageViewTouchBase.Recycler() {
			@Override
			public void recycle(Bitmap b) {
				b.recycle();
				System.gc();
			}
		});

		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveClicked();
			}
		});
	}

	private void setupFromIntent() {
		Intent extras = getIntent();
		if (extras != null) {
			aspectX = extras.getIntExtra(Crop.Extra.ASPECT_X, 1);
			aspectY = extras.getIntExtra(Crop.Extra.ASPECT_Y, 1);
			maxX = extras.getIntExtra(Crop.Extra.MAX_X, 50);
			maxY = extras.getIntExtra(Crop.Extra.MAX_Y, 50);
			path = extras.getStringExtra("data");
		}
		if (EmptyUtil.notEmpty(path)) {
			File file = new File(path);
			if (!file.exists()) return;
			exifRotation = CropUtil.getExifRotation(file);
			InputStream is = null;
			try {
				sampleSize = calculateBitmapSampleSize(file);
				is = new FileInputStream(file);
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = sampleSize;
				rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), exifRotation);
			} catch (IOException e) {
				LogUtils.e("", "Error reading image: " + e.getMessage());
				setResultException(e);
			} catch (OutOfMemoryError e) {
				LogUtils.e("", "OOM reading image: " + e.getMessage());
				setResultException(e);
			} finally {
				CropUtil.closeSilently(is);
			}
		}
	}

	private int calculateBitmapSampleSize(File file) throws IOException {
		InputStream is = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			is = new FileInputStream(file);
			BitmapFactory.decodeStream(is, null, options); // Just get image size
		} finally {
			CropUtil.closeSilently(is);
		}

		int maxSize = getMaxImageSize();
		int sampleSize = 1;
		while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
			sampleSize = sampleSize << 1;
		}
		return sampleSize;
	}

	private int getMaxImageSize() {
		int textureLimit = getMaxTextureSize();
		if (textureLimit == 0) {
			return SIZE_DEFAULT;
		} else {
			return Math.min(textureLimit, SIZE_LIMIT);
		}
	}

	private int getMaxTextureSize() {
		// The OpenGL texture size is the maximum size that can be drawn in an ImageView
		int[] maxSize = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		return maxSize[0];
	}

	private void startCrop() {
		if (isFinishing()) {
			return;
		}
		imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
		CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.loading), new Runnable() {
			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				handler.post(new Runnable() {
					public void run() {
						if (imageView.getScale() == 1F) {
							imageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				new Cropper().crop();
			}
		}, handler);
	}

	private class Cropper {

		private void makeDefault() {
			if (rotateBitmap == null) {
				return;
			}

			HighlightView hv = new HighlightView(imageView);
			final int width = rotateBitmap.getWidth();
			final int height = rotateBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// Make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			@SuppressWarnings("SuspiciousNameCombination") int cropHeight = cropWidth;

			if (aspectX != 0 && aspectY != 0) {
				if (aspectX > aspectY) {
					cropHeight = cropWidth * aspectY / aspectX;
				} else {
					cropWidth = cropHeight * aspectX / aspectY;
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, aspectX != 0 && aspectY != 0);
			imageView.add(hv);
		}

		public void crop() {
			handler.post(new Runnable() {
				public void run() {
					makeDefault();
					imageView.invalidate();
					if (imageView.highlightViews.size() == 1) {
						cropView = imageView.highlightViews.get(0);
						cropView.setFocus(true);
					}
				}
			});
		}
	}

	/*
	 * TODO
	 * This should use the decode/crop/encode single step API so that the whole
	 * (possibly large) Bitmap doesn't need to be read into memory
	 */
	private void onSaveClicked() {
		if (cropView == null || isSaving) {
			return;
		}
		isSaving = true;

		Bitmap croppedImage = null;
		Rect r = cropView.getScaledCropRect(sampleSize);
		int width = r.width();
		int height = r.height();

		int outWidth = width, outHeight = height;
		if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
			float ratio = (float) width / (float) height;
			if ((float) maxX / (float) maxY > ratio) {
				outHeight = maxY;
				outWidth = (int) ((float) maxY * ratio + .5f);
			} else {
				outWidth = maxX;
				outHeight = (int) ((float) maxX / ratio + .5f);
			}
		}

		if (IN_MEMORY_CROP && rotateBitmap != null) {
			croppedImage = inMemoryCrop(rotateBitmap, croppedImage, r, width, height, outWidth, outHeight);
			if (croppedImage != null) {
				croppedImage = cropView.getCircleBitmap(croppedImage);
				imageView.setImageBitmapResetBase(croppedImage, true);
				imageView.center(true, true);
				imageView.highlightViews.clear();
			}
		} else {
			try {
				croppedImage = decodeRegionCrop(croppedImage, r);
			} catch (IllegalArgumentException e) {
				setResultException(e);
				finish();
				return;
			}
//
			if (croppedImage != null) {
				croppedImage = cropView.getCircleBitmap(croppedImage);
				imageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, exifRotation), true);
				imageView.center(true, true);
				imageView.highlightViews.clear();
				Date date = new Date(System.currentTimeMillis());
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
				String newPath = Environment.getExternalStorageDirectory() + "/" + MyConstant.PATH + "/" + dateFormat.format(date) + ".png";
				BitmapUtil.save(newPath, croppedImage, Bitmap.CompressFormat.PNG, 90);

				Intent intent = new Intent();
				intent.putExtra("data", newPath);
				setResult(RESULT_OK, intent);
				this.finish();
			}
		}
	}

	@TargetApi(10)
	private Bitmap decodeRegionCrop(Bitmap croppedImage, Rect rect) {
		// Release memory now
		clearImageView();

		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
			final int width = decoder.getWidth();
			final int height = decoder.getHeight();

			if (exifRotation != 0) {
				// Adjust crop area to account for image rotation
				Matrix matrix = new Matrix();
				matrix.setRotate(-exifRotation);

				RectF adjusted = new RectF();
				matrix.mapRect(adjusted, new RectF(rect));

				// Adjust to account for origin at 0,0
				adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
				rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
			}

			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				croppedImage = decoder.decodeRegion(rect, options);

			} catch (IllegalArgumentException e) {
				// Rethrow with some extra information
				throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image (" + width + "," + height + "," + exifRotation + ")", e);
			}

		} catch (IOException e) {
			LogUtils.e("", "Error cropping image: " + e.getMessage());
			finish();
		} catch (OutOfMemoryError e) {
			LogUtils.e("", "OOM cropping image: " + e.getMessage());
			setResultException(e);
		} finally {
			CropUtil.closeSilently(is);
		}
		return croppedImage;
	}

	private Bitmap inMemoryCrop(RotateBitmap rotateBitmap, Bitmap croppedImage, Rect r, int width, int height, int outWidth, int outHeight) {
		// In-memory crop means potential OOM errors,
		// but we have no choice as we can't selectively decode a bitmap with this API level
		System.gc();

		try {
			croppedImage = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.RGB_565);

			Canvas canvas = new Canvas(croppedImage);
			RectF dstRect = new RectF(0, 0, width, height);

			Matrix m = new Matrix();
			m.setRectToRect(new RectF(r), dstRect, Matrix.ScaleToFit.FILL);
			m.preConcat(rotateBitmap.getRotateMatrix());
			canvas.drawBitmap(rotateBitmap.getBitmap(), m, null);
		} catch (OutOfMemoryError e) {
			LogUtils.e("", "OOM cropping image: " + e.getMessage());
			setResultException(e);
			System.gc();
		}

		// Release bitmap memory as soon as possible
		clearImageView();
		return croppedImage;
	}

	private void clearImageView() {
		imageView.clear();
		if (rotateBitmap != null) {
			rotateBitmap.recycle();
		}
		System.gc();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (rotateBitmap != null) {
			rotateBitmap.recycle();
		}
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	public boolean isSaving() {
		return isSaving;
	}

	private void setResultException(Throwable throwable) {
		setResult(Crop.RESULT_ERROR, new Intent().putExtra(Crop.Extra.ERROR, throwable));
	}

}
