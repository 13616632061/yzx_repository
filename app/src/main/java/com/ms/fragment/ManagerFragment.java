package com.ms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.ms.adapter.MainStoreViewPagerAdapter;
import com.ms.ks.GoodsManagementActivity;
import com.ms.ks.Goods_Sales_StatisticsAcitvity;
import com.ms.ks.H5GoodsPreviewActivity;
import com.ms.ks.R;
import com.ms.ks.ShopActivity;
import com.ms.ks.WholeSaleOrdersActivity;
import com.ms.ks.WholeSaleOrdersActivity1;
import com.ms.util.BitmapCache;
import com.ms.util.BitmapUtils;
import com.ms.util.CustomRequest;
import com.ms.util.RequestManager;
import com.ms.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class  ManagerFragment extends BaseFragment {
    private ShopActivity mainAct;
    private ImageView iv_managmentfragment_back;
    private TextView tv_previews;
    private ViewPager viewpager;
    private LinearLayout point_layout;
    private RelativeLayout goods_management_layout,sales_statistics_layout,wholesaleorders_layout;
    private ArrayList<String> imageView_url=new ArrayList<String>();
    private ArrayList<ImageView>  imageView_list;
    private MainStoreViewPagerAdapter mainStoreViewPagerAdapter;
    private int preposition=0;//上一次高亮的位置
    private boolean isDragging=false;//判断图片是否拖拽
    private boolean isinitscoll=true;//判断图片是否g滚动
    private ImageView imageView_point ;

    //自动轮播
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int item=0;
            if(imageView_url.size()>1){
                item=viewpager.getCurrentItem()+1;
                viewpager.setCurrentItem(item);
                handler.sendEmptyMessageDelayed(0,3000);
            }else {
                item=viewpager.getCurrentItem();
                viewpager.setCurrentItem(item);
                handler.removeCallbacksAndMessages(null);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainAct = (ShopActivity) getActivity();
    }

    public static ManagerFragment newInstance() {
        ManagerFragment f = new ManagerFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mainAct.updateView("home");


        View view = inflater.inflate(R.layout.fragment_manager, container, false);
        initView(view);
        initListener();
        getData();

        return view;
    }

    private void getData() {
        //获取图片url
        //获取到图片，获取失败添加默认图片
        imageView_list=new ArrayList<ImageView>();
        if(imageView_url.size()>0){
            for(int i=0;i<imageView_url.size();i++){
                NetworkImageView networkImageView=new NetworkImageView(getActivity());
                RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
                ImageLoader imageLoader=new ImageLoader(requestQueue,new BitmapCache());
                networkImageView.setDefaultImageResId(R.drawable.mainstoredefaut);
                networkImageView.setErrorImageResId(R.drawable.mainstoredefaut);
                networkImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                networkImageView.setImageUrl(imageView_url.get(i),imageLoader);
                //将图片放进集合，创建适配器
                imageView_list.add(networkImageView);
                //添加图片的小圆点
                imageView_point.setBackgroundResource(R.drawable.viewpager_point_res);

                //设置滚动图片的左右间距
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(BitmapUtils.Dp2Px(getActivity(), 5),
                        BitmapUtils.Dp2Px(getActivity(), 5));
                if (i == preposition) {
                    imageView_point.setEnabled(true);
                } else {
                    imageView_point.setEnabled(false);
                    layoutParams.leftMargin = BitmapUtils.Dp2Px(getActivity(), 5);
                }
                imageView_point.setLayoutParams(layoutParams);
                point_layout.addView(imageView_point);
            }
        }
        //将图片放进集合，创建适配器
        mainStoreViewPagerAdapter = new MainStoreViewPagerAdapter(getActivity(), imageView_list, handler);
        viewpager.setAdapter(mainStoreViewPagerAdapter);
        //发消息
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    /**
     * 限制滚动
     */
    private void istonch(){
        viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isinitscoll){
                    return false;
                }else {
                    return true;
                }
            }
        });
    }
    //监听事件
    private void initListener() {
        //预览
        tv_previews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),H5GoodsPreviewActivity.class);
                startActivity(intent);
            }
        });
        //返回
        iv_managmentfragment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        //商品管理
        goods_management_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),GoodsManagementActivity.class);
                startActivity(intent);
            }
        });
        //商品销售统计
        sales_statistics_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Goods_Sales_StatisticsAcitvity.class);
                startActivity(intent);
            }
        });
        //批发订货
        wholesaleorders_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),WholeSaleOrdersActivity1.class);
                startActivity(intent);
            }
        });
        //设置自动滚动，及滚动监听
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int realposition=position%imageView_url.size();
                point_layout.getChildAt(preposition).setEnabled(false);
                point_layout.getChildAt(realposition).setEnabled(true);
                preposition=realposition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state==ViewPager.SCROLL_STATE_DRAGGING){
                    isDragging=true;

                }else if(state==ViewPager.SCROLL_STATE_SETTLING){

                }else if(state==ViewPager.SCROLL_STATE_IDLE&&isDragging){
                    handler.removeCallbacksAndMessages(null);
                    handler.sendEmptyMessageDelayed(0,3000);
                }

            }
        });
    }

    public void initView(View view) {
        iv_managmentfragment_back= (ImageView) view.findViewById(R.id.iv_managmentfragment_back);
        viewpager= (ViewPager) view.findViewById(R.id.viewpager);
        point_layout= (LinearLayout) view.findViewById(R.id.point_layout);
        goods_management_layout= (RelativeLayout) view.findViewById(R.id.goods_management_layout);
        sales_statistics_layout= (RelativeLayout) view.findViewById(R.id.sales_statistics_layout);
        wholesaleorders_layout= (RelativeLayout) view.findViewById(R.id.wholesaleorders_layout);
        tv_previews= (TextView) view.findViewById(R.id.tv_previews);

        imageView_point = new ImageView(getActivity());
        isinitscoll=true;
        //获取图片url
        getMainStore();
    }
    /**
     * 获取轮播图
     */
    private void getMainStore() {

        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("main_store"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    isinitscoll=true;
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("图片结果："+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject dataObject = ret.getJSONObject("data");

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        JSONArray array = dataObject.getJSONArray("banner");
                        if (array != null && array.length() > 0) {
                            if(imageView_url.size()>0){
                                imageView_url.clear();
                                imageView_list.clear();
                                point_layout.removeView(imageView_point);
                            }
                            for (int i = 0; i < array.length(); i++) {
                                String str_url=SysUtils.getWebUri()+array.get(i);
                                imageView_url.add(str_url);
                            }
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    getData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                SysUtils.showError("网络不给力");
                imageView_list=new ArrayList<ImageView>();
                ImageView imageView=new ImageView(getActivity());
                imageView.setBackgroundResource(R.drawable.mainstoredefaut);
                imageView_list.add(imageView);
                mainStoreViewPagerAdapter = new MainStoreViewPagerAdapter(getActivity(), imageView_list, handler);
                viewpager.setAdapter(mainStoreViewPagerAdapter);
                isinitscoll=false;
                istonch();
            }
        });

        executeRequest(r);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestManager.cancelAll(this);
    }
}

