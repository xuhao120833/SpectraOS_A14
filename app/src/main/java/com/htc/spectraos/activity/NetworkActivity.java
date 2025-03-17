package com.htc.spectraos.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.spectraos.MyApplication;
import com.htc.spectraos.R;
import com.htc.spectraos.databinding.ActivityNetworkBinding;

public class NetworkActivity extends BaseActivity {

    ActivityNetworkBinding networkBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkBinding = ActivityNetworkBinding.inflate(LayoutInflater.from(this));
        setContentView(networkBinding.getRoot());
        initView();
    }
    private void initView(){
        networkBinding.rlWirelessNetwork.setOnClickListener(this);
        networkBinding.rlHotspot.setOnClickListener(this);
        networkBinding.rlWiredNetwork.setOnClickListener(this);

        networkBinding.rlWirelessNetwork.setOnHoverListener(this);
        networkBinding.rlHotspot.setOnHoverListener(this);
        networkBinding.rlWiredNetwork.setOnHoverListener(this);

        networkBinding.rlWirelessNetwork.setVisibility(MyApplication.config.wifi?View.VISIBLE:View.GONE);
        networkBinding.rlHotspot.setVisibility(MyApplication.config.hotpots?View.VISIBLE:View.GONE);

        networkBinding.rlWirelessNetwork.requestFocus();
        networkBinding.rlWirelessNetwork.requestFocusFromTouch();
        if (isNetworkConnect()){
            networkBinding.rlWiredNetwork.setVisibility(View.VISIBLE);
        }else {

            networkBinding.rlWiredNetwork.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkConnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return networkInfo!=null&& networkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_wireless_network){
            startNewActivityWifi(WifiActivity.class);
        }else if (v.getId() == R.id.rl_wired_network){
            startNewActivity(WiredActivity.class);
        }else if (v.getId() == R.id.rl_hotspot){
            startNewActivity(HotspotActivity.class);
        }
    }
}