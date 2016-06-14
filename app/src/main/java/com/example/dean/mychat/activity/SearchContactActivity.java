package com.example.dean.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.db.FriendDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.domain.Friend;
import com.example.dean.mychat.lib.HMChatManager;
import com.example.dean.mychat.lib.HMURL;
import com.example.dean.mychat.lib.callback.ObjectCallback;
import com.example.dean.mychat.utils.ToastUtil;
import com.example.dean.mychat.widget.DialogLoading;

import java.util.HashMap;
import java.util.Map;


public class SearchContactActivity extends BaseActivity implements
		OnClickListener, TextWatcher {
	private ImageView ivBack;
	private EditText etSearch;
	private Button btnClearSearch;

	private View vClickItem;
	private TextView tvSearchContent;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_search_contact);

		initView();
		initEvent();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.bar_btn_back);
		etSearch = (EditText) findViewById(R.id.bar_et_search);
		btnClearSearch = (Button) findViewById(R.id.bar_btn_clear_search);

		vClickItem = findViewById(R.id.search_item);
		tvSearchContent = (TextView) findViewById(R.id.search_tv_content);

		vClickItem.setVisibility(View.GONE);
		btnClearSearch.setVisibility(View.GONE);
	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		btnClearSearch.setOnClickListener(this);
		vClickItem.setOnClickListener(this);

		etSearch.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == ivBack) {
			clickBack();
		} else if (v == btnClearSearch) {
			clickClearSearch();
		} else if (v == vClickItem) {
			clickItem();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		String searchContent = etSearch.getText().toString().trim();
		if (TextUtils.isEmpty(searchContent)) {
			vClickItem.setVisibility(View.GONE);
			btnClearSearch.setVisibility(View.GONE);
			return;
		}

		tvSearchContent.setText(searchContent);
		vClickItem.setVisibility(View.VISIBLE);
		btnClearSearch.setVisibility(View.VISIBLE);
	}

	private void clickBack() {
		finish();
	}

	private void clickClearSearch() {
		etSearch.setText("");
	}

	private void clickItem() {
		String account = etSearch.getText().toString().trim();

		Account currentAccount = ((ChatApplication) getApplication())
				.getCurrentAccount();
		String currentUser = currentAccount.getAccount();
		if (currentUser.equals(account)) {
			ToastUtil.show(getApplicationContext(), "不要找自己啦");
			return;
		}

		// 已有的朋友
		FriendDao dao = new FriendDao(this);
		Friend friend = dao.queryFriendByAccount(currentUser, account);
		if (friend != null) {
			Intent intent = new Intent(this, FriendDetailActivity.class);
			intent.putExtra(FriendDetailActivity.KEY_ENTER,
					FriendDetailActivity.ENTER_CONTACT);
			intent.putExtra(FriendDetailActivity.KEY_DATA, friend);
			startActivity(intent);

			return;
		}

		final DialogLoading dialog = new DialogLoading(this);
		dialog.show();

		String url = HMURL.BASE_HTTP + "/user/search";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("account", currentAccount.getAccount());
		headers.put("token", currentAccount.getToken());

		Map<String, String> paramters = new HashMap<String, String>();
		paramters.put("search", account);
		
		HMChatManager.getInstance(this).sendRequest(url, headers, paramters,
				new ObjectCallback<Friend>() {

					@Override
					public void onSuccess(Friend t) {
						dialog.dismiss();
						if (t != null) {
							Log.d("", "" + t.toString());

							Intent intent = new Intent(
									SearchContactActivity.this,
									FriendDetailActivity.class);
							intent.putExtra(FriendDetailActivity.KEY_ENTER,
									FriendDetailActivity.ENTER_SEARCH);
							intent.putExtra(FriendDetailActivity.KEY_DATA, t);
							startActivity(intent);

							finish();
						}
					}

					@Override
					public void onFailure(int errorCode, String errorString) {
						dialog.dismiss();
						Log.d("", errorCode + " : " + errorString);

						if (errorCode == 200) {
							ToastUtil
									.show(getApplicationContext(), "你搜索的用户不存在");
						}
					}
				});

	}
}
