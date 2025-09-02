package com.htc.spectraos.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.InitAngleLayoutBinding;
import com.htc.spectraos.utils.KeystoneUtils_726;
import com.htc.spectraos.utils.LogUtils;
import com.htc.spectraos.utils.ReflectUtil;
import com.htc.spectraos.utils.ToastUtil;

public class InitAngleActivity extends BaseActivity {

    private InitAngleLayoutBinding initAngleLayoutBinding;
    private ProgressDialog dialog = null;
    private static String TAG = "InitAngleActivity";
    Handler handler = new Handler();
    //是不是主动关闭了自动梯形矫正
    private boolean activeClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAngleLayoutBinding = InitAngleLayoutBinding.inflate(LayoutInflater.from(this));
        setContentView(initAngleLayoutBinding.getRoot());
        initData();
        initAngleLayoutBinding.startInitAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCorrectAngle();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },3000);
            }
        });
        initAngleLayoutBinding.startInitAngle.requestFocus();
    }

    private void initData() {   //再做初始角度矫正之前，先关闭“自动梯形矫正”开关，重置矫正画面。
//        if (getAuto()) { //打开了先关闭
//            activeClose = true;
//            setAuto();
//        }

        KeystoneUtils_726.resetKeystone();
//        KeystoneUtils_726.writeGlobalSettings(getApplicationContext(), KeystoneUtils_726.ZOOM_VALUE, 0);
        KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_VALUE,0);
        SystemProperties.set("persist.sys.keystone_offset", "0");
        SystemProperties.set("persist.sys.keystonefinalAngle", "0");

        if (getAuto()) { //打开了先关闭
            activeClose = true;
            setAuto();
        }

//        KeystoneUtils_726.writeSystemProperties(KeystoneUtils_726.PROP_ZOOM_SCALE,0);
    }

    private void initCorrectAngle() {
        ReflectUtil.invokeSet_angle_offset();
        dialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(getResources().getString(R.string.defaultcorrectionin));
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                LogUtils.d("get_angle_offset " + ReflectUtil.invokeGet_angle_offset());
            }
        }, 3000);
    }

    @Override
    protected void onStop() {
        Log.d(TAG," onStop");
        super.onStop();
        if (activeClose) { //主动关闭了，需要恢复打开状态
            activeClose = false;
            setAuto();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setAuto() {
        //int auto = PrjScreen.get_prj_auto_keystone_enable();
        //PrjScreen.set_prj_auto_keystone_enable(auto == 0 ? 1 : 0);
        boolean auto = getAuto();
        SystemProperties.set("persist.sys.tpryauto", String.valueOf(auto ? 0 : 1));
        //自动梯形打开的时候发送一次更新
        if (!auto) {
//            sendKeystoneBroadcast();
            sendKeystoneBroadcastByAuto();
        } else {
            updateZoomValue();
        }
    }

    private void sendKeystoneBroadcastByAuto() {
        Intent intent = new Intent("android.intent.hotack_keystone");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("ratio", 1);
        intent.putExtra("keystone",1);
        sendBroadcast(intent);
    }

    private void updateZoomValue() {
        String value = SystemProperties.get("persist.sys.zoom.value", "0,0,0,0,0,0,0,0");
        String[] va = value.split(",");
        if (va.length == 8) {
            KeystoneUtils_726.lb_X = Integer.parseInt(va[0]);
            KeystoneUtils_726.lb_Y = Integer.parseInt(va[1]);
            KeystoneUtils_726.lt_X = Integer.parseInt(va[2]);
            KeystoneUtils_726.lt_Y = Integer.parseInt(va[3]);
            KeystoneUtils_726.rt_X = Integer.parseInt(va[4]);
            KeystoneUtils_726.rt_Y = Integer.parseInt(va[5]);
            KeystoneUtils_726.rb_X = Integer.parseInt(va[6]);
            KeystoneUtils_726.rb_Y = Integer.parseInt(va[7]);
            KeystoneUtils_726.UpdateKeystoneZOOM(true);
        }
    }

    public boolean getAuto() {
        return SystemProperties.getBoolean("persist.sys.tpryauto", false);
    }

}