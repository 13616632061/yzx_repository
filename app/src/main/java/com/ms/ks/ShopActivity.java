package com.ms.ks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.ms.adapter.DisposeFragment;
import com.ms.adapter.MainFragment;
import com.ms.adapter.ProfileFragment;
import com.ms.adapter.ReportFragment;
import com.ms.global.Global;
import com.ms.util.LoginUtils;
import com.ms.util.SysUtils;
import com.ms.view.FragmentTabHost;

public class ShopActivity extends BaseActivity implements OnTabChangeListener {
    private String init_tab = "";

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {MainFragment.class,DisposeFragment.class,ReportFragment.class,ProfileFragment.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.selector_btn_main,R.drawable.selector_btn_main2,R.drawable.selector_btn_report,R.drawable.selector_btn_profile};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"新订单", "已处理", "营业统计", "商户中心"};


    //定义数组来存放Fragment界面
    private Class fragmentArray2[] = {MainFragment.class,DisposeFragment.class,ProfileFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray2[] = {R.drawable.selector_btn_main,R.drawable.selector_btn_main2,R.drawable.selector_btn_profile};
    //Tab选项卡的文字
    private String mTextviewArray2[] = {"新订单", "已处理", "商户中心"};


    private String current_tab = "";

    private boolean jurisdiction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, 1);
        setContentView(R.layout.activity_shop);

        initToolbar(this);
        setTitle(null);
        setToolbarTitle("新订单");

        jurisdiction = LoginUtils.jurisdiction();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("init_tab")) {
                init_tab = bundle.getString("init_tab");
            }
        }

        initView();
    }

    /**
     * 初始化组件
     */
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.setOnTabChangedListener(this);

        //得到fragment的个数
        int count = jurisdiction ? fragmentArray2.length : fragmentArray.length;

        for(int i = 0; i < count; i++){
            if (jurisdiction) {
                TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray2[i]).setIndicator(getTabItemView(i));
                mTabHost.addTab(tabSpec, fragmentArray2[i], null);
            } else {
                TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
                mTabHost.addTab(tabSpec, fragmentArray[i], null);
            }
            //设置Tab按钮的背景
//            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

        if(init_tab.equals("user")) {
            updateView("user");
            mTabHost.setCurrentTab(4);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_home_image);
        TextView textView = (TextView) view.findViewById(R.id.iv_home_text);

        if (jurisdiction) {
            imageView.setImageResource(mImageViewArray2[index]);
            textView.setText(mTextviewArray2[index]);
        } else {
            imageView.setImageResource(mImageViewArray[index]);
            textView.setText(mTextviewArray[index]);
        }

        return view;
    }

    public void updateView(String type) {
        current_tab = type;
        if(type.equals("main2")) {
            setToolbarTitle("已处理");
        } else if(type.equals("report")) {
            setToolbarTitle("营业统计");
        } else if(type.equals("profile")) {
            setToolbarTitle("商户中心");
        } else {
            setToolbarTitle("新订单");
        }
        toolbar.setVisibility(View.VISIBLE);

        invalidateOptionsMenu();
    }

    @Override
    public void onTabChanged(String tabId) {
        if(tabId.equals("已处理")) {
            updateView("main2");
        } else if(tabId.equals("营业统计")) {
            updateView("report");
        } else if(tabId.equals("商户中心")) {
            updateView("profile");
        } else if(tabId.equals("新订单")) {
            updateView("main");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(current_tab.equals("main") || current_tab.equals("main2")) {
            getMenuInflater().inflate(R.menu.menu_search, menu);
        } else if(current_tab.equals("profile")) {
            getMenuInflater().inflate(R.menu.menu_refresh, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(current_tab.equals("main") || current_tab.equals("main2")) {
            if (id == R.id.menu_search) {
                SysUtils.startAct(ShopActivity.this, new SearchActivity());
                return true;
            }
        } else if(current_tab.equals("profile")) {
            if (id == R.id.menu_refresh) {
                sendBroadcast(new Intent(Global.BROADCAST_REFRESH_PROFILE_ACTION)
                        .putExtra("type", 2));
//                SysUtils.startAct(ShopActivity.this, new SearchActivity());
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle b = data.getExtras();
            //城市定位
            if(b != null && b.containsKey("refreshReport")) {
                sendBroadcast(new Intent(Global.BROADCAST_REFRESH_SHOP_REPORT_ACTION));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
