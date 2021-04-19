package com.lemedebug.personaltrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lemedebug.personaltrainer.CreateWorkoutFragment
import com.lemedebug.personaltrainer.R

class WorkoutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)




        supportFragmentManager.beginTransaction()
            .replace(R.id.exercise_view_container, CreateWorkoutFragment())
            .commit()



    }
}