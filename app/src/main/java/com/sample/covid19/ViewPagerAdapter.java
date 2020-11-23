package com.sample.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FragMonday.newInstance();
            case 1:
                return FragTuseday.newInstance();
            case 2:
                return FragWednesday.newInstance();
            case 3:
                return FragJtbc.newInstance();
            case 4:
                return FragChannelA.newInstance();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "SBS";
            case 1:
                return "KBS";
            case 2:
                return "MBC";
            case 3:
                return "JTBC";
            case 4:
                return "채널A";
            default:
                return null;
        }
    }
}
