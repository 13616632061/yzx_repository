package com.ms.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.entity.Order;
import com.ms.global.Global;
import com.ms.ks.OrderDetailActivity;
import com.ms.ks.R;
import com.ms.listview.PagingListView;
import com.ms.util.CustomRequest;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;
import com.ms.view.BaseLazyLoadFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderFragment extends BaseLazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener{
    private int my_position;
    private PagingListView lv_content;
    private SwipeRefreshLayout refresh_header;
    public ArrayList<Order> cat_list;
    private PartyAdapter adapter;
    private boolean hasInit = false;
    int PAGE = 0;
    int NUM_PER_PAGE = 20;
    boolean loadingMore = false;
    private boolean isPrepared; //标识是否初始化完成

    private View layout_err, include_nowifi, include_noresult;
    private Button load_btn_refresh_net, load_btn_retry;
    private TextView load_tv_noresult;
    private String type = "";

    private String orderId = "";
    private int pay = 1;    //默认已支付

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        my_position = getArguments() != null ? getArguments().getInt("position") : 0;
        type = getArguments() != null ? getArguments().getString("type") : "";
    }

    public static OrderFragment newInstance(int position, String type) {
        OrderFragment f = new OrderFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("type", type);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        layout_err = (View) view.findViewById(R.id.layout_err);
        include_noresult = layout_err.findViewById(R.id.include_noresult);
        load_btn_retry = (Button) layout_err.findViewById(R.id.load_btn_retry);
        load_btn_retry.setVisibility(View.GONE);
        load_tv_noresult = (TextView) layout_err.findViewById(R.id.load_tv_noresult);
        load_tv_noresult.setText("还没有订单记录哦");
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
                loadFirst();
            }
        });

        cat_list = new ArrayList<Order>();
        lv_content = (PagingListView) view.findViewById(R.id.lv_content);

        lv_content.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (loadingMore) {
                    //加载更多
                    loadData();
                } else {
                    updateAdapter();
                }
            }
        });
        adapter = new PartyAdapter();
        lv_content.setAdapter(adapter);

        //点击
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int actualPosition = position - lv_content.getHeaderViewsCount();
                if (actualPosition >= 0 && actualPosition < cat_list.size()) {
                    //点击
                    Order data = cat_list.get(actualPosition);

                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", data.getOrderSn());
                    SysUtils.startAct(getActivity(), new OrderDetailActivity(), bundle);
                }
            }
        });

        refresh_header = (SwipeRefreshLayout) view.findViewById(R.id.refresh_header);
        refresh_header.setColorSchemeResources(R.color.ptr_red, R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow);
        refresh_header.setOnRefreshListener(this);

        isPrepared = true;
        lazyLoad();

        return view;
    }
    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }

        if(!hasInit) {
            refresh_header.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    loadFirst();
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        loadFirst();
    }

    private void loadFirst() {
        PAGE = 1;
        loadData();
    }

    private void setRefreshing(boolean refreshing) {
        refresh_header.setRefreshing(refreshing);
    }

    private void loadData() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("page", String.valueOf(PAGE));
        map.put("pagelimit", String.valueOf(NUM_PER_PAGE));
        map.put("type", String.valueOf(my_position));
        map.put("pay", String.valueOf(pay));

        String uri = type.equals("1") ? SysUtils.getSellerServiceUrl("orders") : SysUtils.getSellerServiceUrl("dispose");

        CustomRequest r = new CustomRequest(Request.Method.POST, uri, map, new Response.Listener<JSONObject>() {
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
                        if(PAGE <= 1) {
                            cat_list.clear();
                        }

                        JSONArray array = dataObject.getJSONArray("orders_info");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.optJSONObject(i);

                                Order b = SysUtils.getOrderRow(data);

                                cat_list.add(b);
                            }
                        }
                    }

                    if(!hasInit) {
                        hasInit = true;
                    }


                    int total = dataObject.getInt("total");
                    int totalPage = (int)Math.ceil((float)total / NUM_PER_PAGE);
                    loadingMore = totalPage > PAGE;

                    setView();

                    if (loadingMore) {
                        PAGE++;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    updateAdapter();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                setRefreshing(false);
                lv_content.setIsLoading(false);
                setNoNetwork();
            }
        });

        executeRequest(r);
    }

    private void updateAdapter() {
        lv_content.onFinishLoading(loadingMore, null);
        adapter.notifyDataSetChanged();
    }


    public class PartyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        public PartyAdapter() {
            super();
            this.inflater = LayoutInflater.from(getActivity());

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.placeholder_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
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
                    convertView = inflater.inflate(R.layout.item_order, null);

                    holder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
                    holder.textView10 = (TextView) convertView.findViewById(R.id.textView10);
                    holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
                    holder.textView5 = (TextView) convertView.findViewById(R.id.textView5);
                    holder.textView6 = (TextView) convertView.findViewById(R.id.textView6);
                    holder.textView7 = (TextView) convertView.findViewById(R.id.textView7);

                    holder.linearLayout5 = (LinearLayout) convertView.findViewById(R.id.linearLayout5);
                    holder.editText1 = (TextView) convertView.findViewById(R.id.editText1);
                    holder.editText2 = (TextView) convertView.findViewById(R.id.editText2);
                    holder.textView11 = (TextView) convertView.findViewById(R.id.textView11);

                    convertView.setTag(holder);
                } catch (Exception e) {
                }
            }else{
                holder = (ViewHolder) convertView.getTag();
            }


            final Order data = cat_list.get(position);
            if(data != null) {
//                if (data.getPayStatus() == 1) {
//                    //已付款
//                    holder.linearlayout01.setBackgroundResource(R.drawable.selector_cell_pressed);
//                } else {
//                    holder.linearlayout01.setBackgroundResource(R.drawable.selector_cell_pressed_2);
//                }

                holder.textView3.setText(data.getOrderTime());
                holder.textView10.setText(Html.fromHtml(data.getStatusStr()));
//                holder.textView10.setText(data.getPayStatusStr());
//
//                if (data.getPayStatus() == 1) {
//                    holder.textView10.setTextColor(textColor);
//                } else {
//                    holder.textView10.setTextColor(redColor);
//                }

//                if (data.hasShippingAddr()) {
//                    holder.textView2.setText("配送地址：" + data.getShipAddr());
//                    holder.textView2.setVisibility(View.VISIBLE);
//                } else {
//                    holder.textView2.setVisibility(View.GONE);
//                }
                holder.imageView1.setImageResource(data.getShippingRes());
//                holder.textView4.setText(data.getShippingStr());
                holder.textView5.setText("订单号：" + data.getOrderSn());

                if (data.getCost_item() > 0) {
                    holder.textView7.setText(SysUtils.getMoneyFormat(data.getCost_item()));
                } else {
                    holder.textView7.setText("");
                }

                if(!TextUtils.isEmpty(data.getOrder_num())) {
                    holder.textView11.setText("#" + data.getOrder_num());
                    holder.textView11.setVisibility(View.VISIBLE);
                } else {
                    holder.textView11.setVisibility(View.GONE);
                }

//                if (data.canClose() || data.canComplete()) {
//                    if (data.canComplete()) {
//                        holder.editText1.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.editText1.setVisibility(View.GONE);
//                    }
//
//                    if (data.canClose()) {
//                        holder.editText2.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.editText2.setVisibility(View.GONE);
//                    }
//
//                    holder.linearLayout5.setVisibility(View.VISIBLE);
//                } else {
//                    holder.linearLayout5.setVisibility(View.GONE);
//                }
                holder.linearLayout5.setVisibility(View.GONE);

                //点击确认
                holder.editText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(getActivity())
                                .content("确认订单？")
                                .theme(SysUtils.getDialogTheme())
                                .positiveText("确定")
                                .negativeText("取消")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        Map<String,String> map = new HashMap<String,String>();
                                        map.put("order_id", data.getOrderSn());
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

                                                        getActivity().sendBroadcast(new Intent(Global.BROADCAST_REFRESH_ORDER_ACTION)
                                                                .putExtra("type", 2));

                                                        getActivity().sendBroadcast(new Intent(Global.BROADCAST_AFFIRM_ORDER_ACTION)
                                                                .putExtra("order_id", data.getOrderSn()));
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
                                        showLoading(getActivity());
                                    }
                                })
                                .show();
                    }
                });

                //取消
                holder.editText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(getActivity())
                                .content("确认取消订单？")
                                .theme(SysUtils.getDialogTheme())
                                .positiveText("确定")
                                .negativeText("取消")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        Map<String,String> map = new HashMap<String,String>();
                                        map.put("order_id", data.getOrderSn());
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

                                                        getActivity().sendBroadcast(new Intent(Global.BROADCAST_REFRESH_ORDER_ACTION)
                                                                .putExtra("type", 2));

                                                        getActivity().sendBroadcast(new Intent(Global.BROADCAST_AFFIRM_ORDER_ACTION)
                                                                .putExtra("order_id", data.getOrderSn()));
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
                                        showLoading(getActivity());
                                    }
                                })
                                .show();
                    }
                });
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView textView3, textView10, textView5, textView6, textView7, textView11;
        public LinearLayout linearLayout5;
        public TextView editText1, editText2;
        public ImageView imageView1;
    }

    private void setView() {
        if(PAGE <= 1) {
//            Log.v("ks", "size: " + cat_list.size());
            if(cat_list.size() < 1) {
                //没有结果
                lv_content.setVisibility(View.GONE);
                include_nowifi.setVisibility(View.GONE);
                include_noresult.setVisibility(View.VISIBLE);
                layout_err.setVisibility(View.VISIBLE);
            } else {
                //有结果
                include_noresult.setVisibility(View.GONE);
                include_nowifi.setVisibility(View.GONE);
                layout_err.setVisibility(View.GONE);
                lv_content.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setNoNetwork() {
        //网络不通
        if(PAGE <= 1 && cat_list.size() < 1) {
            if(!include_nowifi.isShown()) {
                lv_content.setVisibility(View.GONE);
                include_noresult.setVisibility(View.GONE);
                include_nowifi.setVisibility(View.VISIBLE);
                layout_err.setVisibility(View.VISIBLE);
            }
        } else {
            SysUtils.showNetworkError();
        }
    }


    //更新广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean canRefresh = false;
            if(intent.hasExtra("type")) {
                int my_type = intent.getIntExtra("type", 0);

                if(my_type > 0 && my_type == Integer.parseInt(type)) {
                    canRefresh = true;
                }
            }

            if(canRefresh) {
                refresh_header.post(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshing(true);
                        loadFirst();
                    }
                });
            }
        }
    };

    private BroadcastReceiver broadcastCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("order_id")) {
                String order_id = intent.getStringExtra("order_id");
                if (!StringUtils.isEmpty(order_id)) {
                    for (int i = 0; i < cat_list.size(); i++) {
                        Order bean = cat_list.get(i);

                        if (bean.getOrderSn().equals(order_id)) {
                            cat_list.remove(i);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    };

    private BroadcastReceiver broadcastAffirmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("order_id")) {
                String order_id = intent.getStringExtra("order_id");
                if (!StringUtils.isEmpty(order_id)) {
                    for (int i = 0; i < cat_list.size(); i++) {
                        Order bean = cat_list.get(i);

                        if (bean.getOrderSn().equals(order_id)) {
                            cat_list.remove(i);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REFRESH_ORDER_ACTION));

        if (type.equals("1")) {
            //新订单
            getActivity().registerReceiver(broadcastCancelReceiver, new IntentFilter(Global.BROADCAST_CANCEL_ORDER_ACTION));
            getActivity().registerReceiver(broadcastAffirmReceiver, new IntentFilter(Global.BROADCAST_AFFIRM_ORDER_ACTION));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getActivity().unregisterReceiver(broadcastReceiver);

            if (type.equals("1")) {
                //新订单
                getActivity().unregisterReceiver(broadcastCancelReceiver);
                getActivity().unregisterReceiver(broadcastAffirmReceiver);
            }
        } catch(Exception e) {

        }
    }

    public void setPay(int pay) {
        this.pay = pay;

        if(isPrepared) {
//            Log.v("ks", "pay: " + pay);
            refresh_header.post(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                    loadFirst();
                }
            });
        }
    }
}