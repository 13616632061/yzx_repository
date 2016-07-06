package com.ms.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ms.global.Global;
import com.ms.ks.LoginActivity;
import com.ms.ks.KsApplication;

import org.json.JSONObject;
import com.tencent.android.tpush.XGPushManager;

//import com.ms.yiyou.LoginActivity;

public class LoginUtils {

    public static void toLogin(Context ctx, int loginType) {
        //未登录
//        showError("请先登录");
        Bundle b = new Bundle();
        b.putInt("loginType", loginType);

        SysUtils.startAct(ctx, new LoginActivity(), b);
    }

    public static boolean hasLogin() {
        boolean hasLogin = false;
        int login_type = KsApplication.getInt("login_type", 0);
        String token = KsApplication.getString("token", "");

        if (!StringUtils.isEmpty(token)) {
            if (login_type == 1) {
                //店铺
                int seller_id = KsApplication.getInt("seller_id", 0);
                if (seller_id > 0) {
                    hasLogin = true;
                }
            } else if (login_type == 2) {
                //业务员
                hasLogin = true;
            }

        }

        return hasLogin;
    }

    /**
     * 是否店家
     * @return
     */
    public static boolean isSeller() {
        boolean isSeller = false;

        int login_type = KsApplication.getInt("login_type", 0);
        if (login_type == 1) {
            String token = KsApplication.getString("token", "");
            if (!StringUtils.isEmpty(token)) {
                int seller_id = KsApplication.getInt("seller_id", 0);
                if (seller_id > 0) {
                    isSeller = true;
                }
            }
        }

        return isSeller;
    }

    /**
     * 是否业务员
     * @return
     */
    public static boolean isMember() {
        boolean isMember = false;

        int login_type = KsApplication.getInt("login_type", 0);
        if (login_type == 2) {
            String token = KsApplication.getString("token", "");
            if (!StringUtils.isEmpty(token)) {
                isMember = true;
            }
        }

        return isMember;
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
        LoginUtils.afterLogin(ctx, jsonObject, true, 0);
    }

    public static void afterLogin(Context ctx, JSONObject jsonObject, int loginType) {
        LoginUtils.afterLogin(ctx, jsonObject, true, loginType);
    }

    public static void afterLogin(Context ctx, JSONObject jsonObject, boolean finish, int loginType) {
        try {
            KsApplication.putInt("login_type", loginType);
            if (loginType > 0) {
                KsApplication.putInt("type", jsonObject.getInt("type"));
                KsApplication.putString("token", jsonObject.getString("token"));
                if (loginType == 1) {
                    //店铺
                    KsApplication.putInt("seller_id", jsonObject.getInt("id"));
                }
            }

            //发送登录广播
            ctx.sendBroadcast(new Intent(Global.BROADCAST_LOGIN_ACTION));

            KsApplication.putString("login_mod", "");

            //注册推送tag
            LoginUtils.setTag(ctx);

            if(finish) {
                ((Activity) ctx).finish();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTag(Context ctx) {
        String account = "seller_" + KsApplication.getInt("seller_id", 0);
        if (LoginUtils.isSeller()) {
            //商家，设置tag
            XGPushManager.setTag(ctx, account);
        } else {
            XGPushManager.deleteTag(ctx, account);
        }
    }

    public static void logout(final Context ctx, final int type) {
        KsApplication.putInt("login_type", 0);

        //删除tag
        LoginUtils.setTag(ctx);
        //发出登录广播
        ctx.sendBroadcast(new Intent(Global.BROADCAST_LOGIN_ACTION));
    }
}
