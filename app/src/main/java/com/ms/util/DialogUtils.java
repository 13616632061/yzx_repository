package com.ms.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ms.ks.R;

public class DialogUtils {
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
}
