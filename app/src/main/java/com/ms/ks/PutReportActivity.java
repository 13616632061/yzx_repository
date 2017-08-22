package com.ms.ks;

import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ms.util.CustomRequest;
import com.ms.util.DateUtils;
import com.ms.util.StringUtils;
import com.ms.util.SysUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class PutReportActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_nearly_seven,btn_nearly_thirty,btn_e_mail,btn_native;
    private RelativeLayout layout_set_starttime,layout_set_endtime;
    private TextView tv_starttime,tv_endtime;
    private EditText et_input_e_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_report);
        SysUtils.setupUI(PutReportActivity.this,findViewById(R.id.activity_put_report));
        initToolbar(this);

        initView();
    }

    private void initView() {
        btn_nearly_seven= (Button) findViewById(R.id.btn_nearly_seven);
        btn_nearly_thirty= (Button) findViewById(R.id.btn_nearly_thirty);
        btn_e_mail= (Button) findViewById(R.id.btn_e_mail);
        btn_native= (Button) findViewById(R.id.btn_native);
        layout_set_starttime= (RelativeLayout) findViewById(R.id.layout_set_starttime);
        layout_set_endtime= (RelativeLayout) findViewById(R.id.layout_set_endtime);
        tv_starttime= (TextView) findViewById(R.id.tv_starttime);
        tv_endtime= (TextView) findViewById(R.id.tv_endtime);
        et_input_e_mail= (EditText) findViewById(R.id.et_input_e_mail);

        btn_nearly_seven.setOnClickListener(this);
        btn_nearly_thirty.setOnClickListener(this);
        btn_e_mail.setOnClickListener(this);
        btn_native.setOnClickListener(this);
        layout_set_starttime.setOnClickListener(this);
        layout_set_endtime.setOnClickListener(this);

        initData();

    }

    private void initData() {
        tv_endtime.setText(DateUtils.getCurDate());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_nearly_seven:
               String str_nearly_seven= DateUtils.getNearlyDate(tv_endtime.getText().toString().trim(),7);
                tv_starttime.setText(str_nearly_seven);
                break;
            case R.id.btn_nearly_thirty:
                String str_nearly_thirty= DateUtils.getNearlyDate(tv_endtime.getText().toString().trim(),30);
                tv_starttime.setText(str_nearly_thirty);
                break;
            case R.id.btn_e_mail:
                sendEmail();
                break;
            case R.id.btn_native:
                downLoadExcel();
                break;
            case R.id.layout_set_starttime:
                DateUtils.runTime(PutReportActivity.this,tv_starttime);
                break;
            case R.id.layout_set_endtime:
                DateUtils.runTime(PutReportActivity.this,tv_endtime);
                break;
        }
    }
    /**
     * 发送至邮箱
     */
    private  void sendEmail(){
        String starttime=tv_starttime.getText().toString().trim();
        String endtime=tv_endtime.getText().toString().trim();
        String e_mail=et_input_e_mail.getText().toString().trim();
        if(TextUtils.isEmpty(starttime)||starttime.length()<5){
            Toast.makeText(PutReportActivity.this,"开始时间不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(endtime)){
            Toast.makeText(PutReportActivity.this,"结束时间不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(e_mail)){
            Toast.makeText(PutReportActivity.this,"邮箱不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!StringUtils.isEmail(e_mail)){
            Toast.makeText(PutReportActivity.this,"请输入正确的邮箱地址！",Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("begintime",starttime);
        map.put("endtime",endtime);
        map.put("Email",e_mail);
        CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("export"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("对账单导出ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    JSONObject  data=null;
                    if(!status.equals("200")){
                        SysUtils.showError(message);
                    }else {
                        et_input_e_mail.setText("");
                        Toast.makeText(PutReportActivity.this,"对账单已成功发送至邮箱！",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SysUtils.showNetworkError();

            }
        });
        executeRequest(customRequest);
    }
    private  void downLoadExcel(){
        String starttime=tv_starttime.getText().toString().trim();
        String endtime=tv_endtime.getText().toString().trim();
        if(TextUtils.isEmpty(starttime)||starttime.length()<5){
            Toast.makeText(PutReportActivity.this,"开始时间不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(endtime)){
            Toast.makeText(PutReportActivity.this,"结束时间不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("begintime",starttime);
        map.put("endtime",endtime);
        CustomRequest customRequest=new CustomRequest(Request.Method.POST, SysUtils.getSellerServiceUrl("download"), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONObject ret = SysUtils.didResponse(jsonObject);
                    System.out.println("对账单导出本地ret="+ret);
                    String status = ret.getString("status");
                    String message = ret.getString("message");
                    if(!status.equals("200")){
                        SysUtils.showError(message);
                    }else {
                        String data=ret.getString("data");
                        System.out.println("data="+data);
                        download(data);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SysUtils.showNetworkError();

            }
        });
        executeRequest(customRequest);
    }

    //下载具体操作
    private void download(final String downloadUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(downloadUrl);
                    //打开连接
                    URLConnection conn = url.openConnection();
                    //打开输入流
                    InputStream is = conn.getInputStream();
                    //获得长度
                    int contentLength = conn.getContentLength();
                    //创建文件夹 MyDownLoad，在存储卡下
                    String dirName = Environment.getExternalStorageDirectory() + "/MyDownLoad/";
                    File file = new File(dirName);
                    //不存在创建
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    //下载后的文件名
                    String fileName = dirName +DateUtils.getCurDate()+ ".xlsx";
                    File file1 = new File(fileName);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    //创建字节流
                    byte[] bs = new byte[1024];
                    int len;
                    OutputStream os = new FileOutputStream(fileName);
                    //写数据
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    Looper.prepare();
                    Toast.makeText(PutReportActivity.this,"对账单已成功导入至本地！",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    //完成后关闭流
                    os.close();
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("e="+e.toString());
                }
            }
        }).start();
    }
}
