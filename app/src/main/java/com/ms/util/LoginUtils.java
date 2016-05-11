package com.ms.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ms.global.Global;
import com.ms.ks.LoginActivity;
import com.ms.ks.KsApplication;

import org.json.JSONObject;

//import com.ms.yiyou.LoginActivity;

public class LoginUtils {

    public static void toLogin(Context ctx) {
        //未登录
//        showError("请先登录");
        SysUtils.startAct(ctx, new LoginActivity());
    }

    public static void doLogoutAfter(Context ctx, int type) {
        if(type == 2) {
            LoginUtils.toLogin(ctx);
            ((Activity)ctx).finish();
        }
    }

    public static boolean hasLogin() {
        String uid = KsApplication.getString("uid", "");
        String token = KsApplication.getString("token", "");

        if(uid.length() <= 0 || token.length() <= 0) {
            return false;
        }
        else {
            return true;
        }
    }

    public static String ssoTypeStr() {
        int sso_type = KsApplication.getInt("sso_type", 0);

        if(sso_type == 1) {
            return "新浪";
        } else if(sso_type == 2) {
            return "QQ";
        } else if(sso_type == 3) {
            return "微信";
        }

        return "";
    }

    public static void afterLogin(Context ctx, JSONObject jsonObject) {
        LoginUtils.afterLogin(ctx, jsonObject, true, true);
    }

    public static void afterLogin(Context ctx, JSONObject jsonObject, boolean writeToken, boolean finish) {
        try {
            JSONObject userObject = jsonObject.getJSONObject("user");

            KsApplication.putString("uid", userObject.getString("id"));
            KsApplication.putString("email", userObject.getString("email"));
            KsApplication.putString("mobile", userObject.getString("mobile"));
            KsApplication.putString("level", userObject.getString("level"));
            KsApplication.putString("levelText", userObject.getString("levelText"));
            KsApplication.putString("nickname", userObject.getString("nickname"));
            KsApplication.putString("signature", userObject.getString("signature"));
            KsApplication.putString("sex", userObject.getString("sexName"));
            KsApplication.putString("birthday", userObject.getString("birthday"));
            KsApplication.putString("city", userObject.getString("city"));
            KsApplication.putString("cityName", userObject.getString("cityName"));
            KsApplication.putString("job", userObject.getString("jobName"));
            KsApplication.putString("smallAvatar", userObject.getString("smallAvatar"));
            KsApplication.putString("mediumAvatar", userObject.getString("mediumAvatar"));
            KsApplication.putString("largeAvatar", userObject.getString("largeAvatar"));

            String gender = userObject.getString("gender");
            String originGender = KsApplication.getString("gender", "male");

            if (!gender.equals(originGender)) {
                //更改了性别
                KsApplication.putString("gender", gender);
                ctx.sendBroadcast(new Intent(Global.BROADCAST_REFRESH_THEME_ACTION).putExtra("gender", gender));
            }

            if(writeToken) {
                KsApplication.putString("token", jsonObject.getString("token"));
            }

            //发送登录广播
            ctx.sendBroadcast(new Intent(Global.BROADCAST_LOGIN_ACTION));

            KsApplication.putString("login_mod", "");

            if(finish) {
                ((Activity) ctx).finish();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(final Context ctx, final int type) {
        KsApplication.putString("token", "");

        //发出登录广播
        ctx.sendBroadcast(new Intent(Global.BROADCAST_LOGIN_ACTION));
    }
}
