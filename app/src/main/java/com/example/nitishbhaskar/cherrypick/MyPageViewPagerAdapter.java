package com.example.nitishbhaskar.cherrypick;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Nitish on 4/27/2016.
 */
public class MyPageViewPagerAdapter extends FragmentPagerAdapter {
    int count = 2;
    public MyPageViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return new BarCodeFragment();
        }
        else{
            return new DrawingFragment();
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name;
        if(position==0){
            name = "Bar Code Scanner";
        }
        else{
            name = "Custom Drawing activity";
        }

        return name.toUpperCase(l);
    }



}
