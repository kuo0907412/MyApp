package com.kuo.myapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.kuo.myapp.Fragment.GsensorFragment;
import com.kuo.myapp.Fragment.HomeFragment;
import com.kuo.myapp.Fragment.SpeahTextFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE = 999;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment home = new HomeFragment();
                    home.disableTransitionAnimation();
                    ft.replace(R.id.fragment_base,home);
                    ft.commit();
                    return true;
                case R.id.navigation_speah_text:
                    SpeahTextFragment speach = new SpeahTextFragment();
                    speach.disableTransitionAnimation();
                    ft.replace(R.id.fragment_base,speach);
                    ft.commit();
                    return true;
                case R.id.navigation_gsensor:
                    GsensorFragment gsensor = new GsensorFragment();
                    gsensor.enableTransitionAnimation();
                    ft.replace(R.id.fragment_base,gsensor);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAllPermissions(this, REQUEST_CODE);
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment home = new HomeFragment();
        home.disableTransitionAnimation();
        ft.replace(R.id.fragment_base,home);
        ft.commit();
    }

    public ArrayList<PermissionInfo> getSettingPermissions(Context context){
        ArrayList<PermissionInfo> list = new ArrayList<PermissionInfo>();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if(packageInfo.requestedPermissions != null){
                for(String permission : packageInfo.requestedPermissions){
                    list.add(context.getPackageManager().getPermissionInfo(permission, PackageManager.GET_META_DATA));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean hasSelfPermission(Context context, String permission) {
        if(Build.VERSION.SDK_INT < 23) return true;
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
    public void requestAllPermissions(Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> requestPermissionNames = new ArrayList<String>();
            ArrayList<PermissionInfo> permissions = getSettingPermissions(activity);
            for(int i = 0;i < permissions.size();++i){
                PermissionInfo permission = permissions.get(i);
                if(permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS && !hasSelfPermission(activity, permission.name)){
                    requestPermissionNames.add(permission.name);
                } else if(permission.name.equals("android.permission.RECORD_AUDIO") &&
                        permission.protectionLevel != PermissionInfo.PROTECTION_DANGEROUS && !hasSelfPermission(activity, permission.name)){
                    requestPermissionNames.add(permission.name);
                }
            }
            if(!requestPermissionNames.isEmpty()) {
                activity.requestPermissions(requestPermissionNames.toArray(new String[0]), requestCode);
            } else {
            }
        }
    }
    public boolean existConfirmPermissions(Activity activity){
        if(Build.VERSION.SDK_INT >= 23) {
            ArrayList<PermissionInfo> permissions = getSettingPermissions(activity);
            boolean isRequestPermission = false;
            for(PermissionInfo permission : permissions){
                if(permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS && !hasSelfPermission(activity, permission.name)){
                    isRequestPermission = true;
                    break;
                }
            }
            return isRequestPermission;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != REQUEST_CODE)
            return;
        if(!existConfirmPermissions(this)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if(!showPermissionAlert()) {
                        requestAllPermissions(this, REQUEST_CODE);
                    }
                }
            }
        } else {
            requestAllPermissions(this, REQUEST_CODE);
        }
    }

    private boolean showPermissionAlert(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                    && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String message = "";
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                    message = "「許可」からすべての項目にチェックを入れてください。";
                } else {
                    message = "「権限」からすべての項目にチェックを入れてください。";
                }
                builder.setTitle("権限の許可が必要です。")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null); //Fragmentの場合はgetContext().getPackageName()
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                builder.show();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
