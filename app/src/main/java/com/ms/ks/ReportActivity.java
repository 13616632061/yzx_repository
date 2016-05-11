package com.ms.ks;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ms.global.Global;
import com.ms.util.SysUtils;

public class ReportActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initToolbar(this);

//        View set_tongji_item = (View) findViewById(R.id.set_tongji_item);
//        SysUtils.setLine(set_tongji_item, Global.SET_SINGLE_LINE, "收入统计", R.drawable.icon_tongji_item, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_set) {
            //设置
            SysUtils.startAct(ReportActivity.this, new MoneyActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
