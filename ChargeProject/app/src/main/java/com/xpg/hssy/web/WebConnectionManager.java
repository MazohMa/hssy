package com.xpg.hssy.web;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpException;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * NetWork
 *
 * @Description
 * @author Joke Huang
 * @createDate 2014年6月30日
 * @version 1.0.0
 */

public class WebConnectionManager {
	public static final String tag = WebConnectionManager.class.getSimpleName();

	private static WebConnectionManager webConnectionManager;

	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";

	private static final int SET_CONNECTION_TIMEOUT = 20 * 1000;
	private static final int SET_SOCKET_TIMEOUT = 20 * 1000;

	private static final String BOUNDARY = WebHelper.getBoundry();
	private static final String BOUNDARY_ = "--" + BOUNDARY;
	private static final String BOUNDARY_END = "--" + BOUNDARY + "--";
	private static final String NL = "\r\n";

	private WebConnectionManager() {
	}

	public static WebConnectionManager getInstance() {
		if (webConnectionManager == null) {
			webConnectionManager = new WebConnectionManager();
		}
		return webConnectionManager;
	}

	/** POST */
	public byte[] post(String url, Map<String, Object> headers,
			Map<String, Object> params, final boolean isPostJson)
			throws HttpException {
		String tempContent;
		if (isPostJson) {
			tempContent = new Gson().toJson(params);
		} else {
			tempContent = WebHelper.encodeParams(params);
		}
		final String content = tempContent;
		String logContent = tempContent.replaceAll(
				"(\"?password\"?)([=:])\"?\\w+\"?", "$1$2\\*\\*\\*\\*\\*\\*");
		Log.v(tag,
				"method: post\nurl: " + url + "\nheaders: "
						+ WebHelper.encodeParams(headers) + "\nformParams: "
						+ logContent);

		Outputer outputer = new Outputer() {
			@Override
			public OutputStream doOutput(HttpURLConnection conn)
					throws IOException {
				if (content == null || content.equals(""))
					return null;
				if (isPostJson) {
					conn.setRequestProperty("Content-Type",
							"application/json;charset=UTF-8");
				}
				OutputStream os = conn.getOutputStream();
				os.write(content.getBytes("UTF-8"));
				return os;
			}
		};

		return execute(url, METHOD_POST, headers, outputer, null);
	}

	/** GET */
	public byte[] get(String url, Map<String, Object> headers,
			Map<String, Object> params) throws HttpException {
		String urlParams = WebHelper.encodeParams(params);
		url += urlParams == null || urlParams.equals("") ? "" : "?" + urlParams;
		Log.v(tag,
				"method: get\nurl: " + url + "\nheaders: "
						+ WebHelper.encodeParams(headers) + "\nurlParams: "
						+ urlParams);

		return execute(url, METHOD_GET, headers, null, null);
	}

	/** download */
	public byte[] download(String url, OnProgressListener proListener)
			throws HttpException {
		Log.v(tag, "method: get\n" + "url: " + url);

		return execute(url, METHOD_GET, null, null, proListener);
	}

	/** upload */
	public byte[] upload(String url,Map<String, Object> headers, final Map<String, Object> params,
			final String fileKey, final String filePath,
			final OnProgressListener proListener) throws HttpException {
		Log.v(tag, "method: post\n" + "url: " + url + "\n" + "file: " + fileKey
				+ " = " + filePath);

		Outputer outputer = new Outputer() {
			@Override
			public OutputStream doOutput(HttpURLConnection conn)
					throws IOException {
				// -----------------------------7da2e536604c8
				// Content-Disposition: form-data; name="username"
				//
				// hello word
				// -----------------------------7da2e536604c8
				// Content-Disposition: form-data; name="file1";
				// filename="D:/haha.txt"
				// Content-Type: text/plain
				//
				// haha
				// hahaha
				// -----------------------------7da2e536604c8
				// Content-Disposition: form-data; name="file2";
				// filename="D:/huhu.txt"
				// Content-Type: text/plain
				//
				// messi
				// huhu
				// -----------------------------7da2e536604c8--
				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY);
				OutputStream os = conn.getOutputStream();
				// 上传参数
				for (String key : params.keySet()) {
					Object value = params.get(key);
					if (value == null)
						continue;

					Log.v(tag, "params: " + key + " = " + value);

					os.write((BOUNDARY_ + NL).getBytes());
					os.write(("Content-Disposition: form-data; name=\"" + key
							+ "\"" + NL).getBytes());
					os.write(NL.getBytes());
					os.write((value.toString() + NL).getBytes());
				}
				// 上传文件
				if (filePath != null && !filePath.equals("")) {
					int start = filePath.lastIndexOf("/");
					int end = filePath.lastIndexOf(".");
					String fileName = filePath.substring(start + 1);
					String filetype = WebHelper.getContentType(filePath
							.substring(end));
					os.write((BOUNDARY_ + NL).getBytes());
					os.write(("Content-Disposition: form-data; name=\""
							+ fileKey + "\"; filename=\"" + fileName + "\"" + NL)
							.getBytes());
					os.write(("Content-Type: " + filetype + NL).getBytes());
					os.write(NL.getBytes());

					File file = new File(filePath);
					FileInputStream fis = new FileInputStream(file);
					byte[] buffer = new byte[1024 * 32];
					long maxSize = file.length();
					long size = 0;
					int len = 0;
					while ((len = fis.read(buffer)) != -1) {
						os.write(buffer, 0, len);
						size += len;

						Log.v(tag, "upload size: " + size);

						if (proListener != null)
							proListener.onUpload(size, maxSize);
					}
					fis.close();

					os.write(NL.getBytes());
				}

				os.write((BOUNDARY_END + NL).getBytes());
				os.flush();

				return os;
			}

		};

		return execute(url, METHOD_POST, headers, outputer, proListener);
	}

	/** 批量图片表单形式upload */
	public byte[] uploads(String url,Map<String, Object> headers, final Map<String, Object> params,
			final Map<String, Object> coverWithIntrols,
			final OnProgressListener proListener) throws HttpException {

		Outputer outputer = new Outputer() {
			@Override
			public OutputStream doOutput(HttpURLConnection conn)
					throws IOException {

				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY);
				OutputStream os = conn.getOutputStream();
				// 上传参数
				for (String key : params.keySet()) {
					Object value = params.get(key);
					if (value == null)
						continue;

					Log.v(tag, "params: " + key + " = " + value);

					os.write((BOUNDARY_ + NL).getBytes());
					os.write(("Content-Disposition: form-data; name=\"" + key
							+ "\"" + NL).getBytes());
					os.write(NL.getBytes());
					os.write((value.toString() + NL).getBytes());
				}

				if (coverWithIntrols != null && coverWithIntrols.size() > 0) {
					for (String key : coverWithIntrols.keySet()) {
						Object value = coverWithIntrols.get(key);
						if (value == null)
							continue;
						if (value instanceof List) {
							for (String path : (List<String>) value) {
								uploadOnce(key, path, os);
							}
						} else if (value instanceof String) {
							uploadOnce(key, (String) value, os);
						}
					}

				}
				os.write((BOUNDARY_END + NL).getBytes());
				os.flush();
				return os;
			}

			private void uploadOnce(String key, String value, OutputStream os)
					throws IOException {
				Log.v(tag, "params: " + key + " = " + value);
				int start = value.lastIndexOf("/");
				int end = value.lastIndexOf(".");
				String fileName = value.substring(start + 1);
				String filetype = WebHelper
						.getContentType(value.substring(end));
				os.write((BOUNDARY_ + NL).getBytes());
				os.write(("Content-Disposition: form-data; name=\"" + key
						+ "\"; filename=\"" + fileName + "\"" + NL).getBytes());

				os.write(("Content-Type: " + filetype + NL).getBytes());
				os.write(NL.getBytes());

				File file = new File(value);
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				long maxSize = file.length();
				long size = 0;
				int len = 0;
				while ((len = fis.read(buffer)) != -1) {
					os.write(buffer, 0, len);
					size += len;

					Log.v(tag, "upload size: " + size);

					if (proListener != null)
						proListener.onUpload(size, maxSize);
				}
				fis.close();

				os.write(NL.getBytes());
			}

		};

		return execute(url, METHOD_POST, headers, outputer, proListener);
	}

	/** execute */
	private byte[] execute(String url, String method,
			Map<String, Object> headers, Outputer outputer,
			OnProgressListener proListener) throws HttpException {
		HttpURLConnection conn = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			// 创建链接
			conn = openConnection(url, method);
			// 设置头
			if (headers != null && headers.size() > 0) {
				Object value = null;
				for (String key : headers.keySet()) {
					value = headers.get(key);
					if (value != null && !value.equals("")) {
						conn.addRequestProperty(key, value.toString());
					}
				}
			}
			// 上传数据
			if (outputer != null) {
				conn.setDoOutput(true);
				os = outputer.doOutput(conn);
			}

			// 响应码
			int responseCode = conn.getResponseCode();
			Log.v(tag, "responseCode: " + responseCode);
			if (responseCode % 200 > 99) {
			throw new HttpException("http status code: "
					+ conn.getResponseCode());
		}

		// 返回数据
			is = conn.getInputStream();
			if (is == null) {
				throw new HttpException("InputStream null: "
						+ conn.getResponseCode());
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			long maxSize = conn.getContentLength();
			long size = 0;
			int len = 0;
			byte[] buffer = new byte[1024 * 32];
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
				size += len;
				if (proListener != null)
					proListener.onDownload(size, maxSize);
			}

			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException("request error: " + url, e);
		} finally {
			// 关闭资源
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					is = null ;//TODO io操作关闭之后设为null，系统回收  by mazoh
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					os = null ;//TODO io操作设为null，系统回收
				}
			}
			conn.disconnect();
		}
	}

	/**
	 * 创建连接
	 *
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private HttpURLConnection openConnection(String url, String method)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestMethod(method);// 请求
		conn.setConnectTimeout(SET_CONNECTION_TIMEOUT);// 连接超时
		conn.setReadTimeout(SET_SOCKET_TIMEOUT);// 读取超时
		conn.setUseCaches(false);// 不缓存
		conn.setDoInput(true);// 可输入
		conn.setRequestProperty("Connection", HTTP.CONN_KEEP_ALIVE);// 保持连接
		conn.setRequestProperty("Charset", HTTP.UTF_8);// 字符编码
		return conn;
	}

	/** 上传者 */
	private interface Outputer {
		OutputStream doOutput(HttpURLConnection conn) throws IOException;
	}
}
