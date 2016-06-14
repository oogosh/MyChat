package com.example.dean.mychat.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.utils.ToastUtil;


public class FriendValidateActivity extends BaseActivity implements
		OnClickListener, TextWatcher {
	private TextView mTvPositive;
	private TextView mTvNegative;
	private TextView mTvTitle;

	private EditText mEtContent;
	private Button mBtnClear;

	private String receiver;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_friend_validate);

		receiver = getIntent().getStringExtra("receiver");

		initView();
		initEvent();
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.bar_title);
		mTvPositive = (TextView) findViewById(R.id.bar_positive);
		mTvNegative = (TextView) findViewById(R.id.bar_negative);

		mEtContent = (EditText) findViewById(R.id.friend_validate_et_content);
		mBtnClear = (Button) findViewById(R.id.friend_validate_btn_clear);
		mBtnClear.setVisibility(View.GONE);

		mTvTitle.setText("朋友验证");
		mTvPositive.setText("发送");
		mTvNegative.setText("取消");
	}

	private void initEvent() {
		mTvNegative.setOnClickListener(this);
		mTvPositive.setOnClickListener(this);
		mBtnClear.setOnClickListener(this);

		mEtContent.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mTvNegative) {
			clickCancel();
		} else if (v == mTvPositive) {
			clickSend();
		} else if (v == mBtnClear) {
			clickClear();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		String content = mEtContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			mBtnClear.setVisibility(View.GONE);
		} else {
			mBtnClear.setVisibility(View.VISIBLE);
		}
	}

	private void clickClear() {
		mEtContent.setText("");
	}

	private void clickSend() {
		String content = mEtContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			ToastUtil.show(this, "请输入邀请验证信息");
			return;
		}

		Account account = ((ChatApplication) getApplication())
				.getCurrentAccount();

//		ChatMessage message = ChatMessage
//				.createMessage(ChatMessage.Type.INVITATION);
//		message.setReceiver(receiver);
//		message.setBody(new InvitationBody(content));
//		message.setAccount(account.getAccount());
//		message.setToken(account.getToken());
//
//		HMChatManager.getInstance().sendMessage(message, new HMChatCallBack() {
//
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				ToastUtil.show(FriendValidateActivity.this, "邀请发送成功");
//				finish();
//			}
//
//			@Override
//			public void onProgress(int progress) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onError(int error, String msg) {
//				// TODO Auto-generated method stub
//				ToastUtil.show(FriendValidateActivity.this, "邀请发送失败");
//			}
//		});
	}

	private void clickCancel() {
		finish();
	}
}
