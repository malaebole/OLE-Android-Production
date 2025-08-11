package ae.oleapp.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import ae.oleapp.BuildConfig;
import ae.oleapp.R;
import ae.oleapp.activities.NotificationsActivityLineup;
import ae.oleapp.activities.OleNotificationsActivity;
import ae.oleapp.player.OleChatActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> stringMap = remoteMessage.getData();
        String notType = stringMap.get("type");
        String bookingId = stringMap.get("booking_id");
        String clubId = stringMap.get("club_id");
        String bookingType = stringMap.get("booking_type");
        String isRated = stringMap.get("is_rated");

        if (Functions.getPrefValue(getApplicationContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            String gameId = stringMap.get("game_id");
            Intent intent = new Intent("receive_push");
            intent.putExtra("type", notType);
            intent.putExtra("game_id", gameId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            sendMyNotification(remoteMessage.getNotification().getBody(), notType, gameId);

            if (notType.equalsIgnoreCase("lineupGameRemoved")
//                   || notType.equalsIgnoreCase("oleUserAddedAsFriend") //Remove these type so you can receive notification body
                    // || notType.equalsIgnoreCase("lineupGameAdded")
//                    || notType.equalsIgnoreCase("oleUserRemovedAsFriend")
//                    || notType.equalsIgnoreCase("lineupEmployeeAdded")
//                    || notType.equalsIgnoreCase("lineupEmployeeRemoved")
                    || notType.equalsIgnoreCase("lineupGameEnd")) {
            } else {
                sendMyNotification(remoteMessage.getNotification().getBody(), notType, gameId);
            }
        } else {
            if (Functions.getPrefValue(this, Constants.kIsSignIn).equalsIgnoreCase("1")) {
                moveToRatingScreen(notType, bookingId, clubId, bookingType, isRated);

                if (notType.equalsIgnoreCase("newMessage")) {
                    Intent intent = new Intent("receive_new_msg");
                    intent.putExtra("booking_id", bookingId);
                    intent.putExtra("receiver_id", stringMap.get("receiver_id"));
                    intent.putExtra("sender_id", stringMap.get("sender_id"));
                    intent.putExtra("date", stringMap.get("date"));
                    intent.putExtra("message_id", stringMap.get("message_id"));
                    intent.putExtra("message_text", stringMap.get("message_text"));
                    intent.putExtra("sender_name", stringMap.get("sender_name"));
                    intent.putExtra("sender_photo", stringMap.get("sender_photo"));
                    intent.putExtra("sender_ranking", stringMap.get("sender_ranking"));
                    intent.putExtra("team_name", stringMap.get("team_name"));
                    intent.putExtra("unread_chat_count", stringMap.get("unread_chat_count"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
            Intent intent = new Intent("receive_push");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            sendMyNotification(remoteMessage.getNotification().getBody(), notType, bookingId);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.kFCMToken, s);
        editor.apply();
        if (Functions.getPrefValue(this, Constants.kIsSignIn).equalsIgnoreCase("1")) {
            sendFcmTokenApi(s);
        }
    }

    private void moveToRatingScreen(String notType, String bookingId, String clubId, String bookingType, String isRated) {
        if (notType.equalsIgnoreCase(Constants.kBookingCompleteNotification) && !bookingId.isEmpty()) {
//            Intent intent = new Intent(this, PlayerMainTabsActivity.class);
            Intent intent = new Intent("move_to_rating");
            intent.putExtra("type", notType);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("club_id", clubId);
            intent.putExtra("booking_type", bookingType);
            intent.putExtra("is_rated", isRated);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    }

    private void sendMyNotification(String message, String notType, String bookingId) {
        if (Functions.getPrefValue(getApplicationContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kLineupModule)) {
            Intent intent1 = new Intent(getApplicationContext(), NotificationsActivityLineup.class);
            intent1.putExtra("from_notif", true);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);

            Uri soundUri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String NOTIFICATION_CHANNEL_ID1 = "my_channel_id_01";

            NotificationManager notificationManager1 =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID1, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationManager1.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder1 = new NotificationCompat.Builder(this, "my_channel_id_01")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 500, 500)
                    .setSound(soundUri1);
            notificationBuilder1.setContentIntent(pendingIntent1);
            notificationManager1.notify(1, notificationBuilder1.build());
        } else {
            //On click of notification it redirect to this Activity
            Intent intent = new Intent(getApplicationContext(), OleNotificationsActivity.class);
            intent.putExtra("from_notif", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "my_channel_id_01")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setLights(Color.RED, 500, 500)
                    .setSound(soundUri);
            if (Functions.getPrefValue(this, Constants.kIsSignIn).equalsIgnoreCase("1")) {
                if (notType.equalsIgnoreCase("newMessage")) {
                    Intent chatIntent = new Intent(this, OleChatActivity.class);
                    chatIntent.putExtra("from_notif", true);
                    chatIntent.putExtra("booking_id", bookingId);
                    PendingIntent chatPintent = PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_IMMUTABLE);
                    notificationBuilder.setContentIntent(chatPintent);
                } else {
                    notificationBuilder.setContentIntent(pendingIntent);
                }
            }

            notificationManager.notify(1, notificationBuilder.build());
        }

    }

    private void sendFcmTokenApi(String token) {
        String userId = Functions.getPrefValue(this, Constants.kUserID);
        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.sendFcmToken(Functions.getAppLang(this), userId, uniqueID, "android", token, BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
