package com.chatwork.android.realmcw.utilities;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.chatwork.android.realmcw.models.Room;
import com.chatwork.android.realmcw.models.RoomAA;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;

public class ChatWorkClient {
    private static final String TAG = ChatWorkClient.class.getSimpleName();
    private static final String API_ENDPOINT = "https://api.chatwork.com/v1";
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    @SuppressWarnings("unused")
    private static final Gson GSON = new GsonBuilder().
            setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaringClass().equals(RealmObject.class);
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    /**
     * Get chat rooms data from ChatWork API
     *
     * @param token   ChatWork API token text
     * @param context Context object
     * @param update  Update room data = true, Initialize room data = false
     */
    public static void listRooms(final String token, final Context context, final boolean update) {
        final Request request = new Request.Builder()
                .url(API_ENDPOINT + "/rooms")
                .header("X-ChatWorkToken", token)
                .get()
                .build();

        OK_HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new ListRoomsEvent(false));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseText = response.body().string();
                final JsonArray jsonArray = new JsonParser().parse(responseText).getAsJsonArray();

                if (update) {
                    createRoomsWithRealm(jsonArray, context, true);
                } else {
                    createRoomsWithRealm(jsonArray, context, false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            createRoomsWithActiveAndroid(jsonArray, context);
                        }
                    }).start();
                }

                EventBus.getDefault().post(new ListRoomsEvent(true));
            }
        });
    }

    /**
     * Send message to specified chat room
     *
     * @param token   ChatWork API token text
     * @param roomId  Specified chat room id
     * @param message Send message
     */
    @SuppressWarnings("StringBufferReplaceableByString")
    public static void sendMessage(final String token, final long roomId, final String message) {
        final RequestBody requestBody = new FormEncodingBuilder().add("body", message).build();
        final Request request = new Request.Builder()
                .url(new StringBuilder().append(API_ENDPOINT).append("/rooms/").append(roomId).append("/messages").toString())
                .header("X-ChatWorkToken", token)
                .post(requestBody)
                .build();

        OK_HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

    /**
     * Get chat rooms data from ChatWork API
     *
     * @param token   ChatWork API token text
     * @param context Context object
     */
    public static void listRooms(final String token, final Context context) {
        listRooms(token, context, false);
    }

    /**
     * Create chat room model with realm
     *
     * @param jsonArray Rooms json array
     * @param context   Context object
     * @param update    Initialize = true, Not initialize = false
     */
    private static void createRoomsWithRealm(final JsonArray jsonArray, final Context context, final boolean update) {
        if (!update) {
            Realm.deleteRealmFile(context);
        }
        final Realm realm = Realm.getInstance(context);
        final long startTime = System.nanoTime();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (final JsonElement jsonElement : jsonArray) {
                    final JsonObject jsonObject = jsonElement.getAsJsonObject();
                    // final Room room = GSON.fromJson(jsonObject, Room.class);
                    final Room room = new Room();
                    room.setRoomId(jsonObject.get("room_id").getAsLong());
                    room.setName(jsonObject.get("name").getAsString());
                    room.setType(jsonObject.get("type").getAsString());
                    room.setRole(jsonObject.get("role").getAsString());
                    room.setSticky(jsonObject.get("sticky").getAsBoolean());
                    room.setUnreadNum(jsonObject.get("unread_num").getAsLong());
                    room.setMentionNum(jsonObject.get("mention_num").getAsLong());
                    room.setMytaskNum(jsonObject.get("mytask_num").getAsLong());
                    room.setMessageNum(jsonObject.get("message_num").getAsLong());
                    room.setFileNum(jsonObject.get("file_num").getAsLong());
                    room.setTaskNum(jsonObject.get("task_num").getAsLong());
                    room.setIconPath(jsonObject.get("icon_path").getAsString());
                    room.setLastUpdateTime(jsonObject.get("last_update_time").getAsLong());
                    realm.copyToRealmOrUpdate(room);
                }
            }
        });
        final long endTime = System.nanoTime();
        Log.d(TAG, "Realm object processing time => " + ((endTime - startTime) / 1000000) + " [ms]");
        Log.d(TAG, "Saved object #" + realm.allObjects(Room.class).size());
        realm.close();
    }

    /**
     * Create chat room model with ActiveAndroid
     *
     * @param jsonArray Rooms json array
     * @param context   Context object
     */
    private static void createRoomsWithActiveAndroid(final JsonArray jsonArray, final Context context) {
        new Delete().from(RoomAA.class).execute();
        ActiveAndroid.execSQL("DELETE FROM sqlite_sequence WHERE name='Rooms'");
        ActiveAndroid.execSQL("VACUUM");
        final long startTime = System.nanoTime();
        try {
            ActiveAndroid.beginTransaction();
            for (final JsonElement jsonElement : jsonArray) {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                final RoomAA roomAA = new RoomAA();
                roomAA.setRoomId(jsonObject.get("room_id").getAsLong());
                roomAA.setName(jsonObject.get("name").getAsString());
                roomAA.setType(jsonObject.get("type").getAsString());
                roomAA.setRole(jsonObject.get("role").getAsString());
                roomAA.setSticky(jsonObject.get("sticky").getAsBoolean());
                roomAA.setUnreadNum(jsonObject.get("unread_num").getAsLong());
                roomAA.setMentionNum(jsonObject.get("mention_num").getAsLong());
                roomAA.setMytaskNum(jsonObject.get("mytask_num").getAsLong());
                roomAA.setMessageNum(jsonObject.get("message_num").getAsLong());
                roomAA.setFileNum(jsonObject.get("file_num").getAsLong());
                roomAA.setTaskNum(jsonObject.get("task_num").getAsLong());
                roomAA.setIconPath(jsonObject.get("icon_path").getAsString());
                roomAA.setLastUpdateTime(jsonObject.get("last_update_time").getAsLong());
                roomAA.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        final long endTime = System.nanoTime();
        Log.d(TAG, "ActiveAndroid object processing time => " + ((endTime - startTime) / 1000000) + " [ms]");
        Log.d(TAG, "Saved object #" + new Select().from(RoomAA.class).count());
    }

    public static class ListRoomsEvent {
        private boolean mSuccess;

        public ListRoomsEvent(boolean success) {
            mSuccess = success;
        }

        public boolean isSuccess() {
            return mSuccess;
        }
    }
}
