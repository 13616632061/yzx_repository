package com.ms.ks;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.adapter.SubmitOrderAdapter;
import com.ms.entity.GetOpenOrder;
import com.ms.entity.GetOpenOrder_info;
import com.ms.entity.Order;
import com.ms.global.Global;
import com.ms.listview.PagingListView;
import com.ms.util.CustomRequest;
import com.ms.util.DialogUtils;
import com.ms.util.PreferencesService;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubmitOrderActivity extends BaseActivity implements View.OnClickListener {

    private int type;
    private double total_price;
    private ArrayList<GetOpenOrder> getOpenOrders_choose;
    private PagingListView list_order;
    private RelativeLayout scancode_layout,paycode_layout,cash_layout;
    private TextView tv_total_price;
    private String order_id;//订单id
    private String time;//订单时间
    private String getOpenOrders;//订单列表字符串
    private ArrayList<GetOpenOrder_info> getOpenOrder_infos;
    private PreferencesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);
        SysUtils.setupUI(SubmitOrderActivity.this,findViewById(R.id.activity_submit_order));
        initToolbar(this);

        initView();

    }

    private void initView() {
        list_order= (PagingListView) findViewById(R.id.list_order);
        scancode_layout= (RelativeLayout) findViewById(R.id.scancode_layout);
        paycode_layout= (RelativeLayout) findViewById(R.id.paycode_layout);
        cash_layout= (RelativeLayout) findViewById(R.id.cash_layout);
        tv_total_price= (TextView) findViewById(R.id.tv_total_price);

        scancode_layout.setOnClickListener(this);
        paycode_layout.setOnClickListener(this);
        cash_layout.setOnClickListener(this);
        initData();
    }

    private void initData() {

        Intent intent=getIntent();
        getOpenOrders_choose=new ArrayList<>();
        if(intent!=null){
            type=intent.getIntExtra("type",0);
            if(type==1){
                total_price=Double.parseDouble(intent.getStringExtra("total_price"));
                getOpenOrders_choose=intent.getParcelableArrayListExtra("getOpenOrders");
                order_id="";
                for(int i=0;i<getOpenOrders_choose.size();i++){
                    order_id+=getOpenOrders_choose.get(i).getOrder_id()+",";
                }
                SubmitOrderAdapter submitOrderAdapter=new SubmitOrderAdapter(SubmitOrderActivity.this,getOpenOrders_choose);
                list_order.setAdapter(submitOrderAdapter);
                tv_total_price.setText(total_price+"");

            }else if(type==2){
                order_id="";
                total_price=Double.parseDouble(intent.getStringExtra("total_price"));
                order_id=intent.getStringExtra("order_id");
                time=intent.getStringExtra("time");
                getOpenOrders=intent.getStringExtra("getOpenOrders");
                System.out.println("getOpenOrders="+getOpenOrders.toString());
                try {
                    JSONArray jsonArray=new JSONArray(getOpenOrders.replace("/","|"));
                    getOpenOrder_infos=new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String id=jsonObject.getString("goods_id");
                        String name=jsonObject.getString("name");
                        String num=jsonObject.getString("nums");
                        String price=jsonObject.getString("price");
                        GetOpenOrder_info getOpenOrder_info=new GetOpenOrder_info(id,"",name,num,price,"","");
                        getOpenOrder_infos.add(getOpenOrder_info);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("e="+e.toString());
                }
                getOpenOrders_choose.add(new GetOpenOrder(order_id,time,"","",total_price,getOpenOrder_infos,false,false));
                SubmitOrderAdapter submitOrderAdapter=new SubmitOrderAdapter(SubmitOrderActivity.this,getOpenOrders_choose);
                list_order.setAdapter(submitOrderAdapter);
                tv_total_price.setText(total_price+"");
            }
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.scancode_layout:
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(SubmitOrderActivity.this, android.Manifest.permission.CAMERA);
                        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(SubmitOrderActivity.this,new String[]{android.Manifest.permission.CAMERA},222);
                            return;
                        }else{
                            Intent intent=new Intent(SubmitOrderActivity.this,MipcaActivityCapture.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("type",3);
                            intent.putExtra("getOpenOrders_choose",getOpenOrders_choose);
                            intent.putExtra("total_price",total_price+"");
                            startActivity(intent);
                        }
                    } else {
                        Intent intent=new Intent(SubmitOrderActivity.this,MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("type",3);
                        intent.putExtra("getOpenOrders_choose",getOpenOrders_choose);
                        intent.putExtra("total_price",total_price+"");
                        startActivity(intent);
                    }
                    break;
                case R.id.paycode_layout:
                    paycode();
                    break;
                case R.id.cash_layout:
                    Intent intent=new Intent(SubmitOrderActivity.this,CashChargeActivity.class);
                    intent.putExtra("type",2);
                    intent.putExtra("getOpenOrders_choose",getOpenOrders_choose);
                    intent.putExtra("total_price",total_price+"");
                    startActivity(intent);

                    break;
            }

    }

    /**
     * 选择二维码支付
     */
    private JSONArray jsonArray;
    private void paycode(){
        Map<String,String> map= new HashMap<String,String>();
            ArrayList<Map<String,String>>  mapArrayList=new ArrayList<>();
            for(int i=0;i<getOpenOrders_choose.size();i++){
                Map<String,String> map1=new HashMap<>();
                map1.put("order_id",getOpenOrders_choose.get(i).getOrder_id());
                map1.put("price",getOpenOrders_choose.get(i).getPrice()+"");
                mapArrayList.add(map1);
            map.put("map",mapArrayList.toString());
            JSONObject jsonObject=new JSONObject(map);
            try {
                String map_str=jsonObject.getString("map");
                jsonArray=new JSONArray(map_str);
                map.put("map",jsonArray+"");//转化为json数组的字符串
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        map.put("total_fee",(new Double(total_price*100)).intValue()+"");
        map.put("pay_type","wxpayjsapi");
        map.put("auth_code","code");
        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("common_pay"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();

                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject data=null;
                    if (!status.equals("200")) {
                        if(status.equals("E.404")){
                            DialogUtils.showbuilder(SubmitOrderActivity.this,message);
                        }else {
                            SysUtils.showError(message);
                        }
                    } else {
                        data=ret.getJSONObject("data");
                        String order_id=data.getString("order_id");
                        String code_url=data.getString("url");

                        service=new PreferencesService(SubmitOrderActivity.this);
                        service.save_order_id(order_id);

                        Intent intent=new Intent(SubmitOrderActivity.this,OpenOrderPayCodeActivity.class);
                        intent.putExtra("code_url",code_url);
                        intent.putExtra("order_id",order_id);
                        intent.putExtra("getOpenOrders_choose",getOpenOrders_choose);
                        intent.putExtra("total_price",total_price+"");
                        startActivity(intent);

                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideLoading();
                SysUtils.showNetworkError();
            }
        });

        executeRequest(r);
        showLoading(SubmitOrderActivity.this);
    }
    private BroadcastReceiver broadcastAffirmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type=intent.getIntExtra("type",0);
            if(type==1){
                finish();
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        SubmitOrderActivity.this.registerReceiver(broadcastAffirmReceiver, new IntentFilter(Global.BROADCAST_SubmitOrderActivity_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubmitOrderActivity.this.unregisterReceiver(broadcastAffirmReceiver);
    }
}
