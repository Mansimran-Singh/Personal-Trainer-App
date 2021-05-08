package com.lemedebug.personaltrainer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.exercise.AllWorkoutsAdapter
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.CompletedWorkout
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.activity_finish.*
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val name = intent.getStringExtra(Constants.COMPLETED_WORKOUT)
        val total = intent.getStringExtra(Constants.TOTAL_EXERCISES)

        val c = Calendar.getInstance() // Calender Current Instance
        val dateTime = c.time // Current Date and Time of the system.
        Log.e("Date : ", "" + dateTime)

        //val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()) // Date Formatter
        //val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        //val date  = sdf.format(formatter)
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
        var loggedUser = Gson().fromJson(user,sType) as User

        val cWorkout = CompletedWorkout(name,total,date)
        loggedUser.completedWorkoutList.add(cWorkout)
        FirestoreClass().updateCompletedWorkoutList(this,loggedUser)

        val docRef = FirestoreClass().mFireStore.collection(Constants.USERS).document(loggedUser.id)
        docRef.get()
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {

                var tloggedUser = snapshot.toObject(User::class.java)

                loggedUser = tloggedUser!!

                val sharedPreferences =
                        this.getSharedPreferences(
                                Constants.PT_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonArrayLoggedUser = Gson().toJson(loggedUser)
                editor.putString(
                        Constants.LOGGED_USER,
                        jsonArrayLoggedUser
                )
                editor.apply()

                Log.d("onstart", "DocumentSnapshot data: ${snapshot.data}")
            } else {
                //Log.d(TAG, "No such document")
            }
        }

        val intent = Intent(this@FinishActivity, MainActivity::class.java)

        toolbar_finish_activity.setNavigationOnClickListener {
            startActivity(intent)
            finish()
        }

        // Navigate the activity on click on back button of action bar.
        btn_back_to_dashboard.setOnClickListener {
            startActivity(intent)
            finish()
        }
    }

}
