package com.ms.ks;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ms.util.SysUtils;

public class MoneyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_money, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_log) {
            //历史记录
            SysUtils.startAct(MoneyActivity.this, new MoneyLogActivity());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
