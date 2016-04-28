package com.example.nitishbhaskar.cherrypick;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

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
        return BuyFragment.newInstance(R.id.buyPageFragment);
    }

    @Override
    public int getCount() {
        return count;
    }
}
