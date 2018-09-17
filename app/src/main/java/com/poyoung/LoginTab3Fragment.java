package com.poyoung;

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
        mRouterIp = view.findViewById(R.id.login_fragment_tab1_router_ip);
        mRouterPassword = view.findViewById(R.id.login_fragment_tab1_router_password);
        view.findViewById(R.id.login_fragment_tab1_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRealise0();
            }
        });
        getPreferences();
        return view;
    }

    private void attemptRealise0() {
        setPreferences();
        RouterInfo.FW300R.sHost = mRouterIp.getText().toString();
        String password = "admin:" + mRouterPassword.getText().toString();
        password = "Basic%20" + Base64.encodeToString(password.getBytes(), Base64.NO_WRAP).replace("=", "%3D");
        HashMap<String, String> params = new HashMap<>();
        params.put("ReleaseIp", "释 放");
        params.put("wan", "1");
        // new Cookie
        final String cookie = "Authorization="+ password + "; ChgPwdSubTag=";
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Cookie", cookie);
        properties.put("Referer", RouterInfo.FW300R.sHost);
        properties.put("Upgrade-Insecure-Requests", "1");
        new HttpClientHelper().setUrl(RouterInfo.FW300R.getsReleaseHost())
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setRequestProperty(properties)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        attemptRenew0();
                        System.out.println(result);
                        System.out.println(RouterInfo.FW300R.getsReleaseHost());
                        System.out.println(cookie);
                    }
                })
                .setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {

                    }
                }).doTask();
    }
    private void attemptRenew0(){
        RouterInfo.FW300R.sHost = mRouterIp.getText().toString();
        String password = "admin:" + mRouterPassword.getText().toString();
        password = "Basic%20" + Base64.encodeToString(password.getBytes(), Base64.NO_WRAP).replace("=", "%3D");
        HashMap<String, String> params = new HashMap<>();
        params.put("ReleaseIp", "释 放");
        params.put("wan", "1");
        // new Cookie
        final String cookie = "Authorization="+ password + "; ChgPwdSubTag=";
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Cookie", cookie);
        properties.put("Referer", RouterInfo.FW300R.sHost);
        properties.put("Upgrade-Insecure-Requests", "1");
        properties.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        properties.put("Accept-Encoding", "gzip, deflate");
        properties.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
        properties.put("Connection", "keep-alive");
        properties.put("Host", RouterInfo.FW300R.sHost);
        new HttpClientHelper().setUrl(RouterInfo.FW300R.getsRenewHost())
                .setMethod(HttpClientHelper.GET)
                .setParams(params)
                .setRequestProperty(properties)
                .setSuccessCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {
                        if(getView() != null) {
                            Snackbar.make(getView(), "释放更新成功", 5000).show();
                        }
                        System.out.println(result);
                    }
                })
                .setErrorCallback(new HttpClientHelper.HttpCallback() {
                    @Override
                    public void doCallback(String result) {

                    }
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
