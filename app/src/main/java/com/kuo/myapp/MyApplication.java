package com.kuo.myapp;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.kuo.myapp.Util.Util;


public class MyApplication extends Application {
    public static MyApplication instance;
    MyLifecycleHandler lifecycleHandler;
    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        lifecycleHandler = new MyLifecycleHandler();
        registerActivityLifecycleCallbacks(lifecycleHandler);
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        Util.debugFileOutput(e.toString(),getApplicationContext());
                        handleUncaughtException (thread, e);
                    }
                });
    }

    private void handleUncaughtException (Thread thread, Throwable e) {
        Toast.makeText(getApplicationContext(),"Application Down",Toast.LENGTH_SHORT).show();
        // The following shows what I'd like, though it won't work like this.
        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean isAppForeground(){
        return lifecycleHandler.isForeground();
    }


    public static MyApplication getInstance(){
        return instance;
    }
}
