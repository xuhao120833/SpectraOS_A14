package com.htc.spectraos.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Author:
 * Date:
 * Description:
 */
public class Constants {
    public static int PAGE_APPS_COUNT = 12;

    public static int subTextSize = 18;
    public static int subTextBgRadius = 4;
    public static String MODIFY = "modify";
    // 时间广播
    public static String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    public static String FILE_NAME = "data";


    public static String getChannel() {
        String ch = SystemProperties.get("persist.sys.storechannel", "");
        if (ch.equals("")) {
            ch = SystemProperties.get("persist.sys.Channel", "project");
        }
        return ch;
    }

    /**
     * 获取以太网MAC
     * @return
     */
    public static String getWan0Mac() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(
                    "/sys/class/addr_mgt/addr_wifi"));
            return reader.readLine();
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  static boolean isOne(int num , int n){
        return (num >> (n - 1) & 1) == 1;
    }

    public static String getHtcDisplay() {
        String result = Build.DISPLAY;
        result = result.trim();
        String preDisplay = SystemProperties.get("persist.display.prefix","");
        if (!"".equals(preDisplay)){
            int beginIndex = result.indexOf(".");
            result =beginIndex==-1?preDisplay+"."+result:preDisplay+result.substring(beginIndex);
        }

        return result;
    }


    //0=未标定 1= 已标定  2=已标定，数据异常 3=已标定，数据正常
    public static int CheckCalibrated(String data){
        if (data ==null || data.length()<=152)
            return 0;

        char[] dataChar = data.toCharArray();
        int length = Math.min(152,dataChar.length);
        for(int i=0;i<length;i++){
            if (( dataChar[i]<='9' && dataChar[i]>='0' ) || ( dataChar[i]>='a' && dataChar[i]<='f' ) || ( dataChar[i]>='A' && dataChar[i]<='F' )){
                continue;
            }
            return 0;
        }
        return CheckCRC(dataChar);
    }

    public static int CheckCRC(char[] dataChar){
        if (dataChar.length<154)
            return 1;

        int crc = 0;
        for (int i=0;i<152;i++){
            crc+=dataChar[i];
        }
        crc = (0x100-(crc&0xFF))&0xFF;
        String crc_hex = Integer.toHexString(crc);
        if (crc_hex.length()==1){
            crc_hex = "0"+crc_hex;
        }
        Log.d("hzj","CRC "+crc_hex);
        boolean status = crc_hex.equals(String.valueOf(dataChar[152])+ dataChar[153]);
        Log.d("hzj","CRC check="+status);
        return status?3:2;
    }

    public static int checkRYXBinExit(){
        if (new File("/oem/ryx_gsensorinit.bin").exists()
                && new File("/oem/ryx_jbcampoints.bin").exists()
                && new File("/oem/ryx_rrcampoints.bin").exists()
                && new File("/oem/ryx_tpcampoints.bin").exists()
        )
            return 1;
        return 0;
    }

    public static void playSoundEffect(Context context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_DOWN);
    }
}
