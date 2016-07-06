package com.ms.ks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Welcome2Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().from(this).inflate(R.layout.activity_welcome2, null);

        setContentView(view);

        new Handler().postDelayed(new Runnable(){
            public void run() {
                Intent i = new Intent(Welcome2Activity.this, WelcomeActivity.class);
                Welcome2Activity.this.startActivity(i);
                Welcome2Activity.this.finish();
            }
        }, 2000);
    }
}
