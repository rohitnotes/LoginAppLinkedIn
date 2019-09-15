package com.login.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {

    /**
     * PREFS_NAME is a file name which generates inside data folder of application
     */
    private static final String PREFS_NAME = "login_app";

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor prefEditor = null;

    private static Context mContext = null;
    public static AppSharedPreferences instance = null;

    public static AppSharedPreferences getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AppSharedPreferences();
        }
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefEditor = sharedPreferences.edit();
        return instance;
    }

    public void setAccessTokenAndExpiresDate(String accessToken,long ExpiresDate) {
        prefEditor.putString("TOKEN", accessToken);
        prefEditor.putLong("EXPIRES", ExpiresDate);
        prefEditor.commit();
    }

    public String getAccessToken() {
        return sharedPreferences.getString("TOKEN", "not found");
    }

    public long getExpiresDate() {
        return sharedPreferences.getLong("EXPIRES", 0);
    }

    public void clearData() {
        prefEditor.clear();
        prefEditor.commit();
    }
}
