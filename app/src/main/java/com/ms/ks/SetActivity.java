package com.ms.ks;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.widget.Switch;
import com.ms.global.Global;
import com.ms.update.UpdateAsyncTask;
import com.ms.util.SysUtils;


public class SetActivity extends BaseUpdateActivity {
    private TextView cache_btn;
    private TextView setVersion;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        initToolbar(this);

        //语音提示
        View set_push = (View) findViewById(R.id.set_push_item);
        Switch ll_set_push_switch = (Switch) set_push.findViewById(R.id.ll_set_switch);
        ImageView ll_set_push_idx = (ImageView) set_push.findViewById(R.id.ll_set_idx);
        ll_set_push_idx.setVisibility(View.GONE);
        ll_set_push_switch.setChecked(KsApplication.getBoolean("notify_sound", true));
        ll_set_push_switch.setVisibility(View.VISIBLE);
        ll_set_push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                KsApplication.putBoolean("notify_sound", arg1);
            }
        });

        SysUtils.setLine(set_push, Global.SET_CELLUP, "语音提示", 0, null);

        //振动开关
        View set_net = (View) findViewById(R.id.set_net_item);
        Switch ll_set_net_switch = (Switch) set_net.findViewById(R.id.ll_set_switch);
        ImageView ll_set_net_idx = (ImageView) set_net.findViewById(R.id.ll_set_idx);
        ll_set_net_idx.setVisibility(View.GONE);
        ll_set_net_switch.setChecked(KsApplication.getBoolean("notify_vibrate", true));
        ll_set_net_switch.setVisibility(View.VISIBLE);
        ll_set_net_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                KsApplication.putBoolean("notify_vibrate", arg1);
            }
        });

        SysUtils.setLine(set_net, Global.SET_SINGLE_LINE, "振动开关", 0, null);

        //系统升级
        versionName = SysUtils.getAppVersionName(this);
        View set_update = (View) findViewById(R.id.set_cache_item);
        setVersion = (TextView) set_update.findViewById(R.id.ll_set_hint_text);
        SysUtils.setLine(set_update, Global.SET_TWO_LINE, "系统升级", 0, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVersion();
            }
        });
    }


    public void checkVersion() {
        UpdateAsyncTask myAsyncTask = new UpdateAsyncTask(SetActivity.this, true);
        myAsyncTask.execute();
    }

    //文件下载完成，尝试打开提醒用户安装
    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent intent) {
            //获取下载的文件id
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(downId > 0) {
                DownloadManager manager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri apk = manager.getUriForDownloadedFile(downId);

                Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                promptInstall.setDataAndType(apk, "application/vnd.android.package-archive");
                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(promptInstall);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (KsApplication.hasNewVersion) {
            setVersion.setText("发现有新版本 V" + KsApplication.newVersionName);
        } else {
            setVersion.setText("V" + versionName);
        }

        //注册监听器
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            //反注册监听器
            unregisterReceiver(onDownloadComplete);
        } catch(Exception e) {

        }
    }
}
