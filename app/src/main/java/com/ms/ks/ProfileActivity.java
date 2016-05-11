package com.ms.ks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.entity.Address;
import com.ms.global.Global;
import com.ms.util.CustomRequest;
import com.ms.util.DeleteEditText;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

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
//    private AreaPicker areaPicker;

    Address addressDetail = null;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SysUtils.setupUI(this, findViewById(R.id.main));

        initToolbar(this);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("address")) {
                addressDetail = bundle.getParcelable("address");

                if(addressDetail != null && addressDetail.getId() > 0) {
                    isEdit = true;
                }
            }
        }

        imageView1 = (TextView) findViewById(R.id.imageView1);
        textView2 = (EditText) findViewById(R.id.textView2);    //姓名
        new DeleteEditText(textView2, imageView1);


        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (EditText) findViewById(R.id.textView4);    //手机号
        new DeleteEditText(textView4, textView3);

        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (EditText) findViewById(R.id.textView6);    //固定电话
        new DeleteEditText(textView6, textView5);

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

        if(isEdit) {
            textView2.setText(addressDetail.getConsignee());
            textView4.setText(addressDetail.getMobile());
            textView6.setText(addressDetail.getZipcode());
            textView8.setText(addressDetail.getAreaStr());
//            textView12.setText(addressDetail.getCityStr());
//            textView14.setText(addressDetail.getTownStr());
            textView10.setText(addressDetail.getAddress());

            provinceId = addressDetail.getProvince();
            cityId = addressDetail.getCity();
            areaId = addressDetail.getTown();
        }


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
                                Map<String,Object> finalMap = new HashMap<String,Object>();
                                Map<String,String> map = new HashMap<String,String>();
                                map.put("name", name);
                                map.put("mobile", mobile);
                                map.put("phone", phone);
                                map.put("province", String.valueOf(provinceId));
                                map.put("city", String.valueOf(cityId));
                                map.put("town", String.valueOf(areaId));
                                map.put("address", address);
                                String method = isEdit ? "my/address/edit" : "my/address/create";

                                finalMap.put("address", map);
                                if(isEdit) {
                                    finalMap.put("id", addressDetail.getId());
                                }

                                Map<String,String> postMap = SysUtils.apiCall(ProfileActivity.this, finalMap);

                                CustomRequest r = new CustomRequest(Request.Method.POST, SysUtils.getServiceUrl(method), postMap, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        hideLoading();

                                        try {
                                            int error = jsonObject.getInt("code");
                                            if(error > 0) {
                                                String errstr = jsonObject.getString("message");
                                                SysUtils.showError(errstr);
                                            } else {
                                                SysUtils.showSuccess("操作已执行");

                                                sendBroadcast(new Intent(Global.BROADCAST_REFRESH_ADDRESS_ACTION));

//                                                int address_id = dataObject.getInt("id");
//                                                Intent returnIntent = new Intent();
//                                                Bundle bundle = new Bundle();
//                                                bundle.putInt("address_id", address_id);
//                                                returnIntent.putExtras(bundle);
//
//                                                setResult(RESULT_OK, returnIntent);

                                                finish();
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
                String areaStr = b.getString("areaStr");
                textView8.setText(areaStr);
            }
        }
    }
}
