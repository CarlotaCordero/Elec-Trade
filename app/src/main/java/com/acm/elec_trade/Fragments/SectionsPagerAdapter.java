package com.acm.elec_trade.Fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.acm.elec_trade.Main;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(Main mainPage, @NonNull FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Home_fragment();
            case 1:
                return new Chat_fragment();
            case 2:
                return new Cart_fragment();
            case 3:
                return new Profile_fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
