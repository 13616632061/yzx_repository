package com.ms.ks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ms.entity.Order;
import com.ms.entity.OrderGoods;
import com.ms.global.Global;
import com.ms.util.BluetoothService;
import com.ms.util.CustomRequest;
import com.ms.util.PicFromPrintUtils;
import com.ms.util.PrintUtil;
import com.ms.util.QRCodeUtil;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends BaseActivity {
    private String order_id = "";
    private ListView lv_content;

    private View layout_err, include_nowifi, include_noresult;
    private Button load_btn_refresh_net, load_btn_retry;
    private TextView load_tv_noresult;
    public ArrayList<OrderGoods> goodsList;
    public ArrayList<OrderGoods> cat_list;
    private PartyAdapter adapter;

    private Order order;

    public TextView textView3, textView10, textView5, textView6, textView7, textView11;
    public LinearLayout linearLayout5, linearLayout6, qrcode_rl;
    public TextView editText1, editText2, desk_num;

    public ImageView imageView1;

    public TextView shipKuaidi, shipWaimai;

    public View shipView;
    private boolean hasKuaidi = false, hasWaimai = false;

    public View addressView;
    public TextView textView15, textView16, textView_title;

    public View remarkView;
    public TextView order_memo;

    //蓝牙服务
    BluetoothService mService = null;
    //表示是否连接上蓝牙打印机
    private boolean hasConnect = false;
    //尝试打开蓝牙
    private static final int REQUEST_ENABLE_BT = 2;

    public FloatingActionButton tv_print;
    private BluetoothAdapter mBluetoothAdapter = null;

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

        mService = new BluetoothService(this, mHandler);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            SysUtils.showError("蓝牙不可用，无法设置打印机参数");
            finish();
        }

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
        goodsList = new ArrayList<OrderGoods>();

        lv_content = (ListView) findViewById(R.id.lv_content);

        //配送地址
        addressView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_address, lv_content, false);
        lv_content.addHeaderView(addressView);
        addressView.setVisibility(View.GONE);
        textView15 = (TextView) addressView.findViewById(R.id.textView15);
        textView16 = (TextView) addressView.findViewById(R.id.textView16);
        textView_title = (TextView) addressView.findViewById(R.id.textView_title);

        //订单备注
        remarkView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_remark, lv_content, false);
        lv_content.addHeaderView(remarkView);
        remarkView.setVisibility(View.GONE);
        order_memo = (TextView) remarkView.findViewById(R.id.order_memo);

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
                                Map<String, String> map = new HashMap<String, String>();
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
                                        } catch (Exception e) {
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

        tv_print = (FloatingActionButton) findViewById(R.id.tv_print);
        tv_print.setSize(FloatingActionButton.SIZE_MINI);
        tv_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(OrderDetailActivity.this)
                        .theme(SysUtils.getDialogTheme())
                        .content("确定打印小票？")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                startPrint();
                            }
                        })
                        .show();
            }
        });

        textView3 = (TextView) firstView.findViewById(R.id.textView3);
        textView10 = (TextView) firstView.findViewById(R.id.textView10);
        imageView1 = (ImageView) firstView.findViewById(R.id.imageView1);
        textView5 = (TextView) firstView.findViewById(R.id.textView5);
        textView6 = (TextView) firstView.findViewById(R.id.textView6);
        textView7 = (TextView) firstView.findViewById(R.id.textView7);
        textView11 = (TextView) firstView.findViewById(R.id.textView11);

        linearLayout5 = (LinearLayout) firstView.findViewById(R.id.linearLayout5);
        linearLayout6 = (LinearLayout) firstView.findViewById(R.id.linearLayout6);
        qrcode_rl = (LinearLayout) firstView.findViewById(R.id.qrcode_rl);
        editText1 = (TextView) firstView.findViewById(R.id.editText1);
        editText2 = (TextView) firstView.findViewById(R.id.editText2);
        desk_num = (TextView) firstView.findViewById(R.id.desk_num);

        adapter = new PartyAdapter();
        lv_content.setAdapter(adapter);

        initView();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            hasConnect = true;

                            doPrint();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    break;
                case BluetoothService.MESSAGE_READ:
                    //byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case BluetoothService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "连接至"
//                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_TOAST:
                    SysUtils.showError(msg.getData().getString(BluetoothService.TOAST));
                    break;
//                case BluetoothService.MESSAGE_CONNECTION_LOST:
//                    hasConnect = false;
//                    break;
//                case BluetoothService.MESSAGE_UNABLE_CONNECT:
//                    hasConnect = false;
//                    SysUtils.showError("无法连接到配对的蓝牙打印机，请先通过打印机测试再进行收银小票打印");
//                    break;
            }
        }

    };

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

                        textView3.setText(order.getOrderTime());
//                        textView10.setText(order.getPayStatusStr());
//
//                        if (order.getPayStatus() == 1) {
//                            textView10.setTextColor(textColor);
//                        } else {
//                            textView10.setTextColor(redColor);
//                        }

                        textView10.setText(Html.fromHtml(order.getStatusStr()));

                        if (order.hasShippingAddr()) {
//                            textView2.setText("配送地址：" + order.getShipAddr());
                            textView15.setText(order.getShipName());
                            textView16.setText(Html.fromHtml("<u>" + order.getShipMobile() + "</u>"));
                            textView16.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SysUtils.callTel(OrderDetailActivity.this, order.getShipMobile());
                                }
                            });
                            textView_title.setText(order.getShipAddr());
                            addressView.setVisibility(View.VISIBLE);
                        } else {
                            addressView.setVisibility(View.GONE);
                            lv_content.removeHeaderView(addressView);
                        }

                        if (!StringUtils.isEmpty(order.getMemo())) {
                            order_memo.setText(Html.fromHtml(order.getMemo()));
                            remarkView.setVisibility(View.VISIBLE);
                        } else {
                            remarkView.setVisibility(View.GONE);
                            lv_content.removeHeaderView(remarkView);
                        }

                        tv_print.setVisibility(View.VISIBLE);

                        imageView1.setImageResource(order.getShippingRes());
                        textView5.setText("订单号：" + order.getOrderSn());
                        if (order.getCost_item() > 0) {
                            textView7.setText(SysUtils.getMoneyFormat(order.getCost_item()));
                        } else {
                            textView7.setText("");
                        }

                        if(!TextUtils.isEmpty(order.getOrder_num())) {
                            textView11.setText("#" + order.getOrder_num());
                            textView11.setVisibility(View.VISIBLE);
                        } else {
                            textView11.setVisibility(View.GONE);
                        }

                        if (order.canClose() || order.canComplete() || !StringUtils.isEmpty(order.getName())) {
                            if (!StringUtils.isEmpty(order.getName())) {
                                textView6.setText(order.getName());
                            } else {
                                textView6.setText("");
                            }

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

                        if(order.hasQrCode() || !TextUtils.isEmpty(order.getDesk_num())) {
                            if(order.hasQrCode()) {
                                qrcode_rl.setVisibility(View.VISIBLE);
                                qrcode_rl.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!TextUtils.isEmpty(order.getQrcode_url())) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("pic_list", order.getQrcode_url());
                                            bundle.putInt("offset", 0);

                                            SysUtils.startAct(OrderDetailActivity.this, new PicViewActivity(), bundle);
                                            ((BaseActivity) OrderDetailActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }
                                    }
                                });
                            } else {
                                qrcode_rl.setVisibility(View.GONE);
                            }

                            if(!TextUtils.isEmpty(order.getDesk_num())) {
                                desk_num.setText("桌号: " + order.getDesk_num());
                                desk_num.setVisibility(View.VISIBLE);
                            } else {
                                desk_num.setVisibility(View.GONE);
                            }
                            linearLayout6.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout6.setVisibility(View.GONE);
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
                        goodsList.clear();

                        JSONArray array = dataObject.getJSONArray("orde_goods");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.optJSONObject(i);

                                OrderGoods b = new OrderGoods();
                                b.setName(data.getString("name"));
                                b.setQuantity(data.getInt("quantity"));
                                double price = data.getDouble("price");
                                b.setAttr(data.optString("product_attr"));
//                                b.setAttr("颜色123");
                                b.setPrice(price);
                                b.setFormatPrice(SysUtils.getMoneyFormat(price));

                                cat_list.add(b);
                                goodsList.add(b);
                            }
                        }
                        OrderGoods b = new OrderGoods();
                        b.setName("商品总价");
                        b.setQuantity(0);
                        b.setPrice(order.getCost_item());
                        b.setFormatPrice(SysUtils.getMoneyFormat(order.getCost_item()));
                        cat_list.add(b);

//                        b = new OrderGoods();
//                        b.setName("运费");
//                        b.setQuantity(0);
//                        b.setPrice(order.getShipped());
//                        b.setFormatPrice("+ " + SysUtils.getMoneyFormat(order.getShipped()));
//                        cat_list.add(b);

                        b = new OrderGoods();
                        b.setName("已优惠");
                        b.setQuantity(0);
                        b.setPrice(order.getPmt_order());
                        b.setFormatPrice(SysUtils.getMoneyFormat(order.getPmt_order()));
                        cat_list.add(b);

                        b = new OrderGoods();
                        b.setName("已支付");
                        b.setQuantity(0);
                        b.setPrice(order.getPayed());
                        b.setFormatPrice(SysUtils.getMoneyFormat(order.getFinalPayed()));
                        cat_list.add(b);

                        b = new OrderGoods();
                        b.setName("可提现");
                        b.setQuantity(0);
                        b.setPrice(order.getApay());
                        b.setFormatPrice(SysUtils.getMoneyFormat(order.getApay()));
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
                    holder.textView11 = (TextView) convertView.findViewById(R.id.textView11);

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
                holder.textView3.setText(data.getFormatPrice());

                if (!StringUtils.isEmpty(data.getAttr())) {
                    holder.textView11.setText(data.getAttr());
                    holder.textView11.setVisibility(View.VISIBLE);
                } else {
                    holder.textView11.setVisibility(View.GONE);
                }

                if (cat_list.size() > 4 && position == (cat_list.size() - 5)) {
                    holder.line.setVisibility(View.VISIBLE);
                } else {
                    holder.line.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView textView1, textView2, textView3, line, textView11;
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

        if (mService != null) {
            mService.stop();
        }

        mService = null;
    }

    //开始打印
    public void startPrint() {
        if(!hasConnect) {
            //还没有连接，先尝试连接
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                //尝试连接
                try {
                    if(mService != null) {
//                        BluetoothDevice printDev = mService.getDevByMac(KsApplication.getString("printer_mac", ""));
                        BluetoothDevice printDev = mBluetoothAdapter.getRemoteDevice(KsApplication.getString("printer_mac", ""));

                        if(printDev == null) {
                            SysUtils.showError("连接打印机失败，请重新选择打印机");

                            //跳到设置界面
                            SysUtils.startAct(OrderDetailActivity.this, new PrintActivity());
                        } else {
                            mService.connect(printDev);
                        }
                    } else {
                        SysUtils.showError("请开启蓝牙并且靠近打印机");
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //已经连接了，直接打印
            doPrint();
        }
    }

    public void doPrint() {
        //连接成功，开始打印，同时提交订单更新到服务器
        try {
            String index = order.getPrint_number();
            String shippingStr = order.getShippingStr2();
            String shopName = order.getSellerName();
            String deskNo = order.getDesk_num();
            String shopTel = order.getSellerTel();
            String orderDate = order.getOrderTime();
            boolean hasPay = order.getPayStatus() > 0;
            double payed = order.getPayed();
            String orderRemark = order.getMemo();
            boolean hasAddress = order.hasShippingAddr();
            String consignee = order.getShipName();
            String mobile = order.getShipMobile();
            String address = order.getShipAddr();

            String tmp = PrintUtil.getPrinterMsg(index,
                    shippingStr,
                    shopName,
                    deskNo,
                    shopTel,
                    order_id,
                    orderDate,
                    goodsList,
                    hasPay,
                    payed,
                    orderRemark,
                    hasAddress,
                    consignee,
                    mobile,
                    address);
            String printMsg = "";
            printMsg += tmp + "\n\n";

            sendMessage(printMsg);

            if(order.hasQrCode()) {
                addQrCode(order.getQrcode_url());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    private void addQrCode(final String uri) {
        final String filePath = QRCodeUtil.getFileRoot(OrderDetailActivity.this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage(uri, 360, 360, null, filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));

                            mService.printCenter();
                            sendMessage("扫码付款\n");
                            sendMessage(BitmapFactory.decodeFile(filePath));
                            sendMessage("\n\n\n");
                        }
                    });
                }
            }
        }).start();
    }

    private void sendMessage(Bitmap bitmap) {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            SysUtils.showError("蓝牙没有连接");
            return;
        }
        // 发送打印图片前导指令
//        byte[] start = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1B,
//                0x40, 0x1B, 0x33, 0x00 };
//        mService.write(start);

        /**获取打印图片的数据**/
//		byte[] send = getReadBitMapBytes(bitmap);

        mService.printCenter();
        byte[] draw2PxPoint = PicFromPrintUtils.draw2PxPoint(bitmap);

        mService.write(draw2PxPoint);
        // 发送结束指令
//        byte[] end = { 0x1d, 0x4c, 0x1f, 0x00 };
//        mService.write(end);
    }

    private void sendMessage(String message) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            SysUtils.showError("蓝牙没有连接");
            return;
        }
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send;
            try {
                send = message.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }

            mService.write(send);
        }
    }

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
