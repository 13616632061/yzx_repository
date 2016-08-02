package com.ms.ks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.global.Global;
import com.ms.update.UpdateAsyncTask;
import com.ms.util.CustomRequest;
import com.ms.util.LoginUtils;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {
//    private boolean hasSpash = false;
Boolean isExit = false;
    Boolean hasTask = false;
    Timer tExit;
    TimerTask task;
    RelativeLayout relativeLayout2, relativeLayout1, relativeLayout3;
    private static final int REQUEST_PERMISSION = 0;
    public IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, 0);
        View view = getLayoutInflater().from(this).inflate(R.layout.activity_welcome, null);

        setContentView(view);

        tExit = new Timer();
        task = new TimerTask() {
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };

        //普通登录
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSellerSite();
            }
        });

        //易星到家
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //店铺
        relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isSeller()) {
                    SysUtils.startAct(WelcomeActivity.this, new ShopActivity());
                } else if (LoginUtils.isMember()) {
                    SysUtils.startAct(WelcomeActivity.this, new ReportActivity());
                } else {
                    SysUtils.startAct(WelcomeActivity.this, new LoginActivity());
                }
            }
        });

        //检测版本更新
        checkVersion();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(isExit==false){
                isExit=true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                if(!hasTask){
                    tExit.schedule(task, 2000);
                }
            }else{
                finish();
                System.exit(0);
            }
        }

        return false;
    }

    public void checkVersion() {
        UpdateAsyncTask myAsyncTask = new UpdateAsyncTask(WelcomeActivity.this, false);
        myAsyncTask.execute();
    }


    private boolean hasInitWx = false;
    public void initWeixin() {
        if(!hasInitWx) {
            String wx_app_id = Global.isDebug ? Global.WX_DEBUG_APP_ID : Global.WX_APP_ID;

//            SysUtils.showSuccess(wx_app_id);
            api = WXAPIFactory.createWXAPI(this, wx_app_id, false);
            boolean ret = api.registerApp(wx_app_id);
//            SysUtils.showSuccess("to register: " + (ret ? "1" : "0"));

            hasInitWx = true;
        }
    }

    private void toH5() {
        wxLogin();
    }

    private void wxLogin() {
        initWeixin();
        if(!api.isWXAppInstalled()) {
            SysUtils.showError("微信未安装");
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "none";
            boolean ret = api.sendReq(req);
//            SysUtils.showSuccess("to login: " + (ret ? "1" : "0"));
        }
    }


    //接受广播，更新ui
    private BroadcastReceiver broadcastWeixinReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //微信登录返回
            if(intent.hasExtra("code")) {
                String code = intent.getStringExtra("code");

                //取得access token
                String u = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                        + (Global.isDebug ? Global.WX_DEBUG_APP_ID : Global.WX_APP_ID)
                        + "&secret=" + (Global.isDebug ? Global.WX_DEBUG_APP_SECRET : Global.WX_APP_SECRET)
                        + "&code=" + code + "&grant_type=authorization_code";
                CustomRequest r = new CustomRequest(Request.Method.GET, u, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hideLoading();

                        try {
                            if(jsonObject.has("access_token")) {
                                String token = jsonObject.getString("access_token");
                                String openid = jsonObject.getString("openid");

                                //根据token和openid去获取用户信息
                                weixinGetUserInfo(token, openid);
                            } else if(jsonObject.has("errcode")) {
                                SysUtils.showError(jsonObject.getString("errmsg"));
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

                showLoading(WelcomeActivity.this, "正在验证......");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastWeixinReceiver, new IntentFilter(Global.BROADCAST_WEIXIN_LOGIN_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(broadcastWeixinReceiver);
        } catch(Exception e) {

        }
    }

    private void weixinGetUserInfo(String token, String openid) {
        String u = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token
                + "&openid=" + openid + "&lang=zh_CN";
        CustomRequest r = new CustomRequest(Request.Method.GET, u, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();

                try {
                    if(jsonObject.has("openid")) {
                        KsApplication.putString("wxUserInfo", jsonObject.toString());

                        toSite();
                    } else if(jsonObject.has("errcode")) {
                        SysUtils.showError(jsonObject.getString("errmsg"));
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

        showLoading(WelcomeActivity.this, "正在获取用户身份......");
    }

    private void toSite() {
//        SysUtils.openUrl(WelcomeActivity.this, "http://www.smgypt.com/webview.php");
        SysUtils.openUrl(WelcomeActivity.this, "http://www.yzx6868.com/wap/seller-seller_page-56.html");
    }

    private void toSellerSite() {
        String u = KsApplication.getString("wxUserInfo", "");

        if (!StringUtils.isEmpty(u)) {
            toSite();
        } else {
            toH5();
        }
    }
}
