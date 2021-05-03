package com.lemedebug.personaltrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_finish.*

class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        setSupportActionBar(toolbar_finish_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="FINISHED"

        toolbar_finish_activity.setNavigationOnClickListener {
            onBackPressed()
        }


        // Navigate the activity on click on back button of action bar.
        btn_back_to_dashboard.setOnClickListener {
            onBackPressed()
        }
    }
}