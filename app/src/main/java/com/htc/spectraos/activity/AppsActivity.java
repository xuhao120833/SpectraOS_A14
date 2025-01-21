package com.htc.spectraos.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;

import com.htc.spectraos.R;
import com.htc.spectraos.adapter.AppsAdapter;
import com.htc.spectraos.databinding.ActivityAppsBinding;
import com.htc.spectraos.entry.AppInfoBean;
import com.htc.spectraos.receiver.AppCallBack;
import com.htc.spectraos.receiver.AppReceiver;
import com.htc.spectraos.utils.AppUtils;
import com.htc.spectraos.utils.DBUtils;
import com.htc.spectraos.utils.ShareUtil;
import com.htc.spectraos.widget.SpacesItemDecoration;

import java.util.List;

public class AppsActivity extends BaseActivity {

    private ActivityAppsBinding appsBinding;

    //app
    private IntentFilter appFilter=new IntentFilter();
    private AppReceiver appReceiver=null;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                List<AppInfoBean> infoBeans =(List<AppInfoBean>)  msg.obj;
                AppsAdapter appsAdapter = new AppsAdapter(AppsActivity.this,infoBeans,appsBinding.appsRv);
                appsBinding.appsRv.setAdapter(appsAdapter);
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsBinding = ActivityAppsBinding.inflate(LayoutInflater.from(this));
        setContentView(appsBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){

        GridLayoutManager layoutManager = new GridLayoutManager(this,6);
        appsBinding.appsRv.setLayoutManager(layoutManager);
        appsBinding.appsRv.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10),SpacesItemDecoration.px2dp(10)));
        //app
        appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appFilter.addDataScheme("package");
        appReceiver=new AppReceiver(appCallBack);
        registerReceiver(appReceiver, appFilter);

    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppInfoBean> appInfoBeans = AppUtils.getApplicationMsg(AppsActivity.this);
                Message message = handler.obtainMessage();
                message.what=1;
                message.obj =appInfoBeans;
                handler.sendMessage(message);
            }
        }).start();
    }

    AppCallBack appCallBack = new AppCallBack() {
        @Override
        public void appChange(String packageName) {
            initData();
        }

        @Override
        public void appUnInstall(String packageName) {
            String resident = ShareUtil.getInstans(AppsActivity.this).getString("resident","");
            if (resident!=null && !resident.contains(packageName))
                DBUtils.getInstance(AppsActivity.this).deleteFavorites(packageName);
            initData();
        }

        @Override
        public void appInstall(String packageName) {
            initData();
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(appReceiver);
        super.onDestroy();
    }
}