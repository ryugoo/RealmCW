package com.chatwork.android.realmcw.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.chatwork.android.realmcw.R;
import com.chatwork.android.realmcw.utilities.Utils;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final boolean authorized = Utils.authorized(getApplicationContext());
        final Intent intent;
        if (authorized) {
            Log.d(TAG, "Already authorized and start initialize");
            intent = RoomListActivity.createIntent(getApplicationContext(), true);
        } else {
            Log.d(TAG, "Not authorize and start login activity");
            intent = LoginActivity.createIntent(getApplicationContext());
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
