package io.github.jamesdonoh.halfpricesushi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class OutletFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_FRAGMENTS = 2;

    // TODO replace with strings resources
    private final String[] pageTitles = new String[] { "List", "Map" };

    OutletFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OutletListFragment();
            case 1:
                return new OutletMapFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
