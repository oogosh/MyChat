package com.example.dean.mychat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.db.HMDB;
import com.example.dean.mychat.db.MessageDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.domain.Message;
import com.example.dean.mychat.utils.CommonUtil;
import com.example.dean.mychat.widget.NormalTopBar;

public class MessageActivity extends BaseActivity implements OnClickListener,
		TextWatcher {
	private NormalTopBar mTopBar;

	private ListView listView;
	private MessageAdapter adapter;

	private Button btnSend;
	private EditText etContent;

	private String messager;

//	private PushReceiver pushReceiver = new PushReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String from = intent.getStringExtra(PushReceiver.KEY_FROM);
//			// String to = intent.getStringExtra(PushReceiver.KEY_TO);
//			// intent.getStringExtra(PushReceiver.KEY_TEXT_CONTENT);
//
//			if (messager.equalsIgnoreCase(from)) {
//				loadData();
//			}
//		}
//	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_message);

		messager = getIntent().getStringExtra("messager");

//		IntentFilter filter = new IntentFilter();
//		filter.addAction(PushReceiver.ACTION_TEXT);
//		registerReceiver(pushReceiver, filter);

		initView();
		initEvent();
		loadData();
	}

	@Override
	protected void onPause() {
		super.onPause();

		Account account = ((ChatApplication) getApplication())
				.getCurrentAccount();
		MessageDao dao = new MessageDao(this);
		dao.clearUnread(account.getAccount(), messager);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

//		unregisterReceiver(pushReceiver);
	}

	private void initView() {
		mTopBar = (NormalTopBar) findViewById(R.id.message_top_bar);
		listView = (ListView) findViewById(R.id.message_list_view);

		btnSend = (Button) findViewById(R.id.message_btn_send);
		etContent = (EditText) findViewById(R.id.message_et_content);
		btnSend.setEnabled(false);

		mTopBar.setTitle(messager);

		adapter = new MessageAdapter(this, null);
		listView.setAdapter(adapter);
	}

	private void initEvent() {
		mTopBar.setOnBackListener(this);
		btnSend.setOnClickListener(this);
		etContent.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mTopBar.getBackView()) {
			finish();
		} else if (v == btnSend) {
			clickSend();
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
		String content = etContent.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			btnSend.setEnabled(false);
		} else {
			btnSend.setEnabled(true);
		}
	}

	private void loadData() {
		ChatApplication application = (ChatApplication) getApplication();
		Account account = application.getCurrentAccount();

		MessageDao dao = new MessageDao(this);
		final Cursor cursor = dao.queryMessage(account.getAccount(), messager);
		adapter.changeCursor(cursor);
		listView.post(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					listView.smoothScrollToPositionFromTop(cursor.getCount(), 0);
				} else {
					listView.smoothScrollToPosition(cursor.getCount());
				}
			}
		});
	}

	// 发送消息
	private void clickSend() {
		String content = etContent.getText().toString().trim();
		final Account account = ((ChatApplication) getApplication())
				.getCurrentAccount();

		// 存储到本地
		final MessageDao dao = new MessageDao(this);
		final Message msg = new Message();
		msg.setAccount(messager);
		msg.setContent(content);
		msg.setCreateTime(System.currentTimeMillis());
		msg.setDirection(0);
		msg.setOwner(account.getAccount());
		msg.setRead(true);
		msg.setState(1);
		msg.setType(0);
		dao.addMessage(msg);
		// 更新ui
		loadData();

		etContent.setText("");

		// 网络调用
//		ChatMessage message = ChatMessage.createMessage(ChatMessage.Type.TEXT);
//		message.setAccount(account.getAccount());
//		message.setToken(account.getToken());
//		message.setReceiver(messager);
//		message.setBody(new TextBody(content));
//		HMChatManager.getInstance().sendMessage(message, new HMChatCallBack() {
//
//			@Override
//			public void onSuccess() {
//				ToastUtil.show(getApplicationContext(), "发送成功");
//
//				msg.setState(2);
//				dao.updateMessage(msg);
//				// 更新ui
//				loadData();
//			}
//
//			@Override
//			public void onProgress(int progress) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onError(int error, String errorString) {
//				ToastUtil.show(getApplicationContext(), "发送失败");
//
//				msg.setState(3);
//				dao.updateMessage(msg);
//				// 更新ui
//				loadData();
//			}
//		});
	}

	private class MessageAdapter extends CursorAdapter {

		public MessageAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return View.inflate(context, R.layout.item_message, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tvTime = (TextView) view
					.findViewById(R.id.item_message_tv_time);
			View senderView = view.findViewById(R.id.item_message_sender);
			View receiverView = view.findViewById(R.id.item_message_receiver);

			int direction = cursor.getInt(cursor
					.getColumnIndex(HMDB.Message.COLUMN_DIRECTION));
			long createTime = cursor.getLong(cursor
					.getColumnIndex(HMDB.Message.COLUMN_CREATE_TIME));
			tvTime.setText(CommonUtil.getDateFormat(createTime));

			if (direction == 0) {
				// 发送
				senderView.setVisibility(View.VISIBLE);
				receiverView.setVisibility(View.GONE);

				ImageView senderIconView = (ImageView) view
						.findViewById(R.id.item_message_sender_icon);
				TextView senderContentView = (TextView) view
						.findViewById(R.id.item_message_sender_tv_content);
				ProgressBar pbLoading = (ProgressBar) view
						.findViewById(R.id.item_message_sender_pb_state);
				ImageView faildView = (ImageView) view
						.findViewById(R.id.item_message_sender_iv_faild);

				senderContentView.setText(cursor.getString(cursor
						.getColumnIndex(HMDB.Message.COLUMN_CONTENT)));

				int state = cursor.getInt(cursor
						.getColumnIndex(HMDB.Message.COLUMN_STATE));

				// 1.正在发送 2.已经成功发送 3.发送失败
				if (state == 1) {
					pbLoading.setVisibility(View.VISIBLE);
					faildView.setVisibility(View.GONE);
				} else if (state == 2) {
					pbLoading.setVisibility(View.GONE);
					faildView.setVisibility(View.GONE);
				} else {
					pbLoading.setVisibility(View.GONE);
					faildView.setVisibility(View.VISIBLE);
				}

			} else {
				// 接收
				senderView.setVisibility(View.GONE);
				receiverView.setVisibility(View.VISIBLE);

				ImageView receiverIconView = (ImageView) view
						.findViewById(R.id.item_message_receiver_icon);
				TextView receiverContentView = (TextView) view
						.findViewById(R.id.item_message_receiver_tv_content);

				receiverContentView.setText(cursor.getString(cursor
						.getColumnIndex(HMDB.Message.COLUMN_CONTENT)));
			}

		}
	}
}
