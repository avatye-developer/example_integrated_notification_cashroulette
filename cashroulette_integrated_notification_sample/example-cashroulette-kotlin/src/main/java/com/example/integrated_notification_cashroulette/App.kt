package com.example.integrated_notification_cashroulette

import androidx.multidex.MultiDexApplication
import com.avatye.cashroulette.CashRouletteSDK
import com.avatye.cashroulette.business.model.config.NotificationServiceConfig

class App : MultiDexApplication() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(this)

        /**
         * 1.
         * initialize for 통합 '알림창 상태바'
         * appID, appSecret -> AndroidManifest.xml 정의된 경우
         * CashRouletteSDK.initializeWithNotification(application: Application, notificationServiceConfig: NotificationServiceConfig, log: Boolean)
         */
        CashRouletteSDK.initializeWithNotification(
            application = this,
            notificationServiceConfig = NotificationServiceConfig().apply {
                channelName = "돌림판 알림창"
                title = "돌림판"
                text = "돌림판 룰렛"
            },
            log = true
        )


        /**
         * 2.
         * initialize for 통합 '알림창 상태바'
         * appID, appSecret -> Application에서 정의할 경우
         * CashRouletteSDK.initializeWithNotification(application: Application, appID: String, appSecret: String, notificationServiceConfig: NotificationServiceConfig, log: Boolean)
         *
        CashRouletteSDK.initializeWithNotification(
            application = this,
            appID = "98d4d4c35d594451b21f54718e2bc986",
            appSecret = "c395dbe200ad4493ade96fb92c988fcf1c8df2d3687d49a9ab6f31f7c05e2bf4",
            notificationServiceConfig = NotificationServiceConfig().apply {
            channelName = "돌림판 알림창"
            title = "돌림판"
            text = "돌림판 룰렛"
            },
            log = true
        )
         */
    }
}