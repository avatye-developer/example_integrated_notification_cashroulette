package com.example.example_cashroulette_java;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.avatye.cashroulette.ITicketCount;


public class NotificationService extends Service {
    public static final String NAME = NotificationService.class.getSimpleName();

    public static String channelId = "partner_notification_test";
    public static String channelName = "파트너사앱-알림창-상태바";
    public static int notificationID = 901;

    // region { ticket-field }
    int ticketBalance = 0;
    int ticketCondition = 0;
    // endregion

    // region { ticket-box-field }
    int ticketBoxCondition = 0;
    PendingIntent ticketBoxPendingIntent = null;
    // endregion

    // region { CashRoulette-Notification-status }
    Boolean cashRouletteNotificationEnabled = false;
    // endregion


    interface IBoxCondition {
        void update();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(NAME, NAME + "onBind");
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNotification();
        NotificationIntegrationSDK.unRegisterNotificationUpdateReceiver(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            registerNotification();

            NotificationIntegrationSDK.registerNotificationUpdateReceiver(this, new IUpdateNotification() {
                @Override
                public void onTicketChanged() {
                    checkTicketCondition(true);
                }

                @Override
                public void onStatusChanged(boolean isActive) {
                    cashRouletteNotificationEnabled = isActive;
                    updateNotification();
                }
            });
        }

        return START_STICKY;
    }


    private void registerNotification() {
        // get CashRoulette enable
        cashRouletteNotificationEnabled = NotificationIntegrationSDK.getSDKNotificationEnabled()
        startForeground(notificationID, makeNotificationBuilder().build());
    }


    private NotificationCompat.Builder makeNotificationBuilder() {
        final NotificationCompat.Builder notifyBuilder;

        checkTicketCondition(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            notifyBuilder = new NotificationCompat.Builder(this, channelId);
        } else {
            notifyBuilder = new NotificationCompat.Builder(this);
        }


        RemoteViews remoteView = makeRemoteView();
        notifyBuilder.setSmallIcon(R.drawable.axcr_drawable_ic_cs);
        notifyBuilder.setContent(remoteView);
        notifyBuilder.setCustomContentView(remoteView);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);

        return notifyBuilder;
    }


    private RemoteViews makeRemoteView() {
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.layout_notification_custom_view);
        rv.setViewVisibility(
                R.id.ly_title_balance_contents_frame,
                cashRouletteNotificationEnabled ? View.VISIBLE : View.GONE
        );
        rv.setViewVisibility(
                R.id.ly_box_button_frame,
                cashRouletteNotificationEnabled ? View.VISIBLE : View.GONE
        );
        rv.setViewVisibility(
                R.id.ly_ticket_button_frame,
                cashRouletteNotificationEnabled ? View.VISIBLE : View.GONE
        );
        rv.setImageViewResource(R.id.notification_icon, R.drawable.ch_plugin_settings_30dp);
        rv.setTextViewText(R.id.notification_title, getString(R.string.str_notification_title));
        rv.setTextViewText(R.id.notification_contents, getString(R.string.str_notification_content));
        rv.setTextViewText(R.id.notification_roulette_name, getString(R.string.str_notification_cashroulette_name));
        rv.setTextViewText(R.id.notification_ticket_balance, "티켓 " + ticketBalance + "장");

        // region {Ticket}
        rv.setImageViewResource(
                R.id.notification_ticket_condition_frame,
                ticketCondition > 0 ? R.drawable.axcr_drawable_notification_ticket_condition_frame_on : R.drawable.axcr_drawable_notification_ticket_condition_frame_off
        );
        rv.setTextViewText(R.id.notification_ticket_condition, String.valueOf(ticketCondition));
        // endregion

        //region {TicketBox}
        rv.setImageViewResource(
                R.id.notification_box_condition_frame,
                ticketBoxCondition > 0 ? R.drawable.axcr_drawable_notification_box_condition_frame_on : R.drawable.axcr_drawable_notification_box_condition_frame_off
        );
        rv.setTextViewText(R.id.notification_box_condition, String.valueOf(ticketBoxCondition));
        rv.setOnClickPendingIntent(R.id.notification_box_condition_frame, ticketBoxPendingIntent);
        // endregion

        return rv;
    }


    private void checkTicketCondition(Boolean needUpdate) {
        NotificationIntegrationSDK.getTicketCondition(new ITicketCount() {
            @Override
            public void callback(int balance, int condition) {
                ticketBalance = balance;
                ticketCondition = condition;
                checkTicketBoxCondition(new IBoxCondition() {
                    @Override
                    public void update() {
                        if (needUpdate) {
                            updateNotification();
                        }
                    }
                });
            }
        });
    }

    private void checkTicketBoxCondition(IBoxCondition iBoxCondition) {
        NotificationIntegrationSDK.getTicketBoxCondition(new ITicketBoxCount() {
            @Override
            public void callback(int condition, @Nullable PendingIntent pendingIntent) {
                ticketBoxCondition = condition;
                ticketBoxPendingIntent = pendingIntent;
                iBoxCondition.update();
            }
        });
    }

    private void updateNotification() {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(notificationID, makeNotificationBuilder().build());
    }

    private void unregisterNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopSelf();
        }
    }

}
