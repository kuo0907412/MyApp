package com.kuo.myapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private boolean isForeground = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * アプリが前面にいるかどうかを取得します.
     * @return Foregroundにいたら<code>true</code>,backgroundにいたら<code>false</code>をかえします
     */
    public boolean isForeground() {
        return isForeground;
    }

}