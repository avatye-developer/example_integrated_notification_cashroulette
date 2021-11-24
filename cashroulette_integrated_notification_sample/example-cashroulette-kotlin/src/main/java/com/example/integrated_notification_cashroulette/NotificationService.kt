package com.example.integrated_notification_cashroulette

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.avatye.cashroulette.*
import com.avatye.library.support.core.log.LogTracer

class NotificationService : Service() {

    // region { ticket-field }
    var ticketBalance: String = ""
    var ticketCondition: String = ""
    var ticketDrawable: Int = 0
    // endregion

    // region { ticket-box-field }
    var ticketBoxCondition: String = ""
    var ticketBoxDrawable: Int = 0
    var pendingIntent: PendingIntent? = null
    // endregion

    // region { CashRoulette-Notification-status }
    var cashRouletteNotificationEnabled = false
    // endregion

    companion object {
        const val channelId: String = "cashroulette-test"
        private const val channelName: String = "캐시룰렛-테스트"
        private const val notificationID: Int = 903
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            registerNotification()

            CashRouletteSDK.HostNotification.registerNotificationUpdateReceiver(context = this@NotificationService, object : IUpdateNotification {
                override fun onTicketChanged() {
                    checkTicketCondition(needUpdate = true)
                }

                override fun onStatusChanged(isActive: Boolean) {
                    LogTracer.i { "NotificationService -> onStartCommand -> registerNotificationUpdateReceiver -> { isUpdateEnable failed, CashRoulette-NotificationEnabled: $isActive }" }
                    cashRouletteNotificationEnabled = isActive
                    updateNotification()
                }
            })
        }
        return START_STICKY
    }


    override fun onDestroy() {
        unregisterNotification()
        CashRouletteSDK.HostNotification.unRegisterNotificationUpdateReceiver(this@NotificationService)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogTracer.i { "NotificationService -> onBind" }
        return null
    }

    private fun registerNotification() {
        // get CashRoulette enable
        cashRouletteNotificationEnabled = CashRouletteSDK.HostNotification.getCashRouletteNotificationEnabled()
        startForeground(notificationID, makeNotificationBuilder().build())
    }

    private fun makeNotificationBuilder(): NotificationCompat.Builder {
        checkTicketCondition(needUpdate = false)

        val notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

            NotificationCompat.Builder(this, channelId)
        } else {
            NotificationCompat.Builder(this)
        }
        val remoteView = makeRemoteView()
        notifyBuilder.setSmallIcon(R.drawable.axcr_drawable_ic_cs)
        notifyBuilder.setContent(remoteView)
        notifyBuilder.setCustomContentView(remoteView)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        notifyBuilder.setContentIntent(pendingIntent)

        return notifyBuilder
    }


    private fun makeRemoteView(): RemoteViews {
        return RemoteViews(packageName, R.layout.layout_notification_custom_view).apply {
            this.setViewVisibility(
                R.id.ly_title_balance_contents_frame,
                if (cashRouletteNotificationEnabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            )
            this.setViewVisibility(
                R.id.ly_box_button_frame,
                if (cashRouletteNotificationEnabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            )
            this.setViewVisibility(
                R.id.ly_ticket_button_frame,
                if (cashRouletteNotificationEnabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            )
            this.setImageViewResource(R.id.notification_icon, R.drawable.ch_plugin_settings_30dp)
            this.setTextViewText(R.id.notification_title, getString(R.string.str_notification_title))
            this.setTextViewText(R.id.notification_contents, getString(R.string.str_notification_content))
            this.setTextViewText(R.id.notification_roulette_name, getString(R.string.str_notification_cashroulette_name))
            this.setTextViewText(R.id.notification_ticket_balance, "티켓 ${ticketBalance}장")

            // region {Ticket}
            this.setImageViewResource(R.id.notification_ticket_condition_frame, ticketDrawable)
            this.setTextViewText(R.id.notification_ticket_condition, ticketCondition)
            // endregion

            //region {TicketBox}
            this.setImageViewResource(R.id.notification_box_condition_frame, ticketBoxDrawable)
            this.setTextViewText(R.id.notification_box_condition, ticketBoxCondition)
            this.setOnClickPendingIntent(
                R.id.notification_box_condition_frame,
                pendingIntent
            )
            // endregion
        }
    }


    private fun unregisterNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }


    private fun checkTicketCondition(needUpdate: Boolean) {
        CashRouletteSDK.HostNotification.getTicketCondition(listener = object : ITicketCount {
            override fun callback(balance: Int, condition: Int, ticketImage: Int) {
                ticketBalance = balance.toString()
                ticketCondition = condition.toString()
                ticketDrawable = ticketImage
                checkTicketBoxCondition {
                    if (needUpdate) {
                        updateNotification()
                    }
                }
            }
        })
    }


    private fun checkTicketBoxCondition(updateNotification: () -> Unit) {
        CashRouletteSDK.HostNotification.getTicketBoxCondition(context = this@NotificationService, listener = object : ITicketBoxCount {
            override fun callback(condition: Int, ticketBoxImage: Int, pendingIntent: PendingIntent?) {
                ticketBoxCondition = condition.toString()
                ticketBoxDrawable = ticketBoxImage
                this@NotificationService.pendingIntent = pendingIntent
                updateNotification()
            }
        })
    }


    private fun updateNotification() {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(notificationID, makeNotificationBuilder().build())
    }
}