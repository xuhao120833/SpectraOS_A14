// IRequestChannelInterface.aidl
package com.htc.storeos;

// Declare any non-default types here with import statements
import com.htc.storeos.IResponseListener;

interface IRequestChannelInterface {

    void requestChannelData();
    void CheckAppsUpdate(String pkg,int verCode);
    void GoUpdate(String appsData);
    void registerListener(IResponseListener listener);
    void unregisterListener(IResponseListener listener);
}