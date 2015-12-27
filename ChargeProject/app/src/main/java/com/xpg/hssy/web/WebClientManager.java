package com.xpg.hssy.web;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * WebManager
 * 
 * @Description
 * @author Joke Huang
 * @createDate 2014年7月8日
 * @version 1.0.0
 */

public class WebClientManager {
	private static final String tag = WebClientManager.class.getSimpleName();

	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";

	private static final int SET_CONNECTION_TIMEOUT = 20 * 1000;
	private static final int SET_SOCKET_TIMEOUT = 20 * 1000;

	private static final String BOUNDARY = WebHelper.getBoundry();
	private static final String BOUNDARY_ = "--" + BOUNDARY;
	private static final String BOUNDARY_END = "--" + BOUNDARY + "--";

	private static WebClientManager webClientManager;

	private WebClientManager() {
	}

	public static WebClientManager getInstance() {
		if (webClientManager == null) {
			webClientManager = new WebClientManager();
		}
		return webClientManager;
	}

	/** POST */
	public byte[] post(String url, Map<String, Object> params)
			throws HttpException {
		String formParams = WebHelper.encodeParams(params);
		HttpUriRequest request = newHttpRequest(METHOD_POST, url,
				formParams.getBytes(), null);
		return execute(request, null);
	}

	public byte[] post(String url, String content) throws HttpException {
		HttpUriRequest request = newHttpRequest(METHOD_POST, url,
				content.getBytes(), null);
		return execute(request, null);
	}

	/** PUT */
	public byte[] put(String url, Map<String, Object> params)
			throws HttpException {
		String formParams = WebHelper.encodeParams(params);
		HttpUriRequest request = newHttpRequest(METHOD_PUT, url,
				formParams.getBytes(), null);
		return execute(request, null);
	}

	public byte[] put(String url, String content) throws HttpException {
		HttpUriRequest request = newHttpRequest(METHOD_PUT, url,
				content.getBytes(), null);
		return execute(request, null);
	}

	/** GET */
	public byte[] get(String url, Map<String, Object> params)
			throws HttpException {
		String urlParams = WebHelper.encodeParams(params);
		url += urlParams == null ? "" : "?" + urlParams;
		HttpUriRequest request = newHttpRequest(METHOD_GET, url, null, null);
		return execute(request, null);
	}

	/** DELETE */
	public byte[] delete(String url, Map<String, Object> params)
			throws HttpException {
		String urlParams = WebHelper.encodeParams(params);
		url += urlParams == null ? "" : "?" + urlParams;
		HttpUriRequest request = newHttpRequest(METHOD_DELETE, url, null, null);
		return execute(request, null);
	}

	/** upload */
	public byte[] upload(String url, Map<String, Object> params,
			String fileKey, String filePath) {
		ByteArrayOutputStream baos = null;
		FileInputStream input = null;
		try {
			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type", "multipart/form-data; boundary="
					+ BOUNDARY);

			baos = new ByteArrayOutputStream();
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
			for (String key : params.keySet()) {
				StringBuilder temp = new StringBuilder();
				temp.append(BOUNDARY_).append("\r\n");
				temp.append("content-disposition: form-data; name=\"")
						.append(key).append("\"\r\n\r\n");
				temp.append(params.get(key)).append("\r\n");
				byte[] res = temp.toString().getBytes();
				baos.write(res);
			}

			StringBuilder temp = new StringBuilder();
			int start = filePath.lastIndexOf("/");
			int end = filePath.lastIndexOf(".");
			String fileName = filePath.substring(start + 1, end);
			String filetype = WebHelper.getContentType(filePath.substring(end));
			temp.append(BOUNDARY_).append("\r\n");
			temp.append("Content-Disposition: form-data; name=\"")
					.append(fileKey).append("\"; filename=\"").append(fileName)
					.append("\"\r\n");
			temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
			byte[] res = temp.toString().getBytes();
			baos.write(res);

			input = new FileInputStream(filePath);
			byte[] buffer = new byte[1024 * 50];
			while (true) {
				int count = input.read(buffer);
				if (count == -1) {
					break;
				}
				baos.write(buffer, 0, count);
			}
			baos.write("\r\n".getBytes());
			baos.write(("\r\n" + BOUNDARY_END).getBytes());

			ByteArrayEntity formEntity = new ByteArrayEntity(baos.toByteArray());
			post.setEntity(formEntity);

			return execute(post, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/** download */
	public byte[] download(String url) {
		try {
			HttpUriRequest request = newHttpRequest(METHOD_GET, url, null, null);
			return execute(request, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** execute */
	private byte[] execute(HttpUriRequest httpRequest,
			OnProgressListener proListener) throws HttpException {
		HttpClient httpClient = newHttpClient();
		if (httpClient == null || httpRequest == null) {
			throw new HttpException("create httpRequest or httpClient fail!");
		}

		InputStream is = null;
		try {
			HttpResponse response = httpClient.execute(httpRequest);
			// OK
			if (response.getStatusLine().getStatusCode() / 100 == 2) {

				is = response.getEntity().getContent();
				long maxSize = response.getEntity().getContentLength();
				long size = 0;

				int len = 0;
				byte[] bytes = new byte[1024 * 32];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				if (proListener == null) {
					while ((len = is.read(bytes)) != -1) {
						baos.write(bytes, 0, len);
					}
				} else {
					while ((len = is.read(bytes)) != -1) {
						baos.write(bytes, 0, len);
						size += len;
						proListener.onDownload(size, maxSize);
					}
				}
				bytes = baos.toByteArray();

				return bytes;
			} else {
				// fail
				Log.e(tag, "http status code: "
						+ response.getStatusLine().getStatusCode());
				throw new HttpException("http status code: "
						+ response.getStatusLine().getStatusCode());
			}
		} catch (IOException e) {
			Log.e(tag, "response error: " + httpRequest.getURI());
			throw new HttpException("response error: " + httpRequest.getURI(),
					e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpClient.getConnectionManager().shutdown();
		}
	}

	/** create httpclient */
	private HttpClient newHttpClient() {
		try {
			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params,
					SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			HttpClient client = new DefaultHttpClient(params);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * create httprequest
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private HttpUriRequest newHttpRequest(String method, String url,
			byte[] content, Map<String, String> headers) {

		// url
		Log.v(tag, "method: " + method + "\n" + "url: " + url);

		// method
		HttpUriRequest httpRequest = null;
		if (METHOD_GET.equals(method)) {
			httpRequest = new HttpGet(url);
		}
		if (METHOD_DELETE.equals(method)) {
			httpRequest = new HttpDelete(url);
		}
		if (METHOD_POST.equals(method)) {
			httpRequest = new HttpPost(url);
		}
		if (METHOD_PUT.equals(method)) {
			httpRequest = new HttpPut(url);
		}
		if (httpRequest == null) {
			return null;
		}

		// header
		if (headers != null && headers.size() != 0) {
			for (Entry<String, String> header : headers.entrySet()) {
				if (header.getValue() == null) {
					continue;
				}
				httpRequest.setHeader(header.getKey(), header.getValue());
				Log.v(tag,
						"header: " + header.getKey() + ":" + header.getValue());
			}
		}

		// content
		if (content != null && content.length != 0) {
			if (METHOD_POST.equals(method) || METHOD_PUT.equals(method)) {
				ByteArrayEntity bae = new ByteArrayEntity(content);
				((HttpEntityEnclosingRequest) httpRequest).setEntity(bae);
				Log.v(tag, "content: " + content);
			}
		}

		return httpRequest;
	}

}
