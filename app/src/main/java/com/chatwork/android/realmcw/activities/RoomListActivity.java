package com.chatwork.android.realmcw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatwork.android.realmcw.R;
import com.chatwork.android.realmcw.models.Room;
import com.chatwork.android.realmcw.utilities.ChatWorkClient;
import com.chatwork.android.realmcw.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RoomListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
    private static final String TAG = RoomListActivity.class.getSimpleName();
    private static final String GET_ON_LOAD = "GET_ON_LOAD";
    private static final int SEND_MESSAGE_REQUEST = 200;
    @InjectView(R.id.room_list_layout)
    public SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.room_list)
    public ListView mListView;

    private Realm mRealm;
    private RoomAdapter mAdapter;


    /**
     * Create "RoomListActivity" intent object
     *
     * @param context Context object
     * @return Intent object
     */
    public static Intent createIntent(final Context context) {
        return createIntent(context, false);
    }

    /**
     * Create "RoomListActivity" intent object
     *
     * @param context   Context object
     * @param getOnLoad Get room list data on load
     * @return Intent object
     */
    public static Intent createIntent(final Context context, final boolean getOnLoad) {
        final Intent intent = new Intent(context, RoomListActivity.class);
        intent.putExtra(GET_ON_LOAD, getOnLoad);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        ButterKnife.inject(this);
        mRealm = Realm.getInstance(this);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.black, R.color.tan, R.color.cream);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Super easy to sort!
        final RealmResults<Room> results = mRealm.allObjectsSorted(Room.class, "sticky", false, "lastUpdateTime", false);
        mAdapter = new RoomAdapter(getApplicationContext(), results, true); // Auto update
        mListView.setAdapter(mAdapter);

        final boolean getOnLoad = getIntent().getBooleanExtra(GET_ON_LOAD, false);
        if (getOnLoad) {
            mSwipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwipeRefreshLayout.setOnRefreshListener(null);
        if (mRealm != null) {
            mRealm.close();
        }
    }

    @Override
    public void onRefresh() {
        final String token = Utils.getSharedPreferences(getApplicationContext()).getString(Utils.CW_API_TOKEN_PREFS_KEY, "");
        if (!token.isEmpty()) {
            ChatWorkClient.listRooms(token, getApplicationContext(), true);
        } else {
            Log.d(TAG, "ChatWork API Token is empty");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_list, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
            return true;
        } else if (id == R.id.action_logout) {
            mRealm.close();
            Utils.unAuthorize(getApplicationContext());
            startActivity(LoginActivity.createIntent(getApplicationContext()));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        } else if (id == R.id.action_license) {
            startActivity(LicenseActivity.createIntent(getApplicationContext()));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @OnItemClick(R.id.room_list)
    public void onItemClick(final int position) {
        final Room room = (Room) mListView.getAdapter().getItem(position);
        final long roomId = room.getRoomId();

        final EditTextDialog dialog = EditTextDialog.newInstance(roomId);
        if (!dialog.isAdded()) {
            dialog.setTargetFragment(null, SEND_MESSAGE_REQUEST);
            dialog.show(getSupportFragmentManager(), TAG);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final ChatWorkClient.ListRoomsEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_MESSAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                final long roomId = data.getLongExtra("ROOM_ID", 0);
                final String sendMessage = data.getStringExtra("SEND_MESSAGE");
                final String token = Utils.getSharedPreferences(getApplicationContext()).getString(Utils.CW_API_TOKEN_PREFS_KEY, "");
                if (token != null) {
                    ChatWorkClient.sendMessage(token, roomId, sendMessage);
                } else {
                    Log.d(TAG, "ChatWork API Token is empty");
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Input message is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(final String text) {
        RealmQuery<Room> query = mRealm.where(Room.class);
        final String[] texts = text.split("\\s|　");
        final RealmResults<Room> results;
        if (texts.length == 1) {
            results = query.contains("name", text.trim(), RealmQuery.CASE_INSENSITIVE).findAllSorted("sticky", false, "lastUpdateTime", false);
        } else {
            query = query.beginGroup();
            for (final String _text : texts) {
                query = query.contains("name", _text.trim(), RealmQuery.CASE_INSENSITIVE);
            }
            results = query.endGroup().findAllSorted("sticky", false, "lastUpdateTime", false);
        }
        if (mAdapter != null) {
            mAdapter.updateRealmResults(results);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String text) {
        if (text.isEmpty() && mAdapter != null) {
            final RealmResults<Room> results = mRealm.where(Room.class).contains("name", text.trim(), RealmQuery.CASE_INSENSITIVE).findAllSorted("sticky", false, "lastUpdateTime", false);
            mAdapter.updateRealmResults(results);
        }
        return false;
    }

    public static class RoomAdapter extends RealmBaseAdapter<Room> implements ListAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private HashSet<Long> mUnreadRooms;

        public RoomAdapter(final Context context, final RealmResults<Room> realmResults, final boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mUnreadRooms = new HashSet<>();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Room room = realmResults.get(position);
            final long roomId = room.getRoomId();
            if (room.getUnreadNum() > 0) {
                mUnreadRooms.add(roomId);
            } else {
                mUnreadRooms.remove(roomId);
            }

            final RoomViewHolder roomViewHolder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_room, new RelativeLayout(mContext));
                roomViewHolder = new RoomViewHolder(view);
                view.setTag(roomViewHolder);
            } else {
                roomViewHolder = (RoomViewHolder) view.getTag();
            }

            final TextView textView = roomViewHolder.mRoomName;
            final TextView unreadNumView = roomViewHolder.mRoomUnreadNum;
            textView.setText(room.getName());
            Picasso.with(mContext).load(room.getIconPath()).into(roomViewHolder.mRoomIcon);

            if (mUnreadRooms.contains(roomId)) {
                textView.setTypeface(null, Typeface.BOLD);
                final long unreadNum = room.getUnreadNum();
                if (unreadNum >= 10) {
                    unreadNumView.setText("+");
                } else {
                    unreadNumView.setText(String.valueOf(unreadNum));
                }
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
                unreadNumView.setText("");
            }

            return view;
        }

        public static class RoomViewHolder {
            @InjectView(R.id.room_icon)
            public ImageView mRoomIcon;
            @InjectView(R.id.room_name)
            public TextView mRoomName;
            @InjectView(R.id.room_unread_num)
            public TextView mRoomUnreadNum;

            public RoomViewHolder(final View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

    public static class EditTextDialog extends DialogFragment {
        public static EditTextDialog newInstance(final long roomId) {
            final EditTextDialog dialog = new EditTextDialog();
            Bundle bundle = dialog.getArguments();
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putLong("ROOM_ID", roomId);
            dialog.setArguments(bundle);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity().getApplicationContext();
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.dialog_edittext, new RelativeLayout(context), true);
            final EditText editText = ButterKnife.findById(view, R.id.send_message);
            return new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .setPositiveButton("Send", (dialog, which) -> {
                        final long roomId = getArguments().getLong("ROOM_ID");
                        final String message = editText.getText().toString();
                        final Intent intent = new Intent();
                        final int sendCode;
                        if (message.trim().length() != 0) {
                            intent.putExtra("ROOM_ID", roomId);
                            intent.putExtra("SEND_MESSAGE", message);
                            sendCode = Activity.RESULT_OK;
                        } else {
                            sendCode = Activity.RESULT_CANCELED;
                        }
                        final PendingIntent pendingIntent = getActivity().createPendingResult(getTargetRequestCode(), intent, PendingIntent.FLAG_ONE_SHOT);
                        try {
                            pendingIntent.send(sendCode);
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
        }
    }
}
