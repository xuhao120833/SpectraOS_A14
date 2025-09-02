package com.htc.spectraos.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.ActivityMainSettingBinding;

public class MainSettingActivity extends BaseActivity {

    ActivityMainSettingBinding mainSettingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainSettingBinding = ActivityMainSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(mainSettingBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        mainSettingBinding.rlAbout.setOnClickListener(this);
        mainSettingBinding.rlAppsManager.setOnClickListener(this);
        mainSettingBinding.rlBluetooth.setOnClickListener(this);
        mainSettingBinding.rlDateTime.setOnClickListener(this);
        mainSettingBinding.rlLanguage.setOnClickListener(this);
        mainSettingBinding.rlOther.setOnClickListener(this);
        mainSettingBinding.rlProject.setOnClickListener(this);
        mainSettingBinding.rlWifi.setOnClickListener(this);

        mainSettingBinding.rlAbout.setOnHoverListener(this);
        mainSettingBinding.rlAppsManager.setOnHoverListener(this);
        mainSettingBinding.rlBluetooth.setOnHoverListener(this);
        mainSettingBinding.rlDateTime.setOnHoverListener(this);
        mainSettingBinding.rlLanguage.setOnHoverListener(this);
        mainSettingBinding.rlOther.setOnHoverListener(this);
        mainSettingBinding.rlProject.setOnHoverListener(this);
        mainSettingBinding.rlWifi.setOnHoverListener(this);

        mainSettingBinding.rlProject.requestFocus();
        mainSettingBinding.rlProject.requestFocusFromTouch();

        setGrayBorder();
    }

    private void setGrayBorder() {
        if(MyApplication.config.grayBorder) {
            mainSettingBinding.rlProject.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlWifi.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlBluetooth.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlAppsManager.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlLanguage.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlDateTime.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlOther.setBackgroundResource(R.drawable.main_settings_bg2);
            mainSettingBinding.rlAbout.setBackgroundResource(R.drawable.main_settings_bg2);
        }
    }

    private void initData(){

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_wifi) {
            startNewActivity(NetworkActivity.class);
        } else if (id == R.id.rl_bluetooth) {
            startNewActivityBlue(BluetoothActivity.class);
        } else if (id == R.id.rl_project) {
            startNewActivity(ProjectActivity.class);
        } else if (id == R.id.rl_apps_manager) {
            startNewActivity(AppsManagerActivity.class);
        } else if (id == R.id.rl_language) {
            startNewActivity(LanguageAndKeyboardActivity.class);
        } else if (id == R.id.rl_date_time) {
            startNewActivity(DateTimeActivity.class);
        } else if (id == R.id.rl_other) {
            startNewActivity(OtherSettingsActivity.class);
        } else if (id == R.id.rl_about) {
            startNewActivity(AboutActivity.class);
        }
    }
}