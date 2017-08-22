package com.ms.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ms.ks.MipcaActivityCapture;
import com.ms.ks.ProfileActivity;
import com.ms.ks.R;

public class DialogUtils {
     private static AlertDialog mAlertDialog;
    private static EditText et_accountinfo_input;
    private static  InputMethodManager imm;
    public static Dialog createLoadingDialog(Context context, String text, boolean canCancel) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progress_bar, null);

        final Dialog loadingDialog = new Dialog(context, R.style.MyDialog);
        loadingDialog.setContentView(v);
        loadingDialog.setCancelable(false);
        ImageButton imgbtn_guanbi = (ImageButton)v.findViewById(R.id.imgbtn_guanbi);
        if(canCancel) {
            //可以被取消
            imgbtn_guanbi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingDialog.dismiss();
                }
            });
        }

        TextView top_process_promot = (TextView)v.findViewById(R.id.top_process_promot);
        top_process_promot.setText(text);

        return loadingDialog;
    }

    public static Dialog createLoadingDialog(Context context, String text) {
        return createLoadingDialog(context, text, true);
    }

    public static Dialog createLoadingDialog(Context context) {
        return createLoadingDialog(context, "请稍等......", true);
    }

    /**
     * 添加账户资料弹窗
     * @param mContext
     * @param str_name
     * @param text
     */
    public static void editAccountDialog(final Context mContext, String str_name, final TextView text){

        AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        View view =View.inflate(mContext, R.layout.editaccountinfo_dialog,null);
        TextView tv_accountinfo_name=(TextView)view.findViewById(R.id.tv_accountinfo_name);
                et_accountinfo_input=(EditText)view.findViewById(R.id.et_accountinfo_input);
        et_accountinfo_input.requestFocus();
        et_accountinfo_input.setFocusable(true);
        //软键盘显示
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        TextView tv_editaccount_cancel=(TextView)view.findViewById(R.id.tv_editaccount_cancel);
        TextView tv_editaccount_save=(TextView)view.findViewById(R.id.tv_editaccount_save);
        tv_accountinfo_name.setText(str_name);
        tv_editaccount_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                //再次调用软键盘消失
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        tv_editaccount_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_accountinfo_input.getText().toString().trim())){
                    text.setText(et_accountinfo_input.getText().toString().trim());
                    mAlertDialog.dismiss();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
        mAlertDialog= dialog.setView(view).show();
        mAlertDialog.show();
    }

    /**
     * 含有确定的弹框
     * @param context
     * @param message
     */
    public  static void showbuilder(final Activity context, String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                });
        builder.show();

    }
    //判断软键盘是否存在
    public  static boolean isSoftShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }
}
