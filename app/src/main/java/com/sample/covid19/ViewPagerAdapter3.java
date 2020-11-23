package com.sample.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter3 extends FragmentPagerAdapter {
    public ViewPagerAdapter3(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FragMask1.newinstance();
            case 1:
                return FragMask2.newinstance();
            case 2:
                return FragMask3.newinstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "마스크 효과";
            case 1:
                return "마스크 판매처";
            case 2:
                return "마스크 종류";
            default:
                return null;
        }
    }
}