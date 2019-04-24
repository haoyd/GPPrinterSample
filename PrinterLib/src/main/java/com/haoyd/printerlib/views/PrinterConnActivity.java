package com.haoyd.printerlib.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haoyd.printerlib.R;
import com.haoyd.printerlib.entities.BluetoothDeviceInfo;
import com.haoyd.printerlib.interfaces.RecyclerItemClickListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnBluetoothPairdChangedListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFindNewBluetoothListener;
import com.haoyd.printerlib.interfaces.bluetooth.OnFinishDiscoveryBluetoothListener;
import com.haoyd.printerlib.manager.BluetoothDeviceManager;
import com.haoyd.printerlib.manager.PrinterListManager;
import com.haoyd.printerlib.manager.PrinterManager;
import com.haoyd.printerlib.utils.BluetoothUtil;
import com.haoyd.printerlib.utils.SysBroadcastUtil;
import com.kaopiz.kprogresshud.KProgressHUD;

public class PrinterConnActivity extends AppCompatActivity {

    private static final int STATE_LOADING = 0;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_SUCCESS = 2;
    private static final int STATE_EMPTY = 3;

    private ImageView icon;
    private TextView tip;
    private ProgressBar progressBar;
    private RecyclerView listView;
    private PrinterEmptyView emptyView;
    private PrinterListAdapter adapter;
    private KProgressHUD loadHud;

    /**
     * init tool
     */
    private BluetoothDeviceManager bluetoothDeviceManager;
    private PrinterManager printerManager;
    private SysBroadcastUtil sysBroadcastUtil;

    /**
     * init variables
     */
    private PrinterListManager dataManager;
    private boolean isPairdDeviceBacked = false;
    private String connectedPrinterName = "";
    private boolean canStopConn = false;
    private boolean isItemClicked = false;

    private boolean isFromBDPrint;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            canStopConn = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_conn);

        initViewById();
        loadView();
        loadData();
        loadListener();

        bluetoothDeviceManager.scanDevice();
        setLoadState(STATE_LOADING);
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothDeviceManager.cancelScan();
        sysBroadcastUtil.unregistReceiver();
        printerManager.unbindService();
        printerManager = null;
    }

    public void initViewById() {
        this.listView = findViewById(R.id.rv_printer_conn);
        this.icon = findViewById(R.id.iv_printer_conn_icon);
        this.tip = findViewById(R.id.tv_printer_conn);
        this.emptyView = findViewById(R.id.pev_printer_conn);
        this.progressBar = findViewById(R.id.pb_printer_conn);
    }

    public void loadView() {
        isFromBDPrint = getIntent().getBooleanExtra("isFromBDPrint", false);

        dataManager = new PrinterListManager();
        adapter = new PrinterListAdapter(this);
        adapter.setData(dataManager.getData());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);

        listView.setAdapter(adapter);

        loadHud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("连接中，请稍候...")
                .setDimAmount(0.5f);
    }

    public void loadData() {
        sysBroadcastUtil = new SysBroadcastUtil(this);
        bluetoothDeviceManager = new BluetoothDeviceManager(this);
        printerManager = new PrinterManager(this);
        printerManager.bindService();
    }

    public void loadListener() {
        sysBroadcastUtil.setOnDiscoveryNewBluetoothListener(new OnFindNewBluetoothListener() {
            @Override
            public void onFindNew(BluetoothDeviceInfo info) {
                // 判断是不是打印机
                if (info == null || !info.isPrinterDevice()) {
                    return;
                }
                canStopConn = true;
                dataManager.addNewDevice(info);
                adapter.setData(dataManager.getData());
            }

            @Override
            public void onFindPaird(BluetoothDeviceInfo info) {
                // 判断是不是打印机
                if (info == null || !info.isPrinterDevice()) {
                    return;
                }

                if (info.name.equals(connectedPrinterName)) {
                    info.isConnected = true;
                } else {
                    info.isConnected = false;
                }

                canStopConn = true;
                dataManager.addPairdDevice(info);
                adapter.setData(dataManager.getData());
            }
        });

        sysBroadcastUtil.setOnFinishDiscoveryBluetoothListener(new OnFinishDiscoveryBluetoothListener() {
            @Override
            public void onFinish() {
                setLoadState(STATE_NORMAL);

                if (isPairdDeviceBacked) {
                    return;
                }

                isPairdDeviceBacked = true;

                if (dataManager.getTotalSize() == 0) {
                    if (canStopConn) {
                        setLoadState(STATE_EMPTY);
                    } else {
                        doScanWork();
                    }
                } else if (!TextUtils.isEmpty(connectedPrinterName)) {
                    setLoadState(STATE_SUCCESS);
                } else {
                    setLoadState(STATE_NORMAL);
                }
            }
        });

        sysBroadcastUtil.setOnSysPairedListener(new OnBluetoothPairdChangedListener() {
            @Override
            public void onPared(BluetoothDeviceInfo info) {
                if (info != null && !TextUtils.isEmpty(info.name)) {
                    printerManager.connectToPrinter(info);
                }
            }

            @Override
            public void onDispared(BluetoothDeviceInfo info) {

            }
        });


        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScanWork();
            }
        });

        emptyView.setConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScanWork();
            }
        });

        adapter.setItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                isItemClicked = true;
                bluetoothDeviceManager.cancelScan();
                loadHud.setLabel("连接中，请稍候...");
                loadHud.show();
                printerManager.connectToPrinter(dataManager.getItem(position));
            }
        });
    }

    /**
     * 显示不同连接状态
     *
     * @param state
     */
    private void setLoadState(int state) {
        switch (state) {
            case STATE_LOADING:
                progressBar.setVisibility(View.VISIBLE);
                tip.setTextColor(Color.parseColor("#333333"));
                tip.setText("查找中");
                emptyView.hide();
                break;
            case STATE_NORMAL:
                progressBar.setVisibility(View.GONE);
                tip.setTextColor(Color.parseColor("#33c298"));
                tip.setText("查找完成");

                if (dataManager.getTotalSize() > 0) {
                    emptyView.hide();
                } else {
                    emptyView.show();
                }
                break;
            case STATE_SUCCESS:
                progressBar.setVisibility(View.GONE);
                icon.setImageResource(R.mipmap.ic_printer_set_linked);
                tip.setTextColor(Color.parseColor("#33c298"));
                tip.setText("已连接");
                emptyView.hide();
                break;
            case STATE_EMPTY:
                progressBar.setVisibility(View.GONE);
                icon.setImageResource(R.mipmap.ic_printer_set_unlinked);
                tip.setTextColor(Color.parseColor("#f14f51"));
                tip.setText("无可用打印机，点击刷新");
                emptyView.show();
                break;
        }
    }

    private void doScanWork() {
        if (BluetoothUtil.isOpening()) {
            isPairdDeviceBacked = false;
            dataManager.clear();

            if (bluetoothDeviceManager.isScaning()) {
                bluetoothDeviceManager.cancelScan();
            }

            bluetoothDeviceManager.scanDevice();
            setLoadState(STATE_LOADING);
        } else {
            new AlertDialog.Builder(PrinterConnActivity.this)
                    .setTitle("提示")
                    .setMessage("蓝牙未开启将影响您的正常使用，请打开蓝牙")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .show();
        }
    }
}
