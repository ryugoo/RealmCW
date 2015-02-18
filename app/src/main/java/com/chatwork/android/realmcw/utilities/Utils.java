package com.chatwork.android.realmcw.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import io.realm.Realm;

public class Utils {
    public static final String CW_API_TOKEN_PREFS_KEY = "CW_API_TOKEN";
    private static final String PREFS_NAME = "prefs";

    private Utils() {
    }

    /**
     * Check authorize
     *
     * @param context Context object
     * @return Authorized = true, Not authorized = false
     */
    public static boolean authorized(final Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(CW_API_TOKEN_PREFS_KEY);
    }

    /**
     * Get "SharedPreferences" object
     *
     * @param context Context object
     * @return Application standard SharedPreferences object
     */
    public static SharedPreferences getSharedPreferences(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Prepare logout
     *
     * @param context Context object
     */
    public static void unAuthorize(final Context context) {
        Realm.deleteRealmFile(context);
        getSharedPreferences(context).edit().clear().apply();
    }
}
