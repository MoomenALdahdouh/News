package com.moomen.news.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.moomen.news.viewModel.PageViewModelLR;
import com.moomen.news.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragmentLR extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModelLR pageViewModelLR;

    public static PlaceholderFragmentLR newInstance(int index) {
        PlaceholderFragmentLR fragment = new PlaceholderFragmentLR();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModelLR = new ViewModelProvider(this).get(PageViewModelLR.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModelLR.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_login_register, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModelLR.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}