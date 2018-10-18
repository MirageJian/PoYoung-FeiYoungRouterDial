package com.poyoung.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.poyoung.R;
import com.server_auth.HttpClientHelper;
import com.server_auth.RouterInfo;
import java.util.HashMap;

public class LoginTab3Fragment extends Fragment {
    private EditText mRouterIp;
    private EditText mRouterPassword;

    public LoginTab3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab3, container, false);
        mRouterIp = view.findViewById(R.id.login_fragment_tab3_router_ip);
        mRouterPassword = view.findViewById(R.id.login_fragment_tab3_router_password);
        view.findViewById(R.id.login_fragment_tab3_realise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRealise0();
            }
        });
        view.findViewById(R.id.login_fragment_tab3_renew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRenew0();
            }
        });
        getPreferences();
        return view;
    }

    private void attemptRealise0() {
//        设置保存信息
        setPreferences();
        RouterInfo.WR740N.sHost = mRouterIp.getText().toString();
        String password =  mRouterPassword.getText().toString();
        password = "Basic " + Base64.encodeToString(("admin:" +password).getBytes(), Base64.NO_WRAP); // %20.replace("=", "%3D");
        HashMap<String, String> params = new HashMap<>();
        params.put("ReleaseIp", "释 放");
        params.put("wan", "1");
        final HashMap<String, String> properties = new HashMap<>();
//        new Cookie
//        final String cookie = "Authorization="+ password + "; ChgPwdSubTag=";
//        properties.put("Cookie", cookie);
        properties.put("Authorization", password);
        properties.put("Referer", RouterInfo.WR740N.sHost);
        properties.put("Upgrade-Insecure-Requests", "1");
        properties.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        properties.put("Accept-Encoding", "gzip, deflate");
        properties.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        properties.put("Connection", "keep-alive");
        properties.put("Host", RouterInfo.WR740N.sHost);
        new HttpClientHelper().setUrl(RouterInfo.WR740N.getsReleaseHost())
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setRequestProperty(properties)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        if(getView() != null) {
                            Snackbar.make(getView(), "释放成功", 5000).show();
                        }
                    }
                })
                .setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {}
                }).doTask();
    }
    private void attemptRenew0(){
        RouterInfo.WR740N.sHost = mRouterIp.getText().toString();
        String password =  mRouterPassword.getText().toString();
        password = "Basic " + Base64.encodeToString(("admin:" +password).getBytes(), Base64.NO_WRAP); // %20.replace("=", "%3D");
        HashMap<String, String> params = new HashMap<>();
        params.put("RenewIp", "更 新");
        params.put("wan", "1");
        final HashMap<String, String> properties = new HashMap<>();
//        new Cookie
//        final String cookie = "Authorization="+ password + "; ChgPwdSubTag=";
//        properties.put("Cookie", cookie);
        properties.put("Authorization", password);
        properties.put("Referer", RouterInfo.WR740N.sHost);
        properties.put("Upgrade-Insecure-Requests", "1");
        properties.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        properties.put("Accept-Encoding", "gzip, deflate");
        properties.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        properties.put("Connection", "keep-alive");
        properties.put("Host", RouterInfo.WR740N.sHost);

        new HttpClientHelper().setUrl(RouterInfo.WR740N.getsRenewHost())
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setRequestProperty(properties)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        if(getView() != null) {
                            Snackbar.make(getView(), "更新响应成功，正在更新，请等待几秒", 5000).show();
                        }
                        System.out.println(result);
                    }
                })
                .setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {}
                }).doTask();
    }
    private void getPreferences() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        mRouterIp.setText(preferences.getString("router_ip", ""));
        mRouterPassword.setText(preferences.getString("router_password", ""));
    }

    private void setPreferences() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("router_ip", mRouterIp.getText().toString());
        editor.putString("router_password", mRouterPassword.getText().toString());
        editor.apply();
    }
}
