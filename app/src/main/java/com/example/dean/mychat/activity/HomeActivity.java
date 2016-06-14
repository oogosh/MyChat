package com.example.dean.mychat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.example.dean.mychat.R;
import com.example.dean.mychat.fragment.ChatFra;
import com.example.dean.mychat.fragment.ContactFra;
import com.example.dean.mychat.fragment.DiscoverFra;
import com.example.dean.mychat.fragment.MeFra;
import com.example.dean.mychat.widget.TabIndicatorView;

public class HomeActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
    private static final String TAB_CHAT = "chat";
    private static final String TAB_CONTACT = "contact";
    private static final String TAB_DISCOVER = "discover";
    private static final String TAB_ME = "me";

    private FragmentTabHost tabhost;

    private TabIndicatorView chatIndicator;
    private TabIndicatorView contactIndicator;
    private TabIndicatorView discoverIndicator;
    private TabIndicatorView meIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // 1. 初始化TabHost
        tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabhost.setup(this, getSupportFragmentManager(),
                android.R.id.tabcontent);

        // 2. 新建TabSpec
        TabSpec spec = tabhost.newTabSpec(TAB_CHAT);
        chatIndicator = new TabIndicatorView(this);
        chatIndicator.setTabTitle("消息");
        chatIndicator.setTabIcon(R.mipmap.tab_icon_chat_normal,
                R.mipmap.tab_icon_chat_focus);
        spec.setIndicator(chatIndicator);

        // 3. 添加TabSpec
        tabhost.addTab(spec, ChatFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_CONTACT);
        contactIndicator = new TabIndicatorView(this);
        contactIndicator.setTabIcon(R.mipmap.tab_icon_contact_normal,
                R.mipmap.tab_icon_contact_focus);
        contactIndicator.setTabTitle("通讯录");
        contactIndicator.setTabUnreadCount(10);
        spec.setIndicator(contactIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, ContactFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_DISCOVER);
        discoverIndicator = new TabIndicatorView(this);
        discoverIndicator.setTabIcon(R.mipmap.tab_icon_discover_normal,
                R.mipmap.tab_icon_discover_focus);
        discoverIndicator.setTabTitle("发现");
        discoverIndicator.setTabUnreadCount(10);
        spec.setIndicator(discoverIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, DiscoverFra.class, null);

        // 2. 新建TabSpec
        spec = tabhost.newTabSpec(TAB_ME);
        meIndicator = new TabIndicatorView(this);
        meIndicator.setTabIcon(R.mipmap.tab_icon_me_normal,
                R.mipmap.tab_icon_me_focus);
        meIndicator.setTabTitle("我");
        meIndicator.setTabUnreadCount(10);
        spec.setIndicator(meIndicator);
        // 3. 添加TabSpec
        tabhost.addTab(spec, MeFra.class, null);


        // 初始化 tab选中
        tabhost.setCurrentTabByTag(TAB_CHAT);
        chatIndicator.setTabSelected(true);

        // 设置tab切换的监听
        tabhost.setOnTabChangedListener(this);

    }

    @Override
    public void onTabChanged(String tag) {
        chatIndicator.setTabSelected(false);
        contactIndicator.setTabSelected(false);
        discoverIndicator.setTabSelected(false);
        meIndicator.setTabSelected(false);

        if (TAB_CHAT.equals(tag)) {
            chatIndicator.setTabSelected(true);
        } else if (TAB_CONTACT.equals(tag)) {
            contactIndicator.setTabSelected(true);
        } else if (TAB_DISCOVER.equals(tag)) {
            discoverIndicator.setTabSelected(true);
        } else if (TAB_ME.equals(tag)) {
            meIndicator.setTabSelected(true);
        }
    }
}
