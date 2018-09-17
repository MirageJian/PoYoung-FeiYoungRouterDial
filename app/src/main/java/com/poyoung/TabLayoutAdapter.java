package com.poyoung;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutAdapter extends FragmentPagerAdapter {

    private Context context;
    private Fragment[] fragmentList;
    private String[] list_Title;
    public TabLayoutAdapter(FragmentManager fm, Context context, Fragment[] fragmentList, String[] list_Title) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.list_Title = list_Title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList[position];
    }

    @Override
    public int getCount() {
        return list_Title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list_Title[position];
    }
}
