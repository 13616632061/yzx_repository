package com.ms.ks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.global.Global;
import com.ms.util.Code;
import com.ms.util.CustomRequest;
import com.ms.util.DeleteEditText;
import com.ms.util.LoginUtils;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    TextView textView1, textView4;
    EditText textView3, textView6;
    private boolean is_eye_open = false;
    ImageView textView7;
    EditText textView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this, 3);

        setToolbarTitle(null);
        //隐藏工具栏
        setToolbarTitle(null);
        toolbar.setVisibility(View.GONE);


        ImageButton btn_done = (ImageButton) findViewById(R.id.btn_back);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        textView1 = (TextView) findViewById(R.id.textView1);
        textView4 = (TextView) findViewById(R.id.textView4);

        textView3 = (EditText) findViewById(R.id.textView3);    //账号
        new DeleteEditText(textView3, textView1);
        if(!StringUtils.isEmpty(KsApplication.getString("login_username", ""))) {
            textView3.setText(KsApplication.getString("login_username", ""));
        }

        //密码
        textView6 = (EditText) findViewById(R.id.textView6);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_eye_open) {
                    textView6.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    textView4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);

                    is_eye_open = false;
                } else {
                    textView6.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    textView4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);

                    is_eye_open = true;
                }
                textView6.setSelection(textView6.getText().length());
            }
        });

        //验证码
        textView7  =(ImageView) findViewById(R.id.textView7);
        textView7.setImageBitmap(Code.getInstance().createBitmap());
        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView7.setImageBitmap(Code.getInstance().createBitmap());
            }
        });
        textView9 = (EditText) findViewById(R.id.textView9);

        PaperButton button1 = (PaperButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = textView3.getText().toString();

                if(StringUtils.isEmpty(username)) {
                    SysUtils.showError("用户名不能为空");
                } else {
                    String password = textView6.getText().toString();

                    if(StringUtils.isEmpty(password)) {
                        SysUtils.showError("登录密码不能为空");
                    } else {
                        String captcha = textView9.getText().toString();

                        if(StringUtils.isEmpty(captcha)) {
                            SysUtils.showError("验证码不能为空");
                        } else {
                            String realCode = Code.getInstance().getCode();
                            if(!captcha.equalsIgnoreCase(realCode)) {
                                SysUtils.showError("验证码不正确");
                            } else {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("username", username);
                                map.put("password", password);

                                Map<String,String> postMap = SysUtils.apiCall(LoginActivity.this, map);

                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl("user/login"), postMap, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        hideLoading();

                                        try {
                                            int error = jsonObject.getInt("code");
                                            if(error > 0) {
                                                String errstr = jsonObject.getString("message");
                                                SysUtils.showError(errstr);
                                            } else {
                                                SysUtils.showSuccess("登录成功");

                                                KsApplication.putString("login_username", username);

                                                LoginUtils.afterLogin(LoginActivity.this, jsonObject);
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

                                showLoading(LoginActivity.this, "正在登录......");

                            }

                        }
                    }
                }
            }
        });
    }


    //接受广播，更新ui
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //注册成功了，销毁这个界面
            finish();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(broadcastReceiver, new IntentFilter(Global.BROADCAST_REGISTER_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(broadcastReceiver);
        } catch(Exception e) {

        }
    }
}
