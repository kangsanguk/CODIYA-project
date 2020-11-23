package com.sample.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter2 extends FragmentPagerAdapter {

    public ViewPagerAdapter2(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FragInformation1.newInstance();
            case 1:
                return FragInformation2.newinstance();
            case 2:
                return FragInformation3.newinstance();
            case 3:
                return FragInformation4.newinstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "확진자 현황";
            case 1:
                return "코로나란?";
            case 2:
                return "대응지침";
            case 3:
                return "단계별 대응지침";
            default:
                return null;
        }
    }
}
