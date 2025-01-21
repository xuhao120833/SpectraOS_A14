package com.htc.spectraos.entry;

import java.util.ArrayList;

/**
 * Author:
 * Date:
 * Description:
 */
public class Config {

    public String filterApps = "";
    public String managerFilterApps = "";
    public String brandLogo = "";
    public boolean ipSetting = true;

    public ArrayList<SpecialApps> specialApps ;

    public String barWifi = "";
    public String barBluetooth = "";
    public String setting = "";


    public  String sourceList ="HDMI1";
    public  String sourceListTitle ="HDMI";

    public  boolean wifi=true;
    public  boolean hotpots=true;

    public  boolean bluetoothSpeaker=true;
    public  boolean language=true;
    public  boolean inputMethod=true;

    public  boolean bootSource=true;
    public  boolean soundEffects=true;
    public  boolean screenSaver=true;
    public  boolean timerOff=true;
    public  boolean resetFactory=true;
    public  boolean powerMode=true;
    public  boolean account=false;
    public  boolean developer=true;

    public  boolean autoCheckCamera=false;
    public  boolean autoKeystone=true;
    public  boolean autoFourCorner=true;
    public  boolean manualKeystone=true;
    public  boolean initAngle=true;
    public  boolean resetKeystone=true;
    public  int manualKeystoneWidth=1000;
    public  int manualKeystoneHeight=1000;
    public  boolean autoFocus=true;
    public  boolean screenRecognition=true;
    public  boolean intelligentObstacle=true;
    public  boolean calibration=true;
    public  boolean projectMode=true;
    public  boolean audioMode=false;
    public  boolean displaySetting=true;
    public  boolean deviceMode=false;
    public  boolean deviceModeTestHigh=false;
    public  boolean wholeZoom=true;


    public boolean deviceModel = true;
    public boolean uiVersion = true;
    public boolean androidVersion = true;
    public boolean resolution = true;
    public boolean memory = true;
    public int memoryScale = 1;
    public boolean storage = true;
    public int storageScale = 1;
    public boolean wlanMacAddress = true;
    public boolean updateFirmware = true;
    public boolean onlineUpdate = true;
    public boolean serialNumber = true;

    public boolean pictureMode = true;
    public  boolean displayAudioMode=false;
    public boolean pictureModeShowCustom = true;
    public int brightnessLevel = 1;

    public  boolean brightness=false;
    public  boolean brightnessSystem=true;
    public  boolean contrast=true;
    public  boolean hue=true;
    public  boolean saturation=true;
    public  boolean sharpness=true;

    public  boolean red=false;
    public  boolean green=false;
    public  boolean blue=false;

}
