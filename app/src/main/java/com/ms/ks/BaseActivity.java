package com.ms.ks;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.update.UpdateAsyncTask;
import com.ms.util.DialogUtils;
import com.ms.util.LoginUtils;
import com.ms.util.RequestManager;
import com.ms.util.SysUtils;
import com.ms.util.SystemBarTintManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

public class BaseActivity extends ActionBarActivity {
    private Dialog progressDialog = null;
    public Toolbar toolbar = null;
    private SystemBarTintManager tintManager = null;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, 0);
    }

    protected  void onCreate(Bundle savedInstanceState, int loginType) {
        onCreate(savedInstanceState, loginType, true);
    }

    protected  void onCreate(Bundle savedInstanceState, int loginType, boolean setTheme) {
        onCreate(savedInstanceState, loginType, setTheme, true);
    }

    protected  void onCreate(Bundle savedInstanceState, int loginType, boolean setTheme, boolean checkUpdate) {
        super.onCreate(savedInstanceState);

        setTitle(null);
//        Log.d("huigu", "Activity Label: " + label);

        boolean canNext = true;
        if(loginType == 1) {
            //卖家
            if (!LoginUtils.isSeller()) {
                canNext = false;
            }
        } else if (loginType == 2) {
            //业务员
            if (!LoginUtils.isMember()) {
                canNext = false;
            }
        }

        if (!canNext) {
            LoginUtils.toLogin(this, loginType);

            finish();
            return;

        }
        //应用主题
        if(setTheme) {
//            Theme.onActivityCreate(this, savedInstanceState);
        }

        if(checkUpdate) {
            //检测版本更新
            checkVersion();
        }
    }

    public void checkVersion() {
        UpdateAsyncTask myAsyncTask = new UpdateAsyncTask(this, false);
        myAsyncTask.execute();
    }

    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideLoading();
    }

    public void showLoading(Context ctx, String msg) {
        showLoading(ctx, msg, true);
    }

    public void showLoading(Context ctx, String msg, boolean canCancel) {
        try {
//            progressDialog = new MaterialDialog.Builder(ctx)
//                    .content(msg)
//                    .theme(com.afollestad.materialdialogs.Theme.LIGHT)
//                    .progress(true, 0)
//                    .show();
            progressDialog = DialogUtils.createLoadingDialog(ctx, msg, canCancel);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading(Context ctx) {
        showLoading(ctx, "请稍等...");
    }

    public void hideLoading() {
        try {
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initToolbar(ActionBarActivity act) {
        initToolbar(act, 1);
    }

    /**
     *
     * @param act
     * @param type      0：不设置，1：红色，2：透明 3 白色
     * @param showHome      是否显示返回按钮
     */
    public void initToolbar(ActionBarActivity act, int type, boolean showHome) {
        toolbar = (Toolbar) act.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        act.setSupportActionBar(toolbar);

        ActionBar actionbar = act.getSupportActionBar ();
        actionbar.setDisplayHomeAsUpEnabled(showHome);

        if(type == 2) {
            //透明时手动调用下这句话，处理某些android版本中虽然指定透明主题，但打开默认还是会有一层颜色的情况
            toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else if(type == 1) {
//            toolbar.getBackground().setAlpha(100);
        }

        setTintColor(type);

        //标题
        String label = null;
        try {
            label = getResources().getString(
                    getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        if(label != null) {
            setToolbarTitle(label);
        }
    }

    public void setTintColor(int type) {
        if (type > 0 && initTint()) {
            tintManager.setStatusBarTintEnabled(true);
            setStatusBarTintColor(getResources().getColor(getColorType(type)));
        }
    }

    private boolean initTint() {
        boolean ret = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(tintManager == null) {
                tintManager = new SystemBarTintManager(this);
            }

            ret = true;
        }

        return ret;
    }

    public void setStatusBarTintColor(int color) {
        if(initTint()) {
            tintManager.setStatusBarTintColor(color);
        }
    }

    public void initToolbar(ActionBarActivity act, int type) {
        initToolbar(act, type, true);
    }

    private int getColorType(int type) {
        if(type == 2) {
            return R.color.transparent;
        } else if(type == 3) {
            return R.color.black;
        } else if(type == 4) {
            return R.color.black;
        } else {
//            return Theme.isFemaleMode() ? R.color.female_dark_primary_color : R.color.dark_primary_color;
            return R.color.dark_primary_color;
        }
    }

    //销毁activity前，销毁掉所有的网络请求
    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestManager.cancelAll(this);
    }

    //请求网络
    public void executeRequest(Request<?> request) {
        RequestManager.addRequest(request, this);
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SysUtils.showNetworkError();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        if(menuId == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

//    public void settingBackground() {
//        ImageView backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
//        try {
//            BitmapDrawable bitmapDrawable;
//            bitmapDrawable = createBlur(R.drawable.entrance1);
//            backgroundImage.setImageDrawable(bitmapDrawable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    final float radius = 8;
//    final double scaleFactor = 16;
//
//    public BitmapDrawable createBlur(int bgId) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), bgId, options);
//        int height = options.outHeight;
//        int width = options.outWidth;
//
//        options.outHeight = (int) (height / scaleFactor);
//        options.outWidth = (int) (width / scaleFactor);
//        options.inSampleSize = (int) (scaleFactor + 0.5);
//        options.inJustDecodeBounds = false;
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inMutable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.entrance1, options);
//        Bitmap blurBitmap = NativeStackBlur.process(bitmap, (int) radius);
//
//        return new BitmapDrawable(getResources(), blurBitmap);
//    }

}
