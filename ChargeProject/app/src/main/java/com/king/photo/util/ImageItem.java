package com.king.photo.util;

import java.io.IOException;
import java.io.Serializable;

import android.graphics.Bitmap;

public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public String imageRelativePath;
	public String cover_url ;

	public String getCover_url() {
		return cover_url;
	}

	public void setCover_url(String cover_url) {
		this.cover_url = cover_url;
	}

	public String getImageRelativePath() {
		return imageRelativePath;
	}

	public void setImageRelativePath(String imageRelativePath) {
		this.imageRelativePath = imageRelativePath;
	}

	private Bitmap bitmap;
	public boolean isSelected = false;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Bitmap getBitmap() {
		if (bitmap == null) {
			try {
				bitmap = Bimp.revitionImageSize(imagePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
