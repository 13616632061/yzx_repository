package com.ms.ks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ms.update.UpdateAsyncTask;
import com.ms.util.SysUtils;

import java.util.Timer;
import java.util.TimerTask;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.lang.ref.WeakReference;

public class WelcomeActivity extends BaseActivity {
//    private boolean hasSpash = false;
Boolean isExit = false;
    Boolean hasTask = false;
    Timer tExit;
    TimerTask task;
    RelativeLayout relativeLayout2, relativeLayout1, relativeLayout3;
    Message m = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, 0);
        XGPushConfig.enableDebug(this, true);
        View view = getLayoutInflater().from(this).inflate(R.layout.activity_welcome, null);

        setContentView(view);


        // 1.获取设备Token
        Handler handler = new HandlerExtension(WelcomeActivity.this);
        m = handler.obtainMessage();
        // 注册接口
        XGPushManager.registerPush(getApplicationContext(),
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        Log.v("ks",
                                "+++ register push sucess. token:" + data);
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        Log.v("ks",
                                "+++ register push fail. token:" + data
                                        + ", errCode:" + errCode + ",msg:"
                                        + msg);
                    }
                });

        tExit = new Timer();
        task = new TimerTask() {
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };

        //业务员
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(WelcomeActivity.this, new ReportActivity());
            }
        });

        //商城
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.openUrl(WelcomeActivity.this, "http://www.yzx6868.com/wap/");
            }
        });

        //店铺
        relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SysUtils.startAct(WelcomeActivity.this, new ShopActivity());
            }
        });

        //检测版本更新
        checkVersion();
    }



    private static class HandlerExtension extends Handler {
        WeakReference<WelcomeActivity> mActivity;

        HandlerExtension(WelcomeActivity activity) {
            mActivity = new WeakReference<WelcomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WelcomeActivity theActivity = mActivity.get();
            if (theActivity == null) {
                theActivity = new WelcomeActivity();
            }
            if (msg != null) {
            }
            // XGPushManager.registerCustomNotification(theActivity,
            // "BACKSTREET", "BOYS", System.currentTimeMillis() + 5000, 0);
        }
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
}
