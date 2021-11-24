package com.example.integrated_notification_cashroulette

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avatye.cashroulette.CashRouletteSDK
import com.avatye.cashroulette.ITicketCount
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sw_notification.isChecked = App.prefs.getChecked("notification_status")

        /** init  */
        init()

        /** OnClickListener */
        button_auth.setOnClickListener(this)
        bt_roulette.setOnClickListener(this)
        ticket_balance.setOnClickListener(this)
        ticket_condition.setOnClickListener(this)
    }


    private fun init() {
        var intent = Intent(this, NotificationService::class.java)

        /** 프로필 정보 */
        viewProfile()

        /** init service */
        setNotificationService(intent = intent, enabled = sw_notification.isChecked)

        /** OnCheckedChangeListener */
        onCheckedChangeListener(intent)
    }


    private fun viewProfile() {
        val appUserID = CashRouletteSDK.getAppUserID()
        if (appUserID.isNotEmpty()) {
            wrap_auth.visibility = View.GONE
            wrap_profile.visibility = View.VISIBLE
            profile_id.text = "ID : $appUserID"
            ticketCondition()
        } else {
            wrap_auth.visibility = View.VISIBLE
            wrap_profile.visibility = View.GONE
        }
    }


    private fun ticketCondition() {
        CashRouletteSDK.getTicketCondition(listener = object : ITicketCount {
            override fun callback(balance: Int, condition: Int) {
                ticket_balance.text = "총 티켓 : $balance"
                ticket_condition.text = "받을 수 있는 티켓 : $condition"
            }
        })
    }


    private fun setProfile() {
        val appUserID = user_id.text.toString()
        CashRouletteSDK.setAppUserID(appUserID)
        viewProfile()
    }


    private fun onCheckedChangeListener(intent: Intent) {
        sw_notification.setOnCheckedChangeListener{ _, isChecked ->
            /** local preference */
            App.prefs.setChecked("notification_status", isChecked)

            setNotificationService(intent = intent, enabled = isChecked)

            /** 파트너사 Notification Status 저장 */
            CashRouletteSDK.HostNotification.setHostNotificationEnabled(context = this@MainActivity, enabled = isChecked)
        }
    }


    private fun setNotificationService(intent: Intent, enabled: Boolean) {
        if(enabled) {
            startNotificationService(intent);
        } else {
            stopNotificationService(intent)
        }
    }


    private fun startNotificationService(intent: Intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    private fun stopNotificationService(intent: Intent) {
        stopService(intent)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            /** set profile */
            R.id.button_auth -> {
                if (user_id.text.isNullOrEmpty()) {
                    Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
                } else {
                    setProfile()
                }
            }

            /** start cash roulette */
            R.id.bt_roulette -> CashRouletteSDK.start(this)

            /** refresh ticket balance */
            R.id.ticket_balance -> ticketCondition()

            /** refresh ticket condition */
            R.id.ticket_condition -> ticketCondition()
        }
    }
}