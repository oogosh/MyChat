package com.example.dean.mychat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.fragment.PersonalListFra;
import com.example.dean.mychat.fragment.PersonalQRFra;
import com.example.dean.mychat.widget.NormalTopBar;


public class PersonalInfoActivity extends BaseActivity implements
		OnClickListener {
	public static final String KEY_INTENT = "data";

	private static final String TAG_PERSONAL = "personal";
	private static final String TAG_QR = "qr";

	// top 栏
	private NormalTopBar mTopBar;

	private Account account;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_personal);

		account = getIntent().getParcelableExtra(KEY_INTENT);

		initView();
		initEvent();

		initFragment();
	}

	private void initView() {
		mTopBar = (NormalTopBar) findViewById(R.id.personal_top_bar);
		mTopBar.setTitle("个人信息");
	}

	private void initEvent() {
		mTopBar.setOnBackListener(this);
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();

		PersonalListFra fragment = new PersonalListFra();
		Bundle args = new Bundle();
		args.putParcelable(KEY_INTENT, account);
		fragment.setArguments(args);
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.activity_personal_container, fragment, TAG_PERSONAL);
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			clickBack();
		}
	}

	@Override
	public void onBackPressed() {
		clickBack();
	}

	private void clickBack() {

		FragmentManager fm = getSupportFragmentManager();

		int count = fm.getBackStackEntryCount();

		if (count == 0) {
			finish();
		} else {
			fm.popBackStack();
			if (count == 1) {
				mTopBar.setTitle("个人信息");
			}
		}
	}

	public void go2PersonalQR() {
		mTopBar.setTitle("我的二维码");

		FragmentManager fm = getSupportFragmentManager();

		PersonalQRFra fragment = new PersonalQRFra();
		Bundle args = new Bundle();
		args.putParcelable(KEY_INTENT, account);
		fragment.setArguments(args);
		FragmentTransaction ft = fm.beginTransaction();

		ft.setCustomAnimations(R.anim.fragment_slide_left_enter,
				R.anim.fragment_slide_left_exit,
				R.anim.fragment_slide_right_enter,
				R.anim.fragment_slide_right_exit);
		ft.replace(R.id.activity_personal_container, fragment, TAG_QR);
		ft.addToBackStack(TAG_QR);
		ft.commit();
	}
}