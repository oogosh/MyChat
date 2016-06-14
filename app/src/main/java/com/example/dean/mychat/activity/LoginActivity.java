package com.example.dean.mychat.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.db.AccountDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.fragment.FillInfoFra;
import com.example.dean.mychat.fragment.LogoFra;
import com.example.dean.mychat.fragment.SignInFra;
import com.example.dean.mychat.fragment.SignUpFra;
import com.example.dean.mychat.widget.NormalTopBar;


public class LoginActivity extends BaseActivity implements OnClickListener {
	public static final String TAG_LOGO = "logo";
	public static final String TAG_SIGN_IN = "sign_in";
	public static final String TAG_SIGN_UP = "sign_up";
	public static final String TAG_FILL_INFO = "fill_info";

	public static final String ENTER_KEY = "enter";
	public static final int ENTER_FIRST = 0;
	public static final int ENTER_LOGINED = 1;
	public static final int ENTER_SIGN_IN = 2;
	public static final int ENTER_SIGN_UP = 3;
	public static final int ENTER_FILL_INFO = 4;

	private NormalTopBar mTopBar;

	private Fragment currentFra;
	private String currentTag;
	private FragmentManager fm;

	private int enterFlag = 0;

	private AccountDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		mTopBar = (NormalTopBar) findViewById(R.id.activity_login_top_bar);
		mTopBar.setOnBackListener(this);

		fm = getSupportFragmentManager();
		enterFlag = getIntent().getIntExtra(ENTER_KEY, ENTER_FIRST);

		dao = new AccountDao(this);
		Account account = dao.getCurrentAccount();
		if (account != null && !TextUtils.isEmpty(account.getName())) {
			enterFlag = ENTER_LOGINED;
		} else if (account != null) {
			enterFlag = ENTER_FILL_INFO;
		}

		if (enterFlag == ENTER_FIRST) {
			mTopBar.setVisibility(View.GONE);

			// 第一次登录		
			currentFra = new LogoFra();
			Bundle args = new Bundle();
			args.putInt(LogoFra.ARG_KEY, LogoFra.ARG_TYPE_FIRST);
			currentFra.setArguments(args);
			currentTag = TAG_LOGO;
		} else if (enterFlag == ENTER_LOGINED) {
			mTopBar.setVisibility(View.GONE);

			// 用户已经登录
			currentFra = new LogoFra();
			Bundle args = new Bundle();
			args.putInt(LogoFra.ARG_KEY, LogoFra.ARG_TYPE_LOGINED);
			currentFra.setArguments(args);
			currentTag = TAG_LOGO;
		} else if (enterFlag == ENTER_SIGN_IN) {
			currentFra = new SignInFra();

			// 设置顶部title
			mTopBar.setVisibility(View.VISIBLE);
			mTopBar.setTitle(R.string.sign_in_title);
			mTopBar.setBackVisibility(false);

			currentTag = TAG_SIGN_IN;
		} else if (enterFlag == ENTER_FILL_INFO) {
			currentFra = new FillInfoFra();

			// 设置顶部title
			mTopBar.setVisibility(View.VISIBLE);
			mTopBar.setTitle("填写信息");
			mTopBar.setBackVisibility(false);

			currentTag = TAG_FILL_INFO;
		}

		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.contanier_login, currentFra, currentTag);
		transaction.addToBackStack(currentTag);
		transaction.commit();
	}

	public void go2SignIn() {
		Fragment fragment = fm.findFragmentByTag(TAG_SIGN_IN);
		if (fragment == null) {
			fragment = new SignInFra();
		}

		// 设置 Topbar
		mTopBar.setVisibility(View.VISIBLE);
		mTopBar.setTitle(R.string.sign_in_title);
		mTopBar.setBackVisibility(true);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.contanier_login, fragment, TAG_SIGN_IN);
		ft.addToBackStack(TAG_SIGN_IN);
		ft.commit();
	}

	public void go2SignUp() {
		Fragment fragment = fm.findFragmentByTag(TAG_SIGN_UP);
		if (fragment == null) {
			fragment = new SignUpFra();
		}

		// 设置Topbar		
		mTopBar.setVisibility(View.VISIBLE);
		mTopBar.setTitle(R.string.sign_up_title);
		mTopBar.setBackVisibility(true);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.contanier_login, fragment, TAG_SIGN_UP);
		ft.addToBackStack(TAG_SIGN_UP);
		ft.commit();
	}

	public void go2FillInfo() {

		Fragment fragment = fm.findFragmentByTag(TAG_FILL_INFO);
		int count = fm.getBackStackEntryCount();
		for (int i = 0; i < count; i++) {
			fm.popBackStack();
		}

		// fm.popBackStackImmediate(R.id.contanier_login,
		// FragmentManager.POP_BACK_STACK_INCLUSIVE);

		if (fragment == null) {
			fragment = new FillInfoFra();
		}

		mTopBar.setVisibility(View.VISIBLE);
		mTopBar.setTitle("填写信息");
		mTopBar.setBackVisibility(false);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.contanier_login, fragment, TAG_FILL_INFO);
		ft.addToBackStack(TAG_FILL_INFO);
		ft.commit();

	}

	@Override
	public void onBackPressed() {
		clickBack();
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			clickBack();
		}
	}

	private void clickBack() {
		int count = fm.getBackStackEntryCount();
		Log.d("", "count : " + count);
		if (count <= 1) {
			finish();
		} else {
			fm.popBackStack();
			if (count == 2) {
				BackStackEntry entry = fm.getBackStackEntryAt(0);
				String name = entry.getName();
				if (TAG_LOGO.equals(name)) {
					mTopBar.setVisibility(View.GONE);
				}
			}
		}
	}

}