package com.lemedebug.personaltrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WorkoutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)




        supportFragmentManager.beginTransaction()
            .replace(R.id.exercise_view_container, AllWorkoutsFragment())
            .commit()



    }
}