package com.huytran.goodlife.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.huytran.goodlife.fragment.IntroFragment1;
import com.huytran.goodlife.fragment.IntroFragment2;
import com.huytran.goodlife.fragment.IntroFragment3;
import com.huytran.goodlife.fragment.IntroFragment4;
import com.huytran.goodlife.fragment.IntroFragment5;

public class IntroPagerAdapter extends FragmentPagerAdapter {

    public IntroPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new IntroFragment1();
            case 1:
                return new IntroFragment2();
            case 2:
                return new IntroFragment3();
            case 3:
                return new IntroFragment4();
            case 4:
                return new IntroFragment5();
            default:
                return new IntroFragment1();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}

