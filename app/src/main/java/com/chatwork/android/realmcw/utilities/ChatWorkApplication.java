package com.chatwork.android.realmcw.utilities;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.chatwork.android.realmcw.models.RoomAA;

public class ChatWorkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE);

        final Configuration configuration = new Configuration.Builder(getApplicationContext())
                .addModelClass(RoomAA.class)
                .setCacheSize(1024 * 1024 * 8)
                .setSqlParser(Configuration.SQL_PARSER_DELIMITED)
                .create();
        ActiveAndroid.initialize(configuration, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
