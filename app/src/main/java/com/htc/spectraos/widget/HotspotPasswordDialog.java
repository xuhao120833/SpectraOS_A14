package com.htc.spectraos.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.spectraos.R;
import com.htc.spectraos.databinding.HotspotPasswordDialogBinding;
import com.htc.spectraos.utils.InputMethodUtil;
import com.htc.spectraos.utils.WifiHotUtil;

/**
 * Author:
 * Date:
 * Description:
 */
public class HotspotPasswordDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private HotspotPasswordDialogBinding hotspotPasswordDialogBinding;
    private HotspotPasswordCallBack mcallback;
    private WifiHotUtil wifiHotUtil;
    private WifiConfiguration wifiConfiguration;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enter:
                if (hotspotPasswordDialogBinding.etPassword.getText().toString().isEmpty()){
                    hotspotPasswordDialogBinding.errMsg.setVisibility(View.VISIBLE);
                    hotspotPasswordDialogBinding.errMsg.setText(mContext.getString(R.string.empty_string));
                    break;
                }else if (hotspotPasswordDialogBinding.etPassword.getText().toString().length()<8){
                    hotspotPasswordDialogBinding.errMsg.setVisibility(View.VISIBLE);
                    hotspotPasswordDialogBinding.errMsg.setText(mContext.getString(R.string.passwordmsglength));
                    break;
                }
                if (mcallback!=null)
                    mcallback.onClick(hotspotPasswordDialogBinding.etPassword.getText().toString());
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public interface HotspotPasswordCallBack {
        public void onClick(String password);
    }

    public HotspotPasswordDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public HotspotPasswordDialog(Context context, boolean cancelable,
                             OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public HotspotPasswordDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public void HotspotConfig(WifiHotUtil wifiHotUtil){
        this.wifiHotUtil = wifiHotUtil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        wifiConfiguration = wifiHotUtil.getWifiConfig();
        hotspotPasswordDialogBinding.hotspotName.setText(wifiConfiguration.SSID);
        super.onStart();
    }

    private void init() {
        hotspotPasswordDialogBinding = HotspotPasswordDialogBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (hotspotPasswordDialogBinding.getRoot() != null) {
            setContentView(hotspotPasswordDialogBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.right_in_right_out_anim);
                //去除系统自带的margin
                dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //设置dialog在界面中的属性
                dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //背景全透明
                dialogWindow.setDimAmount(0f);
            }
            WindowManager manager = ((Activity) mContext).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
            params.height = (int) (d.getHeight() * 0.4);
            //params.x = parent.getWidth();
            dialogWindow.setGravity(Gravity.CENTER);// 设置对话框位置
            dialogWindow.setAttributes(params);
        }
    }

    private void initView(){
        hotspotPasswordDialogBinding.enter.setOnClickListener(this);
        hotspotPasswordDialogBinding.cancel.setOnClickListener(this);

        hotspotPasswordDialogBinding.enter.setOnHoverListener(this);
        hotspotPasswordDialogBinding.cancel.setOnHoverListener(this);
        InputMethodUtil.openInputMethod(mContext,hotspotPasswordDialogBinding.etPassword,new Handler());
    }

    public void setOnClickCallBack(HotspotPasswordCallBack callback) {
        this.mcallback = callback;
    }
}
