package com.htc.spectraos.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;

import com.htc.spectraos.adapter.AppsManagerAdapter;
import com.htc.spectraos.databinding.ActivityAppsManagerBinding;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.receiver.AppCallBack;
import com.htc.spectraos.receiver.AppReceiver;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.DBUtils;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.widget.SpacesItemDecoration;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppsManagerActivity extends BaseActivity {

    private ActivityAppsManagerBinding appsManagerBinding;
    private List<ApplicationInfo> mApplications = new ArrayList<>();
    private static PackageManager mPm;

    //app
    private IntentFilter appFilter=new IntentFilter();
    private AppReceiver appReceiver=null;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
              List<AppInfoBean> infoBeans =(List<AppInfoBean>)  msg.obj;
                AppsManagerAdapter adapter = new AppsManagerAdapter(AppsManagerActivity.this,infoBeans,appsManagerBinding.appManagerRv);
                appsManagerBinding.appManagerRv.setAdapter(adapter);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsManagerBinding = ActivityAppsManagerBinding.inflate(LayoutInflater.from(this));
        setContentView(appsManagerBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        appsManagerBinding.appManagerRv.addItemDecoration(new SpacesItemDecoration(0,0,
                SpacesItemDecoration.px2dp(4),0));
        appsManagerBinding.appManagerRv.setItemAnimator(null);
        //app
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        appReceiver=new AppReceiver(appCallBack);
        registerReceiver(appReceiver, appFilter);
    }

    private void initData(){
        mPm =getPackageManager();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppInfoBean> appInfoBeans = AppUtils.getApplicationManagerMsg(AppsManagerActivity.this);
                Message message = handler.obtainMessage();
                message.what=1;
                message.obj =appInfoBeans;
                handler.sendMessage(message);
            }
        }).start();


       /* mApplications = mPm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES );
        if(mApplications.size()>0){
            Collections.sort(mApplications, Label_COMPARATOR);
            AppsManagerAdapter adapter = new AppsManagerAdapter(this,mApplications,appsManagerBinding.appManagerRv);
            appsManagerBinding.appManagerRv.setAdapter(adapter);
        }*/
    }

    AppCallBack appCallBack = new AppCallBack() {
        @Override
        public void appChange(String packageName) {
            initData();
        }

        @Override
        public void appUnInstall(String packageName) {
            String resident = ShareUtil.getInstans(AppsManagerActivity.this).getString("resident","");
            if (resident!=null && !resident.contains(packageName))
                DBUtils.getInstance(AppsManagerActivity.this).deleteFavorites(packageName);
            initData();
        }

        @Override
        public void appInstall(String packageName) {
            initData();
        }
    };


    public static final Comparator<ApplicationInfo> Label_COMPARATOR = new Comparator<ApplicationInfo>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(ApplicationInfo object1, ApplicationInfo object2) {
//            if (object1.uid < object2.uid) return 1;
//            if (object1.uid > object2.uid) return -1;
            return sCollator.compare(object1.loadLabel(mPm), object2.loadLabel(mPm));
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(appReceiver);
        super.onDestroy();
    }
}