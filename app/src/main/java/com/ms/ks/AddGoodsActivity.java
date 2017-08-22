package com.ms.ks;

import android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.global.Global;
import com.ms.util.CustomRequest;
import com.ms.util.GetImagePath;
import com.ms.util.JsonUtil;
import com.ms.util.RequestManager;
import com.ms.util.SelectPicPopupWindow;
import com.ms.util.SetEditTextInput;
import com.ms.util.SysUtils;
import com.ms.util.UploadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddGoodsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Switch btn_switch,good_switch_up;
    private EditText et_goodname,et_goodprice,et_goodcode,et_good_costprice,et_good_grossprice,
            et_good_stocknum,et_good_stock,et_good_remark;
    private ImageView iv_scangoodcode,iv_goodpicture,iv_back;
    private Button btn_scangoodcode,btn_save,btn_edit_cell,btn_edit_save,btn_cell,btn_sure,btn_add_store;
    private TextView tv_good_type,tv_title_name,tv_message,tv_good_size;
    private LinearLayout good_stocknum_layout,good_edit_bottombtn_layout;
    private RelativeLayout good_stock_layout,layout_goodscode,layout_good_type,layout_good_size;
    private int btn_switch_type=1;//1无码商品添加，2有码商品添加
    private int good_switch_type=1;//0商品下架，1商品上架
    private SelectPicPopupWindow mSelectPicPopupWindow;
    private  int INTENT_BTN_PICK_PHOTO=200;
    private  int INTENT_BTN_TAKE_PHOTO=201;
    private  int PHOTO_CROP_CODE=203;//图片裁剪
    private  int INTENT_GOODS_SORT=202;
    private String mFilePath;
    private Uri uri;
    private Uri photoUri;
    private final String requrl=SysUtils.getUploadImageServiceUrl("fileup");
    private File myCaptureFile=null;
    private String good_trpe_result;
    private String good_trpe_result_id;
    private String image_id="";
    private int type;
    private Uri pick_photo;
    private Bitmap bit=null;
    private int isTakePhoto=1;//1表示从相册获取图片，2表示拍照获取
    private String goods_id="";//商品id
    private String product_id="";//产品id，编辑删除后台需要
    private String spec_info="";//规格
    private DisplayImageOptions options=new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.picture_default)
            .showImageForEmptyUri(R.drawable.picture_default)
            .cacheInMemory(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(20))
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        SysUtils.setupUI(this,findViewById(R.id.activity_add_goods));
        initToolbar(this);
//        setToolbarTitle("无码商品添加");
        initView();
        Intent intent=getIntent();
        if(intent!=null){
            type=intent.getIntExtra("type",0);
            goods_id=intent.getStringExtra("goods_id");
            if(type==2||type==3){
                tv_title_name.setText("商品编辑");
                good_edit_bottombtn_layout.setVisibility(View.VISIBLE);
                layout_good_size.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.GONE);
                btn_add_store.setVisibility(View.VISIBLE);
                getGoodsDeatil();
            }else {
                good_edit_bottombtn_layout.setVisibility(View.GONE);
                layout_good_size.setVisibility(View.GONE);
                btn_save.setVisibility(View.VISIBLE);
                btn_add_store.setVisibility(View.GONE);
            }
        }

    }

    private void initView() {
        btn_switch= (Switch) findViewById(R.id.btn_switch);
        good_switch_up= (Switch) findViewById(R.id.good_switch_up);
        et_goodname= (EditText) findViewById(R.id.et_goodname);
        et_goodprice= (EditText) findViewById(R.id.et_goodprice);
        et_goodcode= (EditText) findViewById(R.id.et_goodcode);
        et_good_costprice= (EditText) findViewById(R.id.et_good_costprice);
        et_good_grossprice= (EditText) findViewById(R.id.et_good_grossprice);
        et_good_stocknum= (EditText) findViewById(R.id.et_good_stocknum);
        et_good_stock= (EditText) findViewById(R.id.et_good_stock);
        et_good_remark= (EditText) findViewById(R.id.et_good_remark);
        iv_scangoodcode= (ImageView) findViewById(R.id.iv_scangoodcode);
        iv_goodpicture= (ImageView) findViewById(R.id.iv_goodpicture);
        iv_back= (ImageView) findViewById(R.id.iv_back);
        btn_scangoodcode= (Button) findViewById(R.id.btn_scangoodcode);
        btn_save= (Button) findViewById(R.id.btn_save);
        btn_edit_cell= (Button) findViewById(R.id.btn_edit_cell);
        btn_edit_save= (Button) findViewById(R.id.btn_edit_save);
        btn_add_store= (Button) findViewById(R.id.btn_add_store);
        tv_good_type= (TextView) findViewById(R.id.tv_good_type);
        tv_title_name= (TextView) findViewById(R.id.tv_title_name);
        layout_goodscode= (RelativeLayout) findViewById(R.id.layout_goodscode);
        good_stock_layout= (RelativeLayout) findViewById(R.id.good_stock_layout);
        good_stocknum_layout= (LinearLayout) findViewById(R.id.good_stocknum_layout);
        layout_good_type= (RelativeLayout) findViewById(R.id.layout_good_type);
        good_edit_bottombtn_layout= (LinearLayout) findViewById(R.id.good_edit_bottombtn_layout);
        layout_good_size= (RelativeLayout) findViewById(R.id.layout_good_size);//规格布局
        tv_good_size= (TextView) findViewById(R.id.tv_good_size);//规格

        et_goodcode.setInputType(InputType.TYPE_NULL);//设置输入框不能点击
        SetEditTextInput.judgeNumber(et_good_grossprice);
        SetEditTextInput.judgeNumber(et_good_costprice);
        SetEditTextInput.judgeNumber(et_goodprice);
        btn_switch.setOnCheckedChangeListener(this);
        good_switch_up.setOnCheckedChangeListener(this);
        iv_goodpicture.setOnClickListener(this);
        layout_good_type.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_edit_save.setOnClickListener(this);
        btn_edit_cell.setOnClickListener(this);
        iv_scangoodcode.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_scangoodcode.setOnClickListener(this);
        btn_add_store.setOnClickListener(this);
        layout_good_size.setOnClickListener(this);


        /**
         * 毛利根据输入的成本价 自动变化
         */
        et_good_costprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String goodprice_src=et_goodprice.getText().toString().trim();
                String good_costprice_src=et_good_costprice.getText().toString().trim();
                Double goodprice_double =0.00;
                Double good_costprice_double=0.00;
                if(goodprice_src.length()>0){
                   goodprice_double=Double.parseDouble(goodprice_src);
                }
                if(good_costprice_src.length()>0){
                    good_costprice_double=Double.parseDouble(good_costprice_src);
                }
                Double good_grossprice_double=goodprice_double-good_costprice_double;
                et_good_grossprice.setText(good_grossprice_double+"");
            }
        });
    }

    /**
     * 光标在最后
     * @param editText
     */
    private void edittext(EditText editText){
    String editText_str=editText.getText().toString().trim();
    if(!TextUtils.isEmpty(editText_str)){
        editText.setSelection(editText_str.length());
    }


}
    /**
     * 商品上传，及编辑上传
     */
   private void addSave(){
       String goodname=et_goodname.getText().toString().trim();
       String goodprice=et_goodprice.getText().toString().trim();
       String goodcode=et_goodcode.getText().toString().trim();
       String good_costprice=et_good_costprice.getText().toString().trim();
       String good_grossprice=et_good_grossprice.getText().toString().trim();
       String good_stocknum=et_good_stocknum.getText().toString().trim();
       String good_stock=et_good_stock.getText().toString().trim();
       String good_remark=et_good_remark.getText().toString().trim();
       if(TextUtils.isEmpty(goodname)){
           SysUtils.showError("商品名称不能为空！");
           return;
       }
       if(TextUtils.isEmpty(goodprice)){
           SysUtils.showError("商品售价不能为空！");
           return;
       }
       if(TextUtils.isEmpty(good_trpe_result_id)){
           SysUtils.showError("商品类别不能为空！");
           return;
       }
       if(btn_switch_type==2){
           if(TextUtils.isEmpty(goodcode)){
               SysUtils.showError("商品条码不能为空！");
               return;
           }
           if(TextUtils.isEmpty(good_stocknum)){
               SysUtils.showError("商品库存量不能为空！");
               return;
           }
           if(TextUtils.isEmpty(good_stock)){
               SysUtils.showError("商品库存预警量不能为空！");
               return;
           }
       }
    Map<String,String> map=new HashMap<>();
       map.put("name",goodname);//名称
       map.put("price",goodprice);//售价
       map.put("cost",good_costprice);//成本价
       map.put("bncode",goodcode);//条码
       map.put("little_profit",good_grossprice);//毛利
       map.put("store",good_stocknum);//库存量
       map.put("isup",good_switch_type+"");//是否上下架
       map.put("image_id",image_id);//图片url
       map.put("good_stock",good_stock);//库存预警量
       map.put("good_remark",good_remark);//备注
       map.put("tag_id",good_trpe_result_id);//类别
       map.put("btn_switch_type",btn_switch_type+"");
       if(type==2||type==3){
           map.put("edit","edit");
           map.put("goods_id",goods_id);
           map.put("product_id",product_id);
       }
       System.out.println("商品downGoods()map="+map);
       CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getGoodsServiceUrl("goodsToAdd"), map, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject jsonObject) {
               hideLoading();
               try {
                   JSONObject ret = SysUtils.didResponse(jsonObject);
                   System.out.println("商品ret="+ret);
                   String status = ret.getString("status");
                   String message = ret.getString("message");
                   JSONObject  data=null;
                   if(!status.equals("200")){
                       SysUtils.showError(message);
                   }else {
                       data=ret.getJSONObject("data");
                       String msg = data.getString("msg");
                       SysUtils.showError(msg);
                       finish();
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }finally {
                   if(type==2){
                       sendBroadcast(new Intent(Global.BROADCAST_GoodsManagementactivity_ACTION));
                   }else if(type==1){
                       sendBroadcast(new Intent(Global.BROADCAST_GoodsManagementactivity_ACTION).putExtra("tag_id",good_trpe_result_id));
                   }
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               hideLoading();
               SysUtils.showNetworkError();

           }
       });
       executeRequest(customRequest);
       showLoading(AddGoodsActivity.this);
    }

    /**
     * 获取商品详情
     */
    String name;
    private void getGoodsDeatil(){
        Map<String,String> map=new HashMap<>();
        map.put("goods_id",goods_id);//图片url
        CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getGoodsServiceUrl("goods_page"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("商品ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject  data=null;
                    if(!status.equals("200")){
                        SysUtils.showError(message);
                    }else {
                        data=ret.getJSONObject("data");
                        JSONObject goods_info=data.getJSONObject("goods_info");
                        if(goods_info!=null){
                            String name=goods_info.getString("name");
                            Double price=goods_info.getDouble("price");
                            String bncode=goods_info.getString("bncode");
                            Double cost=goods_info.getDouble("cost");
                            Double little_profit= JsonUtil.getJsonDouble(goods_info,"little_profit");
                            String good_remark=goods_info.getString("good_remark");
                            String status_up=goods_info.getString("status");
                            String img_src=data.getString("img_src");
                            spec_info=data.getString("spec_info");//规格
                            int store= JsonUtil.getJsonInt(goods_info,"store");
                            int good_stock=JsonUtil.getJsonInt(goods_info,"good_stock");
                            btn_switch_type=goods_info.getInt("btn_switch_type");
                            good_trpe_result=data.getString("tag_name");
                            good_trpe_result_id=data.getString("tag_id");
                            product_id=data.getString("product_id");
                            if( btn_switch_type==2){
                                btn_switch.setChecked(true);
                                et_goodcode.setText(bncode);
                            }
                            if(status_up.equals("true")){
                                good_switch_up.setChecked(true);
                            }else if(status_up.equals("false")){
                                good_switch_up.setChecked(false);
                            }
                            et_goodname.setText(name);
                            et_goodprice.setText(price+"");
                            et_good_costprice.setText(cost+"");
                            et_good_grossprice.setText(little_profit+"");

                            tv_good_type.setText(good_trpe_result);
                            et_good_stocknum.setText(store+"");
                            et_good_stock.setText(good_stock+"");
                            et_good_remark.setText(good_remark);
                            tv_good_size.setText(spec_info);

                            //加载图片
                            ImageLoader.getInstance().displayImage(img_src,iv_goodpicture,options);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print("e="+e.toString());
                }finally {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                SysUtils.showNetworkError();

            }
        });
        executeRequest(customRequest);
        showLoading(AddGoodsActivity.this);
    }
    /**
     * 获取商品条码
     */
    private void getGoodsCode(){
        CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getGoodsServiceUrl("get_code"),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject  data=null;
                    if(!status.equals("200")){
                        SysUtils.showError(message);
                    }else {
                        data=ret.getJSONObject("data");
                        String code=data.getString("code");
                        et_goodcode.setText(code);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                SysUtils.showNetworkError();

            }
        });
        executeRequest(customRequest);
        showLoading(AddGoodsActivity.this);
    }
    /**
     * 删除商品
     */
    private void deteleGoods(){
        Map<String,String> map=new HashMap<>();
        map.put("goods_id",goods_id);
        map.put("product_id",product_id);
        CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getGoodsServiceUrl("remove_goods"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                hideLoading();
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("商品ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject  data=null;
                    if(!status.equals("200")){
                        SysUtils.showError(message);
                    }else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    sendBroadcast(new Intent(Global.BROADCAST_GoodsManagementactivity_ACTION));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                SysUtils.showNetworkError();

            }
        });
        executeRequest(customRequest);
        showLoading(AddGoodsActivity.this);
    }

    /**
     * 各控件的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_goodpicture:
                getSelectPicPopupWindow();
                mSelectPicPopupWindow.showAtLocation(findViewById(R.id.iv_goodpicture) , Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.layout_good_type://获取商品类别
                Intent good_typeintent=new Intent(AddGoodsActivity.this,SortManagementActivity.class);
                good_typeintent.putExtra("type",2);
                startActivityForResult(good_typeintent,INTENT_GOODS_SORT);
                break;
            case R.id.btn_edit_cell://商品删除
                isDelete();
                break;
            case R.id.btn_edit_save://商品编辑保存
                addSave();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_save://商品添加
                addSave();
                break;
            case R.id.btn_scangoodcode://生成条码
                getGoodsCode();
                break;
            case R.id.iv_scangoodcode://获取扫描条码
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(AddGoodsActivity.this, android.Manifest.permission.CAMERA);
                    if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddGoodsActivity.this,new String[]{android.Manifest.permission.CAMERA},222);
                        return;
                    }else{
                        Intent intent=new Intent(AddGoodsActivity.this,MipcaActivityCapture.class);
                        intent.putExtra("type",2);
                        startActivityForResult(intent,205);
                    }
                } else {
                    Intent intent=new Intent(AddGoodsActivity.this,MipcaActivityCapture.class);
                    intent.putExtra("type",2);
                    startActivityForResult(intent,205);
                }
                break;
            case R.id.btn_add_store://补充库存
                addstore();
                break;
            case R.id.layout_good_size://规格
                if(TextUtils.isEmpty(spec_info)){//为空新增规格，则规格编辑
                    Intent intent=new Intent(AddGoodsActivity.this,GoodsSizeActivity.class);
                    intent.putExtra("goods_id",goods_id);
                    intent.putExtra("goods_name",et_goodname.getText().toString().trim());
                    intent.putExtra("goodprice",et_goodprice.getText().toString().trim());
                    intent.putExtra("good_stocknum",et_good_stocknum.getText().toString().trim());
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(AddGoodsActivity.this,GoodsSize_spec_InfoActivity.class);
                    intent.putExtra("goods_id",goods_id);
                    intent.putExtra("type",1);//规格编辑
                    startActivity(intent);
                }

                break;
        }
    }

    /**
     * 补充库存弹窗
     */
    AlertDialog maddstoredoalog;
    EditText et_accountinfo_input;
    InputMethodManager imm;
    private void addstore() {
        AlertDialog.Builder builder=new AlertDialog.Builder(AddGoodsActivity.this);
        View view=View.inflate(AddGoodsActivity.this,R.layout.add_store_dialog,null);
        TextView tv_sure= (TextView) view.findViewById(R.id.tv_sure);
        TextView tv_nomark= (TextView) view.findViewById(R.id.tv_nomark);
        et_accountinfo_input=(EditText)view.findViewById(R.id.et_input);
        et_accountinfo_input.requestFocus();
        et_accountinfo_input.setFocusable(true);
        //软键盘显示
        imm = (InputMethodManager) AddGoodsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        tv_nomark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maddstoredoalog.dismiss();
                //再次调用软键盘消失
                if(isSoftShowing()){
                    //再次调用软键盘消失
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et_input_str=et_accountinfo_input.getText().toString().trim();
                if(TextUtils.isEmpty(et_input_str)){
                    Toast.makeText(AddGoodsActivity.this,"请输入补充库存量！",Toast.LENGTH_SHORT).show();
                    return;
                }
               String et_good_stocknum_str=et_good_stocknum.getText().toString().trim();
                if(!TextUtils.isEmpty(et_good_stocknum_str)){
                    int et_good_stocknum_int=Integer.parseInt(et_good_stocknum_str)+Integer.parseInt(et_input_str);
                    et_good_stocknum.setText(et_good_stocknum_int+"");
                }
                maddstoredoalog.dismiss();
                //再次调用软键盘消失
                if(isSoftShowing()){
                    //再次调用软键盘消失
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
        maddstoredoalog=builder.setView(view).show();
        maddstoredoalog.setCancelable(false);

        Window dialogWindow = maddstoredoalog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        lp.y = 200; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
        maddstoredoalog.show();
    }
    //判断软键盘是否存在
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    /**
     * 商品删除
     */
    private void isDelete(){
        View view=View.inflate(AddGoodsActivity.this,R.layout.dialog_sure_delete_layout,null);
        final Dialog dialog=new Dialog(AddGoodsActivity.this);
        dialog.setContentView(view);
        btn_sure= (Button) view.findViewById(R.id.btn_sure);
        btn_cell= (Button) view.findViewById(R.id.btn_cell);
        tv_message= (TextView) view.findViewById(R.id.tv_message);
        tv_message.setText("确定删除该商品吗？");
        btn_sure.setText("确定");
        btn_cell.setText("取消");
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deteleGoods();
                dialog.dismiss();
                finish();
            }
        });
        btn_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 选择按钮的监听
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.btn_switch:
                if(isChecked){
                    if(type==1){
                        tv_title_name.setText("有码商品添加");
                    }else {
                        tv_title_name.setText("商品编辑");
                    }
                    btn_switch_type=2;
                    layout_goodscode.setVisibility(View.VISIBLE);
                    good_stock_layout.setVisibility(View.VISIBLE);
                    good_stocknum_layout.setVisibility(View.VISIBLE);
                }else {
                    if(type==1){
                    tv_title_name.setText("无码商品添加");
                    }else {
                        tv_title_name.setText("商品编辑");
                    }
                    btn_switch_type=1;
                    layout_goodscode.setVisibility(View.GONE);
                    good_stock_layout.setVisibility(View.GONE);
                    good_stocknum_layout.setVisibility(View.GONE);
                }
                break;
            case R.id.good_switch_up:
                if(isChecked){
                    good_switch_type=1;
                }else {
                    good_switch_type=0;
                }
                break;


        }
    }
    /**
     * 弹出拍照，从相册获取添加图片的窗口
     */
    private void getSelectPicPopupWindow(){
        mSelectPicPopupWindow=new SelectPicPopupWindow(AddGoodsActivity.this, new View.OnClickListener() {
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
                            int checkwritefile= ContextCompat.checkSelfPermission(AddGoodsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if(checkwritefile != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(AddGoodsActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},223);
                                return;
                            } else {
                                creatfile();
                            }
                            //动态添加拍照权限
                            int checkCallPhonePermission = ContextCompat.checkSelfPermission(AddGoodsActivity.this, android.Manifest.permission.CAMERA);
                            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(AddGoodsActivity.this,new String[]{android.Manifest.permission.CAMERA},222);
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
     * @param
     * @description 裁剪图片
     * @author ldm
     * @time 2016/11/30 15:19
     */
    // 定义拍照后存放图片的文件位置和名称，使用完毕后可以方便删除
    File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
    private void startPhotoZoom(Uri uri, int type) {
        Bitmap bit = null;
        try {
            bit = UploadUtil.getBitmapFormUri(AddGoodsActivity.this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = GetImagePath.getPath(AddGoodsActivity.this, uri);
        myCaptureFile= UploadUtil.saveFile(bit,path+".jpg");
        photoUri = Uri.fromFile(myCaptureFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // 去黑边
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        // aspectX aspectY 是宽高的比例，根据自己情况修改
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高像素
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //取消人脸识别功能
        intent.putExtra("noFaceDetection", true);
        //设置返回的uri
//        if(type==INTENT_BTN_PICK_PHOTO)
//        {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        }else {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        //设置为不返回数据
        intent.putExtra("return-data",false);
        startActivityForResult(intent, PHOTO_CROP_CODE);
    }
    /**
     *创建存储拍照图片的文件夹
     */
    private void creatfile(){
        String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String str_path=getSDPath()+"/image";
//        File file=new File(str_path.trim());
//        //判断文件夹是否存在,如果不存在则创建文件夹
        // 获取SD卡路径
        mFilePath= str_path+ name + ".jpg";
        File file=new File(mFilePath);
//        if (!file.exists()) {
//            try {
//                file.mkdirs();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        uri= Uri.fromFile(new File(mFilePath));;
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

    /**
     * 页面结果返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==INTENT_GOODS_SORT&&resultCode==204){
            Bundle bundle=data.getExtras();
            good_trpe_result=bundle.getString("goodtype");
            good_trpe_result_id=bundle.getString("goodtype_id");
            tv_good_type.setText(good_trpe_result);
        }
        if(requestCode==205&&resultCode==206){
            Bundle bundle=data.getExtras();
            et_goodcode.setText(bundle.getString("barcode"));
        }
        if (resultCode == RESULT_OK) {
            if(requestCode==222){
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    onOpenCamera();
                } else {
                    Toast.makeText(AddGoodsActivity.this, "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            if(requestCode==223){
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    creatfile();
                } else {
                    Toast.makeText(AddGoodsActivity.this, "很遗憾你把读写权限禁用了。请务必开启相机权限享受我们提供的服务吧。", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            //从相册获取图片并显示
            if (requestCode == INTENT_BTN_PICK_PHOTO) {
                mSelectPicPopupWindow.dismiss();
                pick_photo = data.getData();
                isTakePhoto=1;
                Bitmap bit=null;
                try {
                    bit = UploadUtil.getBitmapFormUri(AddGoodsActivity.this, pick_photo);
                    String path = GetImagePath.getPath(AddGoodsActivity.this, pick_photo);
                    myCaptureFile = UploadUtil.saveFile(bit,path+".jpg");
                    photoUri = Uri.fromFile(myCaptureFile);
                    startPhotoZoom(photoUri,INTENT_BTN_PICK_PHOTO);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ee="+e.toString());
                }
//                upImageAsyTack upImageAsyTack=new upImageAsyTack();
//                upImageAsyTack.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            }
            //拍照获取图片并显示
            if (requestCode == INTENT_BTN_TAKE_PHOTO) {
                mSelectPicPopupWindow.dismiss();
                isTakePhoto=2;
                try {
                    bit=  UploadUtil.getBitmapFormUri(AddGoodsActivity.this,uri);
                    myCaptureFile= UploadUtil.saveFile(bit,mFilePath+".jpg");
                     photoUri = Uri.fromFile(myCaptureFile);
                    startPhotoZoom(photoUri,INTENT_BTN_TAKE_PHOTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode ==PHOTO_CROP_CODE){
            upImageAsyTack upImageAsyTack=new upImageAsyTack();
            upImageAsyTack.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
    /**
     * uri格式的图片显示
     * @param uri
     */
    private void showUriImage(Uri uri){
        iv_goodpicture.setImageURI(uri);
    }
    /**
     * 上传图片
     */
    class  upImageAsyTack extends AsyncTask<Void,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading(AddGoodsActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {
            String str = UploadUtil.uploadFile( myCaptureFile, requrl, "photo");
            if (str != null) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("添加商品图片ret=" + ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject data = ret.getJSONObject("data");
                    image_id = data.getString("image_id");
                    if (!status.equals("200")) {
                        SysUtils.showError(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return image_id;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideLoading();
            if (s != null) {

                if (isTakePhoto == 2) {
                        showUriImage(photoUri);
                }
                if (isTakePhoto == 1) {
                    if (pick_photo != null) {
                        showUriImage(photoUri);
                    }
                }
            }else {
                SysUtils.showError("图片上传失败");
            }
            if( myCaptureFile.exists()){
                myCaptureFile.delete();  //删除原图片
            }
        }
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        AddGoodsActivity.this.registerReceiver(broadcastReceiver,new IntentFilter("AddGoodsActivity.Action"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AddGoodsActivity.this.unregisterReceiver(broadcastReceiver);
    }
}
