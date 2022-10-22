package com.moomen.news.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moomen.news.R;
import com.moomen.news.ui.fragment.AdminFragmentAdmin;
import com.moomen.news.ui.fragment.CompanyFragmentAdmin;
import com.moomen.news.ui.fragment.LoginFragment;
import com.moomen.news.ui.fragment.RegisterFragment;
import com.moomen.news.ui.fragment.UserFragmentAdmin;


public class SectionsPagerAdapterAdmin extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_3_admin, R.string.tab_text_2_admin,R.string.tab_text_1_admin};
    private final Context mContext;

    public SectionsPagerAdapterAdmin(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AdminFragmentAdmin();
                break;
            case 1:
                fragment = new CompanyFragmentAdmin();
                break;
            case 2:
                fragment = new UserFragmentAdmin();
                break;
        }
        assert fragment != null;
        return fragment;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}