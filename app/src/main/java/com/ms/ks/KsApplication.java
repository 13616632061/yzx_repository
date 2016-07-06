package com.ms.ks;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ms.db.DBHelper;
import com.ms.global.Global;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.List;

public class KsApplication extends Application {
    private static SharedPreferences sp;
    private static Context sContext;
    private static boolean hasGetInviteInfo = false;
    public static String invite_reg_title, invite_wx_title, app_down_uri, invite_reg_resume, invite_wx_resume, invite_reg_picurl, invite_wx_picurl;
    private DBHelper sqlHelper;
    private static KsApplication mExamApplication;
    private File saveDir;

    public static boolean hasNewVersion = false;
    public static String newVersionName = "";


    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 在主进程设置信鸽相关的内容
        if (isMainProcess()) {
            // 为保证弹出通知前一定调用本方法，需要在application的onCreate注册
            // 收到通知时，会调用本回调函数。
            // 相当于这个回调会拦截在信鸽的弹出通知之前被截取
            // 一般上针对需要获取通知内容、标题，设置通知点击的跳转逻辑等等
            XGPushManager
                    .setNotifactionCallback(new XGPushNotifactionCallback() {

                        @Override
                        public void handleNotify(XGNotifaction xGNotifaction) {
                            Log.v("ks", "处理信鸽通知：" + xGNotifaction);
                            // 获取标签、内容、自定义内容
                            String title = xGNotifaction.getTitle();
                            String content = xGNotifaction.getContent();
                            String customContent = xGNotifaction
                                    .getCustomContent();
                            // 其它的处理
                            // 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
                            xGNotifaction.doNotify();
                        }
                    });
        }

        sContext = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        Global.screenWidth = dm.widthPixels;
        Global.screenHeight = dm.heightPixels;
        Global.magicWidth = Global.screenWidth;
        Global.magicHeight = Global.screenWidth * 8 / 15;

        initImageLoader(sContext);
        mExamApplication = this;

        //bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "900026905", false);
    }

    public static KsApplication getApp() {
        return mExamApplication;
    }

    // 初始化ImageLoader
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB硬盘缓存
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        ImageLoader.getInstance().init(config.build());
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }

    public static SharedPreferences getConfig() {
        return sp;
    }

    public static void putString(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String value) {
        return sp.getString(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public static void putInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int value) {
        return sp.getInt(key, value);
    }

    public static void putFloat(String key, float value) {
        sp.edit().putFloat(key, value).commit();
    }

    public static float getFloat(String key, float value) {
        return sp.getFloat(key, value);
    }

    public static void putLong(String key, long value) {
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(String key, long value) {
        return sp.getLong(key, value);
    }

    public static boolean isHasGetInviteInfo() {
        return hasGetInviteInfo;
    }

    public static void setHasGetInviteInfo(boolean h) {
        hasGetInviteInfo = h;
    }

    public static String getInvite_reg_title() {
        return invite_reg_title;
    }


    public static void setInvite_reg_title(String i) {
        invite_reg_title = i;
    }

    public static String getInvite_wx_title() {
        return invite_wx_title;
    }

    public static void setInvite_wx_title(String i) {
        invite_wx_title = i;
    }


    public DBHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new DBHelper(mExamApplication);
        return sqlHelper;
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        if (sqlHelper != null) {
            //尝试关闭sqlite连接
            sqlHelper.close();
        }

        super.onTerminate();
    }
}
