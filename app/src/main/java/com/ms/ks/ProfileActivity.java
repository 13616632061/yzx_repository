package com.ms.ks;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.material.widget.PaperButton;
import com.ms.util.CustomRequest;
import com.ms.util.DeleteEditText;
import com.ms.util.DialogUtils;
import com.ms.util.GetImagePath;
import com.ms.util.SelectPicPopupWindow;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;
import com.ms.util.UploadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private TextView  yh, kh;
    private TextView khr,branchbank;
    private ImageView logo, yyzz, wsxkz, qtzp;
    private DisplayImageOptions options;
    private RelativeLayout logo_rl, yyzz_rl, wsxkz_rl, qt_rl;

    private String logoV = "";
    private String businessV = "";
    private String healthV = "";
    private String otherV = "";
    private String khr_str="";
    private String yh_str="";
    private String kh_str="";
    private String branchbank_str="";

    private  int INTENT_BTN_PICK_PHOTO=200;
    private  int INTENT_BTN_TAKE_PHOTO=201;
    private SelectPicPopupWindow mSelectPicPopupWindow;
    final String requrl=SysUtils.getUploadImageServiceUrl("fileup");
    private String strname;
    private ImageView mShowImageView;
    // 获取SD卡路径
    private String mFilePath;
    private Uri uri;;
    private FileInputStream is = null;

    private LinearLayout layout_khr;
    private LinearLayout layout_yh;
    private LinearLayout layout_branchbank;
    private LinearLayout layout_bankcard;
   private  boolean isImage=false;

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
        initView();
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

        khr = (TextView) findViewById(R.id.khr);
        yh = (TextView)findViewById(R.id.yh);
        kh = (TextView)findViewById(R.id.kh);
        branchbank=(TextView) findViewById(R.id.branchbank);

        layout_khr=(LinearLayout) findViewById(R.id.layout_khr);
        layout_yh=(LinearLayout) findViewById(R.id.layout_yh);
        layout_branchbank=(LinearLayout) findViewById(R.id.layout_branchbank);
        layout_bankcard=(LinearLayout) findViewById(R.id.layout_bankcard);
        //点击开户人
        layout_khr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.editAccountDialog(ProfileActivity.this,"添加开户人",khr);
            }
        });
        //点击开户银行
        layout_yh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.editAccountDialog(ProfileActivity.this,"添加开户银行",yh);
            }
        });
        //点击开户支行
        layout_branchbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.editAccountDialog(ProfileActivity.this,"添加开户支行",branchbank);
            }
        });
        //点击卡号
        layout_bankcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.editAccountDialog(ProfileActivity.this,"添加银行卡号",kh);
            }
        });
        logo = (ImageView)findViewById(R.id.logo);
        yyzz = (ImageView)findViewById(R.id.yyzz);
        wsxkz = (ImageView)findViewById(R.id.wsxkz);
        qtzp = (ImageView)findViewById(R.id.qtzp);

        logo_rl = (RelativeLayout) findViewById(R.id.logo_rl);
        //判断是否有图片，如没有图片这调用弹窗添加图片
        logo_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(logoV)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("pic_list", logoV);
                    bundle.putInt("offset", 0);

                    SysUtils.startAct(ProfileActivity.this, new PicViewActivity(), bundle);
                    ((BaseActivity) ProfileActivity.this).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else {
                    strname="logo";
                    mShowImageView=logo;
                    getSelectPicPopupWindow();
                    mSelectPicPopupWindow.showAtLocation(findViewById(R.id.logo_rl) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
                }else {
                    strname="business";
                    mShowImageView=yyzz;
                    getSelectPicPopupWindow();
                    mSelectPicPopupWindow.showAtLocation(findViewById(R.id.yyzz_rl) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
                }else {
                    strname="health";
                    mShowImageView=wsxkz;
                    getSelectPicPopupWindow();
                    mSelectPicPopupWindow.showAtLocation(findViewById(R.id.wsxkz_rl) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
                }else {
                     strname="other";
                    mShowImageView=qtzp;
                    getSelectPicPopupWindow();
                    mSelectPicPopupWindow.showAtLocation(findViewById(R.id.wsxkz_rl) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });


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
                if( !isImage){
                    SysUtils.showError("图片至少上传一张");
                    return;
                }
                if(TextUtils.isEmpty(khr.getText().toString().trim())||khr.getText().toString().trim().equals("null")){
                    SysUtils.showError("请填写开户人");
                    return;
                }
                if(TextUtils.isEmpty(yh.getText().toString().trim())||yh.getText().toString().trim().equals("null")){
                    SysUtils.showError("请填写银行");
                    return;
                }
                if(TextUtils.isEmpty(branchbank.getText().toString().trim())||branchbank.getText().toString().trim().equals("null")){
                    SysUtils.showError("请填写开户支行");
                    return;
                }
                if(TextUtils.isEmpty(kh.getText().toString().trim())||kh.getText().toString().trim().equals("null")){
                    SysUtils.showError("请填写卡号");
                    return;
                }
                if(StringUtils.isEmpty(name)||name.equals("null")) {
                    SysUtils.showError("请填写姓名");
                } else {
                    String mobile = textView4.getText().toString();
                    String phone = textView6.getText().toString();
                    if(StringUtils.isEmpty(mobile) && StringUtils.isEmpty(phone)||mobile.equals("null")) {
                        SysUtils.showError("手机号码和固定电话必填其一");
                    } else {

                        if(provinceId <= 0) {
                            SysUtils.showError("请选择所在地区");
                        } else {
                            String address = textView10.getText().toString();
                            if(StringUtils.isEmpty(address)||address.equals("null")) {
                                SysUtils.showError("请填写详细地址");
                            } else {
                                Map<String,String> map = new HashMap<String,String>();
                                map.put("name", name);
                                map.put("mobile", mobile);
                                map.put("tel", phone);
                                map.put("area", getPostArea());
                                map.put("addr", address);
                                map.put("khr", khr.getText().toString().trim());
                                map.put("acbank", yh.getText().toString().trim());
                                map.put("bankcard", kh.getText().toString().trim());
                                map.put("branchbank", branchbank.getText().toString().trim());

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
            if (resultCode == RESULT_OK) {
                if(requestCode==1){
                Bundle b = data.getExtras();
                if (b != null && b.containsKey("provinceId") && b.containsKey("cityId") && b.containsKey("townId") && b.containsKey("areaStr")) {
                    provinceId = b.getInt("provinceId");
                    cityId = b.getInt("cityId");
                    areaId = b.getInt("townId");
                    area = b.getString("areaStr");

                    textView8.setText(area);
                }

                }
                if(requestCode==222){
                    if (resultCode == PackageManager.PERMISSION_GRANTED) {
                        onOpenCamera();
                    } else {
                        Toast.makeText(ProfileActivity.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                if(requestCode==223){
                    if (resultCode == PackageManager.PERMISSION_GRANTED) {
                        creatfile();
                    } else {
                        Toast.makeText(ProfileActivity.this, "很遗憾你把读写权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                //从相册获取图片并显示
                if (requestCode == INTENT_BTN_PICK_PHOTO) {
                    mSelectPicPopupWindow.dismiss();
                    Uri pick_photo = data.getData();
                    showUriImage(pick_photo);
                    Bitmap bit=null;
                    File myCaptureFile=null;
                    try {
                        bit = UploadUtil.getBitmapFormUri(ProfileActivity.this, pick_photo);
                        String path = GetImagePath.getPath(ProfileActivity.this, pick_photo);
                        myCaptureFile = UploadUtil.saveFile(bit,path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    String path = GetImagePath.getPath(ProfileActivity.this, pick_photo);
//                    final File file = new File(path);
                    upImageThread(myCaptureFile);
                    isImage=true;

                }
                //拍照获取图片并显示
                if (requestCode == INTENT_BTN_TAKE_PHOTO) {
                    mSelectPicPopupWindow.dismiss();
                    Bitmap bit=null;
                    File myCaptureFile=null;
                    try {
                         bit=  UploadUtil.getBitmapFormUri(ProfileActivity.this,uri);
                        mShowImageView.setImageBitmap(bit);
                        myCaptureFile= UploadUtil.saveFile(bit,mFilePath);
                        isImage=true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("生成图片失败："+e.toString());
                    }
                    upImageThread(myCaptureFile);
            }
            }
    }

    /**
     *创建存储拍照图片的文件夹
     */
    private void creatfile(){
        String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String str_path=getSDPath()+"/image";
        File file=new File(str_path.trim());
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 获取SD卡路径
        mFilePath= str_path+ name + ".jpg";
        uri=Uri.fromFile(new File(mFilePath));;
    }
    /**
     * 调用相机拍照
     */
    private void onOpenCamera() {
        // 加载路径
        // 指定存储路径，这样就可以保存原图了
        Intent intent_btn_take_photo=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent_btn_take_photo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent_btn_take_photo,INTENT_BTN_TAKE_PHOTO);
    }
//    android获取sd卡路径方法：
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.getPath();
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
                        khr_str=sellerObject.optString("khr");
                        //开户人，银行，开户支行，卡号为空时可以编辑，有数据不可编辑
                        if(!TextUtils.isEmpty(khr_str)&&!khr_str.equals("null")) {
                            layout_khr.setOnClickListener(null);
                        }
                        khr.setText( khr_str);
                        yh_str=sellerObject.optString("acbank");
                        if(!TextUtils.isEmpty( yh_str)&&!yh_str.equals("null")) {
                            layout_yh.setOnClickListener(null);
                        }
                        yh.setText( yh_str);
                        kh_str=sellerObject.optString("bankcard");
                        if(!TextUtils.isEmpty( kh_str)&&!kh_str.equals("null")) {
                            layout_bankcard.setOnClickListener(null);
                        }
                        kh.setText(kh_str);
                        branchbank_str=sellerObject.optString("branchbank");
                        if(!TextUtils.isEmpty( branchbank_str)&&!branchbank_str.equals("null")) {
                            layout_branchbank.setOnClickListener(null);
                        }
                        branchbank.setText(branchbank_str);

                        logoV = sellerObject.optString("logo");
                        if(!TextUtils.isEmpty(logoV)) {
                            isImage=true;
                            imageLoader.displayImage(logoV, logo, options);
                        }
                        businessV = sellerObject.optString("business");
                        if(!TextUtils.isEmpty(businessV)) {
                            isImage=true;
                            imageLoader.displayImage(businessV, yyzz, options);
                        }
                        healthV = sellerObject.optString("health");
                        if(!TextUtils.isEmpty(healthV)) {
                            isImage=true;
                            imageLoader.displayImage(healthV, wsxkz, options);
                        }
                        otherV = sellerObject.optString("other");
                        if(!TextUtils.isEmpty(otherV)) {
                            isImage=true;
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

    /**
     * 弹出拍照，从相册获取添加图片的窗口
     */
    private void getSelectPicPopupWindow(){
        mSelectPicPopupWindow=new SelectPicPopupWindow(ProfileActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_pick_photo:
                        //从相册获取图片
                        Intent intent_btn_pick_photo=new Intent();
                        intent_btn_pick_photo.setType("image/*");
                        intent_btn_pick_photo.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent_btn_pick_photo,INTENT_BTN_PICK_PHOTO);
                        break;
                    case R.id.btn_take_photo:
                        if (Build.VERSION.SDK_INT >= 23) {
                            //动态添加sdk写入权限，主要适配与android6.0以上的系统
                            int checkwritefile=ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if(checkwritefile != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},223);
                                return;
                            } else {
                                    creatfile();
                            }
                            //动态添加拍照权限
                            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.CAMERA);
                            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{android.Manifest.permission.CAMERA},222);
                                return;
                            }else{
                                onOpenCamera();
                            }
                        } else {
                            creatfile();
                            onOpenCamera();
                        }
                        break;

                }
            }
        });
    }

    /**
     * 上传图片
     * @param file
     */
    private void  upImageThread(final File file){
        new Thread(new Runnable(){
            @Override
            public void run() {
                String  str= UploadUtil.uploadFile(file,requrl,strname);
            }
        }).start();
    }

    /**
     * uri格式的图片显示
     * @param uri
     */
    private void showUriImage(Uri uri){
        mShowImageView.setImageURI(uri);
    }

    /**
     * Bitmap格式的图片显示
     * @param b
     */
    private void showBitmapImage(Bitmap b){
        mShowImageView.setImageBitmap(b);
    }

}
