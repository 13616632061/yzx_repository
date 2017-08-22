package com.ms.ks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.ms.util.SysUtils;

public class GetMoneyExplainActivity extends BaseActivity {
     private ImageView iv_getmoney_rule;
    private WebView webview_rule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_money_explain);

        SysUtils.setupUI(GetMoneyExplainActivity.this,findViewById(R.id.activity_get_money_explain));
        initToolbar(this);


        webview_rule= (WebView) findViewById(R.id.webview_rule);
        webview_rule.loadUrl("http://www.czxshop.net/rule/main.html");
    }
}
