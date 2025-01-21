package com.htc.spectraos.manager;

import com.htc.spectraos.utils.VerifyUtil;

public class RequestManager {

    private static final RequestManager requestManager = new RequestManager();

    public static RequestManager getInstance(){
        return requestManager;
    }

    public String getSign(String body,String chanId,String time){
        String stringBuilder = "body" + body +
                "chanId" + chanId +
                "chanKey" + VerifyUtil.KEY +
                "timestamp" + time;
        return VerifyUtil.sha1(stringBuilder);
    }

    public  static boolean isOne(int num , int n){
        return (num >> (n - 1) & 1) == 1;
    }
}
