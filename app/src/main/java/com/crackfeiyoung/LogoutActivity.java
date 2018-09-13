package com.crackfeiyoung;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.feiyoung.NetLogin;
import com.server_auth.AuthServer;
import com.server_auth.HttpClientHelper;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoutActivity extends AppCompatActivity {
    private String mLogoutUrl;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLogoutUrl = getIntent().getStringExtra("logoutUrl");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doLogout(view);
            }
        });
        getPreferences();
        checkUpgrade();
        sendInfoToAuth();
    }
    private void doLogout(final View view){
        new HttpClientHelper(new HttpClientHelper.CustomDoInBackground() {
            @Override
            public String getResult() {
                if (NetLogin.doLogout(mLogoutUrl)) {
                    return "success";
                } else {
                    return null; // fail
                }
            }
        }).setSuccessCallback(new HttpClientHelper.HttpCallback() {
            @Override
            public void doCallback(String result) {
                if (mDialog == null || !mDialog.isShowing()) finish(); // if mDialog not showed
            }
        }).setErrorCallback(new HttpClientHelper.HttpCallback() {
            @Override
            public void doCallback(String result) {
                if (view != null){
                    Snackbar.make(view, "登出失败，点我或后退关闭本页", Snackbar.LENGTH_LONG)
                            .setAction("关闭", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).show();
                }
            }
        }).doTask();
    }
    private void getPreferences() {
        // read info from Extra param
        String clientip = null;
        Matcher matcher= Pattern.compile("wlanuserip=([^&]+)").matcher(mLogoutUrl);
        if (matcher.find()){
            clientip = matcher.group(1);
        }
        // config the ip, and get the ip form preferences
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        TextView tv = findViewById(R.id.ip_addr);
        if (clientip == null) {
            clientip = preferences.getString("clientip", "无历史ip");
            clientip = "网络畅通，无需拨号，历史ip：".concat(clientip);
            mLogoutUrl = preferences.getString("logoutUrl", null);
            tv.setText(clientip);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("clientip", clientip);
            editor.putString("logoutUrl", mLogoutUrl);
            editor.apply();
            tv.setText(clientip);
        }
    }
    // check updating
    private void checkUpgrade(){
        HashMap<String, String> params = new HashMap<>();
        params.put("imei", "apk");
        new HttpClientHelper().setUrl(AuthServer.AUTH_HOST)
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        if (!result.equals("/PoYoung"+AuthServer.sVersion +".apk")) {
                            AuthServer.sApkName = result;
                            mDialog = new AlertDialog.Builder(LogoutActivity.this).setTitle("新版本")
                                    .setMessage("破样发布了新的版本，请使用其他网络环境更新")
                                    .setCancelable(false)
                                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Uri uri = Uri.parse(AuthServer.FILE_HOST +AuthServer.sApkName);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("偏不更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).create();
                            mDialog.show();
                            LogoutActivity.this.doLogout(null);
                        } else {
                            checkAuthorization();
                        }
                    }
                })
                .setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        mDialog = new AlertDialog.Builder(LogoutActivity.this).setTitle("出错")
                                .setMessage("连接更新服务器失败")//设置显示的内容
                                .setCancelable(false)
                                .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        mDialog.show();
                        LogoutActivity.this.doLogout(null);
                    }
                }).doTask();
    }
    // check authorization
    private void checkAuthorization() {
        HashMap<String, String> params = this.getImei();
        new HttpClientHelper().setUrl(AuthServer.AUTH_HOST)
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        if (result.isEmpty()){
                            mDialog = new AlertDialog.Builder(LogoutActivity.this).setTitle("出错")
                                    .setMessage("你的设备没有被授权")//设置显示的内容
                                    .setCancelable(false)
                                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .create();
                            mDialog.show();
                            LogoutActivity.this.doLogout(null);
                        }
                    }
                }).setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        mDialog = new AlertDialog.Builder(LogoutActivity.this).setTitle("出错")
                                .setMessage("连接验证服务器失败")//设置显示的内容
                                .setCancelable(false)
                                .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        mDialog.show();
                        LogoutActivity.this.doLogout(null);
                    }
                }).doTask();
    }
    // send login info
    private void sendInfoToAuth(){
        HashMap<String, String> params = this.getImei();
        params.put("ip", mLogoutUrl);
        new HttpClientHelper().setUrl(AuthServer.RECORDER_HOST)
                .setMethod(HttpClientHelper.POST)
                .setParams(params)
                .doTask();
    }
    // get device id
    private HashMap<String, String> getImei() {
        HashMap<String, String> params = new HashMap<>();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                params.put("imei", tel.getDeviceId());
            } else {
                params.put("imei", null);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
            params.put("imei", null);
        }
        return params;
    }
}
