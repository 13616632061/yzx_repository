package com.ms.ks;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.entity.Order;
import com.ms.listview.PagingListView;
import com.ms.util.CustomRequest;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mEdit;
    private String mSearchText;
    private PagingListView lv_content;
    private SwipeRefreshLayout refresh_header;
    public ArrayList<Order> cat_list;
    private PartyAdapter adapter = null;
    private View layout_err, include_nowifi, include_noresult;
    private Button load_btn_refresh_net, load_btn_retry;
    private TextView load_tv_noresult;

    int PAGE = 1;
    int NUM_PER_PAGE = 20;
    boolean loadingMore = false;

    private int textColor, redColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initToolbar(this);


        textColor = getResources().getColor(R.color.text_color);
        redColor = getResources().getColor(R.color.red_color);

        layout_err = (View) findViewById(R.id.layout_err);
        include_noresult = layout_err.findViewById(R.id.include_noresult);
        load_btn_retry = (Button) layout_err.findViewById(R.id.load_btn_retry);
        load_btn_retry.setVisibility(View.GONE);
        load_tv_noresult = (TextView) layout_err.findViewById(R.id.load_tv_noresult);
        load_tv_noresult.setText("订单搜索结果为空");
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
                doSearch();
            }
        });

        cat_list = new ArrayList<Order>();
        lv_content = (PagingListView) findViewById(R.id.lv_content);
        lv_content.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                if (loadingMore) {
                    //加载更多
                    __doSearch();
                } else {
                    updateAdapter();
                }
            }
        });

        //点击
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int actualPosition = position - lv_content.getHeaderViewsCount();
                if (actualPosition >= 0 && actualPosition < cat_list.size()) {
                    //点击
                    Order data = cat_list.get(actualPosition);

                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", data.getOrderSn());
                    SysUtils.startAct(SearchActivity.this, new OrderDetailActivity(), bundle);
                }
            }
        });

        refresh_header = (SwipeRefreshLayout) findViewById(R.id.refresh_header);
        refresh_header.setColorSchemeResources(R.color.ptr_red, R.color.ptr_blue, R.color.ptr_green, R.color.ptr_yellow);
        refresh_header.setOnRefreshListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_search, menu);

        final MenuItem item = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);

        setSearchView();
        return true;
    }


    private void setSearchView() {
        mSearchView.setIconified(false);
        mSearchView.setIconifiedByDefault(false);

        //更改输入框样式
        final int editViewId = getResources().getIdentifier("search_src_text", "id", getPackageName());
        mEdit = (SearchView.SearchAutoComplete) mSearchView.findViewById(editViewId);
        if (mEdit != null) {
            try {
                //保证有个光标
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(mEdit, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
            } catch (Exception ex) {}
//            mEdit.setHintTextColor(getResources().getColor(R.color.gray_bg));
//            mEdit.setTextColor(getResources().getColor(R.color.white));
//            mEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            mEdit.setHint("订单搜索..");
        }

        //移除搜索图标
        int magId = getResources().getIdentifier("search_mag_icon", "id", getPackageName());
        ImageView magImage = (ImageView) mSearchView.findViewById(magId);
        if (magImage != null) {
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

        //更改提交按钮
        int goButtonId = getResources().getIdentifier("search_go_btn", "id", getPackageName());
        ImageView goButtonImage = (ImageView) mSearchView.findViewById(goButtonId);
        if (goButtonImage != null) {
            goButtonImage.setImageResource(R.drawable.icon_search);
        }
//
        mSearchView.setSubmitButtonEnabled(true);
//        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                mSearchView.clearFocus();
                mSearchText = query;
                doSearch();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    if (mSearchView.hasFocus()) {
                        SysUtils.hideSoftKeyboard(SearchActivity.this);
                    }
                }
                return false;
            }
        });
//        mSearchView.clearFocus();   //默认隐藏键盘
    }

    private void setRefreshing(boolean refreshing) {
        refresh_header.setRefreshing(refreshing);
    }

    private void doSearch() {
        setRefreshing(true);
        __doSearch();
    }



    private void __doSearch() {
        if(!StringUtils.isEmpty(mSearchText)) {
            if (mSearchView.hasFocus()) {
                SysUtils.hideSoftKeyboard(this);
            }

            Map<String,String> map = new HashMap<String,String>();
            map.put("page", String.valueOf(PAGE));
            map.put("pagelimit", String.valueOf(NUM_PER_PAGE));
            map.put("filter", mSearchText);

            CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("search"), map, new Response.Listener<JSONObject>() {
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

                            JSONArray array = dataObject.getJSONArray("orders_list");
                            if (array != null && array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject data = array.optJSONObject(i);

                                    Order b = SysUtils.getOrderRow(data);

                                    cat_list.add(b);
                                }
                            }
                        }

                        int total = dataObject.getInt("total");
                        int totalPage = (int)Math.ceil(total / NUM_PER_PAGE);
                        loadingMore = totalPage > PAGE;

                        if (loadingMore) {
                            PAGE++;
//                            SysUtils.showSuccess("more");
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
                        setView();

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

        } else {
            SysUtils.showError("搜索关键字不能为空");

            setRefreshing(false);
        }
    }


    private void updateAdapter() {
        lv_content.onFinishLoading(loadingMore, null);

        if(adapter == null) {
            adapter = new PartyAdapter();
            lv_content.setAdapter(adapter);
//            lv_content.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        __doSearch();
    }

    public class PartyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        public PartyAdapter() {
            super();
            this.inflater = LayoutInflater.from(SearchActivity.this);

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
                    holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                    holder.textView4 = (TextView) convertView.findViewById(R.id.textView4);
                    holder.textView5 = (TextView) convertView.findViewById(R.id.textView5);
                    holder.relativeLayout1 = (RelativeLayout) convertView.findViewById(R.id.relativeLayout1);
                    holder.textView6 = (TextView) convertView.findViewById(R.id.textView6);
                    holder.textView7 = (TextView) convertView.findViewById(R.id.textView7);

                    holder.linearLayout5 = (LinearLayout) convertView.findViewById(R.id.linearLayout5);
                    holder.editText1 = (TextView) convertView.findViewById(R.id.editText1);
                    holder.editText2 = (TextView) convertView.findViewById(R.id.editText2);

                    convertView.setTag(holder);
                } catch (Exception e) {
                }
            }else{
                holder = (ViewHolder) convertView.getTag();
            }


            final Order data = cat_list.get(position);
            if(data != null) {
                holder.textView3.setText(data.getOrderTime());
                holder.textView10.setText(data.getPayStatusStr());

                if (data.getPayStatus() == 1) {
                    holder.textView10.setTextColor(textColor);
                } else {
                    holder.textView10.setTextColor(redColor);
                }

                if (data.hasShippingAddr()) {
                    holder.textView2.setText("配送地址：" + data.getShipAddr());
                    holder.textView2.setVisibility(View.VISIBLE);
                } else {
                    holder.textView2.setVisibility(View.GONE);
                }
                holder.textView4.setText(data.getShippingStr());
                holder.textView5.setText("订单号：" + data.getOrderSn());

                if (data.getPayed() > 0 || !StringUtils.isEmpty(data.getName())) {
                    if (!StringUtils.isEmpty(data.getName())) {
                        holder.textView6.setText("下单用户：" + data.getName());
                    } else {
                        holder.textView6.setText("");
                    }

                    if (data.getPayed() > 0) {
                        holder.textView7.setText(SysUtils.getMoneyFormat(data.getPayed()));
                    } else {
                        holder.textView7.setText("");
                    }
                    holder.relativeLayout1.setVisibility(View.VISIBLE);
                } else {
                    holder.relativeLayout1.setVisibility(View.GONE);
                }

                holder.linearLayout5.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView textView3, textView10, textView2, textView4, textView5, textView6, textView7;
        public RelativeLayout relativeLayout1;
        public LinearLayout linearLayout5;
        public TextView editText1, editText2;
    }


    private void setView() {
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

    private void setNoNetwork() {
        //网络不通
        if(!include_nowifi.isShown()) {
            lv_content.setVisibility(View.GONE);
            include_noresult.setVisibility(View.GONE);
            include_nowifi.setVisibility(View.VISIBLE);
            layout_err.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        SysUtils.hideSoftKeyboard(SearchActivity.this);
    }
}
