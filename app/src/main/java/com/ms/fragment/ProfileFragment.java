package com.ms.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.global.Global;
import com.ms.ks.AboutActivity;
import com.ms.ks.AddressActivity;
import com.ms.ks.EditBnakCardActivity;
import com.ms.ks.GetMoneyActivity;
import com.ms.ks.MsgActivity;
import com.ms.ks.PrintActivity;
import com.ms.ks.ProfileEntryActivity;
import com.ms.ks.ProfilePasswordActivity;
import com.ms.ks.R;
import com.ms.ks.SetActivity;
import com.ms.ks.ShopActivity;
import com.ms.util.CustomRequest;
import com.ms.util.LoginUtils;
import com.ms.util.SysUtils;

import org.json.JSONObject;

import java.util.TimerTask;

public class ProfileFragment extends BaseFragment{
    private ShopActivity mainAct;
    private TextView textView2;
    private boolean isLoading = false;
    private ImageView iv_profilefragment_back;
    private ImageView iv_profilefragment_refresh;
    private int isgetmoney;//1表示有提现权限，0表示没有提现权限
    private String mainseller;
    private boolean iscleck;

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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        iv_profilefragment_refresh= (ImageView) view.findViewById(R.id.iv_profilefragment_refresh);
        iv_profilefragment_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        textView2 = (TextView) view.findViewById(R.id.textView2);
        iv_profilefragment_back = (ImageView) view.findViewById(R.id.iv_profilefragment_back);
        iv_profilefragment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        //余额中心
        LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.linearLayout2);
        linearLayout2.setVisibility(View.VISIBLE);
        iscleck=false;
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SysUtils.startAct(getActivity(), new MoneyActivity());
                iscleck=true;
                getData();
            }
        });

        //账号信息
        LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.linearLayout3);
        linearLayout3.setVisibility(View.VISIBLE);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new ProfileEntryActivity());
            }
        });

        //修改密码
        LinearLayout linearLayout8 = (LinearLayout) view.findViewById(R.id.linearLayout8);
        linearLayout8.setVisibility(View.VISIBLE);
        linearLayout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new ProfilePasswordActivity());
            }
        });

        //商户设置
        LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.linearLayout1);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new SetActivity());
            }
        });

        //发货地址
        LinearLayout linearLayout5 = (LinearLayout) view.findViewById(R.id.linearLayout5);
        linearLayout5.setVisibility(View.VISIBLE);
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new AddressActivity());
            }
        });

        //消息推送
        LinearLayout linearLayout6 = (LinearLayout) view.findViewById(R.id.linearLayout6);
        linearLayout6.setVisibility(View.VISIBLE);
        linearLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new MsgActivity());
            }
        });

        LinearLayout linearlayout01 = (LinearLayout) view.findViewById(R.id.linearlayout01);
        linearlayout01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new PrintActivity());
            }
        });
        //修改银行卡
        LinearLayout linearLayout_editcard=(LinearLayout)view.findViewById(R.id.linearLayout_editcard);
        linearLayout_editcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new EditBnakCardActivity());
            }
        });

        //关于我们
        View set_about = (View) view.findViewById(R.id.set_about_item);
        SysUtils.setLine(set_about, Global.SET_CELLUP, "关于我们", 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(getActivity(), new AboutActivity());
            }
        });

        //联系客服
        View set_kefu = (View) view.findViewById(R.id.set_kefu_item);
        TextView set_kefu_text = (TextView) set_kefu.findViewById(R.id.ll_set_hint_text);
        set_kefu_text.setText(Global.SERVICE_PHONE);
        SysUtils.setLine(set_kefu, Global.SET_SINGLE_LINE, "联系客服", 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.callTel(getActivity(), Global.SERVICE_PHONE);
            }
        });


        PaperButton button1 = (PaperButton) view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .theme(SysUtils.getDialogTheme())
                        .content("确定退出登录？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                LoginUtils.logout(getActivity(), 2);
                                SysUtils.showSuccess("已退出登录");

                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });


        if (LoginUtils.jurisdiction()) {
//        if (false) {
            //受限
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            linearLayout5.setVisibility(View.GONE);
            linearLayout6.setVisibility(View.GONE);
            linearLayout8.setVisibility(View.GONE);

            linearLayout1.setBackgroundResource(R.drawable.selector_cell_left_blank);
//            linearlayout01.setBackgroundResource(R.drawable.selector_cell_single_line);
        } else {
            getData();
//            myTimerTask timerTask = new myTimerTask();
//            Timer timer = new Timer(true);
//            timer.schedule(timerTask, 0, 1000 * 30);//每30秒执行一次
        }

        return view;
    }

    public void getData() {
        if (isLoading) {
            return;
        }

        isLoading = true;
        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("center"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();
                isLoading = false;

                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("center:"+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject dataObject = ret.getJSONObject("data");

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        double advance = dataObject.getDouble("advance");
                        isgetmoney= dataObject.getInt("ketixian");
                        mainseller=dataObject.getString("mainseller");
                        if(mainseller.equals("true")){
                            if(isgetmoney==1){
                                isgetmoney=0;
                            }else{
                                isgetmoney=1;
                            }
                        }
                        if(isgetmoney==1){
                            textView2.setText(SysUtils.getMoneyFormat(advance));
                        }else if (isgetmoney==0){
                            textView2.setText("￥"+0.00);
                        }
                        if (iscleck){
                            if(isgetmoney==1){
                                Intent intentgetmoney=new Intent(getActivity(),GetMoneyActivity.class);
                                intentgetmoney.putExtra("isstore",2);
                                startActivity(intentgetmoney);
                            }else {
                                SysUtils.showError("您没有开启提现功能！");
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoading = false;
                hideLoading();
                SysUtils.showNetworkError();
            }
        });

        executeRequest(r);

        showLoading(getActivity());
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        iscleck=false;
        getData();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REFRESH_PROFILE_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch(Exception e) {

        }
    }

    private class myTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 2;
            myHandler.sendMessage(message);  //发送message
        }
    }

    Handler myHandler = new Handler() {
        // 接收到消息后处理
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //UI操作
                    break;
                case 2:
                    //UI操作
                    getData();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}

