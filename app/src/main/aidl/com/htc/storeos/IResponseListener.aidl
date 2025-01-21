// ResponseListener.aidl
package com.htc.storeos;


interface IResponseListener {

    void responseChannelData(String channelData);
    void responseCheckAppsUpdate(String appsData);
}