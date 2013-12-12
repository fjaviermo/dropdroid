package com.fjaviermo.Utils;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public class DropDroidConfig {

    private DropDroidConfig() {}

    public static final String APP_KEY = "tre5u0zdqv0wnv8";
    public static final String APP_SECRET = "ytmc69de98qhplx";

    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), APP_KEY, APP_SECRET);
    }
}