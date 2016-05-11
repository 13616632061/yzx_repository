package com.ms.ks;

import android.os.Bundle;

public class MsgActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        initToolbar(this);
    }
}
