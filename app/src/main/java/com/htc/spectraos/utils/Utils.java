package com.htc.spectraos.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.htc.spectraos.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static boolean hasfocus = false;

    public static boolean hasUsbDevice = false;

    //首页默认背景resId,无配置默认-1
    public static int mainBgResId = -1;

    public static int usbDevicesNumber = 0;

    //默认背景使用的ArrayList
    public static ArrayList<Object> drawables = new ArrayList<>();

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    //一个全局的特定IP APP信息

    public static int[] drawablesId = {
            R.drawable.background0,
            R.drawable.background_main,
            R.drawable.background1,
            R.drawable.background2,
            R.drawable.background3,
            R.drawable.background4,
            R.drawable.background5,
            R.drawable.background6,
            R.drawable.background7,
            R.drawable.background8,
            R.drawable.background9,
    };

    //实际启动信源用到的名称 HDMI1,HDMI2,CVBS1
    public static String[] sourceList = null;

    //用来显示的名称 HDMI,HDMI2,AV
    public static String[] sourceListTitle = null;

    //全局时区列表
    public static ArrayList<HashMap> list = null;

    /**
     * 打印 Intent 的 Extras 信息
     *
     * @param intent 需要打印的 Intent
     * @param tag    用于日志的 TAG
     */
    public static void logIntentExtras(Intent intent, String tag) {
        if (intent == null) {
            Log.d(tag, "logIntentExtras Intent is null");
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(tag, "logIntentExtras Intent extras:");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(tag, "[" + key + "] = " + value);
            }
        } else {
            Log.d(tag, "logIntentExtras No extras in the Intent");
        }
    }

}
