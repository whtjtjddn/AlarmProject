package com.example.kotlinproject6

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver(){

    override fun onReceive(p0: Context, p1: Intent) {

        createNotificationChannel(p0)
        notifyNotification(p0)

    }

    private fun createNotificationChannel(context : Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                "1000",
                "기상 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }
    private fun notifyNotification(context : Context){
        with(NotificationManagerCompat.from(context)){
            val build = NotificationCompat.Builder(context,"1000")
                .setContentTitle("알람")
                .setContentText("지정한 시간이 되었습니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(100, build.build())
        }
    }

}
