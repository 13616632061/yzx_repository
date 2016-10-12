package com.ms.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.ms.entity.News;
import com.ms.global.Global;
import com.ms.ks.AdActivity;
import com.ms.ks.BaseFragment;
import com.ms.ks.NoticeActivity;
import com.ms.ks.OpenActivity;
import com.ms.ks.R;
import com.ms.ks.ShopActivity;
import com.ms.util.CustomRequest;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportFragment extends BaseFragment {
    private ShopActivity mainAct;
    private TextView id_text_1, id_text_2, id_text_3, id_text_5;
    private SwipeRefreshLayout refresh_header;
    private ListView lv_content;
    private TextView textView1, textView2;
    private String intro = "";
    public ArrayList<News> ad_list;
    private View adView = null;
    SliderLayout slider;
    private PagerIndicator custom_indicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (ShopActivity) getActivity();
        ad_list = new ArrayList<News>();
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
        RelativeLayout relativeLayout3 = (RelativeLayout) firstView.findViewById(R.id.relativeLayout3);
        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toOpenList(1);
            }
        });

        //今日全部订单
        View set_item_1 = (View) firstView.findViewById(R.id.set_item_1);
        ImageView idx_1 = (ImageView) set_item_1.findViewById(R.id.ll_set_idx);
        idx_1.setVisibility(View.VISIBLE);

        id_text_1 = (TextView) set_item_1.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_1, Global.SET_CELLUP, "今日全部订单", R.drawable.icon_item_1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenList(2);
            }
        });

        //昨日营业额
        View set_item_2 = (View) firstView.findViewById(R.id.set_item_2);
        ImageView idx_2 = (ImageView) set_item_2.findViewById(R.id.ll_set_idx);
        idx_2.setVisibility(View.VISIBLE);

        id_text_2 = (TextView) set_item_2.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_2, Global.SET_CELLWHITE, "昨日营业额", R.drawable.icon_item_2, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenList(3);
            }
        });

        //本月营业额
        View set_item_3 = (View) firstView.findViewById(R.id.set_item_3);
        ImageView idx_3 = (ImageView) set_item_3.findViewById(R.id.ll_set_idx);
        idx_3.setVisibility(View.VISIBLE);

        id_text_3 = (TextView) set_item_3.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_3, Global.SET_CELLWHITE, "本月营业额", R.drawable.icon_item_3, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenList(4);
            }
        });

        //上月营业额
        View set_item_5 = (View) firstView.findViewById(R.id.set_item_5);
        ImageView idx_5 = (ImageView) set_item_5.findViewById(R.id.ll_set_idx);
        idx_5.setVisibility(View.VISIBLE);

        id_text_5 = (TextView) set_item_5.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_item_5, Global.SET_SINGLE_LINE, "上月营业额", R.drawable.icon_item_3, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenList(5);
            }
        });

        //店铺公告
        View set_item_4 = (View) firstView.findViewById(R.id.set_item_4);
        SysUtils.setLine(set_item_4, Global.SET_TWO_LINE, "店铺公告", R.drawable.icon_item_4, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new MaterialDialog.Builder(getActivity())
//                        .theme(SysUtils.getDialogTheme())
//                        .title("店铺公告")
//                        .content(intro)
//                        .positiveText("关闭")
//                        .show();

                Bundle b = new Bundle();
                b.putString("notice", intro);

                SysUtils.startAct(getActivity(), new NoticeActivity(), b, true);
            }
        });

        textView1 = (TextView) firstView.findViewById(R.id.textView1);
        textView2 = (TextView) firstView.findViewById(R.id.textView2);
//        textView3 = (TextView) firstView.findViewById(R.id.textView3);

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
//                        textView3.setText(SysUtils.getMoneyFormat(dataObject.getDouble("favorable")));

                        id_text_1.setText(dataObject.getString("today_dead_num"));
                        id_text_2.setText(SysUtils.getMoneyFormat(dataObject.getDouble("yesterday_amount")));
                        id_text_3.setText(SysUtils.getMoneyFormat(dataObject.getDouble("month_amount")));
                        id_text_5.setText(SysUtils.getMoneyFormat(dataObject.getDouble("lastmonth_amount")));

                        intro = SysUtils.getFinalString("intro", dataObject);

                        ad_list.clear();
                        JSONArray params_img = dataObject.optJSONArray("params_img");
                        if (params_img != null && params_img.length() > 0) {
                            for (int i = 0; i < params_img.length(); i++) {
                                JSONObject data = params_img.optJSONObject(i);

                                News b = new News(0,
                                        1,
                                        0,
                                        data.getString("linkinfo"),
                                        data.getString("linktarget"),
                                        data.getString("link"),
                                        "");


                                ad_list.add(b);
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    loadAd();
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


    private void loadAd() {
        if(ad_list.size() > 0) {
            //有广告
            if(adView == null) {
                adView = (View) LayoutInflater.from(getActivity()).inflate(R.layout.ad, null);
                RelativeLayout ll = (RelativeLayout) adView.findViewById(R.id.ad_layout);
                slider = (SliderLayout) adView.findViewById(R.id.slider);
                custom_indicator = (PagerIndicator) adView.findViewById(R.id.custom_indicator);
                lv_content.addHeaderView(adView);

                int width = Global.magicWidth - 40;
                int height = width * 8 / 15;

                //设置高度
                ll.setLayoutParams(new AbsListView.LayoutParams(width, height));
                slider.setPresetTransformer(SliderLayout.Transformer.Default);
                slider.setDuration(Global.magicDuration);
            }
            slider.removeAllSliders();
//        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.setCustomIndicator(custom_indicator);
//            slider.setCustomAnimation(new DescriptionAnimation());
            for(int i = 0; i < ad_list.size(); i++) {
                News bean = ad_list.get(i);

                TextSliderView textSliderView = new TextSliderView(getActivity());
                textSliderView
                        .description(bean.getSubject())
                        .image(bean.getPic_url())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                News bean = slider.getBundle().getParcelable("data");
                                String u = bean.getLinkurl();

                                if (!StringUtils.isEmpty(u)) {
                                    Bundle b = new Bundle();
                                    b.putString("title", bean.getSubject());
                                    b.putString("url", u);

                                    SysUtils.startAct(getActivity(), new AdActivity(), b);
                                }
//                                SysUtils.newsClick(getActivity(), bean);
                            }
                        });

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putParcelable("data", bean);

                slider.addSlider(textSliderView);
            }
        } else {
            //没有广告，尝试移除view
            if(adView != null) {
                lv_content.removeHeaderView(adView);
                adView = null;
            }
        }
    }


    @Override
    public void onStart() {
        if(slider != null) {
            slider.startAutoCycle();
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        if(slider != null) {
            slider.stopAutoCycle();
        }

        super.onStop();
    }

    //刷新广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh_header.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    initView();
                }
            });
        }
    };
    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REFRESH_SHOP_REPORT_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch(Exception e) {

        }
    }

    private void toOpenList(int type) {
        Bundle b = new Bundle();
        b.putInt("type", type);

        SysUtils.startAct(getActivity(), new OpenActivity(), b);
    }
}

