package com.example.example_cashroulette_java;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.avatye.cashroulette.CashRouletteSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout wrap_auth;
    private RelativeLayout wrap_profile;

    private AppCompatEditText user_id;
    private AppCompatButton button_auth;
    private AppCompatButton bt_roulette;
    private AppCompatTextView ticket_balance;
    private AppCompatTextView ticket_condition;
    private AppCompatTextView profile_id;

    private SwitchCompat sw_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById
        wrap_auth = findViewById(R.id.wrap_auth);
        wrap_profile = findViewById(R.id.wrap_profile);
        user_id = findViewById(R.id.user_id);
        button_auth = findViewById(R.id.button_auth);
        bt_roulette = findViewById(R.id.bt_roulette);
        ticket_balance = findViewById(R.id.ticket_balance);
        ticket_condition = findViewById(R.id.ticket_condition);
        profile_id = findViewById(R.id.profile_id);
        sw_notification = findViewById(R.id.sw_notification);

        // setOnClickListener
        button_auth.setOnClickListener(this);
        bt_roulette.setOnClickListener(this);
        ticket_balance.setOnClickListener(this);
        ticket_condition.setOnClickListener(this);

        sw_notification.setChecked(App.prefs.getChecked("notification_status", false));

        // init
        init();
    }


    private void init() {
        Intent intent = new Intent(MainActivity.this, NotificationService.class);

        /** 프로필 정보 */
        viewProfile();

        /** init service */
        setNotificationService(intent, sw_notification.isChecked());

        /** OnCheckedChangeListener */
        onCheckedChangeListener(intent);
    }


    private void viewProfile() {
        String appUserID = CashRouletteSDK.getAppUserID();
        if (!TextUtils.isEmpty(appUserID)) {
            wrap_auth.setVisibility(View.GONE);
            wrap_profile.setVisibility(View.VISIBLE);
            profile_id.setText("ID : $appUserID");
            ticketCondition();
        } else {
            wrap_auth.setVisibility(View.VISIBLE);
            wrap_profile.setVisibility(View.GONE);
        }
    }


    private void ticketCondition() {
        CashRouletteSDK.getTicketCondition((balance, condition) -> {
            ticket_balance.setText("총 티켓 : " + balance);
            ticket_condition.setText("받을 수 있는 티켓 : " + condition);
        });
    }


    private void setProfile() {
        String appUserID = user_id.getText().toString();
        CashRouletteSDK.setAppUserID(appUserID);
        viewProfile();
    }


    private void onCheckedChangeListener(final Intent intent) {
        sw_notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            /** local preference */
            App.prefs.setChecked("notification_status", isChecked);

            setNotificationService(intent, isChecked);

            /**
             * 파트너사의 '알림창 상태바' 활성 상태를 설정합니다.
             * 파트너사읭 '알림창 상타바'의 활성 상태가 변경 된다면 해당 메서드를 통해 활성 여부를 전달해야 합니다.
             */
            NotificationIntegrationSDK.setAppNotificationEnabled(this, isChecked);
        });
    }


    private void setNotificationService(final Intent intent, final Boolean enabled) {
        if (enabled) {
            startNotificationService(intent);
        } else {
            stopNotificationService(intent);
        }
    }


    private void startNotificationService(final Intent intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    private void stopNotificationService(final Intent intent) {
        stopService(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /** set profile */
            case R.id.button_auth: {
                if (TextUtils.isEmpty(user_id.getText())) {
                    Toast.makeText(MainActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    setProfile();
                }
                break;
            }

            /** start cash roulette */
            case R.id.bt_roulette:
                CashRouletteSDK.start(MainActivity.this);
                break;

            /** refresh { ticket-balance & ticket-condition } */
            case R.id.ticket_balance:
            case R.id.ticket_condition:
                ticketCondition();
                break;

        }
    }
}