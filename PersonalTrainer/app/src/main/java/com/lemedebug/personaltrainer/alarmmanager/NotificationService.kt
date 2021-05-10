package com.lemedebug.personaltrainer.alarmmanager

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import com.lemedebug.personaltrainer.utils.Constants

class NotificationService(private val context: Context) {

    private val alarmManager:AlarmManager? =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    //Every Day Alarm
    fun setRepetitiveAlarm(timeInMills: Long){
        setAlarm(
                timeInMills,
                getPendingIntent(
                        getIntent().apply {
                            action = Constants.ACTION_SET_REPETITIVE_EXACT
                            putExtra(Constants.EXTRA_EXACT_ALARM_TIME, timeInMills)
                        }
                )
        )
    }

    // set Alarm
    private fun setAlarm(timeInMills: Long, pendingIntent: PendingIntent){
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeInMills,
                        pendingIntent
                )
            }else{
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        timeInMills,
                        pendingIntent
                )
            }
        }
    }

    private fun getIntent() = Intent(context,AlarmReceiver::class.java)

    private fun getPendingIntent(intent:Intent) =
            PendingIntent.getBroadcast(
                    context,
//                    RandomUtil.getRandomInt(),
                0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
}