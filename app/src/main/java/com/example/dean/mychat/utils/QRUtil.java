package com.example.dean.mychat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.example.dean.mychat.lib.HMURL;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;

public class QRUtil {

	public static Bitmap getQRImage(Context context, String account)
			throws WriterException {

		String contents = HMURL.BASE_QR + encode(account, 5);
		return encodeAsBitmap(contents, BarcodeFormat.QR_CODE,
				CommonUtil.dip2px(context, 200),
				CommonUtil.dip2px(context, 200), -1, -1);
	}

	public static Bitmap getQRImage(Context context, String account,
			int backgroundColor, int ponintColor) throws WriterException {

		String contents = HMURL.BASE_QR + encode(account, 5);
		return encodeAsBitmap(contents, BarcodeFormat.QR_CODE,
				CommonUtil.dip2px(context, 200),
				CommonUtil.dip2px(context, 200), backgroundColor, ponintColor);
	}

	public static String encode(String str, int time) {
		String result = new String(str);
		for (int i = 0; i < time; i++) {
			result = new String(
					Base64.encode(result.getBytes(), Base64.NO_WRAP));
		}
		return result;
	}

	public static String decode(String str, int time) {
		System.out.println(str);
		String result = new String(str);
		for (int i = 0; i < time; i++) {
			System.out.println("i : " + i);
			byte[] decode = Base64.decode(result.getBytes(), Base64.NO_WRAP);

			result = new String(decode);
		}
		return result;
	}

	private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
			int desiredWidth, int desiredHeight, int color1, int color2)
			throws WriterException {

		int WHITE = 0xFFFFFFFF; // 可以指定其他颜色，让二维码变成彩色效果
		int BLACK = 0xFF000000;

		if (color1 != -1) {
			WHITE = color1;
		}

		if (color2 != -1) {
			BLACK = color2;
		}

		HashMap<EncodeHintType, String> hints = null;
		String encoding = guessAppropriateEncoding(contents);
		if (encoding != null) {
			hints = new HashMap<EncodeHintType, String>(2);
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, format, desiredWidth,
				desiredHeight, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

}
