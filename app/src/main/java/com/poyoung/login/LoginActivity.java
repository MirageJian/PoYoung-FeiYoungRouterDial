package com.poyoung.login;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.poyoung.R;
import com.poyoung.TabLayoutAdapter;
import com.poyoung.dummy.DummyContent;
import com.poyoung.logout.LogoutActivity;
import com.poyoung.logout.LogoutUrlRecoder;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements LoginTab2Fragment.OnListFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fragment[] fragmentList = new Fragment[] {new LoginTab1Fragment(), new LoginTab2Fragment(), new LoginTab3Fragment()};

        String[] list_Title = new String[] {"拨号", "已连接", "帮助"};
        ViewPager viewPager = findViewById(R.id.login_viewpager);
        TabLayout tableLayout = findViewById(R.id.login_tabLayout);
        viewPager.setAdapter(new TabLayoutAdapter(getSupportFragmentManager(),LoginActivity.this,fragmentList,list_Title));
        tableLayout.setupWithViewPager(viewPager); //此方法就是让tablayout和ViewPager联动
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(HashMap<String, String> item) {

    }
}

