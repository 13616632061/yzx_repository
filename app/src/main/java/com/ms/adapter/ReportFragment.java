package com.ms.adapter;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.global.Global;
import com.ms.ks.BaseFragment;
import com.ms.ks.R;
import com.ms.ks.ShopActivity;
import com.ms.util.CustomRequest;
import com.ms.util.SysUtils;

import org.json.JSONObject;

public class ReportFragment extends BaseFragment {
    private ShopActivity mainAct;
    private TextView id_text_1, id_text_2, id_text_3;
    private SwipeRefreshLayout refresh_header;
    private ListView lv_content;
    private TextView textView1, textView2;

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

        refresh_header = (SwipeRefreshLayout) view.findViewById(R.id.refresh_header);
        refresh_header.setColorSchemeResources(R.color.ptr_red, R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow);
        refresh_header.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
            }
        });

        lv_content = (ListView) view.findViewById(R.id.lv_content);

        View firstView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.report_seller, lv_content, false);
        lv_content.addHeaderView(firstView);
        lv_content.setAdapter(null);

        //今日无效订单
        View set_item_1 = (View) firstView.findViewById(R.id.set_item_1);
//        ImageView idx_1 = (ImageView) set_item_1.findViewById(R.id.ll_set_idx);
//        idx_1.setVisibility(View.GONE);

        id_text_1 = (TextView) set_item_1.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_1, Global.SET_CELLUP, "今日无效订单", R.drawable.icon_item_1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //昨日营业额
        View set_item_2 = (View) firstView.findViewById(R.id.set_item_2);
//        ImageView idx_2 = (ImageView) set_item_2.findViewById(R.id.ll_set_idx);
//        idx_2.setVisibility(View.GONE);

        id_text_2 = (TextView) set_item_2.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_2, Global.SET_CELLWHITE, "昨日营业额", R.drawable.icon_item_2, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //本月营业额
        View set_item_3 = (View) firstView.findViewById(R.id.set_item_3);
//        ImageView idx_3 = (ImageView) set_item_3.findViewById(R.id.ll_set_idx);
//        idx_3.setVisibility(View.GONE);

        id_text_3 = (TextView) set_item_3.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_3, Global.SET_SINGLE_LINE, "本月营业额", R.drawable.icon_item_3, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //店铺公告
        View set_item_4 = (View) firstView.findViewById(R.id.set_item_4);
        SysUtils.setLine(set_item_4, Global.SET_TWO_LINE, "店铺公告", R.drawable.icon_item_4, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        textView1 = (TextView) firstView.findViewById(R.id.textView1);
        textView2 = (TextView) firstView.findViewById(R.id.textView2);

        refresh_header.post(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                initView();
            }
        });

        return view;
    }
    private void setRefreshing(boolean refreshing) {
        refresh_header.setRefreshing(refreshing);
    }

    private void initView() {
        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("open"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                setRefreshing(false);

                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject dataObject = ret.getJSONObject("data");

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        textView1.setText(dataObject.getString("num"));
                        textView2.setText(SysUtils.getMoneyFormat(dataObject.getDouble("amount")));

                        id_text_1.setText(dataObject.getString("today_dead_num"));
                        id_text_2.setText(SysUtils.getMoneyFormat(dataObject.getDouble("yesterday_amount")));
                        id_text_3.setText(SysUtils.getMoneyFormat(dataObject.getDouble("month_amount")));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setRefreshing(false);
                SysUtils.showNetworkError();
            }
        });

        executeRequest(r);
    }
}

