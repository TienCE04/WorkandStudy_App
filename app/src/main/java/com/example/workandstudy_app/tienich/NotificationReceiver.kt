package com.example.workandstudy_app.tienich

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.workandstudy_app.R

//nhận thông báo từ hệ thống
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val eventName = intent?.getStringExtra("eventName") ?: "Sự kiện"
        val type = intent?.getStringExtra("type") ?: "Sự kiện"

        val channelID = "schedule_channel"
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelID,
            "Schedule Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val iconRes = when (type) {
            "Lịch học" -> R.drawable.lichhocicon
            "Công việc" -> R.drawable.workicon
            else -> R.drawable.workicon
        }
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(iconRes)
            .setContentTitle("Nhắc nhở $type: $eventName")
            .setContentText("$type của bạn sắp bắt đầu!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(),notification)
    }
}