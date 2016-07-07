package com.ms.ks;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ms.update.UpdateAsyncTask;
import com.ms.util.SysUtils;

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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);

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
