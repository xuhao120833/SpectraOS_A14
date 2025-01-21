package com.htc.spectraos.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.ActivityDisplaySettingsBinding;
import com.htc.spectraos.utils.ReflectUtil;
import com.softwinner.PQControl;
import com.softwinner.TvAudioControl;
import com.softwinner.tv.AwTvAudioManager;
import com.softwinner.tv.common.AwTvAudioTypes;

public class DisplaySettingsActivity extends BaseActivity implements View.OnKeyListener {

    private int brightness_system = 100;
    private int brightness = 0;
    private int mCurContrast = 50;
    private int mCurSaturation = 50;
    private int mCurHue = 50;
    private int mSharpness = 50;

    private int mR = 50;
    private int mG = 50;
    private int mB = 50;

    private String[] picture_mode_choices;
    private String[] picture_mode_values;

    private int curPosition = 0;//当前图像模式
    private PQControl pqControl;

    private int sound_mode = 0;//当前声音模式下标
    private String[] soundMode_name ;
    TvAudioControl tvAudioControl;
    private int mColorTemp = 0;
    private AwTvAudioManager mAwTvAudioManager = null;

    ActivityDisplaySettingsBinding displaySettingsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displaySettingsBinding = ActivityDisplaySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(displaySettingsBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        displaySettingsBinding.rlPictureMode.setOnClickListener(this);
        displaySettingsBinding.rlAudioMode.setOnClickListener(this);
        displaySettingsBinding.rlBrightness.setOnClickListener(this);
        displaySettingsBinding.rlBrightnessSystem.setOnClickListener(this);
        displaySettingsBinding.rlContrast.setOnClickListener(this);
        displaySettingsBinding.rlSaturation.setOnClickListener(this);
        displaySettingsBinding.rlHue.setOnClickListener(this);
        displaySettingsBinding.rlSharpness.setOnClickListener(this);
        displaySettingsBinding.rlRed.setOnClickListener(this);
        displaySettingsBinding.rlGreen.setOnClickListener(this);
        displaySettingsBinding.rlBlue.setOnClickListener(this);

        displaySettingsBinding.pictureModeLeft.setOnClickListener(this);
        displaySettingsBinding.audioModeLeft.setOnClickListener(this);
        displaySettingsBinding.brightnessLeft.setOnClickListener(this);
        displaySettingsBinding.brightnessSystemLeft.setOnClickListener(this);
        displaySettingsBinding.contrastLeft.setOnClickListener(this);
        displaySettingsBinding.saturationLeft.setOnClickListener(this);
        displaySettingsBinding.hueLeft.setOnClickListener(this);
        displaySettingsBinding.sharpnessLeft.setOnClickListener(this);
        displaySettingsBinding.redLeft.setOnClickListener(this);
        displaySettingsBinding.greenLeft.setOnClickListener(this);
        displaySettingsBinding.blueLeft.setOnClickListener(this);

        displaySettingsBinding.pictureModeRight.setOnClickListener(this);
        displaySettingsBinding.audioModeRight.setOnClickListener(this);
        displaySettingsBinding.brightnessRight.setOnClickListener(this);
        displaySettingsBinding.brightnessSystemRight.setOnClickListener(this);
        displaySettingsBinding.contrastRight.setOnClickListener(this);
        displaySettingsBinding.saturationRight.setOnClickListener(this);
        displaySettingsBinding.hueRight.setOnClickListener(this);
        displaySettingsBinding.sharpnessRight.setOnClickListener(this);
        displaySettingsBinding.redRight.setOnClickListener(this);
        displaySettingsBinding.greenRight.setOnClickListener(this);
        displaySettingsBinding.blueRight.setOnClickListener(this);

        displaySettingsBinding.rlPictureMode.setOnKeyListener(this);
        displaySettingsBinding.rlAudioMode.setOnKeyListener(this);
        displaySettingsBinding.rlBrightness.setOnKeyListener(this);
        displaySettingsBinding.rlBrightnessSystem.setOnKeyListener(this);
        displaySettingsBinding.rlContrast.setOnKeyListener(this);
        displaySettingsBinding.rlSaturation.setOnKeyListener(this);
        displaySettingsBinding.rlHue.setOnKeyListener(this);
        displaySettingsBinding.rlSharpness.setOnKeyListener(this);
        displaySettingsBinding.rlRed.setOnKeyListener(this);
        displaySettingsBinding.rlGreen.setOnKeyListener(this);
        displaySettingsBinding.rlBlue.setOnKeyListener(this);

        displaySettingsBinding.rlPictureMode.setOnHoverListener(this);
        displaySettingsBinding.rlAudioMode.setOnHoverListener(this);
        displaySettingsBinding.rlBrightness.setOnHoverListener(this);
        displaySettingsBinding.rlBrightnessSystem.setOnHoverListener(this);
        displaySettingsBinding.rlContrast.setOnHoverListener(this);
        displaySettingsBinding.rlSaturation.setOnHoverListener(this);
        displaySettingsBinding.rlHue.setOnHoverListener(this);
        displaySettingsBinding.rlSharpness.setOnHoverListener(this);
        displaySettingsBinding.rlRed.setOnHoverListener(this);
        displaySettingsBinding.rlGreen.setOnHoverListener(this);
        displaySettingsBinding.rlBlue.setOnHoverListener(this);

        displaySettingsBinding.rlPictureMode.setVisibility(MyApplication.config.pictureMode?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlAudioMode.setVisibility(MyApplication.config.displayAudioMode?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlBrightness.setVisibility(MyApplication.config.brightness?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlBrightnessSystem.setVisibility(MyApplication.config.brightnessSystem?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlSaturation.setVisibility(MyApplication.config.saturation?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlContrast.setVisibility(MyApplication.config.contrast?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlHue.setVisibility(MyApplication.config.hue?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlSharpness.setVisibility(MyApplication.config.sharpness?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlRed.setVisibility(MyApplication.config.red?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlGreen.setVisibility(MyApplication.config.green?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlBlue.setVisibility(MyApplication.config.blue?View.VISIBLE:View.GONE);
        displaySettingsBinding.line.setVisibility(MyApplication.config.pictureMode||MyApplication.config.displayAudioMode?View.VISIBLE:View.GONE);
        displaySettingsBinding.rlPictureMode.requestFocus();
        displaySettingsBinding.rlPictureMode.requestFocusFromTouch();
    }

    private void initData(){
        if (MyApplication.config.pictureModeShowCustom) {
            picture_mode_values = getResources().getStringArray(R.array.picture_mode_values);
            picture_mode_choices = getResources().getStringArray(R.array.picture_mode_choices);
        }else {
            picture_mode_values = getResources().getStringArray(R.array.picture_mode_values_no_custom);
            picture_mode_choices = getResources().getStringArray(R.array.picture_mode_choices_no_custom);
        }
        pqControl = new PQControl();
        soundMode_name = getResources().getStringArray(R.array.soundMode_name);
        tvAudioControl = new TvAudioControl(this);
        mAwTvAudioManager = AwTvAudioManager.getInstance(this);


        String pictureName = pqControl.getPictureModeName();
        Log.d("hzj","pictureName "+pictureName);
        for (int i = 0; i < picture_mode_values.length; i++) {
            if (picture_mode_values[i].equals(pictureName)) {
                curPosition = i;
                break;
            }
        }
        displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);

        sound_mode = tvAudioControl.getAudioMode();
        displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);

        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

        mColorTemp = pqControl.getColorTemperature();
        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
        mR = mRGBInfo[PQControl.GAIN_R];
        mG = mRGBInfo[PQControl.GAIN_G];
        mB = mRGBInfo[PQControl.GAIN_B];

        updateBrightnessSystem(false);
        getBrightness();
        updateContrast(false);
        updateHue(false);
        updateSaturation(false);
        updateSharpness(false);

        updateR(false);
        updateG(false);
        updateB(false);
    }

    @Override
    public void onClick(View v) {

    switch (v.getId()){
        case R.id.rl_picture_mode:
        case R.id.picture_mode_right:
            if (curPosition == picture_mode_values.length - 1) {
                curPosition = 0;
            } else {
                curPosition += 1;
            }
            pqControl.setPictureMode(picture_mode_values[curPosition]);
            updatePictureMode();
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
            break;
        case R.id.picture_mode_left:
            if (curPosition == 0) {
                curPosition = picture_mode_values.length - 1;
            } else {
                curPosition -= 1;
            }
            pqControl.setPictureMode(picture_mode_values[curPosition]);
            updatePictureMode();
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
            break;
        case R.id.rl_audio_mode:
        case R.id.audio_mode_right:
            if (sound_mode==soundMode_name.length-1){
                sound_mode = 0;
            }else {
                sound_mode+=1;
            }
            tvAudioControl.setAudioMode(sound_mode);
            displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
            break;
        case R.id.audio_mode_left:
            if (sound_mode==0){
                sound_mode = soundMode_name.length-1;
            }else {
                sound_mode-=1;
            }
            tvAudioControl.setAudioMode(sound_mode);
            displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
            break;
        case R.id.rl_brightness:
        case R.id.brightness_right:
            if (brightness == MyApplication.config.brightnessLevel)
                break;

            brightness += 1;
            if (brightness > MyApplication.config.brightnessLevel) {
                brightness = MyApplication.config.brightnessLevel;
            }
            updateBrightness(true);
            break;
        case R.id.brightness_left:
            if (brightness_system == 1)
                break;

            brightness_system -= 1;
            if (brightness_system <= 1) {
                brightness_system = 1;
            }
            updateBrightness(true);
            break;
        case R.id.rl_brightness_system:
        case R.id.brightness_system_right:
            if (brightness_system==100)
                break;

            brightness_system+=1;
            if (brightness_system>100)
                brightness_system=100;

            updateBrightnessSystem(true);
            break;
        case R.id.brightness_system_left:
            if (brightness_system==1)
                break;

            brightness_system-=1;
            if (brightness_system<1)
                brightness_system=1;

            updateBrightnessSystem(true);
            break;
        case R.id.rl_contrast:
        case R.id.contrast_right:
            if (mCurContrast == 100)
                break;

            mCurContrast += 1;
            if (mCurContrast > 100)
                mCurContrast = 100;

            updateContrast(true);
            break;
        case R.id.contrast_left:
            if (mCurContrast == 1)
                break;

            mCurContrast -= 1;
            if (mCurContrast < 1)
                mCurContrast = 1;

            updateContrast(true);
            break;
        case R.id.rl_hue:
        case R.id.hue_right:
            if (mCurHue == 100)
                break;

            mCurHue += 1;
            if (mCurHue > 100)
                mCurHue = 100;

            updateHue(true);
            break;
        case R.id.hue_left:
            if (mCurHue == 1)
                break;

            mCurHue -= 1;
            if (mCurHue < 1)
                mCurHue = 1;

            updateHue(true);
            break;
        case R.id.rl_saturation:
        case R.id.saturation_right:
            if (mCurSaturation == 100)
                break;

            mCurSaturation += 1;
            if (mCurSaturation > 100)
                mCurSaturation = 100;

            updateSaturation(true);
            break;
        case R.id.saturation_left:
            if (mCurSaturation == 1)
                break;

            mCurSaturation -= 1;
            if (mCurSaturation < 1)
                mCurSaturation = 1;

            updateSaturation(true);
            break;

        case R.id.rl_sharpness:
        case R.id.sharpness_right:
            if (mSharpness == 100)
                break;

            mSharpness += 1;
            if (mSharpness > 100)
                mSharpness = 100;

            updateSharpness(true);
            break;
        case R.id.sharpness_left:
            if (mSharpness == 1)
                break;

            mSharpness -= 1;
            if (mSharpness < 1)
                mSharpness = 1;

            updateSharpness(true);
            break;
        case R.id.rl_red:
        case R.id.red_right:
            if (mR == 1023)
                break;

            mR += 5;

            if (mR > 1023)
                mR = 1023;

            updateR(true);
            break;
        case R.id.red_left:
            if (mR == 1)
                break;

            mR -= 5;

            if (mR < 1)
                mR = 1;

            updateR(true);
            break;
        case R.id.rl_green:
        case R.id.green_right:
            if (mG == 1023)
                break;

            mG += 5;

            if (mG > 1023)
                mG = 1023;

            updateG(true);
            break;
        case R.id.green_left:
            if (mG == 1)
                break;

            mG -= 5;

            if (mG < 1)
                mG = 1;

            updateG(true);
            break;
        case R.id.rl_blue:
        case R.id.blue_right:
            if (mB == 1023)
                break;


            mB += 5;


            if (mB > 1023)
                mB = 1023;

            updateB(true);
            break;
        case R.id.blue_left:
            if (mB == 1)
                break;

            mB -= 5;

            if (mB < 1)
                mB = 1;

            updateB(true);
            break;
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.rl_brightness:

                    if (brightness == 0)
                        break;

                    brightness -= 1;
                    if (brightness < 0) {
                        brightness = 0;
                    }
                    updateBrightness(true);
                    break;
                case R.id.rl_brightness_system:
                    if (brightness_system == 1)
                        break;

                    brightness_system -= 1;
                    if (brightness_system <= 1) {
                        brightness_system = 1;
                    }

                    updateBrightnessSystem(true);
                    break;
                case R.id.rl_contrast:
                    if (mCurContrast == 1)
                        break;

                    mCurContrast -= 1;
                    if (mCurContrast < 1)
                        mCurContrast = 1;
                    updateContrast(true);
                    break;
                case R.id.rl_hue:
                    if (mCurHue == 1)
                        break;

                    mCurHue -= 1;
                    if (mCurHue < 1)
                        mCurHue = 1;

                    updateHue(true);
                    break;
                case R.id.rl_saturation:
                    if (mCurSaturation == 1)
                        break;

                    mCurSaturation -= 1;
                    if (mCurSaturation < 1)
                        mCurSaturation = 1;

                    updateSaturation(true);
                    break;
                case R.id.rl_sharpness:
                    if (mSharpness == 1)
                        break;

                    mSharpness -= 1;
                    if (mSharpness < 1)
                        mSharpness = 1;

                    updateSharpness(true);
                    break;

                case R.id.rl_red:
                    if (mR == 1)
                        break;

                    if (event.getRepeatCount()==0)
                        mR -= 1;
                    else
                        mR -= 5;

                    if (mR < 1)
                        mR = 1;

                    updateR(true);
                    break;

                case R.id.rl_green:
                    if (mG == 1)
                        break;

                    if (event.getRepeatCount()==0)
                        mG -= 1;
                    else
                        mG -= 5;

                    if (mG < 1)
                        mG = 1;

                    updateG(true);
                    break;

                case R.id.rl_blue:
                    if (mB == 1)
                        break;

                    if (event.getRepeatCount()==0)
                        mB -= 1;
                    else
                        mB -= 5;

                    if (mB < 1)
                        mB = 1;

                    updateB(true);
                    break;

                case R.id.rl_picture_mode:
                    if (curPosition == 0) {
                        curPosition = picture_mode_values.length - 1;
                    } else {
                        curPosition -= 1;
                    }
                    pqControl.setPictureMode(picture_mode_values[curPosition]);
                    updatePictureMode();
                    displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                    break;
                case R.id.rl_audio_mode:
                    if (sound_mode==0){
                        sound_mode = soundMode_name.length-1;
                    }else {
                        sound_mode-=1;
                    }
                    tvAudioControl.setAudioMode(sound_mode);
                    displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
                    break;

            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.rl_brightness:
                    // if (brightness==100)
                    //     break;
                    //
                    // brightness+=5;
                    // if (brightness>100)
                    //     brightness=100;
                    //
                    // updateBrightness();
                    if (brightness == MyApplication.config.brightnessLevel)
                        break;

                    brightness += 1;
                    if (brightness > MyApplication.config.brightnessLevel) {
                        brightness = MyApplication.config.brightnessLevel;
                    }
                    updateBrightness(true);
                    break;
                case R.id.rl_brightness_system:
                    if (brightness_system==100)
                        break;

                    brightness_system+=1;
                    if (brightness_system>100)
                        brightness_system=100;

                    updateBrightnessSystem(true);

                    break;
                case R.id.rl_contrast:
                    if (mCurContrast == 100)
                        break;

                    mCurContrast += 1;
                    if (mCurContrast > 100)
                        mCurContrast = 100;

                    updateContrast(true);
                    break;
                case R.id.rl_hue:
                    if (mCurHue == 100)
                        break;

                    mCurHue += 1;
                    if (mCurHue > 100)
                        mCurHue = 100;

                    updateHue(true);
                    break;
                case R.id.rl_saturation:
                    if (mCurSaturation == 100)
                        break;

                    mCurSaturation += 1;
                    if (mCurSaturation > 100)
                        mCurSaturation = 100;

                    updateSaturation(true);
                    break;
                case R.id.rl_sharpness:
                    if (mSharpness == 100)
                        break;

                    mSharpness += 1;
                    if (mSharpness > 100)
                        mSharpness = 100;

                    updateSharpness(true);
                    break;
                case R.id.rl_red:
                    if (mR == 1023)
                        break;

                    if (event.getRepeatCount()==0)
                        mR += 1;
                    else
                        mR += 5;


                    if (mR > 1023)
                        mR = 1023;

                    updateR(true);
                    break;

                case R.id.rl_green:
                    if (mG == 1023)
                        break;

                    if (event.getRepeatCount()==0)
                        mG += 1;
                    else
                        mG += 5;


                    if (mG > 1023)
                        mG = 1023;

                    updateG(true);
                    break;

                case R.id.rl_blue:
                    if (mB == 1023)
                        break;

                    if (event.getRepeatCount()==0)
                        mB += 1;
                    else
                        mB += 5;


                    if (mB > 1023)
                        mB = 1023;

                    updateB(true);
                    break;
                case R.id.rl_picture_mode:
                    if (curPosition == picture_mode_values.length - 1) {
                        curPosition = 0;
                    } else {
                        curPosition += 1;
                    }
                    pqControl.setPictureMode(picture_mode_values[curPosition]);
                    updatePictureMode();
                    displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
                    break;
                case R.id.rl_audio_mode:
                    if (sound_mode==soundMode_name.length-1){
                        sound_mode = 0;
                    }else {
                        sound_mode+=1;
                    }
                    tvAudioControl.setAudioMode(sound_mode);
                    displaySettingsBinding.audioModeTv.setText(soundMode_name[sound_mode]);
                    break;
            }
            return true;
        }

        return false;
    }


    private void getBrightness() {

        if (MyApplication.config.brightnessLevel==1 || MyApplication.config.brightnessLevel ==2)
            brightness = ReflectUtil.invoke_get_bright() - (3 - MyApplication.config.brightnessLevel);
        else {
            brightness = ReflectUtil.invoke_get_bright();
        }

        displaySettingsBinding.brightnessTv.setText(String.valueOf(brightness+1));
        displaySettingsBinding.brightnessRight.setVisibility(brightness==MyApplication.config.brightnessLevel?View.GONE:View.VISIBLE);
        displaySettingsBinding.brightnessLeft.setVisibility(brightness==0?View.GONE:View.VISIBLE);
    }

    private void updateBrightness(boolean set) {
        // pqControl.setBasicControl(PQControl.PQ_BASIC_BRIGHTNESS, brightness+1);
        if (set ) {
            if (MyApplication.config.brightnessLevel== 1 || MyApplication.config.brightnessLevel == 2)
                ReflectUtil.invoke_set_bright(brightness + (3 - MyApplication.config.brightnessLevel));
            else
                ReflectUtil.invoke_set_bright(brightness);
        }
        displaySettingsBinding.brightnessTv.setText(String.valueOf(brightness+1));
        displaySettingsBinding.brightnessRight.setVisibility(brightness==MyApplication.config.brightnessLevel?View.GONE:View.VISIBLE);
        displaySettingsBinding.brightnessLeft.setVisibility(brightness==0?View.GONE:View.VISIBLE);
    }

    private void updateBrightnessSystem(boolean set) {
        if (set){
            pqControl.setBasicControl(PQControl.PQ_BASIC_BRIGHTNESS, brightness_system);
            curPosition =3;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }

        displaySettingsBinding.brightnessSystemTv.setText(String.valueOf(brightness_system));

        displaySettingsBinding.brightnessSystemRight.setVisibility(brightness_system==100?View.GONE:View.VISIBLE);
        displaySettingsBinding.brightnessSystemLeft.setVisibility(brightness_system==1?View.GONE:View.VISIBLE);

    }

    private void updateContrast(boolean set) {

        if (set){
            pqControl.setBasicControl(PQControl.PQ_BASIC_CONTRAST, mCurContrast);
            curPosition =3;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.contrastTv.setText("" + mCurContrast);
        displaySettingsBinding.contrastRight.setVisibility(mCurContrast==100?View.GONE:View.VISIBLE);
        displaySettingsBinding.contrastLeft.setVisibility(mCurContrast==1?View.GONE:View.VISIBLE);
    }

    private void updateSaturation(boolean set) {
        if (set) {
            pqControl.setBasicControl(PQControl.PQ_BASIC_SATURATION, mCurSaturation);
            curPosition =3;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.saturationTv.setText("" + mCurSaturation);
        displaySettingsBinding.saturationRight.setVisibility(mCurSaturation==100?View.GONE:View.VISIBLE);
        displaySettingsBinding.saturationLeft.setVisibility(mCurSaturation==1?View.GONE:View.VISIBLE);
    }

    private void updateHue(boolean set) {
        if (set) {
            pqControl.setBasicControl(PQControl.PQ_BASIC_HUE, mCurHue);
            curPosition =3;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.hueTv.setText("" + mCurHue);
        displaySettingsBinding.hueRight.setVisibility(mCurHue==100?View.GONE:View.VISIBLE);
        displaySettingsBinding.hueLeft.setVisibility(mCurHue==1?View.GONE:View.VISIBLE);
    }

    private void updateSharpness(boolean set) {
        if (set) {
            pqControl.setBasicControl(PQControl.PQ_BASIC_SHARPNESS, mSharpness);
            curPosition =3;
            displaySettingsBinding.pictureModeTv.setText(picture_mode_choices[curPosition]);
        }
        displaySettingsBinding.sharpnessTv.setText("" + mSharpness);
        displaySettingsBinding.sharpnessRight.setVisibility(mSharpness==100?View.GONE:View.VISIBLE);
        displaySettingsBinding.sharpnessLeft.setVisibility(mSharpness==1?View.GONE:View.VISIBLE);
    }

    private void updateR(boolean set) {
        if (set) {
            pqControl.factorySetWBInfo(mColorTemp, PQControl.GAIN_R , mR);
        }
        displaySettingsBinding.redTv.setText("" + mR);
        displaySettingsBinding.redRight.setVisibility(mR==1023?View.GONE:View.VISIBLE);
        displaySettingsBinding.redLeft.setVisibility(mR==1?View.GONE:View.VISIBLE);
    }

    private void updateG(boolean set) {
        if (set) {
            pqControl.factorySetWBInfo(mColorTemp, PQControl.GAIN_G, mG);
        }
        displaySettingsBinding.greenTv.setText("" + mG);
        displaySettingsBinding.greenRight.setVisibility(mG==1023?View.GONE:View.VISIBLE);
        displaySettingsBinding.greenLeft.setVisibility(mG==1?View.GONE:View.VISIBLE);
    }

    private void updateB(boolean set) {
        if (set) {
            pqControl.factorySetWBInfo(mColorTemp,PQControl.GAIN_B,mB);
        }
        displaySettingsBinding.blueTv.setText("" + mB);
        displaySettingsBinding.blueRight.setVisibility(mB==1023?View.GONE:View.VISIBLE);
        displaySettingsBinding.blueLeft.setVisibility(mB==1?View.GONE:View.VISIBLE);
    }

    private void updatePictureMode() {
        // brightness = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);

        brightness_system = pqControl.getBasicControl(PQControl.PQ_BASIC_BRIGHTNESS);
        mCurSaturation = pqControl.getBasicControl(PQControl.PQ_BASIC_SATURATION);
        mCurContrast = pqControl.getBasicControl(PQControl.PQ_BASIC_CONTRAST);
        mCurHue = pqControl.getBasicControl(PQControl.PQ_BASIC_HUE);
        mSharpness = pqControl.getBasicControl(PQControl.PQ_BASIC_SHARPNESS);

         mColorTemp = pqControl.getColorTemperature();
        int[] mRGBInfo = pqControl.factoryGetWBInfo(mColorTemp);
        mR = mRGBInfo[PQControl.GAIN_R];
        mG = mRGBInfo[PQControl.GAIN_G];
        mB = mRGBInfo[PQControl.GAIN_B];

        getBrightness();
        updateBrightnessSystem(false);
        updateContrast(false);
        updateSaturation(false);
        updateHue(false);
        updateSharpness(false);

        updateR(false);
        updateG(false);
        updateB(false);
    }
}