package com.moomen.news.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.moomen.news.R;
import com.moomen.news.adapter.SectionsPagerAdapterAdmin;

import com.moomen.news.ui.Activity.CreateNewAdmin;


public class DashboardFragmentAdmin extends Fragment {

    private View view;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard_admin, container, false);

        SectionsPagerAdapterAdmin sectionsPagerAdapterAdmin= new SectionsPagerAdapterAdmin(getContext(), getFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapterAdmin);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        ImageButton createButton = view.findViewById(R.id.button_create_new_admin_id);
        TextView creteNewAdmin = view.findViewById(R.id.text_view_create_new_admin_id);
        creteNewAdmin.setVisibility(View.GONE);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (creteNewAdmin.getVisibility() == View.GONE)
                    creteNewAdmin.setVisibility(View.VISIBLE);
                else
                    creteNewAdmin.setVisibility(View.GONE);
            }
        });
        creteNewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNewAdmin.class));
            }
        });
        return view;
    }
}