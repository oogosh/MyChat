package com.example.dean.mychat.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.widget.NormalTopBar;


public class QRResultActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_URL = "data";

	private NormalTopBar mTopBar;
	private TextView tvResult;

	private String url;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_qr_result);

		url = getIntent().getStringExtra(KEY_URL);

		initView();

		initEvent();
	}

	private void initView() {
		mTopBar = (NormalTopBar) findViewById(R.id.qr_result_top_bar);
		tvResult = (TextView) findViewById(R.id.qr_result_tv_result);

		mTopBar.setTitle("二维码结果");
		tvResult.setText(url);
	}

	private void initEvent() {
		mTopBar.setOnBackListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			clickBack();
		}
	}

	private void clickBack() {
		finish();
	}
}
