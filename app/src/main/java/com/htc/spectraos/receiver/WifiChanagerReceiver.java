package com.htc.spectraos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.htc.spectraos.R;

/**
 * @author  作者：zgr
 * @version 创建时间：2017年3月28日 上午11:50:44
 * 类说明 wifi变化广播  更新wifi列表
 */
public class WifiChanagerReceiver extends BroadcastReceiver {

	private String TAG="WifiChanagerReceiver";
	
	public interface WifiChanagerCallBack{
		public void refreshWifi();
		public void wifiStatueChange(int state);
		void WifiConnectOrLose();
	}
	
	private WifiChanagerCallBack mcallback;
	
	public WifiChanagerReceiver(WifiChanagerCallBack callback){
		this.mcallback=callback;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		if(action!=null){
			Log.i("hxdwifi"," onReceive :" +action);
			if(action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)){
				//请求的连接已经建立或者丢失
				mcallback.refreshWifi();
			}else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if ( null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					NetworkInfo.State state = networkInfo.getState();

					if (state==NetworkInfo.State.CONNECTED){
						mcallback.wifiStatueChange(2);
					}else if (state==NetworkInfo.State.DISCONNECTED){
						mcallback.wifiStatueChange(0);
					}else if (state==NetworkInfo.State.CONNECTING){
						mcallback.wifiStatueChange(1);
					}
				}

			}else if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
				//请求连接的状态发生改变，（已经加入到一个接入点）
				int supl_error=intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
				Log.d("wifi","supl_error "+supl_error);
				if (supl_error == WifiManager.ERROR_AUTHENTICATING ) {
					//ToastUtil.showShortToast(context, context.getString(R.string.Authentication_error));
				}
				mcallback.refreshWifi();
			}else if(action.equals(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION)){

				//已经添加到配置的网络发生改变
				mcallback.refreshWifi();
			}else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				//WiFi扫描完成，可以调用mWifiManager.getScanResults()
				mcallback.refreshWifi();

			}else if(action.equals(WifiManager.RSSI_CHANGED_ACTION)){

			}else if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
				//wifi连接网络状态变化
				mcallback.refreshWifi();
			}
			
		}
	}

}
