package com.xpg.hssy.web;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年8月6日
 * @version 1.0.0
 */

public interface OnProgressListener {
	void onUpload(long size, long maxSize);

	void onDownload(long size, long maxSize);
}
