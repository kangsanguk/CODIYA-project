package com.sample.covid19;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class xxx extends AppCompatActivity {

    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xxx);

        ViewPager viewPager = findViewById(R.id.viewPager2);
        fragmentPagerAdapter = new ViewPagerAdapter2(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.tab_layout2);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
