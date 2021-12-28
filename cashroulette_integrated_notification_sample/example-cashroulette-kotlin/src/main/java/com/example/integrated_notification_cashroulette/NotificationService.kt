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
    var ticketBalance: Int = 0
    var ticketCondition: Int = 0
    // endregion

    // region { ticket-box-field }
    var ticketBoxCondition: Int = 0
    var ticketBoxPendingIntent: PendingIntent? = null
    // endregion

    // region { CashRoulette-Notification-status }
    var cashRouletteNotificationEnabled = false
    // endregion

    companion object {
        const val channelId: String = "partner_notification_test"
        private const val channelName: String = "파트너사앱-알림창-상태바"
        private const val notificationID: Int = 901
    }

    override fun onCreate() {
        super.onCreate()
        /**
         * 티켓박스 PendingIntent를 반환합니다.
         * */
        this.ticketBoxPendingIntent = NotificationIntegrationSDK.getTicketBoxPendingIntent(context = this@NotificationService)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            /** Init Service */
            registerNotification()

            /**
             * 파트너사의 '알림창 상태바'에 표시되는 소유티켓수량, 받을 수 있는 티켓, 박스의 수량, 캐시룰렛 '알림창 상태바' 상태값을 이벤트로 전달 합니다.
             * 이벤트를 통해 전달되는 값을 통해 '알림창 상태바'의 정보를 갱신 할 수 있습니다.
             * 사용중인 '알림창 상태바' 서비스에 등록 합니다.
             */
            NotificationIntegrationSDK.registerNotificationUpdateReceiver(context = this@NotificationService, object : IUpdateNotification {
                override fun onTicketChanged() {
                    LogTracer.i { "NotificationService -> onStartCommand -> registerNotificationUpdateReceiver -> onTicketChanged" }
                    checkTicketCondition(needUpdate = true)
                }

                override fun onStatusChanged(isActive: Boolean) {
                    LogTracer.i { "NotificationService -> onStartCommand -> registerNotificationUpdateReceiver -> onStatusChanged { CashRoulette-NotificationEnabled: $isActive }" }
                    cashRouletteNotificationEnabled = isActive
                    updateNotification()
                }
            })
        }
        return START_STICKY
    }


    override fun onDestroy() {
        unregisterNotification()

        /** 등록된 리시버를 해제합니다. */
        NotificationIntegrationSDK.unregisterNotificationUpdateReceiver(this@NotificationService)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogTracer.i { "NotificationService -> onBind" }
        return null
    }

    private fun registerNotification() {
        /** 캐시룰렛의 '알림창 상태바' 사용 여부를 반환 합니다.*/
        cashRouletteNotificationEnabled = NotificationIntegrationSDK.getSDKNotificationEnabled()

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
            this.setImageViewResource(
                R.id.notification_ticket_condition_frame,
                if (ticketCondition > 0) {
                    R.drawable.ic_ticket
                } else {
                    R.drawable.ic_ticket_disable
                }
            )
            this.setTextViewText(R.id.notification_ticket_condition, ticketCondition.toString())
            // endregion

            //region {TicketBox}
            this.setImageViewResource(
                R.id.notification_box_condition_frame,
                if (ticketBoxCondition > 0) {
                    R.drawable.ic_ticketbox
                } else {
                    R.drawable.ic_ticketbox_disable
                }
            )
            this.setTextViewText(R.id.notification_box_condition, ticketBoxCondition.toString())
            this.setOnClickPendingIntent(
                R.id.notification_box_condition_frame,
                ticketBoxPendingIntent
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
        /**
         * 티켓 정보를 반환합니다.
         * balance: 소유 티켓 수
         * condition: 받을 수 있는 티켓 수 (터치티켓 + 동영상티켓)
         * condition <= 0 : Dimmed 이미지를 사용해 주세요.
         */
        NotificationIntegrationSDK.getTicketCondition(listener = object : ITicketCount {
            override fun callback(balance: Int, condition: Int) {
                ticketBalance = balance
                ticketCondition = condition
                checkTicketBoxCondition {
                    if (needUpdate) {
                        updateNotification()
                    }
                }
            }
        })
    }


    private fun checkTicketBoxCondition(callback: () -> Unit) {
        /**
         * 티켓박스 정보를 반환합니다.
         * condition: 받을 수 있는 티켓박스 수
         * condition <= 0 : Dimmed 이미지를 사용해 주세요.
         * pendingIntent: 아이콘 클릭시 처리할 동작이 전달 됩니다. (티켓박스 수령 화면 이동)
         */
        NotificationIntegrationSDK.getTicketBoxCondition(listener = object : ITicketBoxCount {
            override fun callback(condition: Int) {
                ticketBoxCondition = condition
                callback()
            }
        })
    }


    private fun updateNotification() {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(notificationID, makeNotificationBuilder().build())
    }
}