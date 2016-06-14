package com.google.zxing.client.android;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public abstract class CaptureActivity extends Activity implements Callback {
	private static final String TAG = CaptureActivity.class.getSimpleName();

	static final Collection<BarcodeFormat> PRODUCT_FORMATS;
	static final Collection<BarcodeFormat> ONE_D_FORMATS;
	static final Collection<BarcodeFormat> QR_CODE_FORMATS = EnumSet
			.of(BarcodeFormat.QR_CODE);
	static final Collection<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet
			.of(BarcodeFormat.DATA_MATRIX);
	static {
		PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
				BarcodeFormat.EAN_13, BarcodeFormat.EAN_8,
				BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED);
		ONE_D_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
				BarcodeFormat.CODE_93, BarcodeFormat.CODE_128,
				BarcodeFormat.ITF, BarcodeFormat.CODABAR);
		ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
	}

	private ViewfinderView viewfinderView;

	private CameraManager cameraManager;// 镜头管理
	private boolean hasSurface;// 是否屏幕可见
	private InactivityTimer inactivityTimer;//
	private BeepManager beepManager;// 声音管理
	private AmbientLightManager ambientLightManager;// 环境光线管理
	private CaptureActivityHandler handler;
	private Result savedResultToShow;

	CameraManager getCameraManager() {
		return cameraManager;
	}

	CaptureActivityHandler getHandler() {
		return handler;
	}

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置屏幕保持高亮
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(getLayoutId());

		// 初始化
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		initCamera();

	}

	private void initCamera() {
		// CameraManager must be initialized here, not in onCreate(). This is
		// necessary because we don't
		// want to open the camera driver and measure the screen size if we're
		// going to show the help on
		// first launch. That led to bugs where the scanning rectangle was the
		// wrong size and partially
		// off screen.
		cameraManager = new CameraManager(getApplication());

		// 初始化 finder view
		viewfinderView = (ViewfinderView) findViewById(getViewfinderViewId());
		viewfinderView.setCameraManager(cameraManager);

		// 初始化摄像头
		SurfaceView surfaceView = (SurfaceView) findViewById(getSufaceViewId());
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		// 设置 声音管理 光线管理
		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		// timer 状态代理
		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		releaseCamera();

		if (viewfinderView != null) {
			viewfinderView.drawResultBitmap(null);
			Log.d("", "######################");
		}

		super.onPause();

	}

	private void releaseCamera() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}

		// timer 状态代理
		inactivityTimer.onPause();
		// 关闭
		ambientLightManager.stop();
		cameraManager.closeDriver();

		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(getSufaceViewId());
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			// Handle these events so they don't launch the Camera app
			return true;
			// Use volume up/down to turn on light
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		hasSurface = false;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}

		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this,
						getDecodeFormats(getDecodeMode()), getDecodeHintType(),
						getCharset(), cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (Exception e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
		}
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						DecodeHandler.DECODE_SUCCEEDED, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		// 代理
		inactivityTimer.onActivity();

		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			// Then not from history, so beep/vibrate and we have an image to
			// draw on
			beepManager.playBeepSoundAndVibrate();
			
			if (rawResult != null) {
				drawResultPoints(barcode, scaleFactor, rawResult);
			}
		}

		handleDecodeExternally(rawResult, barcode);
	}

	private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
		if (barcode != null) {
			viewfinderView.drawResultBitmap(barcode);
		}

		handleResult(rawResult);
	}

	public Result paseBitmap(Bitmap bitmap) {
		MultiFormatReader multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(getDecodeHintType());

		if (bitmap == null) {
			return null;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];

		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		RGBLuminanceSource source = new RGBLuminanceSource(width, height,
				pixels);
		// LuminanceSource source = new RGBLuminanceSource(path);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader2 = new QRCodeReader();
		Result result;
		try {
			result = reader2.decode(bitmap1, getDecodeHintType());

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 *
	 * @param barcode
	 *            A bitmap of the captured image.
	 * @param scaleFactor
	 *            amount by which thumbnail was scaled
	 * @param rawResult
	 *            The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, float scaleFactor,
			Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(Color.GREEN);
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				// Hacky special case -- draw two lines, for the barcode and
				// metadata
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					if (point != null) {
						canvas.drawPoint(scaleFactor * point.getX(),
								scaleFactor * point.getY(), paint);
					}
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
					scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 获得activity 对应的 layout
	 * 
	 * @return
	 */
	public abstract int getLayoutId();

	/**
	 * 布局中 ViewfinderView 对应的id
	 * 
	 * @return
	 */
	public abstract int getViewfinderViewId();

	/**
	 * 布局中 中的 surface view
	 * 
	 * @return
	 */
	public abstract int getSufaceViewId();

	/**
	 * 
	 * @return
	 */
	public abstract String getCharset();

	/**
	 * 
	 * @return
	 */
	public abstract Map<DecodeHintType, ?> getDecodeHintType();

	/**
	 * @return Intents.Scan.PRODUCT_MODE Intents.Scan.ONE_D_MODE
	 *         Intents.Scan.QR_CODE_MODE Intents.Scan.DATA_MATRIX_MODE
	 */
	public abstract String getDecodeMode();

	/**
	 * 
	 * @param result
	 */
	public abstract void handleResult(Result result);

	private Collection<BarcodeFormat> getDecodeFormats(String decodeMode) {
		if (Intents.Scan.PRODUCT_MODE.equals(decodeMode)) {
			return PRODUCT_FORMATS;
		}
		if (Intents.Scan.QR_CODE_MODE.equals(decodeMode)) {
			return QR_CODE_FORMATS;
		}
		if (Intents.Scan.DATA_MATRIX_MODE.equals(decodeMode)) {
			return DATA_MATRIX_FORMATS;
		}
		if (Intents.Scan.ONE_D_MODE.equals(decodeMode)) {
			return ONE_D_FORMATS;
		}
		return null;
	}

	// {
	// private Collection<BarcodeFormat> decodeFormats;
	// }
}
