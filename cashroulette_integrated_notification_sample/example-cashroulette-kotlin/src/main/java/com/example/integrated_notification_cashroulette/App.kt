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
         * 1. appID, appSecret -> AndroidManifest.xml 정의된 경우
         * CashRouletteSDK.initialize(application, log:Boolean)
         */
        CashRouletteSDK.initialize(application = this, log = true)

        /**
         * 2. appID, appSecret -> Application에서 정의
         * CashRouletteSDK.initialize(application, appId:String, appSecret:String, log:Boolean)
         *
        CashRouletteSDK.initialize(
        application = this,
        appID = "98d4d4c35d594451b21f54718e2bc986",
        appSecret = "c395dbe200ad4493ade96fb92c988fcf1c8df2d3687d49a9ab6f31f7c05e2bf4",
        log = true
        )
         */


        /**
         * CashRoulette Notification 설정
         */
        CashRouletteSDK.setNotificationServiceConfig(config = NotificationServiceConfig().apply {
            channelName = "돌림판 알림창"
            title = "돌림판"
            text = "돌림판 룰렛"
        })


        /**
         * 통합 노티바 연동 유무 (default: false)
         * true: 사용
         * false: 미사용
         */
        CashRouletteSDK.HostNotification.useIntegrationNotification(true)
    }
}