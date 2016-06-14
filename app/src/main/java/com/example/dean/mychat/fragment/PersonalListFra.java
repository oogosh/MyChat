package com.example.dean.mychat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dean.mychat.R;
import com.example.dean.mychat.activity.PersonalInfoActivity;
import com.example.dean.mychat.base.BaseFragment;
import com.example.dean.mychat.db.AccountDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.utils.CommonUtil;
import com.example.dean.mychat.utils.DirUtil;
import com.example.dean.mychat.widget.DialogChooseImage;

import java.io.File;


public class PersonalListFra extends BaseFragment implements OnClickListener {
	private final static int REQUEST_CODE_GALLERY = 0x11;
	private final static int REQUEST_CODE_CAMERA = 0x12;
	private final static int REQUEST_CODE_CROP = 0x13;

	private View mItemIconView;
	private ImageView mIvIcon;

	private View mItemNameView;
	private TextView mTvNameView;

	private View mItemAccountView;
	private TextView mTvAccountView;

	private View mItemQRView;

	private View mItemSexView;
	private TextView mTvSexView;

	private View mItemSignView;
	private TextView mTvSignView;

	private int crop = 200;
	private File sdcardTempFile;

	private Account account;
	private AccountDao dao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		account = arguments.getParcelable(PersonalInfoActivity.KEY_INTENT);
		dao = new AccountDao(getActivity());

		String iconDir = DirUtil.getIconDir(getActivity());
		sdcardTempFile = new File(iconDir, CommonUtil.string2MD5(account
				.getAccount()));

		if (!sdcardTempFile.getParentFile().exists()) {
			sdcardTempFile.getParentFile().mkdirs();
		}

		account.setIcon(sdcardTempFile.getAbsolutePath());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_personal, container, false);
		initView(view);
		initEvent();
		return view;
	}

	private void initView(View view) {
		mItemIconView = view.findViewById(R.id.personal_item_icon);
		mIvIcon = (ImageView) view.findViewById(R.id.personal_iv_icon);

		mItemNameView = view.findViewById(R.id.personal_item_name);
		mTvNameView = (TextView) view.findViewById(R.id.personal_tv_name);

		mItemAccountView = view.findViewById(R.id.personal_item_account);
		mTvAccountView = (TextView) view.findViewById(R.id.personal_tv_account);

		mItemQRView = view.findViewById(R.id.personal_item_qr);

		mItemSexView = view.findViewById(R.id.personal_item_sex);
		mTvSexView = (TextView) view.findViewById(R.id.personal_tv_sex);

		mItemSignView = view.findViewById(R.id.personal_item_sign);
		mTvSignView = (TextView) view.findViewById(R.id.personal_tv_sign);

		loadInfo();
	}

	private void loadInfo() {
		int sex = account.getSex();
		String sexStr = "未填写";
		switch (sex) {
		case 1:
			sexStr = "女";
			break;
		case 2:
			sexStr = "男";
			break;
		default:
			break;
		}

		mTvNameView.setText(account.getName());
		Bitmap bitmap = BitmapFactory.decodeFile(account.getIcon());
		if (bitmap != null) {
			mIvIcon.setImageBitmap(bitmap);
		}
		mTvAccountView.setText(account.getAccount());
		mTvSexView.setText(sexStr);
		mTvSignView.setText(account.getSign());
	}

	private void initEvent() {
		mItemIconView.setOnClickListener(this);
		mItemQRView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mItemIconView) {
//			openChooseDialog();
		} else if (v == mItemQRView) {
			((PersonalInfoActivity) getActivity()).go2PersonalQR();
		}
	}

	private void openChooseDialog() {

		final DialogChooseImage dialog = new DialogChooseImage(getActivity());
		dialog.show();

		dialog.setClickCameraListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getImageFromCamera();
				dialog.dismiss();
			}
		});
		dialog.setClickGalleryListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getimageFromGallery();
				dialog.dismiss();
			}
		});
	}

	protected void getimageFromGallery() {

		Intent intent = new Intent("android.intent.action.PICK");
		intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
				"image/*");
		intent.putExtra("output", Uri.fromFile(sdcardTempFile));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", crop);// 输出图片大小
		intent.putExtra("outputY", crop);
		startActivityForResult(intent, REQUEST_CODE_GALLERY);

	}

	protected void getImageFromCamera() {
		Uri uri = Uri.fromFile(sdcardTempFile);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra("output", uri);
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_GALLERY: {
			performImageBack();
			break;
		}
		case REQUEST_CODE_CAMERA: {
			Uri uri = Uri.fromFile(sdcardTempFile);
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("output", uri);
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// 裁剪框比例
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", crop);// 输出图片大小
			intent.putExtra("outputY", crop);
			startActivityForResult(intent, REQUEST_CODE_CROP);
			break;
		}
		case REQUEST_CODE_CROP: {
			performImageBack();
			break;
		}
		default:
			break;
		}
	}

	private void performImageBack() {
		String path = sdcardTempFile.getAbsolutePath();
		Bitmap bmp = BitmapFactory.decodeFile(path);
		mIvIcon.setImageBitmap(bmp);

		// 更新地址
		account.setIcon(path);
		dao.updateAccount(account);

		// 添加icon上传任务
//		addIconTask();
	}

//	private void addIconTask() {
//		File iconFile = new File(sdcardTempFile.getAbsolutePath());
//
//		if (!iconFile.exists()) {
//			return;
//		}
//
//		// 存储到后台任务中
//		String taskDir = DirUtil.getTaskDir(getActivity());
//		String fileName = CommonUtil.string2MD5(account.getAccount() + "_"
//				+ System.currentTimeMillis());
//		String path = new File(taskDir, fileName).getAbsolutePath();
//
//		BackTask task = new BackTask();
//		task.setOwner(account.getAccount());
//		task.setPath(path);
//		task.setState(0);
//		new BackTaskDao(getActivity()).addTask(task);
//
//		NetTask netTask = BackTaskFactory.userIconChangeTask(account.getIcon());
//
//		try {
//			// 写入到缓存
//			SerializableUtil.write(netTask, path);
//
//			// 开启后台服务
//			getActivity().startService(
//					new Intent(getActivity(), BackgroundService.class));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
