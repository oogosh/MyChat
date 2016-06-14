package com.example.dean.mychat.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dean.mychat.R;
import com.example.dean.mychat.activity.PersonalInfoActivity;
import com.example.dean.mychat.base.BaseFragment;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.utils.QRUtil;


public class PersonalQRFra extends BaseFragment {
	private ImageView ivQR;
	private ImageView ivIcon;
	private TextView tvName;

	private Account account;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		account = arguments.getParcelable(PersonalInfoActivity.KEY_INTENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fra_personal_qr, container, false);

		initView(view);
		return view;
	}

	private void initView(View view) {
		ivQR = (ImageView) view.findViewById(R.id.personal_qr_iv_qr);
		ivIcon = (ImageView) view.findViewById(R.id.personal_qr_iv_icon);
		tvName = (TextView) view.findViewById(R.id.personal_qr_tv_name);

		tvName.setText(account.getName());

		try {
			ivQR.setImageBitmap(QRUtil.getQRImage(getActivity(), account
					.getAccount(), Color.TRANSPARENT,
					getResources().getColor(R.color.caribbean_green)));
		} catch (Exception e) {
			// TODO: handle exception
		}

		Bitmap bitmap = BitmapFactory.decodeFile(account.getIcon());
		if (bitmap != null) {
			ivIcon.setImageBitmap(bitmap);
		}
	}

}
