package com.ms.ks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.adapter.GoodsSize_specAdapter;
import com.ms.entity.GoodsSizeInfo_spec;
import com.ms.listview.PinnedHeaderListView;
import com.ms.util.SysUtils;

import java.util.ArrayList;

public class GoodsSize_specActivity extends BaseActivity implements View.OnClickListener {
    private View view;
    private View layout_err, include_nowifi, include_noresult;
    private Button load_btn_refresh_net, load_btn_retry;
    private TextView load_tv_noresult;

    private PinnedHeaderListView list_goods_size_spec;
    private GoodsSize_specAdapter mGoodsSize_specAdapter;
    private TextView btn_set;
    private View toolbar_layout;
    private String goodprice;
    private String good_stocknum;
    private String goods_name;
    private String type_id;
    private String goods_id;

    private ArrayList<GoodsSizeInfo_spec> mGoodsSizeInfo_speclist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_size_spec);
        SysUtils.setupUI(this,findViewById(R.id.activity_goods_size_spec));
        initToolbar(this);

        Intent intent=getIntent();
        if(intent!=null){
            mGoodsSizeInfo_speclist=intent.getParcelableArrayListExtra("mGoodsSizeInfo_speclist");
            goodprice=intent.getStringExtra("goodprice");
            good_stocknum=intent.getStringExtra("good_stocknum");
            goods_name=intent.getStringExtra("goods_name");
            type_id=intent.getStringExtra("type_id");
            goods_id=intent.getStringExtra("goods_id");
        }

        initView();
    }

    private void initView() {
        layout_err = (View)findViewById(R.id.layout_err);
        include_noresult = layout_err.findViewById(R.id.include_noresult);
        load_btn_retry = (Button) layout_err.findViewById(R.id.load_btn_retry);
        load_btn_retry.setVisibility(View.GONE);
        load_tv_noresult = (TextView) layout_err.findViewById(R.id.load_tv_noresult);
        load_tv_noresult.setText("没有数据记录哦");
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
//                getGoodsSizeInfo();
            }
        });
        toolbar_layout=findViewById(R.id.toolbar_layout);
        btn_set= (TextView) toolbar_layout.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        btn_set.setBackgroundResource(R.color.primary_color);
        btn_set.setText("下一步");

        btn_set.setOnClickListener(this);

        list_goods_size_spec= (PinnedHeaderListView) findViewById(R.id.list_goods_size_spec);


        mGoodsSize_specAdapter=new GoodsSize_specAdapter(GoodsSize_specActivity.this,mGoodsSizeInfo_speclist);
        list_goods_size_spec.setAdapter(mGoodsSize_specAdapter);
        setView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set:
                if(mGoodsSizeInfo_speclist.size()>0){
                    boolean isHas=false;
                    for(int i=0;i<mGoodsSizeInfo_speclist.size();i++){
                        for(int j=0;j<mGoodsSizeInfo_speclist.get(i).getmGoodsSizeInfo_spec_infolist().size();j++){
                            if(mGoodsSizeInfo_speclist.get(i).getmGoodsSizeInfo_spec_infolist().get(j).isCheck()){
                                isHas=true;
                                break;
                            }
                        }
                        if(isHas){
                            break;
                        }

                    }
                    if(isHas){
                        Intent intent=new Intent(GoodsSize_specActivity.this,GoodsSize_spec_InfoActivity.class);
                        intent.putExtra("mGoodsSizeInfo_speclist",mGoodsSizeInfo_speclist);
                        intent.putExtra("goodprice",goodprice);
                        intent.putExtra("good_stocknum",good_stocknum);
                        intent.putExtra("goods_name",goods_name);
                        intent.putExtra("type_id", type_id);
                        intent.putExtra("goods_id", goods_id);
                        startActivity(intent);
                    }else {
                        Toast.makeText(GoodsSize_specActivity.this,"商品规格不能为空！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        GoodsSize_specActivity.this.registerReceiver(broadcastReceiver,new IntentFilter("GoodsSize_specActivity.Action"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GoodsSize_specActivity.this.unregisterReceiver(broadcastReceiver);
    }
    private void setView() {
        if(mGoodsSizeInfo_speclist.size() < 1) {
            //没有结果
            list_goods_size_spec.setVisibility(View.GONE);
            include_nowifi.setVisibility(View.GONE);
            include_noresult.setVisibility(View.VISIBLE);
            layout_err.setVisibility(View.VISIBLE);
        } else {
            //有结果
            include_noresult.setVisibility(View.GONE);
            include_nowifi.setVisibility(View.GONE);
            layout_err.setVisibility(View.GONE);
            list_goods_size_spec.setVisibility(View.VISIBLE);
        }
    }

    private void setNoNetwork() {
        //网络不通
        if(mGoodsSizeInfo_speclist.size() < 1) {
            if(!include_nowifi.isShown()) {
                list_goods_size_spec.setVisibility(View.GONE);
                include_noresult.setVisibility(View.GONE);
                include_nowifi.setVisibility(View.VISIBLE);
                layout_err.setVisibility(View.VISIBLE);
            }
        } else {
            SysUtils.showNetworkError();
        }
    }
}
