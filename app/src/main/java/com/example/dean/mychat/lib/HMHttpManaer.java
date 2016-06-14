package com.example.dean.mychat.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HMHttpManaer {
	private final static String ERROR = "error";

	private HttpParams httpParams;
	private HttpClient httpClient;

	private Map<String, String> headers = new HashMap<String, String>();

	private static HMHttpManaer instance;

	public static HMHttpManaer getInstance() {
		if (instance == null) {
			synchronized (HMHttpManaer.class) {
				if (instance == null) {
					instance = new HMHttpManaer();
				}
			}
		}
		return instance;
	}

	private HMHttpManaer() {
		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
		this.httpParams = new BasicHttpParams();
		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		// 设置重定向，缺省为 true
		HttpClientParams.setRedirecting(httpParams, true);
		// 设置 user agent
		String userAgent = "HM";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		// 创建一个 HttpClient 实例
		httpClient = new DefaultHttpClient(httpParams);
	}

	public void initAccount(String account, String token) {
		headers.put("account", account);
		headers.put("token", token);
	}

	private String doGet(String url, Map<String, String> headers,
						 Map<String, String> params) {
		/* 建立HTTPGet对象 */
		String paramStr = "";
		HttpGet get = new HttpGet(url);
		String result = ERROR;

		if (headers != null) {
			for (Map.Entry<String, String> me : headers.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				get.setHeader(key, value);
			}
		}

		if (params != null) {
			for (Map.Entry<String, String> me : params.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				paramStr += paramStr = "&" + key + "=" + value;
			}

			if (!paramStr.equals("")) {
				paramStr = paramStr.replaceFirst("&", "?");
				url += paramStr;
			}
		}

		try {
			/* 发送请求并等待响应 */
			HttpResponse response = httpClient.execute(get);
			/* 若状态码为200 ok */
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				/* 读返回数据 */
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String doPost(String url, Map<String, String> headers,
						  List<NameValuePair> params) {
		/* 建立HTTPPost对象 */
		HttpPost post = new HttpPost(url);
		String result = ERROR;

		if (headers != null) {
			for (Map.Entry<String, String> me : headers.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				post.setHeader(key, value);
			}
		}

		try {
			/* 添加请求参数到请求对象 */
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse response = httpClient.execute(post);
			/* 若状态码为200 ok */
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				/* 读返回数据 */
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String doUpload(String url, Map<String, String> headers,
							List<NameValuePair> params, Map<String, File> files) {
		/* 建立HTTPPost对象 */
		HttpPost post = new HttpPost(url);
		String result = ERROR;

		MultipartEntity multipartEntity = new MultipartEntity();

		if (headers != null) {
			for (Map.Entry<String, String> me : headers.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				post.setHeader(key, value);
			}
		}
		try {

			if (params != null) {
				for (NameValuePair pair : params) {
					multipartEntity.addPart(
							pair.getName(),
							new StringBody(pair.getValue(), Charset
									.forName(HTTP.UTF_8)));
				}
			}

			if (files != null) {
				for (Map.Entry<String, File> me : files.entrySet()) {
					String key = me.getKey();
					File file = me.getValue();

					multipartEntity
							.addPart(key, new FileBody(file, HTTP.UTF_8));
				}
			}

			/* 添加请求参数到请求对象 */
			post.setEntity(multipartEntity);
			/* 发送请求并等待响应 */
			HttpResponse response = httpClient.execute(post);
			/* 若状态码为200 ok */
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				/* 读返回数据 */
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String doDownload(String url, Map<String, String> headers,
							  List<NameValuePair> params, File localFile) {
		/* 建立HTTPPost对象 */
		HttpPost post = new HttpPost(url);
		String result = ERROR;

		if (headers != null) {
			for (Map.Entry<String, String> me : headers.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				post.setHeader(key, value);
			}
		}

		try {
			/* 添加请求参数到请求对象 */
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse response = httpClient.execute(post);
			/* 若状态码为200 ok */
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {

				File dir = localFile.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}

				/* 读返回数据 */
				InputStream stream = response.getEntity().getContent();
				FileOutputStream fos = new FileOutputStream(localFile);
				byte[] buffer = new byte[1024];
				int len = -1;

				while ((len = stream.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					fos.flush();
				}
				stream.close();
				fos.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean post(String url, Map<String, String> headers,
						Map<String, String> paramaters) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if (paramaters != null) {
			for (Map.Entry<String, String> me : paramaters.entrySet()) {
				params.add(new BasicNameValuePair(me.getKey(), me.getValue()));
			}
		}
		String result = doPost(url, headers, params);

		return parseResult(result);
	}

	private boolean parseResult(String result) {
		System.out.println("result : " + result);

		if (ERROR.equals(result)) {
			return false;
		} else {
			JsonParser parser = new JsonParser();
			try {
				JsonObject root = parser.parse(result).getAsJsonObject();
				JsonPrimitive flagObject = root.getAsJsonPrimitive("flag");
				return flagObject.getAsBoolean();
			} catch (Exception e) {
				return false;
			}
		}
	}

	public boolean download(String url, Map<String, String> headers,
							Map<String, String> paramaters, File localFile) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if (paramaters != null) {
			for (Map.Entry<String, String> me : paramaters.entrySet()) {
				params.add(new BasicNameValuePair(me.getKey(), me.getValue()));
			}
		}

		String result = doDownload(url, headers, params, localFile);
		return parseResult(result);
	}

	public boolean upload(String url, Map<String, String> headers,
						  Map<String, String> paramaters, Map<String, String> filePaths) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if (paramaters != null) {
			for (Map.Entry<String, String> me : paramaters.entrySet()) {
				params.add(new BasicNameValuePair(me.getKey(), me.getValue()));
			}
		}

		Map<String, File> files = new HashMap<String, File>();
		if (filePaths != null) {

			for (Map.Entry<String, String> me : filePaths.entrySet()) {
				String key = me.getKey();
				String value = me.getValue();

				files.put(key, new File(value));
			}

		}

		String result = doUpload(url, headers, params, files);
		return parseResult(result);
	}
}