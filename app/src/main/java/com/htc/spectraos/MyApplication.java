package com.htc.spectraos;

import static com.htc.spectraos.utils.BlurImageView.MAX_BITMAP_SIZE;
import static com.htc.spectraos.utils.BlurImageView.narrowBitmap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.htc.spectraos.entry.Config;
import com.htc.spectraos.utils.Contants;
import com.htc.spectraos.utils.FileUtils;
import com.htc.spectraos.utils.KeystoneUtils;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.utils.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Author:
 * Date:
 * Description:
 */
public class MyApplication extends Application {

    public static Config config = new Config();
    public static BitmapDrawable mainDrawable = null;

    private static String TAG = "MyApplication";

    private MutableLiveData<Boolean> isDataInitialized = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getIsDataInitialized() {
        return isDataInitialized; // 只暴露不可变的 LiveData
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "执行MyApplication onCreate");
        super.onCreate();
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Contants.TimeOffStatus, false);
        editor.putInt(Contants.TimeOffIndex, 0);
        editor.apply();
        try {
            if (new File(Contants.WALLPAPER_MAIN).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN);
                if(bitmap != null) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    //判断图片大小，如果超过限制就做缩小处理
                    if (width * height * 6 >= MAX_BITMAP_SIZE) {
                        bitmap = narrowBitmap(bitmap);
                    }
                    mainDrawable = new BitmapDrawable(bitmap);
//            mainDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
                }
            }
            //json解析1
            parseConfigFile();
            initDisplaySize();
            initWallpaperData();
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    private void parseConfigFile() {
        String configContent;
        if (new File("/oem/shortcuts.config").exists()) {
            configContent = FileUtils.readFileContent("/oem/shortcuts.config");
        } else {
            configContent = FileUtils.readFileContent("/system/shortcuts.config");
        }
        if (configContent == null || configContent.equals(""))
            return;
        Gson gson = new Gson();
        config = gson.fromJson(configContent, Config.class);
    }

    private void initDisplaySize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d(TAG, "screenWidth " + screenWidth + " screenHeight " + screenHeight);
        KeystoneUtils.lcd_h = screenHeight;
        KeystoneUtils.lcd_w = screenWidth;
        KeystoneUtils.minH_size = config.manualKeystoneWidth;
        KeystoneUtils.minV_size = config.manualKeystoneHeight;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initWallpaperData() {
//        new Thread(() -> {
        if (!config.custombackground.isEmpty() && copyCustomBg()) {
//            copyCustomBg();
            Utils.customBackground = true;
            copyMyWallpaper();
            Utils.drawables.add(getResources().getDrawable(R.drawable.wallpaper_add));
//            isDataInitialized.postValue(true);//UI线程用setValue
        } else {
            Utils.drawables.add(getResources().getDrawable(R.drawable.background0));
            Utils.drawables.add(R.drawable.background_main);
            Utils.drawables.add(R.drawable.background1);
            Utils.drawables.add(R.drawable.background2);
            Utils.drawables.add(R.drawable.background3);
            Utils.drawables.add(R.drawable.background4);
            Utils.drawables.add(R.drawable.background5);
            Utils.drawables.add(R.drawable.background6);
            Utils.drawables.add(R.drawable.background7);
            Utils.drawables.add(R.drawable.background8);
            Utils.drawables.add(R.drawable.background9);
            copyMyWallpaper();
            Utils.drawables.add(getResources().getDrawable(R.drawable.wallpaper_add));
            // 数据加载完成后更新 LiveData
            Log.d(TAG, "执行完initWallpaperData");
            isDataInitialized.postValue(true);//UI线程用setValue
        }
//        }
//        ).start();
    }

    private void copyMyWallpaper() {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};
        File directory = new File("/sdcard/.mywallpaper");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        for (String extension : imageExtensions) {
                            if (file.getName().toLowerCase().endsWith(extension)) {
                                Utils.drawables.add(file.getAbsolutePath());
                                break; // 找到一个匹配后就跳出循环
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean copyCustomBg() {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};
        File directory = new File(config.custombackground);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {//排序
                // 按数字排序
                Arrays.sort(files, (f1, f2) -> {
                    // 提取文件名中的数字
                    int num1 = extractNumber(f1.getName());
                    int num2 = extractNumber(f2.getName());
                    return Integer.compare(num1, num2); // 按数值升序排序
                });
            }
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        for (String extension : imageExtensions) {
                            if (file.getName().toLowerCase().endsWith(extension)) {
                                Utils.drawables.add(file.getAbsolutePath());
                                break; // 找到一个匹配后就跳出循环
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    // 从文件名中提取数字的方法
    private static int extractNumber(String fileName) {
        // 去掉文件后缀
        String name = fileName.replaceAll("\\.[a-zA-Z]+$", "");
        try {
            // 尝试将文件名解析为数字
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // 如果无法解析数字，将其放在排序末尾
        }
    }

}
