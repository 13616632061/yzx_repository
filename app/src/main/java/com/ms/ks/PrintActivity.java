package com.ms.ks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.material.widget.PaperButton;
import com.ms.adapter.Printer;
import com.ms.util.PrintUtil;
import com.ms.util.SysUtils;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zj.btsdk.BluetoothService;

import java.util.ArrayList;
import java.util.Set;

public class PrintActivity extends BaseActivity {
    BluetoothService mService = null;

    private ListView printers;
    private ArrayList<Printer> printer_list;
    private PrinterAdapter adapter;

    private ProgressDialog progressDialog = null;
    private MaterialEditText printnum;
    private String printer_mac;
    private PaperButton btn_test;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean hasConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        initToolbar(this);

        mService = new BluetoothService(this, mHandler);

        if( mService.isAvailable() == false ){
            SysUtils.showError("蓝牙不可用，无法设置打印机参数");
            finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if( mService.isBTopen() == false) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        try {
            //当前设置的打印机
            printnum = (MaterialEditText) findViewById(R.id.printnum);
            printer_mac = KsApplication.getString("printer_mac", "");
            printnum.setText(printer_mac);

            //打印机列表
            printers = (ListView) findViewById(R.id.printers);
            printer_list = new ArrayList<Printer>();
            adapter = new PrinterAdapter();
            printers.setAdapter(adapter);

            //打印测试
            btn_test = (PaperButton) findViewById(R.id.btn_test);
            btn_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mac_str = printnum.getText().toString();

                    if(mac_str.length() < 1) {
                        SysUtils.showError("请输入打印机的mac地址");
                        return;
                    } else {
                        //初始化打印机
                        if(hasConnect) {
                            //已连接
                            String printMsg = PrintUtil.getTestMsg();
                            mService.sendMessage(printMsg, "GBK");
                        } else {
                            //未连接，尝试连接
                            try {
                                if(mService != null) {
                                    BluetoothDevice printDev = mService.getDevByMac(mac_str);

                                    if(printDev == null) {
                                        SysUtils.showError("连接打印机失败，请重新选择打印机");
                                    } else {
                                        mService.connect(printDev);
                                    }
                                } else {
                                    SysUtils.showError("请开启蓝牙并且靠近打印机");
                                }

                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });

            //当扫描到新设备时的操作
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            this.registerReceiver(mReceiver, filter);

            //当扫描完成时的操作
//            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//            this.registerReceiver(mReceiver, filter);

            //开启扫描
            getPairedDeviceList();
//            doDiscovery();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private final  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            doConnectState(true);
                            SysUtils.showSuccess("打印机连接成功");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    doConnectState(false);
//                    SysUtils.showError(PrintActivity.this, "打印机连接丢失");
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    doConnectState(false);
                    SysUtils.showError("无法连接到打印机");
                    break;
            }
        }

    };

    private void doConnectState(boolean hasConnect) {
        this.hasConnect = hasConnect;

        if(hasConnect) {
            btn_test.setText("打印机测试");
        } else {
            btn_test.setText("连接打印机");
        }
    }

//    public void showLoading(final String msg) {
//        hideLoading();
//        progressDialog = new ProgressDialog(PrintActivity.this);
//        progressDialog.setMessage(msg);
//        progressDialog.setIndeterminate(false);
//        progressDialog.show();
//    }
//
//    public void hideLoading() {
//        if(progressDialog != null) {
//            progressDialog.dismiss();
//        }
//    }

    public class PrinterAdapter extends BaseAdapter {
        public int getCount() {
            return printer_list.size();
        }

        public Object getItem(int position) {
            return printer_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(PrintActivity.this).inflate(R.layout.printer_listview_item, null);
            final CheckBox checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView ip = (TextView) convertView.findViewById(R.id.ip);

            Printer bean = printer_list.get(position);
            name.setText(bean.getTitle());
            ip.setText(bean.getMac());

            if(bean.getIsCurrent()) {
                checkBox1.setChecked(true);
            } else {
                checkBox1.setChecked(false);
            }

            checkBox1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0; i < getCount(); i++) {
                        Printer bean = printer_list.get(i);
                        if(i == position) {
                            printnum.setText(bean.getMac());
//                            checkBox1.setChecked(true);
                            bean.setIsCurrent(true);
                        } else {
//                            checkBox1.setChecked(false);
                            bean.setIsCurrent(false);
                        }

                        printer_list.set(i, bean);
                    }

                    notifyDataSetChanged();
                }
            });

//            convertView.setBackgroundResource(R.drawable.category_list_odd_row);

            return convertView;
        }
    }

    //扫描
//    private void doDiscovery() {
//        //首先找到配对的蓝牙设备
//        showLoading("扫描中...");
//
//        if (mService.isDiscovering()) {
//            mService.cancelDiscovery();
//        }
//
//        mService.startDiscovery();
//    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            hideLoading();
//
//            //发现新设备
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    Printer bean = new Printer();
//                    bean.setTitle(device.getName());
//                    bean.setMac(device.getAddress());
//                    bean.setIsCurrent(device.getAddress().equals(printer_mac));
//
//                    printer_list.add(bean);
//                }
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                //没找到
//                if (adapter.getCount() == 0) {
//                    printer_list.clear();
//                }
//            }
//
//            adapter.notifyDataSetChanged();
//        }
//    };

    //得到配对的蓝牙列表
    public void getPairedDeviceList() {
        Set<BluetoothDevice> pairedDevices = mService.getPairedDev();

        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Printer bean = new Printer();
                bean.setTitle(device.getName());
                bean.setMac(device.getAddress());
                bean.setIsCurrent(device.getAddress().equals(printer_mac));

                printer_list.add(bean);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mService != null) {
            mService.stop();
        }

        mService = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        if(menuId == android.R.id.home) {
            onBackPressed();
        } else if(menuId == R.id.menu_save) {
            //保存
            String mac_str = printnum.getText().toString();
            KsApplication.putString("printer_mac", mac_str);

            SysUtils.showSuccess("操作已执行");

            onBackPressed();
        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    //蓝牙打开成功
                    SysUtils.showSuccess("蓝牙已打开");
                } else {
                    SysUtils.showError("蓝牙打开失败，无法设置打印机参数");
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
