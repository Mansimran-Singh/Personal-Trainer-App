package com.lemedebug.personaltrainer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        // loggedUser = FirestoreClass().getUserDetails(activity = this)
        val sharedPreferences = getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        var user = sharedPreferences.getString(Constants.LOGGED_USER, "")
        var sType = object : TypeToken<User>() {}.type
        var loggedUser = Gson().fromJson(user, sType) as User

        setSupportActionBar(toolbar_history_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="HISTORY"

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

            }
        }


       // add bar chart of the completed workouts
        user = sharedPreferences.getString(Constants.LOGGED_USER, "")
        sType = object : TypeToken<User>() {}.type
        loggedUser = Gson().fromJson(user, sType) as User

        // variable for our bar chart
        var barChart: BarChart
        // variable for our bar data set.
        var barDataSet1: BarDataSet
        var barDataSet2: BarDataSet
        // array list for storing entries.
        var barEntries: ArrayList<BarEntry> = ArrayList()
        var dummy:ArrayList<BarEntry> = ArrayList()
        var days = ArrayList<String>()
        barChart = findViewById(R.id.barchart)
        barDataSet2 = BarDataSet(dummy, "")
        barDataSet2.color = applicationContext.resources.getColor(R.color.colorPrimary)

        loggedUser.completedWorkoutList.reverse()

        for ((n, i) in loggedUser.completedWorkoutList.withIndex()) {
            barEntries.add(BarEntry(n.toFloat(), i.totalExercises!!.toFloat()))
            days.add(i.date!!)
            }

        barDataSet1 = BarDataSet(barEntries, "Number of Exercises")
        barDataSet1.color = applicationContext.resources.getColor(R.color.colorPrimary)

        val data  = BarData(barDataSet1, barDataSet2)
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);

        // X and Y axis variables
        val xAxis = barChart.xAxis
        xAxis.setValueFormatter(IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1F);
        xAxis.setGranularityEnabled(true);
        xAxis.textSize = 11f
        val leftAxis = barChart.getAxisLeft()
        leftAxis.textSize = 14f
        leftAxis.setLabelCount(4, false)
        leftAxis.axisMaximum = 20f
        leftAxis.axisMinimum = 0f
        leftAxis.setGranularity(1f)


        val rAxis = barChart.axisRight
        rAxis.setEnabled(false);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(2F);
        val barSpace = 0.1f
        val groupSpace = 0.5f
        data.setBarWidth(0.15f);
        barChart.getXAxis().setAxisMinimum(0F);
        barChart.animateY(1000);
        barChart.groupBars(0F, groupSpace, barSpace);
        barChart.invalidate();

        toolbar_history_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }


}