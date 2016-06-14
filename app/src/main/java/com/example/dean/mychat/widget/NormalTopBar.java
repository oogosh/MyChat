package com.example.dean.mychat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dean.mychat.R;

/**
 * Created by Dean on 2016/5/28.
 */

public class NormalTopBar extends RelativeLayout {
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvAction;

    public NormalTopBar(Context context) {
        this(context, null);
    }

    public NormalTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.bar_normal, this);
        ivBack = (ImageView) findViewById(R.id.bar_back);
        tvTitle = (TextView) findViewById(R.id.bar_title);
        tvAction = (TextView) findViewById(R.id.bar_action);
    }

    public void setBackVisibility(boolean show) {
        ivBack.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void setOnBackListener(OnClickListener listener) {
        ivBack.setOnClickListener(listener);
    }

    public void setOnActionListener(OnClickListener listener) {
        tvAction.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTitle(int titleId) {
        tvTitle.setText(titleId);
    }

    public void setActionText(String text) {
        tvAction.setText(text);
    }

    public void setActionText(int textId) {
        tvAction.setText(textId);
    }

    public void setActionTextVisibility(boolean visibility) {
        tvAction.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public ImageView getBackView() {
        return ivBack;
    }

    public TextView getTitleView() {
        return tvTitle;
    }

    public View getActionView() {
        return tvAction;
    }
}

