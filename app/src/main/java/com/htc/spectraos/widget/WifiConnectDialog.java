package com.htc.spectraos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.htc.spectraos.R;
import com.htc.spectraos.databinding.WifiConnectDialogBinding;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Author:
 * Date:
 * Description:
 */
public class WifiConnectDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private View parent;
    private WifiConnectDialogBinding wifiConnectDialogBinding;
    private String wifi_name = "unknow";
    private OnWifiConnectCallBack mcallback;

    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    public int networkId = -1;
    //当前wifi的链接回调
    public boolean hasRemove = false;
    public boolean connectFlag = false;
    Dialog connectingDialog = null;
    Dialog passwordErrorDialog = null;

    Executor threads = Executors.newCachedThreadPool();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                if (hasRemove && connectFlag) {
                    wifiManager.removeNetwork(networkId);
                    networkId = -1;
                }
            }else if (msg.what==2){
                connectFlag = true;
                networkId = mcallback.onClick(wifiConnectDialogBinding.etPassword.getText().toString());
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        Log.d("hzj","onclick");
        switch (v.getId()){
            case R.id.enter:
                if (wifiConnectDialogBinding.etPassword.getText().toString().isEmpty() ||
                        wifiConnectDialogBinding.etPassword.getText().toString().length()< 8){
                    wifiConnectDialogBinding.errMsg.setVisibility(View.VISIBLE);
                    break;
                }

                //dismiss();
                connectingDialog = ConectingDialog(mContext,mContext.getString(R.string.connecting_ssid,wifi_name));
                connectingDialog.show();
                threads.execute(new Runnable() {
                    @Override
                    public void run() {
                        disEnableNetwork();
                    }
                });
                handler.sendEmptyMessageDelayed(2,1500);
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public interface OnWifiConnectCallBack {
        public int onClick(String password);
    }

    public WifiConnectDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectDialog(Context context, boolean cancelable,
                             DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public WifiConnectDialog(Context context, int theme, View parent) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.parent = parent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        init();
    }

    private void init() {
        wifiConnectDialogBinding = WifiConnectDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (wifiConnectDialogBinding.getRoot() != null) {
            setContentView(wifiConnectDialogBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = d.getWidth(); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = d.getHeight();
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyReceiver();
    }


    boolean disEnable = false;
    private void disEnableNetwork(){
        disEnable =true;
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config:configurationList){
            wifiManager.disableNetwork(config.networkId);
        }

    }

    private void EnableNetwork(){
        if (!disEnable)
            return;

        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config:configurationList){
            wifiManager.enableNetwork(config.networkId,true);
        }
    }


    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(receiver,intentFilter);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInitialStickyBroadcast())//把注册完就发送的粘性(初始状态)广播过滤掉。
                return;

            String action = intent.getAction();
            if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
                //请求连接的状态发生改变，（已经加入到一个接入点）
                int supl_error=intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if (supl_error == WifiManager.ERROR_AUTHENTICATING ) {
                    if (passwordErrorDialog==null) {
                        passwordErrorDialog = new PasswordErrorDialog(mContext, R.style.DialogTheme);
                    }
                    if (!passwordErrorDialog.isShowing())
                        passwordErrorDialog.show();
                }

            }else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
              NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

              if (networkInfo!=null){
                    switch (networkInfo.getState()){
                        case CONNECTING:
                            hasRemove = true;
                            break;
                        case CONNECTED:
                            if (hasRemove  && connectFlag){
                                //不需要清除保存信息，重置false
                                hasRemove = false;
                                connectFlag =false;
                                hideConnectingDialog();
                                dismiss();
                            }
                            break;
                        case DISCONNECTED:
                            if (hasRemove && connectFlag) {
                                hideConnectingDialog();
                                wifiManager.removeNetwork(networkId);
                                networkId = -1;
                                hasRemove = false;
                                connectFlag =false;
                            }

                            break;
                    }

                    Log.d("WIFI","state "+ networkInfo.getState());
              }

            }
        }
    };

    private void destroyReceiver(){
        mContext.unregisterReceiver(receiver);
    }


    public void  setConnectName(String name){
        this.wifi_name = name;
    }

    private void initView(){
        wifiConnectDialogBinding.enter.setOnClickListener(this);
        wifiConnectDialogBinding.cancel.setOnClickListener(this);
        wifiConnectDialogBinding.enter.setOnHoverListener(this);
        wifiConnectDialogBinding.cancel.setOnHoverListener(this);

        wifiConnectDialogBinding.enter.setEnabled(false);
        wifiConnectDialogBinding.connectWifiName.setText(mContext.getString(R.string.network_connect_tips,wifi_name));
        wifiConnectDialogBinding.checkboxShow
                .setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (!isChecked) {
                            // 如果选中，显示密码
                            wifiConnectDialogBinding.etPassword
                                    .setTransformationMethod(HideReturnsTransformationMethod
                                            .getInstance());
                        } else {
                            // 否则隐藏密码
                            wifiConnectDialogBinding.etPassword
                                    .setTransformationMethod(PasswordTransformationMethod
                                            .getInstance());

                        }

                    }
                });
        wifiConnectDialogBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifiConnectDialogBinding.enter.setEnabled(s.length() >= 8);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public  Dialog ConectingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.loadding_layout);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loadding_iv);
        TextView tipTextView = (TextView) v.findViewById(R.id.loadding_tv);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog connectingDialog = new Dialog(context, R.style.DialogTheme);// 创建自定义样式dialog
        connectingDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendEmptyMessage(1);
            }
        });
        connectingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT));// 设置布局
        return connectingDialog;

    }

    public void hideConnectingDialog(){
        if (connectingDialog!=null && connectingDialog.isShowing()){
            connectingDialog.dismiss();
        }
    }

    public void hidePasswordErrorDialog(){
        if (passwordErrorDialog!=null && passwordErrorDialog.isShowing()){
            passwordErrorDialog.dismiss();
        }
    }


    public void setOnClickCallBack(OnWifiConnectCallBack callback) {
        this.mcallback = callback;
    }

    @Override
    public void dismiss() {
        EnableNetwork();
        handler.removeCallbacksAndMessages(null);
        super.dismiss();
    }
}
