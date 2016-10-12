package com.ms.ks;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.global.Global;
import com.ms.util.CustomRequest;
import com.ms.util.LoginUtils;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoneyActivity extends BaseActivity {
    private EditText textView3;
    private PaperButton button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);

        textView3 = (EditText) findViewById(R.id.textView3);
        //充值
        button1 = (PaperButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String depositNumStr = textView3.getText().toString();
                double pay_money = 0;
                if(!StringUtils.isEmpty(depositNumStr)) {
                    pay_money = Double.parseDouble(depositNumStr);
                }

                if(pay_money <= 0) {
                    SysUtils.showError("请输入充值金额");
                } else {
                    Bundle b = new Bundle();
                    b.putDouble("pay_money", pay_money);

                    SysUtils.startAct(MoneyActivity.this, new PayActivity(), b);
                }
            }
        });
        button2 = (PaperButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String depositNumStr = textView3.getText().toString();
                double pay_money = 0;
                if(!StringUtils.isEmpty(depositNumStr)) {
                    pay_money = Double.parseDouble(depositNumStr);
                }

                if(pay_money <= 0) {
                    SysUtils.showError("请输入提现金额");
                } else {
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("money", String.valueOf(pay_money));

                    String uri = LoginUtils.isSeller() ? SysUtils.getSellerServiceUrl("deductAdvance") : SysUtils.getMemberServiceUrl("walletCenter");
                    CustomRequest r = new CustomRequest(Request.Method.POST, uri, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            hideLoading();

                            try {
                                JSONObject ret = SysUtils.didResponse(jsonObject);
                                String status = ret.getString("status");
                                String message = ret.getString("message");

                                if (!status.equals("200")) {
                                    SysUtils.showError(message);
                                } else {
                                    textView3.setText("");
                                    new MaterialDialog.Builder(MoneyActivity.this)
                                            .theme(SysUtils.getDialogTheme())
                                            .content("提现申请已提交，我们会尽快处理您的申请")
                                            .positiveText("知道啦")
                                            .show();
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hideLoading();

                            SysUtils.showNetworkError();
                        }
                    });

                    executeRequest(r);

                    showLoading(MoneyActivity.this, "请稍等......");
                }
            }
        });
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
