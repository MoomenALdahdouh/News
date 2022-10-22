package com.moomen.news.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    public PreferenceUtils() {
    }

    public static boolean saveEmail(String email, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_EMAIL,email);
        editor.apply();
        return true;
    }

    public static String getEmail(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.KEY_EMAIL,null);
    }

    public static boolean savePassword(String password, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_PASSWORD,password);
        editor.apply();
        return true;
    }

    public static String getPassword(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.KEY_PASSWORD,null);
    }

}
