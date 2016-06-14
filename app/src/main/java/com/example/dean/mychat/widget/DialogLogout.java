package com.example.dean.mychat.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.example.dean.mychat.R;


public class DialogLogout extends Dialog {

	public DialogLogout(Context context) {
		super(context, R.style.dialog_logout);
		setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.BOTTOM;
		setContentView(R.layout.dialog_logout);

		findViewById(R.id.dialog_btn_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
	}

	public void setClickLogoutListener(View.OnClickListener listener) {
		findViewById(R.id.dialog_btn_logout).setOnClickListener(listener);
	}

}
