package io.github.jamesdonoh.halfpricesushi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

class OutletFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_FRAGMENTS = 2;

    // TODO replace with strings resources
    private final String[] pageTitles = new String[] { "List", "Map" };

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

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

    /**
     * Allow retrieval of already-created fragments in a lifecycle-safe way
     * See: http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager/15261142
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
