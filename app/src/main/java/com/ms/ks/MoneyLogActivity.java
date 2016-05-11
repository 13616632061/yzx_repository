package com.ms.ks;

import android.os.Bundle;

public class MoneyLogActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_log);

        initToolbar(this);
    }
}
