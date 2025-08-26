package com.htc.spectraos.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.ActivityOtherSettingsBinding;
import com.htc.spectraos.service.TimeOffService;
import com.htc.spectraos.utils.Contants;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.utils.Utils;
import com.htc.spectraos.widget.FactoryResetDialog;
import com.htc.spectraos.widget.SetPasswordDialog;
import com.softwinner.TvAudioControl;
import com.softwinner.tv.AwTvSystemManager;
import com.softwinner.tv.common.AwTvSystemTypes;

public class OtherSettingsActivity extends BaseActivity implements View.OnKeyListener {

    private ActivityOtherSettingsBinding otherSettingsBinding;
    long cur_time = 0;

    private int cur_screen_saver_index = 0;
    String[] screen_saver_title;
    int[] screen_saver_value;

    private int cur_time_off_index = 0;
    String[] time_off_title;
    int[] time_off_value;

    String[] boot_source_name;
    String[] boot_source_value;
    private int boot_source_index = 0;
    private AwTvSystemManager mAwTvSystemManager;
    String[] powerModes;
    int curPowerMode = 0;

    private int sound_mode = 0;//当前声音模式下标
    private String[] soundMode_name;
    TvAudioControl tvAudioControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherSettingsBinding = ActivityOtherSettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(otherSettingsBinding.getRoot());
        initView();
        initData();
    }

    private void initView() {
        otherSettingsBinding.rlButtonSound.setOnClickListener(this);
        otherSettingsBinding.rlAudioMode.setOnClickListener(this);
        otherSettingsBinding.rlAudioMode.setOnKeyListener(this);
        otherSettingsBinding.buttonSoundSwitch.setOnClickListener(this);

        otherSettingsBinding.rlResetFactory.setOnClickListener(this);
        otherSettingsBinding.rlScreenSaver.setOnClickListener(this);
        otherSettingsBinding.rlTimerOff.setOnClickListener(this);

        otherSettingsBinding.rlScreenSaver.setOnKeyListener(this);
        otherSettingsBinding.rlTimerOff.setOnKeyListener(this);

        otherSettingsBinding.rlBootInput.setOnKeyListener(this);
        otherSettingsBinding.rlBootInput.setOnClickListener(this);

        otherSettingsBinding.rlPowerMode.setOnKeyListener(this);
        otherSettingsBinding.rlPowerMode.setOnClickListener(this);

        otherSettingsBinding.rlDeveloper.setOnClickListener(this);
        otherSettingsBinding.rlSetPassword.setOnClickListener(this);
        otherSettingsBinding.rlAccount.setOnClickListener(this);
        otherSettingsBinding.rlBootInput.requestFocus();
        otherSettingsBinding.rlBootInput.requestFocusFromTouch();

        otherSettingsBinding.rlButtonSound.setOnHoverListener(this);
        otherSettingsBinding.rlAudioMode.setOnHoverListener(this);
        otherSettingsBinding.rlResetFactory.setOnHoverListener(this);
        otherSettingsBinding.rlScreenSaver.setOnHoverListener(this);
        otherSettingsBinding.rlTimerOff.setOnHoverListener(this);
        otherSettingsBinding.rlDeveloper.setOnHoverListener(this);
        otherSettingsBinding.rlBootInput.setOnHoverListener(this);
        otherSettingsBinding.rlPowerMode.setOnHoverListener(this);
        otherSettingsBinding.rlAccount.setOnHoverListener(this);
        otherSettingsBinding.rlSetPassword.setOnHoverListener(this);

        otherSettingsBinding.bootInputLeft.setOnClickListener(this);
        otherSettingsBinding.screenSaverLeft.setOnClickListener(this);
        otherSettingsBinding.powerModeLeft.setOnClickListener(this);
        otherSettingsBinding.timerOffLeft.setOnClickListener(this);
        otherSettingsBinding.audioModeLeft.setOnClickListener(this);

        otherSettingsBinding.bootInputRight.setOnClickListener(this);
        otherSettingsBinding.screenSaverRight.setOnClickListener(this);
        otherSettingsBinding.powerModeRight.setOnClickListener(this);
        otherSettingsBinding.timerOffRight.setOnClickListener(this);
        otherSettingsBinding.audioModeRight.setOnClickListener(this);


        if ((boolean) ShareUtil.get(this, Contants.KEY_DEVELOPER_MODE, false) && MyApplication.config.developer) {
            otherSettingsBinding.rlDeveloper.setVisibility(View.VISIBLE);
        }
        otherSettingsBinding.rlBootInput.setVisibility(MyApplication.config.bootSource ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlScreenSaver.setVisibility(MyApplication.config.screenSaver ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlTimerOff.setVisibility(MyApplication.config.timerOff ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlButtonSound.setVisibility(MyApplication.config.soundEffects ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlResetFactory.setVisibility(MyApplication.config.resetFactory ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlPowerMode.setVisibility(MyApplication.config.powerMode ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlAccount.setVisibility(MyApplication.config.account ? View.VISIBLE : View.GONE);
        otherSettingsBinding.rlSetPassword.setVisibility(MyApplication.config.set_password?View.VISIBLE:View.GONE);
    }

    private void initData() {
        mAwTvSystemManager = AwTvSystemManager.getInstance(this);
        soundMode_name = getResources().getStringArray(R.array.soundMode_name);

        tvAudioControl = new TvAudioControl(this);
        sound_mode = tvAudioControl.getAudioMode();
        otherSettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);

        otherSettingsBinding.buttonSoundSwitch.setChecked(getButtonSound());

        screen_saver_title = getResources().getStringArray(R.array.screen_saver_title);
        screen_saver_value = getResources().getIntArray(R.array.screen_saver_value);
        powerModes = getResources().getStringArray(R.array.power_mode_name);
        cur_screen_saver_index = getCurScreenSaverIndex();
        otherSettingsBinding.screenSaverTv.setText(screen_saver_title[cur_screen_saver_index]);

        time_off_title = getResources().getStringArray(R.array.time_off_title);
        time_off_value = getResources().getIntArray(R.array.time_off_value);
        cur_time_off_index = (int) ShareUtil.get(this, Contants.TimeOffIndex, 0);
        otherSettingsBinding.timerOffTv.setText(time_off_title[cur_time_off_index]);
        /*if ((boolean) ShareUtil.get(this, Contants.TimeOffStatus,false)){
            int  timeOffTime =(int) ShareUtil.get(this, Contants.TimeOffTime,0);
            otherSettingsBinding.timerOffTv.setText(timeOffTime/60+"Min");
        }else {
            otherSettingsBinding.timerOffTv.setText(time_off_title[cur_time_off_index]);
        }*/
        curPowerMode = mAwTvSystemManager.getPowerOnMode() == AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT ? 1 : 0;
        otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);

        Utils.sourceList = MainActivity.getSourceListFiltered();
        Utils.sourceListTitle = MainActivity.getSourceListTitleFiltered();
        if (Utils.sourceList.length > 0 && !Utils.sourceList[0].isEmpty()) { //兼容多信源的情况
            boot_source_name = new String[Utils.sourceListTitle.length + 1];
            boot_source_name[0] = getResources().getString(R.string.boot_source_1);
            System.arraycopy(Utils.sourceListTitle, 0, boot_source_name, 1, Utils.sourceListTitle.length);

            boot_source_value = new String[Utils.sourceList.length + 1];
            boot_source_value[0] = "LOCAL";
            System.arraycopy(Utils.sourceList, 0, boot_source_value, 1, Utils.sourceList.length);
//            boot_source_name = Utils.sourceListTitle;
//            boot_source_value = Utils.sourceList;
        } else {
            boot_source_name = getResources().getStringArray(R.array.boot_source_name);
            boot_source_value = getResources().getStringArray(R.array.boot_source_value);
        }

//        boot_source_name = getResources().getStringArray(R.array.boot_source_name);
//        boot_source_value = getResources().getStringArray(R.array.boot_source_value);
        String source_value = get_power_signal();
        for (int i = 0; i < boot_source_value.length; i++) {
            if (source_value.equals(boot_source_value[i])) {
                boot_source_index = i;
                break;
            }
        }
        otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
    }

    private String get_power_signal() {

        return SystemProperties.get("persist.sys.default_source", "LOCAL");
    }

    private void set_power_signal(String source) {
        SystemProperties.set("persist.sys.default_source", source);
    }

    private int getCurScreenSaverIndex() {
        int screen_off_timeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 300000);
        for (int i = 0; i < screen_saver_value.length; i++) {
            if (screen_off_timeout == screen_saver_value[i])
                return i;
        }
        return 0;
    }

    private void updateAudioMode(int index) {
        tvAudioControl.setAudioMode(index);
        otherSettingsBinding.audioModeTv.setText(soundMode_name[index]);
    }

    private void updateScreenSaver(int index) {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screen_saver_value[index]);
        otherSettingsBinding.screenSaverTv.setText(screen_saver_title[index]);
    }

    private void setTimeOff(int index) {
        otherSettingsBinding.timerOffTv.setText(time_off_title[index]);
        ShareUtil.put(this, Contants.TimeOffIndex, index);
        Intent intent = new Intent(this, TimeOffService.class);
        if (index == 0) {
            ShareUtil.put(this, Contants.TimeOffStatus, false);
            intent.putExtra(Contants.TimeOffStatus, false);
            intent.putExtra(Contants.TimeOffTime, -1);
        } else {
            ShareUtil.put(this, Contants.TimeOffStatus, true);
            ShareUtil.put(this, Contants.TimeOffTime, time_off_value[index]);
            intent.putExtra(Contants.TimeOffStatus, true);
            intent.putExtra(Contants.TimeOffTime, time_off_value[index]);
        }
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_button_sound:
            case R.id.button_sound_switch:
                otherSettingsBinding.buttonSoundSwitch.setChecked(!otherSettingsBinding.buttonSoundSwitch.isChecked());
                setButtonSound(otherSettingsBinding.buttonSoundSwitch.isChecked());
                break;
            case R.id.rl_reset_factory:
                FactoryResetDialog factoryResetDialog = new FactoryResetDialog(this, R.style.DialogTheme);
                factoryResetDialog.show();
                break;

            case R.id.rl_audio_mode:
            case R.id.audio_mode_right:
                if (sound_mode == soundMode_name.length - 1)
                    sound_mode = 0;
                else
                    sound_mode++;
                updateAudioMode(sound_mode);
                break;
            case R.id.audio_mode_left:

                if (sound_mode == 0)
                    sound_mode = soundMode_name.length - 1;
                else
                    sound_mode--;
                updateAudioMode(sound_mode);
                break;
            case R.id.rl_screen_saver:
            case R.id.screen_saver_right:
                if (cur_screen_saver_index == screen_saver_title.length - 1)
                    cur_screen_saver_index = 0;
                else
                    cur_screen_saver_index++;
                updateScreenSaver(cur_screen_saver_index);
                break;
            case R.id.screen_saver_left:

                if (cur_screen_saver_index == 0)
                    cur_screen_saver_index = screen_saver_title.length - 1;
                else
                    cur_screen_saver_index--;
                updateScreenSaver(cur_screen_saver_index);
                break;
            case R.id.rl_timer_off:
            case R.id.timer_off_right:
                if (cur_time_off_index == time_off_title.length - 1)
                    cur_time_off_index = 0;
                else
                    cur_time_off_index++;

                setTimeOff(cur_time_off_index);
                break;
            case R.id.timer_off_left:
                if (cur_time_off_index == 0)
                    cur_time_off_index = time_off_title.length - 1;
                else
                    cur_time_off_index--;

                setTimeOff(cur_time_off_index);
                break;
            case R.id.rl_boot_input:
            case R.id.boot_input_right:
                if (boot_source_index == boot_source_name.length - 1)
                    boot_source_index = 0;
                else
                    boot_source_index++;

                otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                set_power_signal(boot_source_value[boot_source_index]);
                break;
            case R.id.boot_input_left:
                if (boot_source_index == 0)
                    boot_source_index = boot_source_name.length - 1;
                else
                    boot_source_index--;

                otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                set_power_signal(boot_source_value[boot_source_index]);
                break;
            case R.id.rl_power_mode:
            case R.id.power_mode_right:
                curPowerMode = curPowerMode == 1 ? 0 : 1;
                otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
                mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                        AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
                break;

            case R.id.rl_developer:
                startNewActivity(DeveloperModeActivity.class);
                break;
            case R.id.rl_account:
//                Intent intent = new Intent("android.settings.SYNC_SETTINGS");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                startNewActivity(AccountActivity.class);
                break;
            case R.id.rl_set_password:
                SetPasswordDialog passwordDialog = new SetPasswordDialog(this);
                passwordDialog.show();
                break;
        }
    }

    private boolean getButtonSound() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, 0) == 1;
    }

    private void setButtonSound(boolean ret) {
        Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, ret ? 1 : 0);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
                && (System.currentTimeMillis() - cur_time < 150)) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.rl_screen_saver:
                    if (cur_screen_saver_index == 0)
                        cur_screen_saver_index = screen_saver_title.length - 1;
                    else
                        cur_screen_saver_index--;
                    updateScreenSaver(cur_screen_saver_index);
                    break;
                case R.id.rl_timer_off:
                    if (cur_time_off_index == 0)
                        cur_time_off_index = time_off_title.length - 1;
                    else
                        cur_time_off_index--;

                    setTimeOff(cur_time_off_index);
                    break;

                case R.id.rl_boot_input:
                    if (boot_source_index == 0)
                        boot_source_index = boot_source_name.length - 1;
                    else
                        boot_source_index--;

                    otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                    set_power_signal(boot_source_value[boot_source_index]);
                    break;
                case R.id.rl_power_mode:
                    curPowerMode = curPowerMode == 1 ? 0 : 1;
                    otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
                    mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                            AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
                    break;
                case R.id.rl_audio_mode:
                    if (sound_mode == 0)
                        sound_mode = soundMode_name.length - 1;
                    else
                        sound_mode--;
                    updateAudioMode(sound_mode);
                    break;

            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.rl_screen_saver:
                    if (cur_screen_saver_index == screen_saver_title.length - 1)
                        cur_screen_saver_index = 0;
                    else
                        cur_screen_saver_index++;
                    updateScreenSaver(cur_screen_saver_index);
                    break;
                case R.id.rl_timer_off:
                    if (cur_time_off_index == time_off_title.length - 1)
                        cur_time_off_index = 0;
                    else
                        cur_time_off_index++;

                    setTimeOff(cur_time_off_index);
                    break;
                case R.id.rl_boot_input:
                    if (boot_source_index == boot_source_name.length - 1)
                        boot_source_index = 0;
                    else
                        boot_source_index++;

                    otherSettingsBinding.bootInputTv.setText(boot_source_name[boot_source_index]);
                    set_power_signal(boot_source_value[boot_source_index]);
                    break;
                case R.id.rl_power_mode:
                    curPowerMode = curPowerMode == 1 ? 0 : 1;
                    otherSettingsBinding.powerModeTv.setText(powerModes[curPowerMode]);
                    mAwTvSystemManager.setPowerOnMode(curPowerMode == 1 ?
                            AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_DIRECT : AwTvSystemTypes.EnumPowerMode.E_AW_POWER_MODE_STANDBY);
                    break;
                case R.id.rl_audio_mode:
                    if (sound_mode == soundMode_name.length - 1)
                        sound_mode = 0;
                    else
                        sound_mode++;
                    updateAudioMode(sound_mode);
                    break;


            }
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return false;
    }
}