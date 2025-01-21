package com.htc.spectraos;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.htc.spectraos.entry.Config;
import com.htc.spectraos.utils.Contants;
import com.htc.spectraos.utils.FileUtils;
import com.htc.spectraos.utils.KeystoneUtils;
import com.htc.spectraos.utils.ShareUtil;

import java.io.File;

/**
 * Author:
 * Date:
 * Description:
 */
public class MyApplication extends Application {

    public static Config config = new Config();
    public static BitmapDrawable mainDrawable = null;
    public static BitmapDrawable otherDrawable = null;


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Contants.TimeOffStatus,false);
        editor.putInt(Contants.TimeOffIndex,0);
        editor.apply();
        if (new File(Contants.WALLPAPER_MAIN).exists())
            mainDrawable =new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
        if (new File(Contants.WALLPAPER_OTHER).exists())
            otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));

        parseConfigFile();
        initDisplaySize();

        // 打开调试开关，可以查看logcat日志。版本发布前，为避免影响性能，移除此代码
        // 查看方法：adb logcat -s sdkstat
        //StatService.setDebugOn(true);

        // 开启自动埋点统计，为保证所有页面都能准确统计，建议在Application中调用。
        // 第三个参数：autoTrackWebview：
        // 如果设置为true，则自动track所有webview；如果设置为false，则不自动track webview，
        // 如需对webview进行统计，需要对特定webview调用trackWebView() 即可。
        // 重要：如果有对webview设置过webchromeclient，则需要调用trackWebView() 接口将WebChromeClient对象传入，
        // 否则开发者自定义的回调无法收到。
        StatService.autoTrace(this, true, false);
    }


    private void parseConfigFile(){
        String configContent;
        if (new File("/oem/shortcuts.config").exists()){
            configContent = FileUtils.readFileContent("/oem/shortcuts.config");
        }else {
            configContent = FileUtils.readFileContent("/system/shortcuts.config");
        }
        if (configContent==null || configContent.equals(""))
            return;

        Gson gson = new Gson();
        config = gson.fromJson(configContent,Config.class);

    }

    private void initDisplaySize(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d("hzj","screenWidth "+screenWidth+" screenHeight "+screenHeight);
        KeystoneUtils.lcd_h = screenHeight;
        KeystoneUtils.lcd_w = screenWidth;
        KeystoneUtils.minH_size = config.manualKeystoneWidth;
        KeystoneUtils.minV_size = config.manualKeystoneHeight;
    }

}
