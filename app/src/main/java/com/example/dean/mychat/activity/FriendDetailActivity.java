package com.example.dean.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.domain.Friend;
import com.example.dean.mychat.widget.NormalTopBar;


public class FriendDetailActivity extends BaseActivity implements
		OnClickListener {
	public static final String KEY_ENTER = "enter";
	public static final String KEY_DATA = "data";

	public static final int ENTER_SEARCH = 1;
	public static final int ENTER_CONTACT = 2;

	private NormalTopBar mTopBar;

	private ImageView mIvIconView;

	private TextView mTvNameView;
	private TextView mTvAccountView;
	private TextView mTvNickNameView;

	private TextView mTvSignView;

	private Button mBtnAdd;
	private Button mBtnSend;

	private int enterFlag;
	private Friend friend;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_friend_detail);

		enterFlag = getIntent().getIntExtra(KEY_ENTER, -1);

		if (enterFlag == -1) {
			throw new RuntimeException("没有定义入口");
		}

		friend = (Friend) getIntent().getSerializableExtra(KEY_DATA);

		initView();
		initEvent();
	}

	private void initView() {
		mTopBar = (NormalTopBar) findViewById(R.id.friend_detail_top_bar);

		mIvIconView = (ImageView) findViewById(R.id.friend_detail_iv_icon);
		mTvNameView = (TextView) findViewById(R.id.friend_detail_tv_name);
		mTvAccountView = (TextView) findViewById(R.id.friend_detail_tv_account);
		mTvNickNameView = (TextView) findViewById(R.id.friend_detail_tv_nickname);

		mTvSignView = (TextView) findViewById(R.id.friend_detail_tv_sign);

		mBtnAdd = (Button) findViewById(R.id.friend_detail_btn_add);
		mBtnSend = (Button) findViewById(R.id.friend_detail_btn_send);

		mTopBar.setTitle("详细资料");

		if (enterFlag == ENTER_SEARCH) {
			mTvAccountView.setVisibility(View.GONE);
			mTvNickNameView.setVisibility(View.GONE);

			mBtnAdd.setVisibility(View.VISIBLE);
			mBtnSend.setVisibility(View.GONE);

			mTvNameView.setText(friend.getName());
		} else if (enterFlag == ENTER_CONTACT) {
			mTvAccountView.setVisibility(View.VISIBLE);
			mTvNickNameView.setVisibility(View.VISIBLE);

			mBtnAdd.setVisibility(View.GONE);
			mBtnSend.setVisibility(View.VISIBLE);

			mTvNameView.setText(friend.getName());
			mTvAccountView.setText("黑信号:" + friend.getAccount());
			mTvNickNameView.setText("昵称:" + friend.getNickName());
		}
	}

	private void initEvent() {
		mTopBar.setOnBackListener(this);
		mBtnAdd.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			clickBack();
		} else if (v == mBtnAdd) {
			clickAdd();
		} else if (v == mBtnSend) {
			clickSend();
		}
	}

	private void clickBack() {
		finish();
	}

	private void clickSend() {
		// 跳转到发消息页面
		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra("messager", friend.getAccount());
		startActivity(intent);
	}

	private void clickAdd() {
		// 发送邀请
		Intent intent = new Intent(this, FriendValidateActivity.class);
		intent.putExtra("receiver", friend.getAccount());
		startActivity(intent);
	}
}
