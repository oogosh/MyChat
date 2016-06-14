package com.example.dean.mychat.lib;

import android.content.Context;
import android.util.Log;

import com.example.dean.mychat.lib.callback.ObjectCallback;
import com.example.dean.mychat.lib.future.HttpFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.entity.mime.Header;

import java.util.Map;

public class HMChatManager {

	private static HMChatManager instance;
	private Context context;

	public static HMChatManager getInstance(Context context) {
		if (instance == null) {
			synchronized (HMChatManager.class) {
				if (instance == null) {
					instance = new HMChatManager(context);
				}
			}
		}
		return instance;
	}

	private HMChatManager(Context context) {
		this.context = context;
	}

	@SuppressWarnings("rawtypes")
	public HttpFuture sendRequest(String url, final ObjectCallback callback) {
		return sendRequest(url, null, callback);
	}

	@SuppressWarnings("rawtypes")
	public HttpFuture sendRequest(String url, Map<String, String> headers,
								  Map<String, String> paramters, final ObjectCallback callback) {
		// 创建 访问端
		AsyncHttpClient client = new AsyncHttpClient();

		// 请求消息头
		if (headers != null) {
			for (Map.Entry<String, String> me : headers.entrySet()) {
				client.addHeader(me.getKey(), me.getValue());
			}
		}

		// 请求参数
		RequestParams params = new RequestParams();
		// params.put("account", account);
		// params.put("password", password);

		if (paramters != null) {
			for (Map.Entry<String, String> me : paramters.entrySet()) {
				params.put(me.getKey(), me.getValue());
			}
		}

		// 调用post方法访问网络
		RequestHandle handle = client.post(context, url, params,
				new TextHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
						Log.d("success", responseString + "");

						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(responseString);
						JsonObject root = element.getAsJsonObject();

						JsonPrimitive flagJson = root
								.getAsJsonPrimitive("flag");
						boolean flag = flagJson.getAsBoolean();

						if (flag) {
							JsonObject dataObject = root
									.getAsJsonObject("data");
							if (dataObject != null) {
								if (callback != null) {
									@SuppressWarnings("unchecked")
									Object obj = new Gson().fromJson(
											dataObject, callback.getDataClass());
									callback.onSuccess(obj);
								}
							}
						} else {
							JsonPrimitive errorCodeJson = root
									.getAsJsonPrimitive("errorCode");
							JsonPrimitive errorStringJson = root
									.getAsJsonPrimitive("errorString");

							Log.d("", errorCodeJson.getAsInt() + " : "
									+ errorStringJson.getAsString());

							if (callback != null) {
								callback.onFailure(errorCodeJson.getAsInt(),
										errorStringJson.getAsString());
							}
						}
					}

					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
//						Log.d("error",responseString + " : " + throwable.getMessage());

						if (callback != null) {
							callback.onFailure(HMError.ERROR_SERVER, "服务器连接问题");
						}
					}


				});

		return new HttpFuture(handle);
	}

	@SuppressWarnings("rawtypes")
	public HttpFuture sendRequest(String url, Map<String, String> paramters,
								  final ObjectCallback callback) {
		return sendRequest(url, null, paramters, callback);
		// // 创建 访问端
		// AsyncHttpClient client = new AsyncHttpClient();
		//
		// // 接口地址
		// // String url = "http://192.168.1.101:8080/ChatServer/register";
		//
		// // 请求参数
		// RequestParams params = new RequestParams();
		// // params.put("account", account);
		// // params.put("password", password);
		//
		// if (paramters != null) {
		// for (Map.Entry<String, String> me : paramters.entrySet()) {
		// params.put(me.getKey(), me.getValue());
		// }
		// }
		//
		// // 调用post方法访问网络
		// RequestHandle handle = client.post(context, url, params,
		// new TextHttpResponseHandler() {
		//
		// @SuppressWarnings("unchecked")
		// @Override
		// public void onSuccess(int statusCode, Header[] headers,
		// String responseString) {
		// Log.d("success", responseString + "");
		//
		// JsonParser parser = new JsonParser();
		// JsonElement element = parser.parse(responseString);
		// JsonObject root = element.getAsJsonObject();
		//
		// JsonPrimitive flagJson = root
		// .getAsJsonPrimitive("flag");
		// boolean flag = flagJson.getAsBoolean();
		//
		// if (flag) {
		// JsonObject dataObject = root
		// .getAsJsonObject("data");
		// if (dataObject != null) {
		// if (callback != null) {
		// @SuppressWarnings("unchecked")
		// Object obj = new Gson().fromJson(
		// dataObject, callback.getDataClass());
		// callback.onSuccess(obj);
		// }
		// }
		// } else {
		// JsonPrimitive errorCodeJson = root
		// .getAsJsonPrimitive("errorCode");
		// JsonPrimitive errorStringJson = root
		// .getAsJsonPrimitive("errorString");
		//
		// Log.d("", errorCodeJson.getAsInt() + " : "
		// + errorStringJson.getAsString());
		//
		// if (callback != null) {
		// callback.onFailure(errorCodeJson.getAsInt(),
		// errorStringJson.getAsString());
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers,
		// String responseString, Throwable throwable) {
		// Log.d("error",
		// responseString + " : " + throwable.getMessage());
		//
		// if (callback != null) {
		// callback.onFailure(HMError.ERROR_SERVER, "服务器连接问题");
		// }
		//
		// }
		// });
		//
		// return new HttpFuture(handle);
	}

}
