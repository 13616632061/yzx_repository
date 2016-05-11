package com.ms.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.global.Global;
import com.ms.ks.BaseFragment;
import com.ms.ks.R;
import com.ms.ks.ShopActivity;
import com.ms.util.SysUtils;

public class ReportFragment extends BaseFragment{
    private ShopActivity mainAct;
    private TextView id_text_1, id_text_2, id_text_3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (ShopActivity) getActivity();
    }

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mainAct.updateView("home");

        View view = inflater.inflate(R.layout.fragment_report, container, false);


        //今日无效订单
        View set_item_1 = (View) view.findViewById(R.id.set_item_1);
//        ImageView idx_1 = (ImageView) set_item_1.findViewById(R.id.ll_set_idx);
//        idx_1.setVisibility(View.GONE);

        id_text_1 = (TextView) set_item_1.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_1, Global.SET_CELLUP, "今日无效订单", R.drawable.icon_item_1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //昨日营业额
        View set_item_2 = (View) view.findViewById(R.id.set_item_2);
//        ImageView idx_2 = (ImageView) set_item_2.findViewById(R.id.ll_set_idx);
//        idx_2.setVisibility(View.GONE);

        id_text_2 = (TextView) set_item_2.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_2, Global.SET_CELLWHITE, "昨日营业额", R.drawable.icon_item_2, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //本月营业额
        View set_item_3 = (View) view.findViewById(R.id.set_item_3);
//        ImageView idx_3 = (ImageView) set_item_3.findViewById(R.id.ll_set_idx);
//        idx_3.setVisibility(View.GONE);

        id_text_3 = (TextView) set_item_3.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_3, Global.SET_SINGLE_LINE, "本月营业额", R.drawable.icon_item_3, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //店铺公告
        View set_item_4 = (View) view.findViewById(R.id.set_item_4);
        SysUtils.setLine(set_item_4, Global.SET_TWO_LINE, "店铺公告", R.drawable.icon_item_4, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        updateView();

        return view;
    }

    private void updateView() {
        id_text_1.setText("0");
        id_text_2.setText("￥461");
        id_text_3.setText("￥1011");
    }
}

