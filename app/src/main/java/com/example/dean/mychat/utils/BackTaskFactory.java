package com.example.dean.mychat.utils;

import com.example.dean.mychat.domain.NetTask;
import com.example.dean.mychat.lib.HMURL;

import java.util.HashMap;


public class BackTaskFactory {

	public static NetTask newFriendAcceptTask(String invitor, String acceptor) {

		NetTask task = new NetTask();
		task.setMethod(0);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("invitor", invitor);
		params.put("acceptor", acceptor);

		task.setParameters(params);
		task.setUrl(HMURL.BASE_HTTP + "/friend/accept");
		return task;
	}

	public static NetTask userNameChangeTask(String name) {
		NetTask task = new NetTask();
		task.setMethod(0);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("name", name);

		task.setParameters(params);
		task.setUrl(HMURL.BASE_HTTP + "/user/name");
		return task;
	}
}
