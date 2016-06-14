package com.example.dean.mychat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {

	public static Bitmap getScaleBitmap(int width, int height, String path) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小

		int sampleSizeWidth = (int) (options.outWidth / (float) width);
		int sampleSizeHeight = (int) (options.outHeight / (float) height);

		int sampleSize = sampleSizeWidth > sampleSizeHeight ? sampleSizeHeight
				: sampleSizeWidth;

		if (sampleSize <= 0) {
			sampleSize = 1;
		}
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeFile(path, options);
	}
}
