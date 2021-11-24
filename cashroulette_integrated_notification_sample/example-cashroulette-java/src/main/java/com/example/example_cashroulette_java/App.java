package com.example.example_cashroulette_java;

import androidx.multidex.MultiDexApplication;

import com.avatye.cashroulette.CashRouletteSDK;
import com.avatye.cashroulette.business.model.config.NotificationServiceConfig;

public class App extends MultiDexApplication {
    public static PreferenceUtil prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new PreferenceUtil(this);

        /**
         * 1. appID, appSecret -> AndroidManifest.xml 정의된 경우
         * CashRouletteSDK.initialize(application, log:Boolean)
         */
        CashRouletteSDK.initialize(this, true);

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
        NotificationServiceConfig config = new NotificationServiceConfig();
        config.setChannelName("돌림판 알림창");
        config.setTitle("돌림판");
        config.setText("돌림판 룰렛");
        CashRouletteSDK.setNotificationServiceConfig(config);

        /**
         * 통합 노티바 연동 유무 (default: false)
         * true: 사용
         * false: 미사용
         */
        CashRouletteSDK.HostNotification.useIntegrationNotification(true);
    }
}
