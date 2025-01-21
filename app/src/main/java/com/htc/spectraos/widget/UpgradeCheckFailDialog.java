package com.htc.spectraos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.UpgradeCheckFailBinding;


/**
 * Author:
 * Date:
 * Description:
 */
public class UpgradeCheckFailDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private UpgradeCheckFailBinding upgradeCheckFailBinding;
    private OnClickCallBack mcallback;

    @Override
    public void onClick(View v) {
        Log.d("hzj","onclick");
        switch (v.getId()){
            case R.id.enter:

                if (mcallback!=null)
                    mcallback.onRetry();
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public interface OnClickCallBack {
        public void onRetry();
    }

    public UpgradeCheckFailDialog(Context context) {
        super(context);
        
        this.mContext = context;
    }

    public UpgradeCheckFailDialog(Context context, boolean cancelable,
                                       DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        
        this.mContext = context;
    }

    public UpgradeCheckFailDialog(Context context, int theme) {
        super(context, theme);
        
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        upgradeCheckFailBinding = UpgradeCheckFailBinding.inflate(LayoutInflater.from(mContext));
        /*View view = LayoutInflater.from(mContext).inflate(
                R.layout.wifi_settings_layout, null);*/
        if (upgradeCheckFailBinding.getRoot() != null) {
            setContentView(upgradeCheckFailBinding.getRoot());
            initView();
            // 设置dialog大小 模块好的控件大小设置
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
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
        upgradeCheckFailBinding.enter.setOnClickListener(this);
        upgradeCheckFailBinding.cancel.setOnClickListener(this);

        upgradeCheckFailBinding.enter.setOnHoverListener(this);
        upgradeCheckFailBinding.cancel.setOnHoverListener(this);

        upgradeCheckFailBinding.enter.requestFocus();
        upgradeCheckFailBinding.enter.requestFocusFromTouch();
    }

    public void setOnClickCallBack(OnClickCallBack callback) {
        this.mcallback = callback;
    }
}
