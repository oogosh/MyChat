package com.example.dean.mychat;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.example.dean.mychat.db.AccountDao;
import com.example.dean.mychat.domain.Account;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by dean on 16-6-2.
 */

public class ChatApplication extends Application {
    private List<Activity> activitys = new LinkedList<Activity>();
    private List<Service> services = new LinkedList<Service>();
    private Account account;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("ChatApplication", "init");
    }

    public void addActivity(Activity activity) {
        activitys.add(activity);
    }

    public void removeActivity(Activity activity) {
        activitys.remove(activity);
    }

    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(Service service) {
        services.remove(service);
    }

    public void closeApplication() {
        closeActivitys();
        closeServices();
    }

    private void closeActivitys() {
        ListIterator<Activity> iterator = activitys.listIterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    private void closeServices() {
        ListIterator<Service> iterator = services.listIterator();
        while (iterator.hasNext()) {
            Service service = iterator.next();
            if (service != null) {
                stopService(new Intent(this, service.getClass()));
            }
        }
    }

    public Account getCurrentAccount() {
        if (account == null) {
            AccountDao dao = new AccountDao(this);
            account = dao.getCurrentAccount();
        }
        return account;
    }

}
