package com.ms.util;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.entity.NewsCat;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ms.ks.BaseActivity;
import com.ms.ks.KsApplication;
import com.ms.ks.R;
import com.ms.global.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Comment {
    MaterialEditText commentText;
    TextView commentSubmit;
    Context context;
    String commentMod;
    String commentTid;
    String commentPid;
    int reviewMaxLength;

    public Comment(final Context ctx,
                   MaterialEditText ct,
                   TextView cb,
                   String commentMod,
                   String commentTid,
                   String commentPid,
                   int rm) {
        this.context = ctx;
        this.commentText = ct;
        this.commentSubmit = cb;

        this.commentMod = commentMod;
        this.commentTid = commentTid;
        this.commentPid = commentPid;

        this.setReviewMaxLength(rm);

        this.commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int colorRes = 0;
                if(editable.length() >= reviewMaxLength) {
                    colorRes = context.getResources().getColor(R.color.primary_color);
                } else {
                    colorRes = context.getResources().getColor(R.color.text_hint_color);
                }

                commentSubmit.setTextColor(colorRes);

                KsApplication.putString("reviewText", editable.toString());
            }
        });


        this.commentSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String comment_str = commentText.getText().toString();
                if (comment_str.length() >= 5) {
                    if (!LoginUtils.hasLogin()) {
                        //如果没有登录，那么跳到登录
                        LoginUtils.toLogin(ctx);
                    } else {
                        doRequest(comment_str);
                    }
                }
            }
        });
    }

    public void setReviewMaxLength(int rm) {
        this.reviewMaxLength = rm;

        if (reviewMaxLength > 1) {
            this.commentText.setHint("努力写够" + reviewMaxLength + "个字吧...");
        } else {
            this.commentText.setHint("我也说两句");
        }

    }

    public void setPid(String pid) {
        this.commentPid = pid;
    }

    private void doRequest(final String comment_str) {
        //首先得到ip
//        CustomRequest r = new CustomRequest(Request.Method.GET, Global.IP_URI, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                String ip = "";
//                try {
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    ip = data.getString("ip");
//                } catch(Exception e) {
//                    e.printStackTrace();
//                } finally {
////                    SysUtils.showSuccess(ip);
//                    doTruthRequest(comment_str, ip);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ((BaseActivity)context).hideLoading();
//
//                SysUtils.showNetworkError();
//            }
//        });
//
//        ((BaseActivity)context).executeRequest(r);

        doTruthRequest(comment_str, "");
        ((BaseActivity)context).showLoading(this.context, "请稍等......");
    }

    private void doTruthRequest(String comment_str, String ip) {
        Map<String,Object> finalMap = new HashMap<String,Object>();
        finalMap.put("articleId", commentTid);
        finalMap.put("content", comment_str);
        Map<String,String> postMap = SysUtils.apiCall(context, finalMap);

        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl("review/create"), postMap, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ((BaseActivity)context).hideLoading();

                try {
                    int error = jsonObject.getInt("code");
                    if(error > 0) {
                        String errstr = jsonObject.getString("message");
                        SysUtils.showError(errstr);
                    } else {
                        commentText.setText("");
                        SysUtils.hideSoftKeyboard(context, commentText);

                        SysUtils.showSuccess("感谢您的评论");

                        String commentNum = jsonObject.getString("postNum");

                        //发送点评广播
                        context.sendBroadcast(new Intent(Global.BROADCAST_COMMENT_ACTION)
                                .putExtra("commentMod", commentMod)
                                .putExtra("commentTid", commentTid)
                                .putExtra("commentPid", commentPid)
                                .putExtra("commentNum", commentNum));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ((BaseActivity)context).hideLoading();

                SysUtils.showNetworkError();
            }
        });

        ((BaseActivity)context).executeRequest(r);


//        Map<String,String> map = new HashMap<String,String>();
//        map.put("text", comment_str);
//        map.put("uid", YiyouApplication.getString("uid", ""));
//        map.put("password", YiyouApplication.getString("password", ""));
//        map.put("mod", commentMod);
//        map.put("tid", commentTid);
//        map.put("comment_pid", commentPid);
//        map.put("comment_ip", ip);
//        CustomRequest rr = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl("comment/ok"), map, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                ((BaseActivity)context).hideLoading();
//
//                try {
//                    int error = jsonObject.getInt("error");
//                    if(error > 0) {
//                        String errstr = jsonObject.getString("errstr");
//                        SysUtils.showError(errstr);
//                    } else {
//                        commentText.setText("");
//                        SysUtils.hideSoftKeyboard(context, commentText);
//
//                        SysUtils.showSuccess("感谢您的评论");
//
//                        String commentNum = jsonObject.getString("comment_num");
//
//                        //发送点评广播
//                        context.sendBroadcast(new Intent(Global.BROADCAST_COMMENT_ACTION)
//                                            .putExtra("commentMod", commentMod)
//                                .putExtra("commentTid", commentTid)
//                                .putExtra("commentPid", commentPid)
//                                .putExtra("commentNum", commentNum));
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                ((BaseActivity)context).hideLoading();
//
//                SysUtils.showNetworkError();
//            }
//        });
//
//        ((BaseActivity)context).executeRequest(rr);
    }
}
