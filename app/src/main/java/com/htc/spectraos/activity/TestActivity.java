package com.htc.spectraos.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.htc.spectraos.databinding.TestLayoutBinding;
import com.htc.spectraos.utils.LogUtils;
import com.htc.storeos.IRequestChannelInterface;
import com.htc.storeos.IResponseListener;

public class TestActivity extends Activity {

    TestLayoutBinding testLayoutBinding;
    IRequestChannelInterface iRequestChannelInterface;

    private String mAppsData =null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testLayoutBinding= TestLayoutBinding.inflate(LayoutInflater.from(this));
        setContentView(testLayoutBinding.getRoot());
        testLayoutBinding.request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iRequestChannelInterface.requestChannelData();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        testLayoutBinding.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iRequestChannelInterface.CheckAppsUpdate("com.htc.launcher",getPackageManager().getPackageInfo("com.htc.launcher",0).versionCode);
                } catch (RemoteException | PackageManager.NameNotFoundException e ) {
                    throw new RuntimeException(e);
                }
            }
        });


        testLayoutBinding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAppsData!=null) {
                    try {
                        iRequestChannelInterface.GoUpdate(mAppsData);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        Intent intent = new Intent("com.htc.requesetService");
        intent.setPackage("com.htc.storeos");
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    IResponseListener responseListener = new IResponseListener.Stub() {
        @Override
        public void responseChannelData(String channelData) throws RemoteException {
            LogUtils.d("responseChannelData ----"+channelData);
        }

        @Override
        public void responseCheckAppsUpdate(String appsData) throws RemoteException {
            LogUtils.d("appsData "+appsData);
            mAppsData = appsData;
        }

    };


    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iRequestChannelInterface = IRequestChannelInterface.Stub.asInterface(service);
            try {
                iRequestChannelInterface.registerListener(responseListener);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iRequestChannelInterface =null;
        }
    };

}
