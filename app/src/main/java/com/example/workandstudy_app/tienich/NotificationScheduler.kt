package com.example.workandstudy_app.tienich

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.workandstudy_app.data_school_schedule.DataTKB
import com.example.workandstudy_app.todolist.Entity.TasksData
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NotificationScheduler(private val context: Context) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun scheduleNotifications(
        schedules: List<DataTKB>,
        tasks: List<TasksData>,
        selectedDate: LocalDate,
        deltaTime: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val currentTime = System.currentTimeMillis()

        // Kiểm tra quyền SCHEDULE_EXACT_ALARM trên Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                "Vui lòng cấp quyền lên lịch chính xác trong Cài đặt",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Lên lịch cho lịch học
        schedules.forEach { schedule ->
            try {
                val scheduleTime = LocalTime.parse(schedule.timeStart, timeFormatter)
                val calendar = Calendar.getInstance().apply {
                    set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, scheduleTime.hour)
                    set(Calendar.MINUTE, scheduleTime.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val notificationTime = calendar.timeInMillis - deltaTime * 60 * 1000

                if (notificationTime > currentTime) {
                    val intent = Intent(context, NotificationReceiver::class.java).apply {
                        putExtra("eventName", "${schedule.subjectName} (${schedule.classCode})")
                        putExtra("type", "Lịch học")
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationTime.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                    Log.d("NotificationScheduler", "Scheduled schedule: ${schedule.subjectName}, time: $notificationTime")
                } else {
                    Log.d("NotificationScheduler", "Schedule ${schedule.subjectName} is in the past: $notificationTime")
                }
            } catch (e: Exception) {
                Log.e("NotificationScheduler", "Error parsing timeStart for schedule ${schedule.subjectName}: ${e.message}")
                Toast.makeText(
                    context,
                    "Lỗi định dạng thời gian cho lịch học: ${schedule.subjectName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Lên lịch cho công việc
        tasks.forEach { task ->
            try {
                val taskTime = LocalTime.parse(task.timeTask, timeFormatter)
                val calendar = Calendar.getInstance().apply {
                    set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, taskTime.hour)
                    set(Calendar.MINUTE, taskTime.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val notificationTime = calendar.timeInMillis - deltaTime * 60 * 1000

                if (notificationTime > currentTime) {
                    val intent = Intent(context, NotificationReceiver::class.java).apply {
                        putExtra("eventName", task.titleTask)
                        putExtra("type", "Công việc")
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationTime.toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                    )
                    Log.d("NotificationScheduler", "Scheduled task: ${task.titleTask}, time: $notificationTime")
                } else {
                    Log.d("NotificationScheduler", "Task ${task.titleTask} is in the past: $notificationTime")
                }
            } catch (e: Exception) {
                Log.e("NotificationScheduler", "Error parsing timeTask for task ${task.titleTask}: ${e.message}")
                Toast.makeText(
                    context,
                    "Lỗi định dạng thời gian cho công việc: ${task.titleTask}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}