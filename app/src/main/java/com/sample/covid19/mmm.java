package com.sample.covid19;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class mmm extends AppCompatActivity {

    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mmm);

        ViewPager viewPager = findViewById(R.id.viewPager3);
        fragmentPagerAdapter = new ViewPagerAdapter3(getSupportFragmentManager() );

        TabLayout tabLayout = findViewById(R.id.tab_layout3);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
