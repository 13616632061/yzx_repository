package com.ms.ks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ms.adapter.MoreShoppingSortAdapter;
import com.ms.entity.ShopperSortInfo;
import com.ms.listview.PagingListView;
import com.ms.util.SysUtils;

import java.util.ArrayList;

public class MoreShoppingSortActivity extends BaseActivity {

    private ArrayList<ShopperSortInfo> shopperSortInfos;
    private MoreShoppingSortAdapter moreShoppingSortAdapter;
    private PagingListView lv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shopping_sort);
        SysUtils.setupUI(MoreShoppingSortActivity.this,findViewById(R.id.activity_more_shopping_sort));
        initToolbar(this);

        initView();
        Intent intent=getIntent();
        if(intent!=null){
            shopperSortInfos=intent.getParcelableArrayListExtra("shopperSortInfos");
            moreShoppingSortAdapter=new MoreShoppingSortAdapter(MoreShoppingSortActivity.this,shopperSortInfos);
            lv_content.setAdapter(moreShoppingSortAdapter);
        }
    }

    private void initView() {
        lv_content= (PagingListView) findViewById(R.id.lv_content);
    }
}
