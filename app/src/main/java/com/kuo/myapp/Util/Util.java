package com.kuo.myapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;

import com.kuo.myapp.Adapter.GsensorLogAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class Util {

    public static Point getDisplaySize(WindowManager wm){
        Point size = new Point();
        Display display = wm.getDefaultDisplay();
        display.getSize(size);
        return size;
    }

    public static void debugFileOutput(String s,Context context) {
        File directory = new File(Environment.getExternalStorageDirectory() , "TestFolder");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File outputFile = new File(directory, "debugMsg.txt");
        FileOutputStream out;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);
        Date date = new Date(System.currentTimeMillis());
        String latlng = df.format(date) + s + "\n";
        try {
            out = new FileOutputStream(outputFile,true);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.append(latlng);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean needDeleteFolder(File file) {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024) {
            // 100Mを超えたら削除
            if((int)(size / 1024) >= 2)
                return true;
        } else {
            return false;
        }
        return false;
    }

    public static long getFolderSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public static int getVersionCode(Context context){
        int versionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  versionCode;
    }
    public static String getVersionName(Context context){
        String versionName = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  versionName;
    }
    public static float getDiffValue(float tmpValue,float sensorValue){
        //　テストため
        float diffValue = 0;
        diffValue = Math.abs(tmpValue - sensorValue);
        return diffValue;
    }

}
