package com.example.dean.mychat.lib.future;

import com.loopj.android.http.RequestHandle;

public class HttpFuture {
	private RequestHandle handle;

	public HttpFuture(RequestHandle handle) {
		this.handle = handle;
	}

	public boolean isCancelled() {
		return handle.isCancelled();
	}

	public boolean isFinished() {
		return handle.isFinished();
	}

	public void cancel(boolean mayInterruptIfRunning) {
		handle.cancel(mayInterruptIfRunning);
	}
}
