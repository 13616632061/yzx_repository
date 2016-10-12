package com.ms.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.ks.BaseFragment;
import com.ms.ks.R;
import com.ms.ks.ShopActivity;

public class ManagerFragment extends BaseFragment {
    private ShopActivity mainAct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (ShopActivity) getActivity();
    }

    public static ManagerFragment newInstance() {
        ManagerFragment f = new ManagerFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mainAct.updateView("home");

        View view = inflater.inflate(R.layout.fragment_manager, container, false);

        return view;
    }
}

