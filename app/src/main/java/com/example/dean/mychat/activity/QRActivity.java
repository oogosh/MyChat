package com.example.dean.mychat.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.R;
import com.example.dean.mychat.db.FriendDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.domain.Friend;
import com.example.dean.mychat.lib.HMChatManager;
import com.example.dean.mychat.lib.HMURL;
import com.example.dean.mychat.lib.callback.ObjectCallback;
import com.example.dean.mychat.utils.BitmapUtil;
import com.example.dean.mychat.utils.QRUtil;
import com.example.dean.mychat.utils.ToastUtil;
import com.example.dean.mychat.widget.NormalTopBar;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import java.util.Map;

public class QRActivity extends CaptureActivity implements OnClickListener {
	private static final int REQUEST_CODE_PICTURE = 100;

	private NormalTopBar mTopBar;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTopBar = (NormalTopBar) findViewById(R.id.qr_top_bar);
		mTopBar.setTitle("二维码");
		mTopBar.setActionText("相册");
		mTopBar.setActionTextVisibility(true);

		mTopBar.setOnBackListener(this);
		mTopBar.setOnActionListener(this);

		account = ((ChatApplication) getApplication()).getCurrentAccount();
	}

	@Override
	public int getLayoutId() {
		return R.layout.act_qr;
	}

	@Override
	public int getViewfinderViewId() {
		return R.id.qr_viewfinder_view;
	}

	@Override
	public int getSufaceViewId() {
		return R.id.qr_preview_view;
	}

	@Override
	public String getCharset() {
		return "utf-8";
	}

	@Override
	public Map<DecodeHintType, ?> getDecodeHintType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDecodeMode() {
		return Intents.Scan.QR_CODE_MODE;
	}

	@Override
	public void handleResult(Result result) {
		if (result == null) {
			return;
		}
		Log.d("result : ", result.toString());
		String text = result.getText();

		if (TextUtils.isEmpty(text)) {
			return;
		}

		if (text.startsWith(HMURL.BASE_QR)) {

			String accountString = QRUtil.decode(
					text.replace(HMURL.BASE_QR, ""), 5);

			String currentUser = ((ChatApplication) getApplication())
					.getCurrentAccount().getAccount();
			if (currentUser.equals(accountString)) {
				ToastUtil.show(getApplicationContext(), "扫自己有意思吗!");

				Intent intent = new Intent(this, QRResultActivity.class);
				intent.putExtra(QRResultActivity.KEY_URL, text);
				startActivity(intent);
				return;
			}

			// 已有的朋友
			FriendDao dao = new FriendDao(this);
			Friend friend = dao
					.queryFriendByAccount(currentUser, accountString);
			if (friend != null) {
				Intent intent = new Intent(this, FriendDetailActivity.class);
				intent.putExtra(FriendDetailActivity.KEY_ENTER,
						FriendDetailActivity.ENTER_CONTACT);
				intent.putExtra(FriendDetailActivity.KEY_DATA, friend);
				startActivity(intent);

				return;
			}

			String url = text;

			HMChatManager.getInstance(this).sendRequest(url,
					new ObjectCallback<Friend>() {

						@Override
						public void onSuccess(Friend t) {
							if (t != null) {
								Log.d("", "" + t.toString());

								Intent intent = new Intent(QRActivity.this,
										FriendDetailActivity.class);
								intent.putExtra(FriendDetailActivity.KEY_ENTER,
										FriendDetailActivity.ENTER_SEARCH);
								intent.putExtra(FriendDetailActivity.KEY_DATA,
										t);
								startActivity(intent);

								finish();
							}
						}

						@Override
						public void onFailure(int errorCode, String errorString) {
							if (errorCode == 200) {
								ToastUtil.show(getApplicationContext(),
										"你扫描的用户不存在");
								finish();
							}
						}
					});
		} else {
			Intent intent = new Intent(this, QRResultActivity.class);
			intent.putExtra(QRResultActivity.KEY_URL, text);
			startActivity(intent);

			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			clickBack();
		} else if (v == mTopBar.getActionView()) {
			clickGallery();
		}
	}

	private void clickBack() {
		finish();
	}

	private void clickGallery() {
		Intent picture = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(picture, REQUEST_CODE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_PICTURE
				&& resultCode == Activity.RESULT_OK && null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumns = { MediaStore.Images.Media.DATA };
			Cursor c = this.getContentResolver().query(selectedImage,
					filePathColumns, null, null, null);
			c.moveToFirst();
			int columnIndex = c.getColumnIndex(filePathColumns[0]);
			String picturePath = c.getString(columnIndex);
			c.close();
			// 获取图片并显示

			// Bitmap bitmap = BitmapFactory.decodeFile(picturePath).copy(
			// Bitmap.Config.ARGB_8888, false);

			Bitmap bitmap = BitmapUtil.getScaleBitmap(200, 200, picturePath)
					.copy(Bitmap.Config.ARGB_8888, true);

			Result result = paseBitmap(bitmap);

			handleDecode(result, bitmap, 1.0f);

		}
	}
}
