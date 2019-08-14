# GPPrinterSample

## 说明

对佳博热敏打印机打印小票功能进行了一些封装，以方便后期开发维护，目前只兼容了80宽度的打印纸。

## 引用依赖

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
	
dependencies {
    implementation 'com.github.haoyd:GPPrinterSample:0.0.7'
    implementation 'com.android.support:recyclerview-v7:27.0.0'
    implementation 'com.kaopiz:kprogresshud:1.2.0'
}	
```

## 使用方法

```
// 继承自 GPPrinterServiceActivity
public class MainActivity extends GPPrinterServiceActivity {

    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitch = findViewById(R.id.switch_main);

        GPPrinterConfig.checkErrorWhenPrinting = true;
        initPrintConfig();
    }

    /**
     * 连接成功回调
     */
    @Override
    public void onConnSuccess() {
        super.onConnSuccess();
        mSwitch.setChecked(true);
    }

    /**
     * 连接失败回调
     */
    @Override
    public void onDisconnect() {
        super.onDisconnect();
        mSwitch.setChecked(false);
    }

    /**
     * 打印失败回调
     * @param error 失败原因
     */
    @Override
    public void onPrintError(String error) {
        super.onPrintError(error);
    }

    /**
     * 打印成功回调
     */
    @Override
    public void onPrintSucc() {
        super.onPrintSucc();
    }

    /**
     * 新页面连接打印机
     * @param view
     */
    public void selectPrinterOnNewPage(View view) {
        startActivity(new Intent(this, GPPrinterConnActivity.class));
    }

    /**
     * 弹窗连接打印机
     * @param view
     */
    public void selectPrinterOnDialog(View view) {
        startActivity(new Intent(this, GPBluetoothDeviceListActivity.class));
    }

    /**
     * 连接历史记录打印机
     * @param view
     */
    public void selectPrinterByHistory(View view) {
        if (GPPrinterDao.getInstance(this).hasHistoryPrinter()) {
            printerManager.connToHistoryDevice();
        } else {
            Toast.makeText(this, "暂无历史记录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 清理历史记录
     * @param view
     */
    public void clearHistory(View view) {
        GPPrinterDao.getInstance(this).clearHistory();
    }

    /**
     * 断开连接
     * @param view
     */
    public void disconnectPrinter(View view) {
        printerManager.disConnectToPrinter();
    }

    /**
     * 打印测试页
     * @param view
     */
    public void printTicket(View view) {
        printerManager.printTestTicket();
    }

    /**
     * 查询打印机状态
     * @param view
     */
    public void queryPrinterStatus(View view) {
        printerManager.queryPrinterStatus();
    }

    /**
     * 打印间隔
     * @param view
     */
    public void lineSpacePrint(View view) {
        printerManager.printTestTicketByLineSpace(80);
    }

    /**
     * 打印配置
     */
    private void initPrintConfig() {
        GPPrinterConfig.checkErrorWhenPrinting = true;
        GPPrinterConfig.showPrintStateDialog = true;
        GPPrinterConfig.alertLackOfPager = true;
    }
}
```



