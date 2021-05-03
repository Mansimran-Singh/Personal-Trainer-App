package com.lemedebug.personaltrainer

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.CompletedWorkout
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_finish.*
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val name = intent.getStringExtra(Constants.COMPLETED_WORKOUT)

        val c = Calendar.getInstance() // Calender Current Instance
        val dateTime = c.time // Current Date and Time of the system.
        Log.e("Date : ", "" + dateTime)

        val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()) // Date Formatter
        val date = sdf.format(dateTime) // dateTime is formatted in the given format.
        Log.e("Formatted Date : ", "" + date)

        setSupportActionBar(toolbar_finish_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="FINISHED"


        val sharedPreferences = getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val user = sharedPreferences.getString(Constants.LOGGED_USER,"")
        val sType = object : TypeToken<User>() { }.type
        val loggedUser = Gson().fromJson(user,sType) as User

        val cWorkout = CompletedWorkout(name,date)
        loggedUser.completedWorkoutList.add(cWorkout)
        FirestoreClass().updateCompletedWorkoutList(this,loggedUser)

        toolbar_finish_activity.setNavigationOnClickListener {
            onBackPressed()
        }


        // Navigate the activity on click on back button of action bar.
        btn_back_to_dashboard.setOnClickListener {
            onBackPressed()
        }
    }
}