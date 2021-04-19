package com.lemedebug.personaltrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_bmi_calculator.setOnClickListener {
            // Launching the BMI Activity
            val intent = Intent(this, BMIActivity::class.java)
            startActivity(intent)
        }

        btn_workout.setOnClickListener {
            //Launching the WorkoutActivity
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

    }


}