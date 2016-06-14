package com.example.dean.mychat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.R;
import com.example.dean.mychat.base.BaseActivity;
import com.example.dean.mychat.db.BackTaskDao;
import com.example.dean.mychat.db.FriendDao;
import com.example.dean.mychat.db.HMDB;
import com.example.dean.mychat.db.InvitationDao;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.domain.BackTask;
import com.example.dean.mychat.domain.Friend;
import com.example.dean.mychat.domain.Invitation;
import com.example.dean.mychat.domain.NetTask;
import com.example.dean.mychat.service.BackgroundService;
import com.example.dean.mychat.utils.BackTaskFactory;
import com.example.dean.mychat.utils.CommonUtil;
import com.example.dean.mychat.utils.DirUtil;
import com.example.dean.mychat.utils.SerializableUtil;
import com.example.dean.mychat.widget.NormalTopBar;

import java.io.File;


public class FriendNewActivity extends BaseActivity implements OnClickListener {
	private NormalTopBar mTopBar;
	private ListView listView;
	private FriendNewAdapter adapter;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.act_friend_new);
		initView();
		initEvent();
		loadData();
	}

	private void initView() {
		mTopBar = (NormalTopBar) findViewById(R.id.friend_new_top_bar);
		listView = (ListView) findViewById(R.id.friend_new_list_view);

		mTopBar.setTitle("新的朋友");

		adapter = new FriendNewAdapter(this, null);
		listView.setAdapter(adapter);
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

	private void loadData() {
		ChatApplication application = (ChatApplication) getApplication();
		Account account = application.getCurrentAccount();

		InvitationDao dao = new InvitationDao(this);
		Cursor cursor = dao.queryCursor(account.getAccount());
		adapter.changeCursor(cursor);
	}

	private void clickBack() {
		finish();
	}

	OnClickListener acceptListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Object o = v.getTag();
			if (o == null) {
				return;
			}

			// 更新数据库
			InvitationDao dao = new InvitationDao(getApplicationContext());
			Invitation invitation = (Invitation) o;
			invitation.setAgree(true);
			dao.updateInvitation(invitation);

			// 添加到好友列表
			FriendDao friendDao = new FriendDao(getApplicationContext());
			Friend friend = friendDao.queryFriendByAccount(
					invitation.getOwner(), invitation.getAccount());
			if (friend == null) {
				friend = new Friend();
				friend.setAccount(invitation.getAccount());
				friend.setAlpha(CommonUtil.getFirstAlpha(invitation.getName()));
				friend.setIcon(invitation.getIcon());
				friend.setName(invitation.getName());
				friend.setOwner(invitation.getOwner());
				friend.setSort(0);
				friendDao.addFriend(friend);
			}

			// ui更新
			adapter.changeCursor(dao.queryCursor(invitation.getOwner()));

			// 添加接受朋友邀请的任务
			addAcceptFriendTask(invitation);
		}
	};

	private void addAcceptFriendTask(Invitation invitation) {

		// 存储到后台任务中
		String taskDir = DirUtil.getTaskDir(this);
		String file = CommonUtil.string2MD5(invitation.getAccount() + "_"
				+ SystemClock.currentThreadTimeMillis());
		String path = new File(taskDir, file).getAbsolutePath();

		BackTask task = new BackTask();
		task.setOwner(invitation.getOwner());
		task.setPath(path);
		task.setState(0);
		new BackTaskDao(getApplicationContext()).addTask(task);

		NetTask netTask = BackTaskFactory.newFriendAcceptTask(
				invitation.getAccount(), invitation.getOwner());
		try {
			// 写入到缓存
			SerializableUtil.write(netTask, path);

			// 开启后台服务
			startService(new Intent(getApplicationContext(),
					BackgroundService.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class FriendNewAdapter extends CursorAdapter {

		public FriendNewAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return View.inflate(context, R.layout.item_new_friend, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.item_new_friend_icon);
			TextView tvName = (TextView) view
					.findViewById(R.id.item_new_friend_name);
			TextView tvAccept = (TextView) view
					.findViewById(R.id.item_new_friend_tv_accept);
			Button btnAccept = (Button) view
					.findViewById(R.id.item_new_friend_btn_accept);

			String account = cursor.getString(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_INVITATOR_ACCOUNT));
			String name = cursor.getString(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_INVITATOR_NAME));
			String icon = cursor.getString(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_INVITATOR_ICON));
			boolean agree = cursor.getInt(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_AGREE)) == 1;
			String content = cursor.getString(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_CONTENT));
			String owner = cursor.getString(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_OWNER));
			long id = cursor.getLong(cursor
					.getColumnIndex(HMDB.Invitation.COLUMN_ID));

			Invitation invitation = new Invitation();
			invitation.setAccount(account);
			invitation.setAgree(agree);
			invitation.setContent(content);
			invitation.setIcon(icon);
			invitation.setName(name);
			invitation.setOwner(owner);
			invitation.setId(id);

			if (!agree) {
				btnAccept.setVisibility(View.VISIBLE);
				tvAccept.setVisibility(View.GONE);
			} else {
				btnAccept.setVisibility(View.GONE);
				tvAccept.setVisibility(View.VISIBLE);
			}

			tvName.setText(name);

			btnAccept.setOnClickListener(acceptListener);
			btnAccept.setTag(invitation);
		}
	}
}
