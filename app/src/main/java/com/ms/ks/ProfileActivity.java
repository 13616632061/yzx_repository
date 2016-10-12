package com.ms.ks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.util.CustomRequest;
import com.ms.util.DeleteEditText;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    //收件人
    TextView imageView1;
    EditText textView2;

    //手机号
    TextView textView3;
    EditText textView4;

    //邮编
    TextView textView5;
    EditText textView6;

    //省
    RelativeLayout relativeLayout1;
    TextView textView8;

    //市
//    RelativeLayout relativeLayout2;
//    TextView textView12;

    //县
//    RelativeLayout relativeLayout3;
//    TextView textView14;

    //详细地址
    TextView textView9;
    EditText textView10;

    private int provinceId = 0, cityId = 0, areaId=  0;
    private String area = "";

    private boolean hasPass = false;

    private TextView khr, yh, kh;
    private ImageView logo, yyzz, wsxkz, qtzp;
    private DisplayImageOptions options;
    private RelativeLayout logo_rl, yyzz_rl, wsxkz_rl, qt_rl;

    private String logoV = "";
    private String businessV = "";
    private String healthV = "";
    private String otherV = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("hasPass")) {
                hasPass = bundle.getBoolean("hasPass");
            }
        }

        if(!hasPass) {
            SysUtils.startAct(this, new ProfileEntryActivity());
            finish();
        }

        imageView1 = (TextView) findViewById(R.id.imageView1);
        textView2 = (EditText) findViewById(R.id.textView2);    //姓名
        new DeleteEditText(textView2, imageView1);

        options = SysUtils.imageOption(false);

        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (EditText) findViewById(R.id.textView4);    //手机号
        new DeleteEditText(textView4, textView3);

        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (EditText) findViewById(R.id.textView6);    //固定电话
        new DeleteEditText(textView6, textView5);

        khr = (TextView)findViewById(R.id.khr);
        yh = (TextView)findViewById(R.id.yh);
        kh = (TextView)findViewById(R.id.kh);


        logo = (ImageView)findViewById(R.id.logo);
        yyzz = (ImageView)findViewById(R.id.yyzz);
        wsxkz = (ImageView)findViewById(R.id.wsxkz);
        qtzp = (ImageView)findViewById(R.id.qtzp);

        logo_rl = (RelativeLayout) findViewById(R.id.logo_rl);
        logo_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(logoV)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_list", logoV);
                    bundle.putInt("offset", 0);

                    SysUtils.startAct(ProfileActivity.this, new PicViewActivity(), bundle);
                    ((BaseActivity) ProfileActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

        yyzz_rl = (RelativeLayout) findViewById(R.id.yyzz_rl);
        yyzz_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(businessV)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_list", businessV);
                    bundle.putInt("offset", 0);

                    SysUtils.startAct(ProfileActivity.this, new PicViewActivity(), bundle);
                    ((BaseActivity) ProfileActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

        wsxkz_rl = (RelativeLayout) findViewById(R.id.wsxkz_rl);
        wsxkz_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(healthV)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_list", healthV);
                    bundle.putInt("offset", 0);

                    SysUtils.startAct(ProfileActivity.this, new PicViewActivity(), bundle);
                    ((BaseActivity) ProfileActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

        qt_rl = (RelativeLayout) findViewById(R.id.qt_rl);
        qt_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(otherV)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_list", otherV);
                    bundle.putInt("offset", 0);

                    SysUtils.startAct(ProfileActivity.this, new PicViewActivity(), bundle);
                    ((BaseActivity) ProfileActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });

//        areaPicker = new AreaPicker(this, new MaterialDialog.ButtonCallback() {
//            @Override
//            public void onPositive(MaterialDialog dialog) {
//                super.onPositive(dialog);
//
//                textView8.setText(areaPicker.getAreaStr());
//                provinceId = areaPicker.getProvinceId();
//                cityId = areaPicker.getCityId();
//                areaId = areaPicker.getAreaId();
////                SysUtils.showSuccess(areaStr);
//            }
//        });

        //省
        relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        textView8 = (TextView) findViewById(R.id.textView8);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putInt("pid", 0);

                SysUtils.startAct(ProfileActivity.this, new AddressLocationActivity(), bundle, true);
            }
        });

        textView9 = (TextView) findViewById(R.id.textView9);
        textView10 = (EditText) findViewById(R.id.textView10);    //详细地址
        new DeleteEditText(textView10, textView9);


        PaperButton button1 = (PaperButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = textView2.getText().toString();
                if(StringUtils.isEmpty(name)) {
                    SysUtils.showError("请填写姓名");
                } else {
                    String mobile = textView4.getText().toString();
                    String phone = textView6.getText().toString();
                    if(StringUtils.isEmpty(mobile) && StringUtils.isEmpty(phone)) {
                        SysUtils.showError("手机号码和固定电话必填其一");
                    } else {

                        if(provinceId <= 0) {
                            SysUtils.showError("请选择所在地区");
                        } else {
                            String address = textView10.getText().toString();
                            if(StringUtils.isEmpty(address)) {
                                SysUtils.showError("请填写详细地址");
                            } else {
                                Map<String,String> map = new HashMap<String,String>();
                                map.put("name", name);
                                map.put("mobile", mobile);
                                map.put("tel", phone);
                                map.put("area", getPostArea());
                                map.put("addr", address);

                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("doAccount"), map, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        hideLoading();

                                        try {
                                            JSONObject ret = SysUtils.didResponse(jsonObject);
                                            String status = ret.getString("status");
                                            String message = ret.getString("message");

                                            if (!status.equals("200")) {
                                                SysUtils.showError(message);
                                            } else {
                                                SysUtils.showSuccess("修改已保存");
                                            }
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        hideLoading();
                                        SysUtils.showNetworkError();
                                    }
                                });

                                executeRequest(r);

                                showLoading(ProfileActivity.this);
                            }
                        }


                    }
                }
            }
        });

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            if(b != null && b.containsKey("provinceId") && b.containsKey("cityId") && b.containsKey("townId") && b.containsKey("areaStr")) {
                provinceId = b.getInt("provinceId");
                cityId = b.getInt("cityId");
                areaId = b.getInt("townId");
                area = b.getString("areaStr");

//                SysUtils.showSuccess(provinceId + "-" + cityId + "-" + areaId);
                textView8.setText(area);
            }
        }
    }

    private void initView() {
        CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("account"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject dataObject = ret.getJSONObject("data");

                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    } else {
                        JSONObject sellerObject = dataObject.getJSONObject("seller_info");

                        textView2.setText(sellerObject.getString("seller_name"));
                        textView4.setText(sellerObject.getString("mobile"));
                        textView6.setText(sellerObject.getString("tel"));

                        JSONObject areaObject = sellerObject.getJSONObject("area");

                        area = areaObject.getString("area");
                        textView8.setText(area);

                        getAreaId(areaObject.getString("area_id"));
                        textView10.setText(sellerObject.getString("addr"));

                        khr.setText(sellerObject.optString("khr"));
                        yh.setText(sellerObject.optString("acbank"));
                        kh.setText(sellerObject.optString("bankcard"));

                        logoV = sellerObject.optString("logo");
                        if(!TextUtils.isEmpty(logoV)) {
                            imageLoader.displayImage(logoV, logo, options);
                        }
                        businessV = sellerObject.optString("business");
                        if(!TextUtils.isEmpty(businessV)) {
                            imageLoader.displayImage(businessV, yyzz, options);
                        }
                        healthV = sellerObject.optString("health");
                        if(!TextUtils.isEmpty(healthV)) {
                            imageLoader.displayImage(healthV, wsxkz, options);
                        }
                        otherV = sellerObject.optString("other");
                        if(!TextUtils.isEmpty(otherV)) {
                            imageLoader.displayImage(otherV, qtzp, options);
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                SysUtils.showNetworkError();
                hideLoading();
            }
        });

        executeRequest(r);
        showLoading(this);
    }


    private String getPostArea() {
        String ret = "mainland";
        ret += ":" + area;
        if(areaId > 0) {
            ret += ":" + areaId;
        } else if(cityId > 0) {
            ret += ":" + cityId;
        } else if(provinceId > 0) {
            ret += ":" + provinceId;
        }

//        SysUtils.showSuccess(ret);

        return ret;
    }

    private void getAreaId(String area_id) {
        String[] aa = area_id.split(",");

        int aIndex = 0;
        for (int  i = 0; i < aa.length; i++) {
            if (!StringUtils.isEmpty(aa[i])) {
                int aid = Integer.parseInt(aa[i]);

                if (aid > 0) {
                    if (aIndex == 0) {
                        provinceId = aid;
                    } else if(aIndex == 1) {
                        cityId = aid;
                    } else if(aIndex == 2) {
                        areaId= aid;
                    }
                    aIndex++;
                }

            }
        }
    }
}
