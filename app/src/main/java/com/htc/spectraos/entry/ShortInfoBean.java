package com.htc.spectraos.entry;

import android.graphics.drawable.Drawable;

public class ShortInfoBean {
    String packageName;
    private String appname;
    private Drawable appicon;
    private String path;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppname() {
        return appname;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
