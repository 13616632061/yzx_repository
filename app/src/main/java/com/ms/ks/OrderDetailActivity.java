package com.ms.ks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.entity.Order;
import com.ms.entity.OrderGoods;
import com.ms.global.Global;
import com.ms.util.CustomRequest;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends BaseActivity {
    private String order_id = "";
    private ListView lv_content;

    private View layout_err, include_nowifi, include_noresult;
    private Button load_btn_refresh_net, load_btn_retry;
    private TextView load_tv_noresult;
    private int textColor, redColor;
    public ArrayList<OrderGoods> cat_list;
    private PartyAdapter adapter;

    private Order order;


    public TextView textView3, textView10, textView2, textView4, textView5, textView6, textView7;
    public RelativeLayout relativeLayout1;
    public LinearLayout linearLayout5;
    public TextView editText1, editText2;

    public TextView shipKuaidi, shipWaimai;

    public View shipView;
    private boolean hasKuaidi = false, hasWaimai = false;
    public View printView;
    public PaperButton btnPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initToolbar(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("order_id")) {
                order_id = bundle.getString("order_id");
            }
        }

        if(StringUtils.isEmpty(order_id)) {
            finish();
        }

        Intent intent = new Intent(this, OrderDetailActivity.class);
        Bundle b = new Bundle();
        b.putString("order_id", "1606271257453732");
        intent.putExtras(b);

        String intentStr = intent.toUri(Intent.URI_INTENT_SCHEME);
        Log.v("ks", intentStr);

        textColor = getResources().getColor(R.color.text_color);
        redColor = getResources().getColor(R.color.red_color);

        layout_err = (View) findViewById(R.id.layout_err);
        include_noresult = layout_err.findViewById(R.id.include_noresult);
        load_btn_retry = (Button) layout_err.findViewById(R.id.load_btn_retry);
        load_btn_retry.setVisibility(View.GONE);
        load_tv_noresult = (TextView) layout_err.findViewById(R.id.load_tv_noresult);
        load_tv_noresult.setText("订单不存在");
        load_tv_noresult.setCompoundDrawablesWithIntrinsicBounds(
                0, //left
                R.drawable.icon_no_result_melt, //top
                0, //right
                0//bottom
        );
        include_nowifi = layout_err.findViewById(R.id.include_nowifi);
        load_btn_refresh_net = (Button) include_nowifi.findViewById(R.id.load_btn_refresh_net);
        load_btn_refresh_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新加载数据
                initView();
            }
        });

        cat_list = new ArrayList<OrderGoods>();

        lv_content = (ListView) findViewById(R.id.lv_content);
        View firstView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_order, lv_content, false);
        lv_content.addHeaderView(firstView);

        shipView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_order_ship, lv_content, false);
        lv_content.addFooterView(shipView);
        shipView.setVisibility(View.GONE);

        shipKuaidi = (TextView) shipView.findViewById(R.id.editText1);
        shipKuaidi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("order", order);

                SysUtils.startAct(OrderDetailActivity.this, new OrderDeliveryActivity(), b);
            }
        });
        shipWaimai = (TextView) shipView.findViewById(R.id.editText2);
        shipWaimai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(OrderDetailActivity.this)
                        .theme(SysUtils.getDialogTheme())
                        .content("确定外卖发货？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Map<String,String> map = new HashMap<String,String>();
                                map.put("order_id", order.getOrderSn());
                                map.put("corp_id", String.valueOf(order.getDeliverySellerDtId()));

                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("ship_status"), map, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        hideLoading();

                                        try {
                                            JSONObject ret = SysUtils.didResponse(jsonObject);
                                            String status = ret.getString("status");
                                            String message = ret.getString("message");

                                            if (!status.equals("200")) {
                                                SysUtils.showError(message);
                                            } else {
                                                SysUtils.showSuccess("订单已外卖发货");

                                                initView();
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

                                showLoading(OrderDetailActivity.this, "请稍等......");
                            }
                        })
                        .show();

            }
        });


        printView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.order_print, lv_content, false);
        lv_content.addFooterView(printView);
        printView.setVisibility(View.GONE);
        btnPrint = (PaperButton) printView.findViewById(R.id.button1);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.showSuccess("print");
            }
        });

        textView3 = (TextView) firstView.findViewById(R.id.textView3);
        textView10 = (TextView) firstView.findViewById(R.id.textView10);
        textView2 = (TextView) firstView.findViewById(R.id.textView2);
        textView4 = (TextView) firstView.findViewById(R.id.textView4);
        textView5 = (TextView) firstView.findViewById(R.id.textView5);
        relativeLayout1 = (RelativeLayout) firstView.findViewById(R.id.relativeLayout1);
        textView6 = (TextView) firstView.findViewById(R.id.textView6);
        textView7 = (TextView) firstView.findViewById(R.id.textView7);

        linearLayout5 = (LinearLayout) firstView.findViewById(R.id.linearLayout5);
        editText1 = (TextView) firstView.findViewById(R.id.editText1);
        editText2 = (TextView) firstView.findViewById(R.id.editText2);


        adapter = new PartyAdapter();
        lv_content.setAdapter(adapter);

        initView();
    }

    private void initView() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("order_id", order_id);

        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("particulars"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();

                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject dataObject = ret.getJSONObject("data");

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        order = SysUtils.getOrderRow(dataObject.getJSONObject("orders_info"));

                        hasKuaidi = order.getDeliveryExpress() == 1;
                        hasWaimai = order.getDeliverySeller() == 1;

                        if (hasKuaidi || hasWaimai) {
                            shipView.setVisibility(View.VISIBLE);

                            if (hasKuaidi) {
                                shipKuaidi.setVisibility(View.VISIBLE);
                            } else {
                                shipKuaidi.setVisibility(View.GONE);
                            }
                            if (hasWaimai) {
                                shipWaimai.setVisibility(View.VISIBLE);
                            } else {
                                shipWaimai.setVisibility(View.GONE);
                            }
                        } else {
                            shipView.setVisibility(View.GONE);

                        }

                        printView.setVisibility(View.VISIBLE);

                        textView3.setText(order.getOrderTime());
                        textView10.setText(order.getPayStatusStr());

                        if (order.getPayStatus() == 1) {
                            textView10.setTextColor(textColor);
                        } else {
                            textView10.setTextColor(redColor);
                        }

                        if (order.hasShippingAddr()) {
                            textView2.setText("配送地址：" + order.getShipAddr());
                            textView2.setVisibility(View.VISIBLE);
                        } else {
                            textView2.setVisibility(View.GONE);
                        }
                        textView4.setText(order.getShippingStr());
                        textView5.setText("订单号：" + order.getOrderSn());

                        if (order.getPayed() > 0 || !StringUtils.isEmpty(order.getName())) {
                            if (!StringUtils.isEmpty(order.getName())) {
                                textView6.setText("下单用户：" + order.getName());
                            } else {
                                textView6.setText("");
                            }

                            if (order.getPayed() > 0) {
                                textView7.setText(SysUtils.getMoneyFormat(order.getPayed()));
                            } else {
                                textView7.setText("");
                            }
                            relativeLayout1.setVisibility(View.VISIBLE);
                        } else {
                            relativeLayout1.setVisibility(View.GONE);
                        }

                        if (order.canClose() || order.canComplete()) {
                            if (order.canComplete()) {
                                editText1.setVisibility(View.VISIBLE);
                            } else {
                                editText1.setVisibility(View.GONE);
                            }

                            if (order.canClose()) {
                                editText2.setVisibility(View.VISIBLE);
                            } else {
                                editText2.setVisibility(View.GONE);
                            }

                            linearLayout5.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout5.setVisibility(View.GONE);
                        }

                        //点击确认
                        editText1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new MaterialDialog.Builder(OrderDetailActivity.this)
                                        .content("确认订单？")
                                        .theme(SysUtils.getDialogTheme())
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                Map<String,String> map = new HashMap<String,String>();
                                                map.put("order_id", order.getOrderSn());
                                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("affirm"), map, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject jsonObject) {
                                                        hideLoading();
                                                        try {
                                                            JSONObject ret = SysUtils.didResponse(jsonObject);
                                                            String status = ret.getString("status");
                                                            String message = ret.getString("message");

                                                            if (!status.equals("200")) {
                                                                SysUtils.showError(message);
                                                            } else {
                                                                SysUtils.showSuccess("订单已确认");

                                                                sendBroadcast(new Intent(Global.BROADCAST_REFRESH_ORDER_ACTION)
                                                                        .putExtra("type", 2));

                                                                sendBroadcast(new Intent(Global.BROADCAST_AFFIRM_ORDER_ACTION)
                                                                        .putExtra("order_id", order.getOrderSn()));
                                                            }
                                                        } catch(Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {
                                                        SysUtils.showNetworkError();
                                                        hideLoading();
                                                    }
                                                });

                                                executeRequest(r);
                                                showLoading(OrderDetailActivity.this);
                                            }
                                        })
                                        .show();
                            }
                        });

                        //取消
                        editText2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new MaterialDialog.Builder(OrderDetailActivity.this)
                                        .content("确认取消订单？")
                                        .theme(SysUtils.getDialogTheme())
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                Map<String,String> map = new HashMap<String,String>();
                                                map.put("order_id", order.getOrderSn());
                                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("cancel"), map, new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject jsonObject) {
                                                        hideLoading();
                                                        try {
                                                            JSONObject ret = SysUtils.didResponse(jsonObject);
                                                            String status = ret.getString("status");
                                                            String message = ret.getString("message");

                                                            if (!status.equals("200")) {
                                                                SysUtils.showError(message);
                                                            } else {
                                                                SysUtils.showSuccess("订单已取消");

                                                                sendBroadcast(new Intent(Global.BROADCAST_REFRESH_ORDER_ACTION)
                                                                        .putExtra("type", 2));

                                                                sendBroadcast(new Intent(Global.BROADCAST_AFFIRM_ORDER_ACTION)
                                                                        .putExtra("order_id", order.getOrderSn()));
                                                            }
                                                        } catch(Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {
                                                        SysUtils.showNetworkError();
                                                        hideLoading();
                                                    }
                                                });

                                                executeRequest(r);
                                                showLoading(OrderDetailActivity.this);
                                            }
                                        })
                                        .show();
                            }
                        });

                        cat_list.clear();

                        JSONArray array = dataObject.getJSONArray("orde_goods");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.optJSONObject(i);

                                OrderGoods b = new OrderGoods();
                                b.setName(data.getString("name"));
                                b.setQuantity(data.getInt("quantity"));
                                b.setPrice(data.getDouble("price"));

                                cat_list.add(b);
                            }
                        }
                        OrderGoods b = new OrderGoods();
                        b.setName("运费");
                        b.setQuantity(0);
                        b.setPrice(order.getShipped());
                        cat_list.add(b);

                        b = new OrderGoods();
                        b.setName("总计");
                        b.setQuantity(0);
                        b.setPrice(order.getPayed());
                        cat_list.add(b);

                        adapter.notifyDataSetChanged();
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

        showLoading(OrderDetailActivity.this, "请稍等......");
    }

    public class PartyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public PartyAdapter() {
            super();
            this.inflater = LayoutInflater.from(OrderDetailActivity.this);
        }

        public int getCount() {
            return cat_list.size();
        }

        public Object getItem(int position) {
            return cat_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                try {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_order_goods, null);

                    holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                    holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                    holder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
                    holder.line = (TextView) convertView.findViewById(R.id.line);

                    convertView.setTag(holder);
                } catch (Exception e) {
                }
            }else{
                holder = (ViewHolder) convertView.getTag();
            }


            final OrderGoods data = cat_list.get(position);
            if(data != null) {
                holder.textView1.setText(data.getName());
                if (data.getQuantity() > 0) {
                    holder.textView2.setText("×" + data.getQuantity());
                } else {
                    holder.textView2.setText("");
                }
                holder.textView3.setText(SysUtils.getMoneyFormat(data.getPrice()));

                if (cat_list.size() > 2 && position == (cat_list.size() - 3)) {
                    holder.line.setVisibility(View.VISIBLE);
                } else {
                    holder.line.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView textView1, textView2, textView3, line;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_search) {
            SysUtils.startAct(OrderDetailActivity.this, new SearchActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //更新广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initView();
        }
    };
    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REFRESH_ORDER_ACTION));
        registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REFRESH_ORDER_DETAIL_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(broadcastReceiver);
        } catch(Exception e) {

        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
//        if (click != null) {
//            String customContent = click.getCustomContent();
//            if (customContent != null && customContent.length() != 0) {
//                try {
//                    //获取信鸽推送结果
//                    JSONObject json = new JSONObject(customContent);
//
//                    String type = json.getString("type");
//
//                    SysUtils.showSuccess("haha");
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            finish();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}
