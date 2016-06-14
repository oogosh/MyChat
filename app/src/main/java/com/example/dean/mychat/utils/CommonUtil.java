package com.example.dean.mychat.utils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtil {

	/**
	 * 判断当前网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * dip 转 px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px 转 dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param clazz
	 *            要判断的服务的class
	 * @return
	 */
	public static boolean isServiceRunning(Context context,
			Class<? extends Service> clazz) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> services = manager.getRunningServices(100);
		for (int i = 0; i < services.size(); i++) {
			String className = services.get(i).service.getClassName();
			if (className.equals(clazz.getName())) {
				return true;
			}
		}
		return false;
	}

	public static String getPinYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

		char[] input = inputString.trim().toCharArray();
		StringBuffer output = new StringBuffer("");
		try {
			for (int i = 0; i < input.length; i++) {
				if (Character.toString(input[i]).matches("[//u4E00-//u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					output.append(temp[0]);
					output.append(" ");
				} else
					output.append(Character.toString(input[i]));
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public static String getFirstAlpha(String inputString) {
		// String pinYin = getPinYin(inputString);
		// if (pinYin != null && pinYin.length() > 0) {
		// return pinYin.substring(0, 1).toUpperCase();
		// }

		if (inputString != null) {

			String[] array = PinyinHelper.toHanyuPinyinStringArray(inputString
					.charAt(0));

			if (array == null) {
				return inputString.substring(0, 1).toUpperCase();
			} else {
				return array[0].toUpperCase();
			}
		}

		return "";
	}

	public static String getDateFormat(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(time));
	}

	public static String string2MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}