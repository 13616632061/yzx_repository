package com.ms.ks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
    RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3;
    TextView textView1, textView4;
    EditText textView3, textView6;
    LinearLayout linearlayout01, linearlayout02;
    private boolean is_eye_open = false;
    ImageView textView7;
    EditText textView9;
    private int fkType = 0;
    CheckBox cb_left, cb_right,cb_mainstore;
    private Spinner login_spinner;
    private  String[] type_list={"商家","总店","营业员","业务员"};
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView4 = (TextView) findViewById(R.id.textView4);
        login_spinner = (Spinner) findViewById(R.id.login_spinner);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,type_list);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        login_spinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        login_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(type_list[position].equals("商家")) {
                   fkType=1;
               }else if(type_list[position].equals("总店")){
                   fkType=3;

               } else if(type_list[position].equals("营业员")){
                   fkType=4;

               } else if(type_list[position].equals("业务员")){
                   fkType=2;

               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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


        cb_left = (CheckBox) findViewById(R.id.cb_left);
        cb_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFkType(2);
            }
        });
        cb_right = (CheckBox) findViewById(R.id.cb_right);
        cb_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFkType(1);
            }
        });
        cb_mainstore = (CheckBox) findViewById(R.id.cb_mainstore);
        cb_mainstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFkType(3);
            }
        });

        setFkType(fkType);

        PaperButton button1 = (PaperButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = textView3.getText().toString();

                if(StringUtils.isEmpty(username)) {
                    SysUtils.showError("用户名不能为空");
                } else {
                    final String password = textView6.getText().toString();

                    if(StringUtils.isEmpty(password)) {
                        SysUtils.showError("登录密码不能为空");
                    } else {
                        String captcha = textView9.getText().toString();

                        if(StringUtils.isEmpty(captcha)) {
                            SysUtils.showError("验证码不能为空");
                        } else {
                            String realCode = Code.getInstance().getCode();
                            if(!captcha.equalsIgnoreCase(realCode)) {
                                textView7.setImageBitmap(Code.getInstance().createBitmap());
                                SysUtils.showError("验证码不正确");
                            } else {
                                if (fkType != 1 && fkType != 2 && fkType != 3 && fkType != 4) {
                                    SysUtils.showError("请选择登录类型");
                                } else {
                                    Map<String,String> map = new HashMap<String,String>();
                                    map.put("key", Global.LOGIN_KEY);

                                    CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("sign"), map, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject jsonObject) {
                                            try {
                                                JSONObject ret = SysUtils.didResponse(jsonObject);
                                                String status = ret.getString("status");
                                                String message = ret.getString("message");
                                                String signs = ret.getString("data");

                                                if (!status.equals("200")) {
                                                    hideLoading();
                                                    SysUtils.showError(message);
                                                } else {
                                                    doLogin(username, password, signs);
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
            }
        });

        //忘记密码
        TextView textView11 = (TextView) findViewById(R.id.textView11);
        textView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(LoginActivity.this, new ForgetPasswordActivity());
                finish();
            }
        });
    }

    private void doLogin(final String username, String password, String sign) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("name", username);
        map.put("pwd", password);
        map.put("signs", sign);
        map.put("type", String.valueOf(fkType));

        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl("log"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();

                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    System.out.println("登录ret:"+ret);

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        SysUtils.showSuccess("登录成功");

                        textView6.setText("");
                        textView9.setText("");

                        KsApplication.putString("login_username", username);
                        JSONObject dataObject = ret.getJSONObject("data");
                        LoginUtils.afterLogin(LoginActivity.this, dataObject, false, fkType);

                        toAct();

                        finish();
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
    }

    private void setFkType(int type) {
        if(type == 1) {
            cb_left.setChecked(false);
            cb_mainstore.setChecked(false);
            cb_right.setChecked(true);
        } else if (type == 2){
            cb_left.setChecked(true);
            cb_right.setChecked(false);
            cb_mainstore.setChecked(false);
        }  else if (type == 3) {
            cb_left.setChecked(false);
            cb_right.setChecked(false);
            cb_mainstore.setChecked(true);
        }

        this.fkType = type;
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

    private void toAct() {
        if (LoginUtils.isSeller()) {
            //店铺
            SysUtils.startAct(LoginActivity.this, new ShopActivity());
        } else if (LoginUtils.isMember()) {
            //业务员
            SysUtils.startAct(LoginActivity.this, new ReportActivity());
        }else if (LoginUtils.isMainStore()) {
            //总店
            SysUtils.startAct(LoginActivity.this, new MainStoreActivity());
        }else if (LoginUtils.isShopper()) {
            //营业员
            SysUtils.startAct(LoginActivity.this, new ShopActivity());
        }
    }
}
