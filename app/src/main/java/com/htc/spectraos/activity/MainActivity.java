package com.htc.spectraos.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.google.gson.Gson;
import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.adapter.ShortcutsAdapter;
import com.htc.spectraos.adapter.SignalAdapter;
import com.htc.spectraos.databinding.ActivityMainBinding;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.entry.AppSimpleBean;
import com.htc.spectraos.entry.AppsData;
import com.htc.spectraos.entry.ChannelData;
import com.htc.spectraos.entry.ShortInfoBean;
import com.htc.spectraos.entry.SpecialApps;
import com.htc.spectraos.manager.RequestManager;
import com.htc.spectraos.receiver.AppCallBack;
import com.htc.spectraos.receiver.AppReceiver;
import com.htc.spectraos.receiver.BluetoothCallBcak;
import com.htc.spectraos.receiver.BluetoothReceiver;
import com.htc.spectraos.receiver.MyTimeCallBack;
import com.htc.spectraos.receiver.MyTimeReceiver;
import com.htc.spectraos.receiver.MyWifiCallBack;
import com.htc.spectraos.receiver.MyWifiReceiver;
import com.htc.spectraos.receiver.NetWorkCallBack;
import com.htc.spectraos.receiver.NetworkReceiver;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.BluetoothUtils;
import com.htc.spectraos.utils.Constants;
import com.htc.spectraos.utils.Contants;
import com.htc.spectraos.utils.DBUtils;
import com.htc.spectraos.utils.LogUtils;
import com.htc.spectraos.utils.NetWorkUtils;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.utils.TimeUtils;
import com.htc.spectraos.utils.ToastUtil;
import com.htc.spectraos.utils.Uri;
import com.htc.spectraos.utils.VerifyUtil;
import com.htc.spectraos.widget.ManualQrDialog;
import com.htc.spectraos.widget.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseMainActivity implements BluetoothCallBcak, MyWifiCallBack, MyTimeCallBack, NetWorkCallBack {

    private ActivityMainBinding mainBinding;
    private ArrayList<ShortInfoBean> short_list = new ArrayList<>();

    boolean  get_default_url = false;
    private ChannelData channelData;
    private List<AppsData> appsDataList;
    /**
     * receiver
     */

    private NetworkReceiver networkReceiver = null;
    // 时间
    private IntentFilter timeFilter = new IntentFilter();
    private MyTimeReceiver timeReceiver = null;
    // wifi
    private IntentFilter wifiFilter = new IntentFilter();
    private MyWifiReceiver wifiReceiver = null;
    // 蓝牙
    private IntentFilter blueFilter = new IntentFilter();
    private BluetoothReceiver blueReceiver = null;

    private String appName = "";
    private boolean requestFlag = false;
    private final int DATA_ERROR = 102;
    private final int DATA_FINISH = 103;

    private IntentFilter appFilter=new IntentFilter();
    private AppReceiver appReceiver=null;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 202:
                    ShortcutsAdapter shortcutsAdapter = new ShortcutsAdapter(MainActivity.this,short_list);
                    shortcutsAdapter.setItemCallBack(itemCallBack);
                    mainBinding.shortcutsRv.setAdapter(shortcutsAdapter);
                    break;
                case DATA_ERROR:
                    requestFlag = false;
                    ToastUtil.showShortToast(MainActivity.this,getString(R.string.data_err));
                    break;
                case DATA_FINISH:
                    requestFlag = false;
                    if (channelData!=null && channelData.getData().size()>0){
                        startAppFormChannel();
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(mainBinding.getRoot());
        initView();
        initData();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTime();
        updateBle();
        if ((boolean)ShareUtil.get(this,Contants.MODIFY,false)){
            short_list =loadHomeAppData();
            handler.sendEmptyMessage(202);
            ShareUtil.put(this,Contants.MODIFY,false);
        }
    }

    private void initView(){
        mainBinding.rlApps.setOnClickListener(this);
        mainBinding.rlGoogle.setOnClickListener(this);
        mainBinding.rlSettings.setOnClickListener(this);
        mainBinding.rlUsb.setOnClickListener(this);
        mainBinding.rlAv.setOnClickListener(this);
        mainBinding.rlHdmi1.setOnClickListener(this);
        mainBinding.rlHdmi2.setOnClickListener(this);
        mainBinding.rlVga.setOnClickListener(this);
        mainBinding.rlManual.setOnClickListener(this);
        mainBinding.rlWifi.setOnClickListener(this);
        mainBinding.rlBluetooth.setOnClickListener(this);
        mainBinding.rlEthernet.setOnClickListener(this);
        mainBinding.rlClear.setOnClickListener(this);
        mainBinding.rlWallpapers.setOnClickListener(this);

        mainBinding.rlApps.setOnHoverListener(this);
        mainBinding.rlGoogle.setOnHoverListener(this);
        mainBinding.rlSettings.setOnHoverListener(this);
        mainBinding.rlUsb.setOnHoverListener(this);
        mainBinding.rlAv.setOnHoverListener(this);
        mainBinding.rlHdmi1.setOnHoverListener(this);
        mainBinding.rlHdmi2.setOnHoverListener(this);
        mainBinding.rlVga.setOnHoverListener(this);
        mainBinding.rlManual.setOnHoverListener(this);
        mainBinding.rlWifi.setOnHoverListener(this);
        mainBinding.rlBluetooth.setOnHoverListener(this);
        mainBinding.rlEthernet.setOnHoverListener(this);
        mainBinding.rlWallpapers.setOnHoverListener(this);
        mainBinding.rlClear.setOnHoverListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mainBinding.shortcutsRv.addItemDecoration(new SpacesItemDecoration(0,
                (int) (getWindowManager().getDefaultDisplay().getWidth()*0.03),0,0));
        mainBinding.shortcutsRv.setLayoutManager(layoutManager);
    }

    private void initData(){

        mainBinding.rlEthernet.setVisibility(isNetworkConnect()?View.VISIBLE:View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getBrandImage();
                initDataApp();
                short_list =loadHomeAppData();
                handler.sendEmptyMessage(202);
            }
        }).start();
        initSourceData();
    }


    private void getBrandImage() {
        if (MyApplication.config.brandLogo.equals(""))
            return;

        Bitmap bitmap = null;
        if (new File(MyApplication.config.brandLogo).exists()){
            bitmap = BitmapFactory.decodeFile(MyApplication.config.brandLogo);
        }

        if (bitmap == null ) {
            return;
        }
        Bitmap finalBitmap = bitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainBinding.brand.setImageBitmap(finalBitmap);
            }
        });

    }

    private void initSourceData(){
        SignalAdapter signalAdapter = new SignalAdapter(this,getSourceListTitle(),getSourceList());
        signalAdapter.setSignalItemCallBack(signalItemCallBack);
        GridLayoutManager gridLayoutManager;
        if (signalAdapter.getIdList().size()==2){
             gridLayoutManager = new GridLayoutManager(this,1);
        }else {
             gridLayoutManager = new GridLayoutManager(this,2);
        }
        mainBinding.signalRv.setLayoutManager(gridLayoutManager);
        mainBinding.signalRv.setAdapter(signalAdapter);
    }

    public static String[] getSourceList(){
        return MyApplication.config.sourceList.split(",");
    }

    public static String[] getSourceListTitle(){
        return MyApplication.config.sourceListTitle.split(",");
    }

    SignalItemCallBack signalItemCallBack = new SignalItemCallBack() {
        @Override
        public void onItemClick(String id) {
            if (id.contains("USB")){
                AppUtils.startNewApp(MainActivity.this,
                        SystemProperties.get("persist.sys.filemanager_package","com.softwinner.TvdFileManager"));
                return;
            }else if (id.contains("SCREEN")){

                return;
            }else if (id.contains("MANUAL")){
                ManualQrDialog manualQrDialog = new ManualQrDialog(MainActivity.this,R.style.DialogTheme);
                manualQrDialog.show();
                return;
            }

            startSource(id);
        }

        @Override
        public void onItemFocus(View view, boolean hasFocus) {
            //focusChangeListener.onFocusChange(view,hasFocus);
        }
    };

    private void initReceiver(){
        IntentFilter networkFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        networkReceiver.setNetWorkCallBack(this);
        registerReceiver(networkReceiver, networkFilter);

        // 时间变化 分为单位
        timeReceiver = new MyTimeReceiver(this);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        timeFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        timeFilter.addAction(Intent.ACTION_USER_SWITCHED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);

        // wifi
        wifiFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        wifiReceiver = new MyWifiReceiver(this);
        registerReceiver(wifiReceiver, wifiFilter);

        // 蓝牙
        // blueFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        // blueFilter
        // .addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        // blueFilter.addAction("android.bluetooth.device.action.FOUND");
        // blueFilter
        // .addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        blueFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        blueFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        blueReceiver = new BluetoothReceiver(this);
        registerReceiver(blueReceiver, blueFilter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("com.htc.refreshApps");
        registerReceiver(networkConnectReceiver,intentFilter);

        //app
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        appReceiver=new AppReceiver(appCallBack);
        registerReceiver(appReceiver, appFilter);
    }

    AppCallBack appCallBack = new AppCallBack() {
        @Override
        public void appChange(String packageName) {
            short_list =loadHomeAppData();
            handler.sendEmptyMessage(202);
        }

        @Override
        public void appUnInstall(String packageName) {
            short_list =loadHomeAppData();
            handler.sendEmptyMessage(202);
        }

        @Override
        public void appInstall(String packageName) {
            short_list =loadHomeAppData();
            handler.sendEmptyMessage(202);
        }
    };

    BroadcastReceiver networkConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (null != info && ConnectivityManager.TYPE_ETHERNET == info.getType()) {
                    if (NetworkInfo.State.CONNECTED == info.getState()) {
                        mainBinding.rlEthernet.setVisibility(View.VISIBLE);
                    } else if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                        mainBinding.rlEthernet.setVisibility(View.GONE);
                    }

                }
            } else if ("com.htc.refreshApps".equals(intent.getAction())) {
                short_list =loadHomeAppData();
                handler.sendEmptyMessage(202);
            }
        }
    };
    
    ShortcutsAdapter.ItemCallBack itemCallBack = new ShortcutsAdapter.ItemCallBack() {
        @Override
        public void onItemClick(int i,String name) {
            if (i < short_list.size()) {
                if ("Netflix".equals(name)) {
                    if (!startNetflix()){
                        appName = name;
                        requestChannelData();
                    }

                }else if ("Youtube".equals(name)) {
                    if (!startYoutube()){
                        appName = name;
                        requestChannelData();
                    }
                }else if ("Fili TV".equals(name)) {
                    if (!AppUtils.startNewApp(MainActivity.this,"com.mm.droid.livetv.fili")){
                        appName = name;
                        requestChannelData();
                    }
                } else if (!AppUtils.startNewApp(MainActivity.this, short_list.get(i).getPackageName())) {
                    appName = name;
                    requestChannelData();
                }
            } else {
                AppUtils.startNewActivity(MainActivity.this, AppFavoritesActivity.class);
            }
        }
    };


    private boolean startYoutube(){
        try {
            if (AppUtils.startNewApp(this,"com.google.android.youtube.tv"))
                return true;
            if (AppUtils.startNewApp(this,"com.google.android.youtube"))
                return true;

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private boolean startNetflix(){
        try {
            if (AppUtils.startNewApp(this,"com.netflix.ninja"))
                return true;
            if (AppUtils.startNewApp(this,"com.netflix.mediaclient"))
                return true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void startAppFormChannel(){
        for (AppsData appsData:channelData.getData()){
            if (appName.equals(appsData.getName())){
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.htc.storeos","com.htc.storeos.AppDetailActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("appData",new Gson().toJson(appsData));
                startActivity(intent);
                return;
            }
        }
        ToastUtil.showShortToast(this,getString(R.string.data_none));
    }



    private void requestChannelData(){
        if (requestFlag)
            return;

        if (!NetWorkUtils.isNetworkConnected(this)){
            ToastUtil.showShortToast(this,getString(R.string.network_disconnect_tip));
            return;
        }
        requestFlag = true;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        String time = String.valueOf(System.currentTimeMillis());
        String chan = Constants.getChannel();
        LogUtils.d("chanId "+chan);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("chanId",chan);
        requestBuilder.addHeader("timestamp",time);
        HashMap<String,Object> requestData = new HashMap<>();
        requestData.put("chanId",chan);
        String deviceId = Constants.getWan0Mac();
        if (Constants.isOne(Uri.complexType,1)) {
            String aesKey = VerifyUtil.initKey();
            LogUtils.d("aesKey "+ aesKey);
            deviceId = VerifyUtil.encrypt(deviceId,aesKey,aesKey,VerifyUtil.AES_CBC);
            LogUtils.d("deviceId "+deviceId);
        }
        requestData.put("deviceId",deviceId);
        requestData.put("model", SystemProperties.get("persist.sys.modelName","project"));
        requestData.put("sysVersion", Constants.getHtcDisplay());
        try {
            requestData.put("verCode",getPackageManager().getPackageInfo(getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            requestData.put("verCode",10);
            throw new RuntimeException(e);
        }

        requestData.put("complexType",Uri.complexType);//
        Gson gson = new Gson();
        String json = gson.toJson(requestData);
        requestBuilder.url(Uri.SIGN_APP_LIST_URL)
                .post(RequestBody.create(json, MediaType.parse("application/json;charset=UTF-8")));
        String sign = RequestManager.getInstance().getSign(json,chan,time);
        LogUtils.d("sign "+ sign);
        requestBuilder.addHeader("sign",sign);
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(channelCallback);
    }

    Callback channelCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
            LogUtils.d("onFailure()");
            handler.sendEmptyMessage(DATA_ERROR);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            try {
                String content = response.body().string();
                LogUtils.d("content "+ content);
                if (RequestManager.isOne(Uri.complexType,3)) {
                    byte[] bytes = Base64.decode(content, Base64.NO_WRAP);
                    content = new String(VerifyUtil.gzipDecompress(bytes), StandardCharsets.UTF_8);
                    LogUtils.d("content " + content);
                }
                channelData = new Gson().fromJson(content, ChannelData.class);
                if (channelData.getCode()!=0){
                    handler.sendEmptyMessage(DATA_ERROR);
                }else {
                    handler.sendEmptyMessage(DATA_FINISH);
                }

            }catch (Exception e){
                handler.sendEmptyMessage(DATA_ERROR);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.rl_wallpapers:
                startNewActivity(WallPaperActivity.class);
                break;
            case R.id.rl_Google:
                AppUtils.startNewApp(MainActivity.this, "com.htc.storeos");
                break;
            case R.id.rl_apps:
                startNewActivity(AppsActivity.class);
                break;
            case R.id.rl_settings:

                if (MyApplication.config.setting.equals("")){
                    startNewActivity(MainSettingActivity.class);
                }else {
                    String[] act = MyApplication.config.setting.split("/");
                    AppUtils.startNewApp(this,act[0],act[1].replace(".",act[0]));
                }

                break;
            case R.id.rl_usb:
                AppUtils.startNewApp(MainActivity.this, "com.softwinner.TvdFileManager");
                break;
            case R.id.rl_av:
                startSource("CVBS1");
                break;
            case R.id.rl_hdmi1:
                startSource("HDMI1");
                break;
            case R.id.rl_hdmi2:
                startSource("HDMI2");
                break;
            case R.id.rl_vga:
                startSource("VGA");
                break;
            case R.id.rl_manual:
                ManualQrDialog manualQrDialog = new ManualQrDialog(this,R.style.DialogTheme);
                manualQrDialog.show();
                break;
            case R.id.rl_wifi:
                if (MyApplication.config.barWifi.equals("")){
                    startNewActivity(WifiActivity.class);
                }else {
                    String[] act = MyApplication.config.barWifi.split("/");
                    AppUtils.startNewApp(this,act[0],act[1].replace(".",act[0]));
                }
                break;
            case R.id.rl_bluetooth:
                if (MyApplication.config.barBluetooth.equals("")){
                    startNewActivity(BluetoothActivity.class);
                }else {
                    String[] act = MyApplication.config.barBluetooth.split("/");
                    AppUtils.startNewApp(this,act[0],act[1].replace(".",act[0]));
                }
                break;
            case R.id.rl_ethernet:
                startNewActivity(WiredActivity.class);
                break;
            case R.id.rl_clear:
                AppUtils.startNewApp(this,"com.htc.clearmemory");
                break;
        }

    }

    private void startSource(String sourceName){
        Intent intent_hdmi = new Intent();
        intent_hdmi.setComponent(new ComponentName("com.softwinner.awlivetv","com.softwinner.awlivetv.MainActivity"));
        intent_hdmi.putExtra("input_source",sourceName);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent_hdmi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_hdmi);
    }

    /**
     * 第一次初始化默认快捷栏app数据
     */
    private boolean initDataApp() {
        boolean isLoad = true;
        SharedPreferences sharedPreferences = ShareUtil.getInstans(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int code = sharedPreferences.getInt("code", 0);
        if (code == 0) {
            // 读取文件
            File file = new File("/oem/shortcuts.config");
            if (!file.exists())
             file = new File("/system/shortcuts.config");
            if (!file.exists()) {
                return false;
            }
            try {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                String result = new String(b);
                List<String> residentList = new ArrayList<>();
                JSONObject obj = new JSONObject(result);
                JSONArray jsonarrray = obj.getJSONArray("apps");
                for (int i = 0; i < jsonarrray.length(); i++) {
                    JSONObject jsonobject = jsonarrray.getJSONObject(i);
                    String packageName = jsonobject.getString("packageName");
                    boolean resident = jsonobject.getBoolean("resident");
                    if (resident){
                        residentList.add(packageName);
                    }
                    if (!DBUtils.getInstance(this).isExistData(
                            packageName)) {
                        long addCode = DBUtils.getInstance(this)
                                .addFavorites(packageName);
                    }
                }
                editor.putString("resident",residentList.toString());
                editor.putInt("residentSize",residentList.size());
                editor.putInt("code", 1);
                editor.apply();
                is.close();
            } catch (IOException | JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isLoad = false;
            }
        }
        return isLoad;
    }


    private ArrayList<ShortInfoBean> loadHomeAppData() {
        ArrayList<AppSimpleBean> appSimpleBeans = DBUtils.getInstance(this).getFavorites();
        ArrayList<ShortInfoBean> shortInfoBeans = new ArrayList<>();
        ArrayList<AppInfoBean> appList = AppUtils.getApplicationMsg(this);
        String country_code = Settings.System.getString(getContentResolver(),"ip_country_code");
        LogUtils.d("ip_country_code "+country_code);
        if (country_code!=null){
            String[] continent_countryCode = country_code.split(",");
            if (continent_countryCode.length>=2 && MyApplication.config.specialApps !=null && MyApplication.config.specialApps.size()>0) {
                for (SpecialApps specialApps : MyApplication.config.specialApps){
                    if (specialApps.getContinent()!=null && !specialApps.getContinent().equals("")){
                        if (specialApps.getContinent().contains("!")){
                            if (specialApps.getContinent().replace("!","").equals(continent_countryCode[0]))
                                continue;
                        }else {
                            if (!specialApps.getContinent().equals(continent_countryCode[0]))
                                continue;
                        }
                    }
                    if (specialApps.getCountryCode()!=null && !specialApps.getCountryCode().equals("")){
                        if (specialApps.getCountryCode().contains("!")){
                            if (specialApps.getCountryCode().replace("!","").equals(continent_countryCode[1]))
                                continue;
                        }else {
                            if (!specialApps.getCountryCode().equals(continent_countryCode[1]))
                                continue;
                        }
                    }
                    LogUtils.d("specialApps.getContinent() "+specialApps.getContinent()+" specialApps.getCountryCode() "+specialApps.getCountryCode());
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename(specialApps.getPackageName());
                    simpleBean.setPath(specialApps.getIconPath());
                    simpleBean.setAppName(specialApps.getAppName());
                    appSimpleBeans.add(0,simpleBean);

                }
                /*if (continent_countryCode[1].equals("BR")) {
                    //巴西IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("GTV");
                    appSimpleBeans.add(0,simpleBean);
                } else if (continent_countryCode[1].equals("PH")) {
                    //菲律宾IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("com.mm.droid.livetv.fili");
                    appSimpleBeans.add(0,simpleBean);

                } else if (continent_countryCode[0].equals("南美洲")) {
                    //南美非巴西IP
                    AppSimpleBean simpleBean = new AppSimpleBean();
                    simpleBean.setPackagename("com.mm.droid.livetv.tvees");
                    appSimpleBeans.add(0,simpleBean);
                }*/
            }
        }

        for (int i = 0; i < appSimpleBeans.size(); i++) {
            ShortInfoBean shortInfoBean = new ShortInfoBean();
            shortInfoBean.setPackageName(appSimpleBeans.get(i).getPackagename());
            shortInfoBean.setPath(appSimpleBeans.get(i).getPath());
            shortInfoBean.setAppname(appSimpleBeans.get(i).getAppName());
            for (int j = 0; j < appList.size(); j++) {
                if (appSimpleBeans.get(i).getPackagename()
                        .equals(appList.get(j).getApppackagename())) {
                    shortInfoBean.setAppicon(appList.get(j).getAppicon());
                    shortInfoBean.setAppname(appList.get(j).getAppname());
                    break;
                }
            }
            shortInfoBeans.add(shortInfoBean);
        }

        return shortInfoBeans;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK)
            return true;
        return super.dispatchKeyEvent(event);
    }

    private void updateBle(){
        boolean isConnected = BluetoothUtils.getInstance(this)
                .isBluetoothConnected();
        if (isConnected){
            mainBinding.homeBluetooth.setBackgroundResource(R.drawable.bluetooth_con);
        }else {
            mainBinding.homeBluetooth.setBackgroundResource(R.drawable.bluetooth_not);
        }
    }

    private boolean isNetworkConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo!=null&& networkInfo.isConnected();
    }
    private void updateTime(){
        String builder = TimeUtils.getCurrentDate() +
                " | " +
                TimeUtils
                        .getCurrentTime(this);
        mainBinding.timeTv.setText(builder);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(networkReceiver);
        unregisterReceiver(timeReceiver);
        unregisterReceiver(blueReceiver);
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }

    @Override
    public void bluetoothChange() {
        updateBle();
    }

    @Override
    public void changeTime() {
        updateTime();
    }

    @Override
    public void getWifiState(int state) {
        if (state == 1) {
            mainBinding.homeWifi.setBackgroundResource(R.drawable.wifi_not);
        }
    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AnimationSet animationSet = new AnimationSet(true);
            v.bringToFront();
            if (hasFocus) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.050f,
                        1.0f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(150);
                animationSet.addAnimation(scaleAnimation);
                animationSet.setFillAfter(true);
                v.startAnimation(animationSet);
            } else {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.05f, 1.0f,
                        1.05f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animationSet.addAnimation(scaleAnimation);
                scaleAnimation.setDuration(150);
                animationSet.setFillAfter(true);
                v.startAnimation(animationSet);
            }
        }
    };

    @Override
    public void getWifiNumber(int count) {
        switch (count) {
            case -1:
                mainBinding.homeWifi.setBackgroundResource(R.drawable.wifi_not);
                break;
            case 0:
                mainBinding.homeWifi.setBackgroundResource(R.drawable.bar_wifi_1_focus);
                break;
            case 1:
                mainBinding.homeWifi.setBackgroundResource(R.drawable.bar_wifi_2_focus);
                break;
            default:
                mainBinding.homeWifi.setBackgroundResource(R.drawable.bar_wifi_full_focus);
                break;

        }
    }

    @Override
    public void connect() {

    }

    @Override
    public void disConnect() {

    }

    public interface SignalItemCallBack{
        void onItemClick(String id);
        void onItemFocus(View view,boolean hasFocus);
    }


}