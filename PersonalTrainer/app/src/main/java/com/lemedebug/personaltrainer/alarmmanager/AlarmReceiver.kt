package com.lemedebug.personaltrainer.alarmmanager

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lemedebug.personaltrainer.utils.Constants
import java.util.*
import java.util.concurrent.TimeUnit
import io.karn.notify.Notify
import android.text.format.DateFormat
import com.lemedebug.personaltrainer.MainActivity
import com.lemedebug.personaltrainer.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val timeInMillis = intent.getLongExtra(Constants.EXTRA_EXACT_ALARM_TIME, 0L)
        when (intent.action) {
            Constants.ACTION_SET_EXACT -> {
                buildNotification(context, "Set Exact Time", convertDate(timeInMillis))
            }

            Constants.ACTION_SET_REPETITIVE_EXACT -> {
                setRepetitiveAlarm(NotificationService(context))
                buildNotification(context, "Workout Reminder", convertDate(timeInMillis))
            }
        }
    }

    private fun buildNotification(context: Context, title: String, message: String) {
        Notify
                .with(context)
                .header {
                    this.icon = R.mipmap.ic_launcher_foreground
                }
                .meta { // this: Payload.Meta
                    // Launch the MainActivity once the notification is clicked.
                    clickIntent = PendingIntent.getActivity(context,
                        1,
                        Intent(context, MainActivity::class.java),
                        0)
                }
                .content {
                    this.title = title
                    text = "Pending workout for $message"
                }
                .show()
    }

    private fun setRepetitiveAlarm(alarmService: NotificationService) {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(1)
            Log.d("ALARM","Set alarm for every day - ${convertDate(this.timeInMillis)}")
        }
        alarmService.setRepetitiveAlarm(cal.timeInMillis)
    }

    private fun convertDate(timeInMillis: Long): String =
            DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()
}