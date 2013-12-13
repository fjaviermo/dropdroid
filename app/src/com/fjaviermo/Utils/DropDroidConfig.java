package com.fjaviermo.Utils;

import android.content.Context;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;

public class DropDroidConfig {

    private DropDroidConfig() {}

    public static final String APP_KEY = "tre5u0zdqv0wnv8";
    public static final String APP_SECRET = "ytmc69de98qhplx";

    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), APP_KEY, APP_SECRET);
    }
    
	public static DbxFileSystem getDbxFileSystem(DbxAccountManager accountManager) {
		DbxAccount account = accountManager.getLinkedAccount();
		if (account != null) {
			try {
				return DbxFileSystem.forAccount(account);
			} catch (DbxException.Unauthorized e) {
				// Account was unlinked asynchronously from server.
				return null;
			}
		}
		return null;
	}
}