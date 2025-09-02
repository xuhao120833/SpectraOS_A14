package com.htc.spectraos.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.android.internal.app.LocalePicker;
import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.ActivityLanguageKeyboardBinding;
import com.htc.spectraos.entry.InputMethodBean;
import com.htc.spectraos.entry.Language;
import com.htc.spectraos.widget.CustomInputMethodDialog;
import com.htc.spectraos.widget.CustomLanguageDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageAndKeyboardActivity extends BaseActivity {
    private static String TAG = "LanguageAndKeyboardActivity";
    private ActivityLanguageKeyboardBinding languageKeyboardBinding;

    private ArrayList<Language> mLocales;
    private String[] mSpecialLocaleCodes;
    private String[] mSpecialLocaleNames;

    private boolean mHaveHardKeyboard = false;
    private int mSelection = -1;

    private String mLastInputMethodId;
    private List<InputMethodInfo> mInputMethodList;
    private ArrayList<InputMethodBean> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageKeyboardBinding = ActivityLanguageKeyboardBinding.inflate(LayoutInflater.from(this));
        setContentView(languageKeyboardBinding.getRoot());
        initView();
        initData();
    }
    
    private void initView(){
        languageKeyboardBinding.rlLanguage.setOnClickListener(this);
        languageKeyboardBinding.rlKeyboardSetting.setOnClickListener(this);

        languageKeyboardBinding.rlLanguage.setOnHoverListener(this);
        languageKeyboardBinding.rlKeyboardSetting.setOnHoverListener(this);

        languageKeyboardBinding.rlLanguage.setVisibility(MyApplication.config.language?View.VISIBLE:View.GONE);
        languageKeyboardBinding.rlKeyboardSetting.setVisibility(MyApplication.config.inputMethod?View.VISIBLE:View.GONE);
    }
    
    private void initData(){
        loadDefault();
    }

    private void loadDefault() {
        String language = getCurrentLauguage();
        if (!TextUtils.isEmpty(language)) {
            if (language.contains("[XB]")){
                languageKeyboardBinding.languageTv.setText("العربية  (XB)");

            }else if (language.contains("[XA]")){

                languageKeyboardBinding.languageTv.setText("English (XA)");
            }else {

                languageKeyboardBinding.languageTv.setText(language);
            }
        }
        String inputMethod = getKeyBoardDefault();
        if (inputMethod != null ) {
            languageKeyboardBinding.keyboardTv.setText(inputMethod);
        }

    }

    private String getCurrentLauguage() {
        // 获取系统当前使用的语言
        String mCurrentLanguage = Locale.getDefault().getDisplayName();
        // 设置成简体中文的时候，getLanguage()返回的是zh
        return mCurrentLanguage;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_language) {
            if (mLocales == null) {
                buildLangListItem();
            }
            if (mLocales != null) {
                setLanguage(mLocales);
            }
        } else if (id == R.id.rl_keyboard_setting) {
            mArrayList.clear();
            getInputMethod();
            if (mArrayList.size() > 0) {
                setInputMethod();
            }
        }
    }

    /**
     * 获取机器语言列表 1、代码优化--return ArrayList<Language> ,这样就可以在其他需要获取语言列表的地方复用
     */
    /**
     * 获取机器语言列表 1、代码优化--return ArrayList<Language> ,这样就可以在其他需要获取语言列表的地方复用
     */
    private void buildLangListItem() {
        mSpecialLocaleCodes = getResources().getStringArray(
                R.array.lang_speciale_codes);
        mSpecialLocaleNames = getResources().getStringArray(
                R.array.lang_special_names);
        String[] locales = getAssets().getLocales();
        Arrays.sort(locales);
        final int origSize = locales.length;
        Language[] preprocess = new Language[origSize];
        int finalSize = 0;
        for (int i = 0; i < origSize; i++) {
            String s = locales[i];
            Log.d(TAG, " buildLangListItem locales[i] " + locales[i] + " " + locales.length);
            String language = "";
            String country = "";
            Locale l = null;
            if(s.equals("zh-CN") || s.equals("en-XA") || s.equals("en-XC"))//1、中国只处理zh、zh-HK、zh-TW三种情况
                continue;//2、Android 14增加了en-XA、en-XC（伪本地化语言)，必须过滤掉

            // 检查是否包含国家码
            String[] parts = s.split("-");
            if (parts.length == 2) {
                // 包括国家码的情况
                language = parts[0];
                country = parts[1];
                l = new Locale(language, country);
            } else {
                // 没有国家码的情况
                language = s;
                l = new Locale(language);
            }
            if (finalSize == 0) {
                preprocess[finalSize++] = new Language(toTitleCase(l.getDisplayLanguage(l)), l);
            } else {
                if (preprocess[finalSize - 1].getLocale().getLanguage().equals(language)
                        && (language.equals("zh") || language.equals("en"))) {  //只有中文、英文区分具体的国家
                    Log.d(TAG, " 语言列表 language " + language + " s " + s + " l.getDisplayLanguage(l)" + l.getDisplayLanguage(l));
                    preprocess[finalSize - 1].setLabel(toTitleCase(getDisplayName(preprocess[finalSize - 1].getLocale())));
                    preprocess[finalSize++] = new Language(toTitleCase(getDisplayName(l)), l);
                } else if (preprocess[finalSize - 1].getLocale().getLanguage().equals(language)) {

                } else {
                    String displayName;
                    if (s.equals("zz_ZZ")) {
                        displayName = "Pseudo...";
                    } else {
                        displayName = toTitleCase(l.getDisplayLanguage(l));
                    }
                    preprocess[finalSize++] = new Language(displayName, l);
                }
            }
        }
        Language mLocales2[] = new Language[finalSize];
        for (int i = 0; i < finalSize; i++) {
            mLocales2[i] = preprocess[i];
            Log.d(TAG, " 语言列表 getLabel " + mLocales2[i].getLabel());
            //阿拉伯语
            if (mLocales2[i].getLabel().contains("[XB]")) {
                mLocales2[i].setLabel("العربية  (XB)");
            }
            //英语（阿拉伯） en_XA
            if (mLocales2[i].getLabel().contains("[XA]")) {
                mLocales2[i].setLabel("English (XA)");
            }
        }
        Arrays.sort(mLocales2);
        for (int b = 0; b < mLocales2.length; b++) {
            Log.d(TAG, " 语言列表 排序后 getLabel " + mLocales2[b].getLabel() + " " + mLocales2.length
                    +" "+mLocales2[b].getLocale().getLanguage()+" "+mLocales2[b].getLocale().getCountry());
        }
        // Arrays.sort(preprocess);
        mLocales = new ArrayList<>(Arrays.asList(mLocales2));
        Log.d(TAG, " buildLangListItem 最后的列表长度 " + mLocales.size());
    }
//    private void buildLangListItem() {
//        mSpecialLocaleCodes = getResources().getStringArray(
//                R.array.lang_speciale_codes);
//        mSpecialLocaleNames = getResources().getStringArray(
//                R.array.lang_special_names);
//        String[] locales = getAssets().getLocales();
//        Arrays.sort(locales);
//        final int origSize = locales.length;
//        Language[] preprocess = new Language[origSize];
//        boolean filter = SystemProperties.get("persist.sys.Channel","pro").equals("D042Q_11_EQ_en");
//        int finalSize = 0;
//        for (int i = 0; i < origSize; i++) {
//            String s = locales[i];
//            int len = s.length();
//            if (len == 5) {
//                String language = s.substring(0, 2);
//                String country = s.substring(3, 5);
//                if (filter && language.equals("iw"))
//                    continue;
//
//                Locale l = new Locale(language, country);
//
//                if (finalSize == 0) {
//                    preprocess[finalSize++] = new Language(
//                            toTitleCase(l.getDisplayLanguage(l)), l);
//                } else {
//                    if (preprocess[finalSize - 1].getLocale().getLanguage()
//                            .equals(language)) {
//                        preprocess[finalSize - 1]
//                                .setLabel(toTitleCase(getDisplayName(preprocess[finalSize - 1]
//                                        .getLocale())));
//                        preprocess[finalSize++] = new Language(
//                                toTitleCase(getDisplayName(l)), l);
//                    } else {
//                        String displayName;
//                        if (s.equals("zz_ZZ")) {
//                            displayName = "Pseudo...";
//                        } else {
//                            displayName = toTitleCase(l.getDisplayLanguage(l));
//                        }
//                        preprocess[finalSize++] = new Language(displayName, l);
//                    }
//                }
//                // setIconRes(preprocess[finalSize-1]);//Set Icon
//            }
//        }
//        Language mLocales2[] = new Language[finalSize];
//        for (int i = 0; i < finalSize; i++) {
//
//            mLocales2[i] = preprocess[i];
//            //阿拉伯语
//            if (mLocales2[i].getLabel().contains("[XB]")){
//                mLocales2[i].setLabel("العربية  (XB)");
//
//            }
//
//            //英语（阿拉伯） en_XA
//            if (mLocales2[i].getLabel().contains("[XA]")){
//                mLocales2[i].setLabel("English (XA)");
//            }
//
//
//            if (preprocess[i].getLocale().getCountry().equals(Locale.getDefault().getCountry())
//                    && preprocess[i].getLocale().getLanguage().equals(Locale.getDefault().getLanguage())){
//                mLocales2[i] = preprocess[0];
//                mLocales2[0] = preprocess[i];
//            }else {
//                mLocales2[i] = preprocess[i];
//            }
//        }
//
//
//        // Arrays.sort(preprocess);
//        mLocales = new ArrayList<>(Arrays.asList(mLocales2));
//    }

    /**
     *  首字符大写
     * @param s
     * @return
     */
    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String getDisplayName(Locale l) {
        String code = l.toString();

        for (int i = 0; i < mSpecialLocaleCodes.length; i++) {
            if (mSpecialLocaleCodes[i].equals(code)) {
                return mSpecialLocaleNames[i];
            }
        }

        return l.getDisplayName(l);
    }



    public String getKeyBoardDefault() {
        ContentResolver resolver = getContentResolver();
        PackageManager pm = getPackageManager();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> imis = imm.getInputMethodList();

        if (resolver == null || imis == null)
            return null;

        final String currentInputMethodId = Settings.Secure.getString(resolver,
                Settings.Secure.DEFAULT_INPUT_METHOD);
        if (TextUtils.isEmpty(currentInputMethodId))
            return null;

        for (InputMethodInfo imi : imis) {
            if (currentInputMethodId.equals(imi.getId())) {
                final CharSequence imiLabel = imi.loadLabel(pm);
                final InputMethodSubtype subtype = imm
                        .getCurrentInputMethodSubtype();
                final CharSequence summary = subtype != null ? TextUtils
                        .concat(subtype.getDisplayName(this,
                                imi.getPackageName(),
                                imi.getServiceInfo().applicationInfo),
                                (TextUtils.isEmpty(imiLabel) ? "" : " - "
                                        + imiLabel)) : imiLabel;
                return summary.toString();
            }
        }
        return null;
    }

    private void getInputMethod() {
        mLastInputMethodId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodList = imm.getInputMethodList();
        int N = (mInputMethodList == null ? 0 : mInputMethodList.size());
        for (int i = 0; i < N; ++i) {
            InputMethodInfo property = mInputMethodList.get(i);
            String prefKey = property.getId();
            if (-1 != prefKey.indexOf("com.google.android.voicesearch")) {
                mInputMethodList.remove(i);
                break;
            }
        }

        N = (mInputMethodList == null ? 0 : mInputMethodList.size());
        for (int i = 0; i < N; ++i) {
            InputMethodInfo property = mInputMethodList.get(i);
            String prefKey = property.getId();
            // Log.i(TAG, mLastInputMethodId+"===+prefKey=="+prefKey);
            CharSequence label = property.loadLabel(getPackageManager());
            boolean systemIME = isSystemIme(property);
            // Add a check box.
            // Don't show the toggle if it's the only keyboard in the system, or
            // it's a system IME.
            Configuration config = this.getResources().getConfiguration();
            if (config.keyboard == Configuration.KEYBOARD_QWERTY) {
                mHaveHardKeyboard = true;
            }
            if (mHaveHardKeyboard || (N >= 1)) {
                InputMethodBean bean = new InputMethodBean(prefKey,
                        label.toString());
                mArrayList.add(bean);
                if ((prefKey != null) && (mLastInputMethodId != null)
                        && (prefKey.equals(mLastInputMethodId))) {
                    mSelection = i;
                }
            }
        }
    }

    private boolean isSystemIme(InputMethodInfo property) {
        return (property.getServiceInfo().applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private CustomInputMethodDialog customInputMethodDialog = null;

    private void setInputMethod() {

        if (customInputMethodDialog != null
                && customInputMethodDialog.isShowing()) {
            customInputMethodDialog.dismiss();
            customInputMethodDialog = null;
        }

        customInputMethodDialog = new CustomInputMethodDialog(
                LanguageAndKeyboardActivity.this, R.style.DialogTheme, mSelection);
        customInputMethodDialog.setContent(mArrayList);
        customInputMethodDialog
                .setOnClickCallBack(new CustomInputMethodDialog.OnItemClickInputMethodCallBack() {

                    @Override
                    public void OnClick(InputMethodBean bean) {


                        if (customInputMethodDialog != null
                                && customInputMethodDialog.isShowing()) {
                            customInputMethodDialog.dismiss();
                            customInputMethodDialog = null;
                        }

                        Settings.Secure.putString(getContentResolver(),
                                Settings.Secure.DEFAULT_INPUT_METHOD,
                                bean.getPrefkey());
                        if (!mHandler.hasMessages(0)) {
                            mHandler.sendEmptyMessageDelayed(0, 1000);
                        }
                    }
                });
        customInputMethodDialog.create();
        customInputMethodDialog.show();
    }

    private CustomLanguageDialog customLanguageDialog = null;

    private void setLanguage(ArrayList<Language> mLocale) {

        if (customLanguageDialog != null && customLanguageDialog.isShowing()) {
            customLanguageDialog.dismiss();
            customLanguageDialog = null;
        }

        customLanguageDialog = new CustomLanguageDialog(LanguageAndKeyboardActivity.this,
                R.style.DialogTheme);
        customLanguageDialog.setContent(mLocale);
        customLanguageDialog
                .setOnClickCallBack(new CustomLanguageDialog.OnItemClickLanguageCallBack() {

                    @Override
                    public void OnClick(Language locale) {
                        if (customLanguageDialog != null
                                && customLanguageDialog.isShowing()) {
                            customLanguageDialog.dismiss();
                            customLanguageDialog = null;
                        }
                        setLanguage(locale);
                    }
                });
        customLanguageDialog.create();
        customLanguageDialog.show();
    }

    private void setLanguage(Language locale) {
        Log.i("hxdii", "the choose locale:" + locale.getLocale().toString());

        LocalePicker.updateLocale(locale.getLocale());
        if (!mHandler.hasMessages(0)) {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }

    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    loadDefault();
                    break;

                default:
                    break;
            }
            return false;
        }
    }) ;
}