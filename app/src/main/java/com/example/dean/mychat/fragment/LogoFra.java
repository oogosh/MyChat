package com.example.dean.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dean.mychat.R;
import com.example.dean.mychat.activity.HomeActivity;
import com.example.dean.mychat.activity.LoginActivity;
import com.example.dean.mychat.base.BaseFragment;


public class LogoFra extends BaseFragment implements OnClickListener {
	public final static String ARG_KEY = "ARG";

	public final static int ARG_TYPE_FIRST = 0;
	public final static int ARG_TYPE_LOGINED = 1;

	public int currentFlag = 0;

	private Button btnSignUp;
	private Button btnSignIn;

	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		currentFlag = arguments.getInt(ARG_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fra_logo, container, false);

		initView(view);
		initEvent();
		return view;
	}

	private void initView(View view) {
		btnSignIn = (Button) view.findViewById(R.id.btn_sign_in);
		btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);

		if (currentFlag == ARG_TYPE_FIRST) {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignUp.setVisibility(View.VISIBLE);
		} else {
			btnSignIn.setVisibility(View.GONE);
			btnSignUp.setVisibility(View.GONE);

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// // 开启服务
					// if (!CommonUtil.isServiceRunning(getActivity(),
					// ChatCoreService.class)) {
					// getActivity()
					// .startService(
					// new Intent(getActivity(),
					// ChatCoreService.class));
					// }

					startActivity(new Intent(getActivity(), HomeActivity.class));
					getActivity().finish();
				}
			}, 1500);
		}
	}

	private void initEvent() {
		btnSignIn.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (btnSignIn == v) {
			signIn();
		} else if (btnSignUp == v) {
			signUp();
		}
	}

	private void signIn() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			((LoginActivity) activity).go2SignIn();
		}
	}

	private void signUp() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			((LoginActivity) activity).go2SignUp();
		}
	}
}
