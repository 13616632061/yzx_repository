package com.ms.ks;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.material.widget.PaperButton;
import com.ms.util.DeleteEditText;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;


public class ProfilePasswordActivity extends BaseActivity {
    //原始密码
    TextView textView1;
    EditText textView3;

    //新密码
    TextView textView4;
    EditText textView6;

    //确认新密码
    TextView textView7;
    EditText textView9;

    private boolean has_password = false;
    private RelativeLayout relativeLayout1;
    PaperButton button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_password);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);

        has_password = true;

        textView1 = (TextView) findViewById(R.id.textView1);
        textView3 = (EditText) findViewById(R.id.textView3);    //原始密码
        new DeleteEditText(textView3, textView1);

        textView4 = (TextView) findViewById(R.id.textView4);
        textView6 = (EditText) findViewById(R.id.textView6);    //新密码
        new DeleteEditText(textView6, textView4);

        textView7 = (TextView) findViewById(R.id.textView7);
        textView9 = (EditText) findViewById(R.id.textView9);    //确认新密码
        new DeleteEditText(textView9, textView7);

        relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        if(has_password) {
            relativeLayout1.setVisibility(View.VISIBLE);
        } else {
            relativeLayout1.setVisibility(View.GONE);
        }


        //下一步
        button1 = (PaperButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = textView3.getText().toString();
                String newpwd = textView6.getText().toString();
                String re_newpwd = textView9.getText().toString();

                if(has_password && StringUtils.isEmpty(username)) {
                    SysUtils.showError("原始密码不能为空");
                } else if(StringUtils.isEmpty(newpwd)) {
                    SysUtils.showError("新密码不能为空");
                } else if(newpwd.length() < 6) {
                    SysUtils.showError("新密码至少6位");
                } else if(!newpwd.equals(re_newpwd)) {
                    SysUtils.showError("确认密码应与新密码一致");
                } else {
//                    Map<String,String> map = new HashMap<String,String>();
//                    map.put("uid", ExamApplication.getString("uid", ""));
//                    map.put("password", ExamApplication.getString("password", ""));
//                    map.put("old_password", username);
//                    map.put("new_password", newpwd);
//                    map.put("re_new_password", re_newpwd);
//
//                    CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl("user/modify_password"), map, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject jsonObject) {
//                            hideLoading();
//
//                            try {
//                                int error = jsonObject.getInt("error");
//                                if(error > 0) {
//                                    String errstr = jsonObject.getString("errmsg");
//                                    SysUtils.showError(errstr);
//                                } else {
//                                    SysUtils.showSuccess("密码已修改");
//
//                                    ExamApplication.putString("password", jsonObject.getString("password"));
//                                    ExamApplication.putString("has_password", "1");
//                                    relativeLayout1.setVisibility(View.VISIBLE);
//                                    has_password = true;
//
//                                    //发送资料修改广播
//                                    sendBroadcast(new Intent(Global.BROADCAST_PROFILE_UPDATE_ACTION));
//
//                                    finish();
//                                }
//                            } catch(Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            hideLoading();
//
//                            SysUtils.showNetworkError();
//                        }
//                    });
//
//                    executeRequest(r);
//
//                    showLoading(ProfilePasswordActivity.this, "请稍等......");
                }
            }
        });
    }

}
