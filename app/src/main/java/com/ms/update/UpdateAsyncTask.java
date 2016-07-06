package com.ms.update;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ms.global.Global;
import com.ms.ks.KsApplication;
import com.ms.util.DialogUtils;
import com.ms.util.SysUtils;

import java.util.Locale;
import java.util.Map;

public class UpdateAsyncTask {
    Context context;
    boolean showLoading = false;
    private Dialog progressDialog = null;
    private RequestQueue mQueue;

    public UpdateAsyncTask(Context context, boolean showLoading) {
        this.context = context;
        this.showLoading = showLoading;
        this.mQueue = Volley.newRequestQueue(context);
    }

    public void execute() {
        StringRequest r = new StringRequest(Request.Method.GET, Global.versionUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String xml) {
                        hideLoading();

                        try {
                            UpdateXmlParser xmlParser = new UpdateXmlParser();
                            final UpdateInfo updateInfo = xmlParser.parse(xml);

                            if (context != null && updateInfo != null) {
                                try {
                                    PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                    Integer versionCode = pinfo.versionCode; // 1
                                    String versionName = pinfo.versionName; // 1.0
                                    String packageName = context.getPackageName();

                                    if(packageName.equals(updateInfo.getPackageName())) {
                                        //包名吻合
                                        if (Integer.parseInt(updateInfo.getVersionCode()) > versionCode) {
                                            KsApplication.hasNewVersion = true;
                                            KsApplication.newVersionName = versionName;
                                            //新版本
                                            new MaterialDialog.Builder(context)
                                                    .title("有新版本：" + updateInfo.getVersionName())
//                                                    .content(getUpdateTips(updateInfo))
                                                    .positiveText("立即更新")
                                                    .theme(SysUtils.getDialogTheme())
                                                    .negativeText("取消")
                                                    .callback(new MaterialDialog.ButtonCallback() {
                                                        @Override
                                                        public void onPositive(MaterialDialog dialog) {
//                                                            startDownload("Yi游", updateInfo.getApkUrl());

                                                            try {
                                                                download(updateInfo.getApkUrl());
                                                            } catch(Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            KsApplication.hasNewVersion = false;
                                            if(showLoading) {
                                                SysUtils.showSuccess("已经是最新版了");
                                            }
                                        }
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(showLoading) {
                            hideLoading();
                            SysUtils.showNetworkError();
                        }
                    }
                }
        );
        mQueue.add(r);

        showLoading(this.context, "正在检查...");
    }

    public String getUpdateTips(UpdateInfo info) {
        String tip = null;
        if (context == null || info == null) {
            return tip;
        }

        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Map<String, String> tips = info.getUpdateTips();
        if (tips == null) {
            return tip;
        }

        if (language != null && tips.containsKey(language)) {
            tip = tips.get(language);
        } else {
            tip = tips.get("default");
        }

        //Android textview not supporting line break.see http://stackoverflow.com/a/12422965/821624
        return tip.replace("\\n", "\n");
    }

    public void showLoading(Context ctx, String msg) {
        if(showLoading) {
            progressDialog = DialogUtils.createLoadingDialog(ctx, msg);
            progressDialog.show();
        }

    }

    public void hideLoading() {
        if(showLoading && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static boolean isDownloadManagerAvailable(Context context) {
        return true;
//        try {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
//                return false;
//            }
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
//            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
//                    PackageManager.MATCH_DEFAULT_ONLY);
//            return list.size() > 0;
//        } catch (Exception e) {
//            return false;
//        }
    }

    public void startDownload(String title, String url) {
        if(isDownloadManagerAvailable(context)) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setVisibleInDownloadsUi(true);
            request.setMimeType("application/vnd.android.package-archive");
            request.setTitle(title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

//            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            savePath += "/oho_food/";
            String savePath = Environment.DIRECTORY_DOWNLOADS;
            request.setDestinationInExternalPublicDir(savePath, title + ".apk");
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    private void download(String dl) throws Exception {
        SysUtils.showSuccess("开始下载。。。");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            Intent service = new Intent(context, DownloadService.class);
            service.putExtra(DownloadService.INTENT_URL, dl);
            context.startService(service);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dl));
            context.startActivity(intent);
        }
    }
}
