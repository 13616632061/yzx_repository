package com.ms.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.ks.R;
import com.ms.view.BaseLazyLoadFragment;

public class OrderFragment extends BaseLazyLoadFragment{
    private boolean isPrepared; //标识是否初始化完成

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static OrderFragment newInstance() {
        OrderFragment f = new OrderFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        isPrepared = true;
        lazyLoad();

        return view;
    }


    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
    }

}

