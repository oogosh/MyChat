package com.example.dean.mychat.lib;

public class HMURL {

	 public static String BASE_HTTP = "http://192.168.1.101:8080/ChatServer";
	 public static String BASE_HM_HOST = "192.168.1.101";
	 public static int BASE_HM_PORT = 9090;
	 public static String BASE_QR = BASE_HTTP + "/QR/";
	//
//	public static String BASE_HTTP = "http://192.168.199.105:8080/ChatServer";
//	public static String BASE_HM_HOST = "192.168.199.105";
//	public static int BASE_HM_PORT = 9090;
//	public static String BASE_QR = BASE_HTTP + "/QR/";

	// public static String BASE_HTTP = "http://54.92.45.62:8080/ChatServer";
	// public static String BASE_HM_HOST = "54.92.45.62";
	// public static int BASE_HM_PORT = 9090;
	// public static String BASE_QR = BASE_HTTP + "/QR/";

	// public static String BASE_HTTP = "http://172.30.58.56:8080/ChatServer";
	// public static String BASE_HM_HOST = "172.30.58.56";
	// public static int BASE_HM_PORT = 9090;
	// public static String BASE_QR = BASE_HTTP + "/QR/";

	/**
	 * 登录部分的url地址
	 */
	public final static String URL_HTTP_LOGIN = BASE_HTTP + "/login";
	public final static String URL_HTTP_REGISTER = BASE_HTTP + "/register";
	public final static String URL_HTTP_LOGOUT = BASE_HTTP + "/logout";

	/**
	 * 搜索用户
	 */
	public final static String URL_HTTP_SEARCH_CONTACT = BASE_HTTP
			+ "/user/search";

}
