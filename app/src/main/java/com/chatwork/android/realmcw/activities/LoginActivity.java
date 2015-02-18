package com.chatwork.android.realmcw.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chatwork.android.realmcw.utilities.ChatWorkClient;
import com.chatwork.android.realmcw.R;
import com.chatwork.android.realmcw.utilities.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class LoginActivity extends ActionBarActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @InjectView(R.id.chatwork_api_token)
    public EditText mTokenField;
    @InjectView(R.id.chatwork_login)
    public Button mLoginButton;

    /**
     * Create "LoginActivity" intent object
     *
     * @param context Context object
     * @return Intent object
     */
    public static Intent createIntent(final Context context) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mLoginButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Try ChatWork login request
     */
    @OnClick(R.id.chatwork_login)
    public void login() {
        mLoginButton.setEnabled(false);

        final String token = mTokenField.getText().toString().trim();
        if (!token.isEmpty()) {
            ChatWorkClient.listRooms(token, getApplicationContext());
        } else {
            Toast.makeText(getApplicationContext(), "Invalid ChatWork API Token", Toast.LENGTH_SHORT).show();
            mLoginButton.setEnabled(true);
        }
    }

    /**
     * Callback at ChatWorkClient.listRooms
     *
     * @param event Event object
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(final ChatWorkClient.ListRoomsEvent event) {
        if (event.isSuccess()) {
            final String token = mTokenField.getText().toString().trim();
            Utils.getSharedPreferences(getApplicationContext()).edit()
                    .putString(Utils.CW_API_TOKEN_PREFS_KEY, token)
                    .apply();

            final Intent intent = RoomListActivity.createIntent(getApplicationContext());
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Login failure", Toast.LENGTH_SHORT).show();
        }
        mLoginButton.setEnabled(true);
    }
}
