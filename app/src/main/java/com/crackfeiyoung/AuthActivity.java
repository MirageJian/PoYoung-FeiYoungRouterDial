package com.crackfeiyoung;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.app.AlertDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.telephony.TelephonyManager;
import android.widget.EditText;

import com.server_auth.AuthServer;

import com.server_auth.HttpClientHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.DriverManager.println;

public class AuthActivity extends AppCompatActivity
{
    private View mProgress;//处理动画
    private Button mStart;//开始
    private View mView;
    private String imei;
    private Button mAuthButon;
    private SharedPreferences mPreferences;
    private String username;
    private EditText imei_text;
    private AlertDialog updateDialog;
    private static final String[] PERMISSION = new String[]{"android.permission.READ_PHONE_STATE"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        // change the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // control the android sdk > 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        else
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_auth);
        mView = findViewById(R.id.auth_view);
        mProgress = findViewById(R.id.pro_guide);
        imei_text = findViewById(R.id.imei_text);
        // 按钮初始化，设置监听
        mStart = findViewById(R.id.btn_start);
        mStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
                intent.putExtra("username", username);
                AuthActivity.this.startActivity(intent);
            }
        }); mAuthButon = findViewById(R.id.btn_auth);
        mAuthButon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showProcessBar(true);
                checkUpgrade();
            }
        });
        //设置参数初始化
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        if (mPreferences.getString("new_app", "").equals("true"))
        {
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            AuthActivity.this.startActivity(intent);
        }
    }

    private boolean islacksOfPermission(String permission)
    {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x12)
        {
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            try
            {
                imei = tel.getDeviceId();

            } catch (SecurityException ex)
            {
                ex.printStackTrace();
                imei = "null";
            }

            imei_text.setText(imei);
        }
        else
        {
            finish();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (islacksOfPermission(PERMISSION[0]))
        {
            ActivityCompat.requestPermissions(this, PERMISSION, 0x12);
        }
        else
        {
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            try
            {
                imei = tel.getDeviceId();

            } catch (SecurityException ex)
            {
                ex.printStackTrace();
                imei = "null";
            }

            imei_text.setText(imei);
        }
    }

    // 验证的背景方法
    // 修改请先看这里
    // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur/44309450
    private static class AuthAsync extends AsyncTask<Void,Void,String>
    {
        private WeakReference<AuthActivity> activityReference;

        // only retain a weak reference to the activity
        AuthAsync(AuthActivity context)
        {
            activityReference = new WeakReference<>(context);
        }
        String sendGet(String urlIn)
        {
            StringBuilder result = new StringBuilder();
            BufferedReader inData = null;//获取数据的变量
            try
            {
                URL url = new URL(urlIn);//设置url
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //打开链接
                connection.connect();
                //取得响应头
                Map<String, List<String>> map = connection.getHeaderFields();
                //设置读取文件的编码格式和读取文件
                inData = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String line = inData.readLine();//读取内容
                while (line != null)
                {
                    result.append(line);
                    line = inData.readLine();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                return "";
            } finally
            {
                try
                {
                    if (inData != null)
                    {
                        inData.close();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            return result.toString();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            AuthActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;
            String result;
            String param = "?imei=" + activity.imei;
            try
            {
                println(param);
                result = sendGet(AuthServer.AUTH_HOST + param);
                return result;
            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String success)
        {
            AuthActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (success == null)
            {
                activity.showProcessBar(false);
                activity.showStart(false);
                Snackbar.make(activity.mView, "服务器连接失败", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else if (success.isEmpty())
            {
                activity.showProcessBar(false);
                activity.showStart(false);
                Snackbar.make(activity.mView, "你的设备没有被授权", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                // 设置启动参数
                SharedPreferences.Editor editor = activity.mPreferences.edit();
                editor.putString("new_app", "false");
                editor.apply();
            }
            else
            {
                activity.showProcessBar(false);
                activity.showStart(true);
                // 设置启动参数
                activity.username = success;
                SharedPreferences.Editor editor = activity.mPreferences.edit();
                editor.putString("new_app", "true");
                editor.apply();
            }
        }

        // 若执行了取消方法，直到整个线程跑完才执行onCancelled
        @Override
        protected void onCancelled()
        {

        }
    }

    private void showStart(Boolean show)
    {
        mStart.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showProcessBar(Boolean show)
    {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mAuthButon.setEnabled(!show);
    }
    // check updating
    private void checkUpgrade()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("imei", "apk");
        new HttpClientHelper().setUrl(AuthServer.AUTH_HOST).setMethod(HttpClientHelper.GET).setParams(params).setSuccessCallback(new HttpClientHelper.HttpCallback()
        {
            @Override
            public void doCallback(String result)
            {
                if (!result.equals("/PoYoung" + AuthServer.version + ".apk"))
                {
                    showProcessBar(false);
                    showStart(false);
                    // 设置启动参数
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString("new_app", "false");
                    editor.apply();
                    AuthServer.apkName = result;
                    new AlertDialog.Builder(AuthActivity.this).setTitle("新版本").setMessage("破样发布了新的版本，请更新").setCancelable(false).setPositiveButton("好", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Uri uri = Uri.parse(AuthServer.FILE_HOST + AuthServer.apkName);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }).setNegativeButton("偏不更新", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                        }
                    }).create().show();
                }
                else
                {
                    new AuthAsync(AuthActivity.this).execute();
                }
            }
        }).setErrorCallback(new HttpClientHelper.HttpCallback()
        {
            @Override
            public void doCallback(String result)
            {
                showProcessBar(false);
                showStart(false);
                // 设置启动参数
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("new_app", "false");
                editor.apply();
                new AlertDialog.Builder(AuthActivity.this).setTitle("出错").setMessage("链接验证服务器失败")//设置显示的内容
                    .setCancelable(false).setPositiveButton("好", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                }).create().show();
            }
        }).doTask();
    }
}