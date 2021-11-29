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
         * 1.
         * initialize for 통합 '알림창 상태바'
         * appID, appSecret -> AndroidManifest.xml 정의된 경우
         * CashRouletteSDK.initializeWithNotification(application: Application, notificationServiceConfig: NotificationServiceConfig, log: Boolean)
         */

        NotificationServiceConfig config = new NotificationServiceConfig();
        config.setChannelName("돌림판 알림창");
        config.setTitle("돌림판");
        config.setText("돌림판 룰렛");
        CashRouletteSDK.initializeWithNotification(this, config, true);


        /**
         * 2.
         * initialize for 통합 '알림창 상태바'
         * appID, appSecret -> Application에서 정의할 경우
         * CashRouletteSDK.initializeWithNotification(application: Application, appID: String, appSecret: String, notificationServiceConfig: NotificationServiceConfig, log: Boolean)
         *

         NotificationServiceConfig config = new NotificationServiceConfig();
         config.setChannelName("돌림판 알림창");
         config.setTitle("돌림판");
         config.setText("돌림판 룰렛");

         CashRouletteSDK.initializeWithNotification(
             this,
             "98d4d4c35d594451b21f54718e2bc986",
             "c395dbe200ad4493ade96fb92c988fcf1c8df2d3687d49a9ab6f31f7c05e2bf4",
             config,
             true
         );
         */
    }
}
