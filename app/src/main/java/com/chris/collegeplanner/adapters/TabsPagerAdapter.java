package com.chris.collegeplanner.adapters;

/**
 * Created by Chris on 07/02/2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chris.collegeplanner.fragments.ProjectDetailsFragment;
import com.chris.collegeplanner.fragments.ProjectNotesFragment;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new ProjectDetailsFragment();
            case 1:
                // Games fragment activity
                return new ProjectNotesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
