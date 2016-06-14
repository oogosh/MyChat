package com.example.dean.mychat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dean.mychat.R;

/**
 * Created by Dean on 2016/5/28.
 */
public class MeFra extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_me, container, false);
        initView(view);
//        initEvent();


        return view;
    }

    private void initView(View view) {
    }
}
