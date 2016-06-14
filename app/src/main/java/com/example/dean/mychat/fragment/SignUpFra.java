package com.example.dean.mychat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.dean.mychat.R;
import com.example.dean.mychat.activity.LoginActivity;
import com.example.dean.mychat.base.BaseFragment;
import com.example.dean.mychat.db.AccountDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.lib.HMChatManager;
import com.example.dean.mychat.lib.HMError;
import com.example.dean.mychat.lib.callback.ObjectCallback;
import com.example.dean.mychat.lib.future.HttpFuture;
import com.example.dean.mychat.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;


public class SignUpFra extends BaseFragment implements OnClickListener {
	private String TAG = "SignUpFra";

	private EditText etAccount;
	private EditText etPwd;
	private Button btnSignUp;

	private HttpFuture future;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_sign_up, container, false);

		initView(view);
		initEvent();
		return view;
	}

	private void initEvent() {
		btnSignUp.setOnClickListener(this);
	}

	private void initView(View view) {
		etAccount = (EditText) view.findViewById(R.id.et_sign_up_account);
		etPwd = (EditText) view.findViewById(R.id.et_sign_up_pwd);
		btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);

	}

	@Override
	public void onClick(View v) {
		if (v == btnSignUp) {
			doSignUp();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (future != null && !future.isCancelled() && !future.isFinished()) {
			future.cancel(true);
			future = null;
		}

	}

	// 处理注册
	private void doSignUp() {
		Context context = getActivity();
		if (context == null) {
			ToastUtil.show(context, "buzhidao");
			return;
		}

		final String account = etAccount.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			ToastUtil.show(context, "用户名为空");
			return;
		}
		final String password = etPwd.getText().toString().trim();
		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(context, "密码为空");
			return;
		}

		String url = "http://192.168.1.101:8080/ChatServer/register";
		Map<String, String> paramters = new HashMap<String, String>();
		paramters.put("account", account);
		paramters.put("password", password);

		future = HMChatManager.getInstance(getActivity()).sendRequest(url,
				paramters, new ObjectCallback<Account>() {

					@Override
					public void onSuccess(Account data) {

						Log.d("onSuccess", data.toString());

						// 数据的存储
						AccountDao dao = new AccountDao(getActivity());
						data.setCurrent(true);

						Account localAccount = dao.getByAccount(data
								.getAccount());
						if (localAccount != null) {
							dao.updateAccount(data);
						} else {
							dao.addAccount(data);
						}

						// 页面跳转
						((LoginActivity) getActivity()).go2FillInfo();
					}

					@Override
					public void onFailure(int errorCode, String errorString) {
						Log.d("onFailure", errorCode + " : " + errorString);

						switch (errorCode) {
						case HMError.ERROR_CLIENT_NET:
							Log.d(TAG, "客户端网络异常");
							ToastUtil.show(getActivity(), "客户端网络异常");
							break;
						case HMError.ERROR_SERVER:
							Log.d(TAG, "服务器异常");
							ToastUtil.show(getActivity(), "服务器异常");
							break;
						case HMError.Register.ACCOUNT_EXIST:
							Log.d(TAG, "用户已经存在");
							ToastUtil.show(getActivity(), "用户已经存在");
							break;
						default:
							break;
						}
					}
				});

		// // 创建 访问端
		// AsyncHttpClient client = new AsyncHttpClient();
		//
		// // 接口地址
		// String url = "http://192.168.1.101:8080/ChatServer/register";
		//
		// //
		// RequestParams params = new RequestParams();
		// params.put("account", account);
		// params.put("password", password);
		//
		// // 调用post方法访问网络
		// client.post(context, url, params, new TextHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers,
		// String responseString) {
		// Log.d("success", responseString + "");
		//
		// JsonParser parser = new JsonParser();
		// JsonElement element = parser.parse(responseString);
		// JsonObject root = element.getAsJsonObject();
		//
		// JsonPrimitive flagJson = root.getAsJsonPrimitive("flag");
		// boolean flag = flagJson.getAsBoolean();
		//
		// if (flag) {
		// JsonObject dataObject = root.getAsJsonObject("data");
		// if (dataObject != null) {
		// Account user = new Gson().fromJson(dataObject,
		// Account.class);
		//
		// Log.d("", user.toString());
		// }
		// } else {
		// JsonPrimitive errorCodeJson = root
		// .getAsJsonPrimitive("errorCode");
		// JsonPrimitive errorStringJson = root
		// .getAsJsonPrimitive("errorString");
		//
		// Log.d("", errorCodeJson.getAsInt() + " : "
		// + errorStringJson.getAsString());
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers,
		// String responseString, Throwable throwable) {
		// Log.d("error", responseString + " : " + throwable.getMessage());
		// }
		// });

		// 网络访问请求
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// String url = "http://192.168.1.101:8080/ChatServer/register";
		// // 创建 httpClient
		// HttpClient client = new DefaultHttpClient();
		//
		// // 创建 post请求
		// HttpPost post = new HttpPost(url);
		// List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		// // 配置请求头
		// // post.addHeader("", "");
		//
		// // 配置请求参数
		// pairs.add(new BasicNameValuePair("account", account));
		// pairs.add(new BasicNameValuePair("password", password));
		//
		// try {
		// post.setEntity(new UrlEncodedFormEntity(pairs));
		// HttpResponse response = client.execute(post);
		//
		// int statusCode = response.getStatusLine().getStatusCode();
		// if (statusCode == 200) {
		// // 拿到网络的返回结果
		// final String result = EntityUtils.toString(response
		// .OgetEntity());
		//
		// Log.d("result : ", "" + result);
		// // 需要在主线线程中做UI操作
		//
		// getActivity().runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// doResult(result);
		// }
		// });
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }).start();
	}

	// private void doResult(String result) {
	// // 主线程中的回调
	// Log.d("主线程", "回调获得  : " + result);
	// }
}