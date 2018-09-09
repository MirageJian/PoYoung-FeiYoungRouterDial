package com.crackfeiyoung;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.feiyoung.FeiyoungServer;
import com.feiyoung.NetLogin;
import com.server_auth.HttpClientHelper;

public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Switch mSwitch;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mUsernameView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.email_sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        findViewById(R.id.btn_recheck).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSwitch = findViewById(R.id.switch_mode);
        getInfoFromPreferences();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Set Preferences
        FeiyoungServer.useMobile = mSwitch.isChecked();
        setPreferences();
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            new HttpClientHelper(new HttpClientHelper.CustomDoInBackground() {
                @Override
                public String getResult() {
                    String[] info = {username, password};
                    return new NetLogin().doLogin(info);
                }
            }).setSuccessCallback(new HttpClientHelper.HttpCallback() {
                @Override
                public void doCallback(String result) {
                    showProgress(false);
                    Intent intent = new Intent(LoginActivity.this, LogoutActivity.class);
                    intent.putExtra("logoutUrl", result);
                    startActivity(intent);
                    // finish();
                }
            }).setErrorCallback(new HttpClientHelper.HttpCallback() {
                @Override
                public void doCallback(String result) {
                    showProgress(false);
                    Snackbar.make(mLoginFormView, "拨号失败" + NetLogin.errMsg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
//                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                    mPasswordView.requestFocus();
                }
            }).setCancelCallback(new HttpClientHelper.HttpCallback() {
                @Override
                public void doCallback(String result) {
                    showProgress(false);
                }
            }).doTask();
        }
    }

    private boolean isEmailValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 4;// email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void getInfoFromPreferences() {
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPreferences.edit();
        // preference param init
        String username = mPreferences.getString("username", "");
        mUsernameView.setText(username);
        mPasswordView.setText(mPreferences.getString("password", ""));
        // 判断启动参数是否为空
        if (username.isEmpty()) {
            mUsernameView.setText(getIntent().getStringExtra("username"));
            // 设置保存账号参数
            editor.putString("username", getIntent().getStringExtra("username"));
            editor.apply();
        }
        mSwitch.setChecked(mPreferences.getBoolean("phoneMode",true));
    }

    private void setPreferences() {
        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString();
        boolean phoneMode = mSwitch.isChecked();
        // 设置保存密码参数
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("password", password);
        editor.putBoolean("phoneMode", phoneMode);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}

