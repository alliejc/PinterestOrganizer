package com.allie.pinterestorganizer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;


public class Pager extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;
        FragmentManager fragmentManager;

        //Constructor to the class
        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount= tabCount;
            this.fragmentManager = fm;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    AllPinsFragment pinsFragment = new AllPinsFragment();
                    return pinsFragment;
                case 1:
                    SavedPinsFragment savedPinsFragment = new SavedPinsFragment();
                    return savedPinsFragment;
                default:
                    return null;
            }
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }

