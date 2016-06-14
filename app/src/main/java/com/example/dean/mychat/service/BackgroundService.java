package com.example.dean.mychat.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

import com.example.dean.mychat.ChatApplication;
import com.example.dean.mychat.db.BackTaskDao;
import com.example.dean.mychat.db.HMDB;
import com.example.dean.mychat.domain.Account;
import com.example.dean.mychat.domain.NetTask;
import com.example.dean.mychat.lib.HMHttpManaer;
import com.example.dean.mychat.utils.CommonUtil;
import com.example.dean.mychat.utils.SerializableUtil;

import java.util.HashMap;
import java.util.Map;

public class BackgroundService extends IntentService {

	// 1.构造函数应该注意的
	public BackgroundService() {
		super("background");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// 2. 此方法执行在子线程中
		// 3. service start多次，此方法会排队执行
		Account account = ((ChatApplication) getApplication())
				.getCurrentAccount();
		if (account == null) {
			return;
		}

		if (!CommonUtil.isNetConnected(this)) {
			return;
		}

		BackTaskDao dao = new BackTaskDao(this);
		Cursor cursor = dao.query(account.getAccount(), 0);

		// 存储到 map中
		Map<Long, String> map = new HashMap<Long, String>();

		if (cursor != null) {

			while (cursor.moveToNext()) {
				final long id = cursor.getLong(cursor
						.getColumnIndex(HMDB.BackTask.COLUMN_ID));
				String filePath = cursor.getString(cursor
						.getColumnIndex(HMDB.BackTask.COLUMN_PATH));

				map.put(id, filePath);
			}
			cursor.close();
		}

		for (Map.Entry<Long, String> me : map.entrySet()) {
			try {
				final Long id = me.getKey();
				String filePath = me.getValue();

				NetTask task = (NetTask) SerializableUtil.read(filePath);
				// TODO: 发送请求
				// 改变状态值
				dao.updateState(id, 1);

				String url = task.getUrl();
				Map<String, String> headers = task.getHeaders();
				Map<String, String> paramaters = task.getParameters();
				boolean result = HMHttpManaer.getInstance().post(url, headers,
						paramaters);

				if (result) {

					System.out.println("#########9");

					dao.updateState(id, 2);
				} else {
					System.out.println("#########10");

					dao.updateState(id, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
