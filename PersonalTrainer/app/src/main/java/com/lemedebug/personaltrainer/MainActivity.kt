package com.lemedebug.personaltrainer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.alarmmanager.NotificationService
import com.lemedebug.personaltrainer.models.Exercise
import com.lemedebug.personaltrainer.models.ExerciseData
import com.lemedebug.personaltrainer.exercise.ExerciseService
import com.lemedebug.personaltrainer.exercise.ExerciseViewModel
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"
    lateinit var alarmService: NotificationService

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirestoreClass().getUserDetails(activity = this)

        val sharedPreferences = getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val user = sharedPreferences.getString(Constants.LOGGED_USER,"")
        val sType = object : TypeToken<User>() { }.type
        val loggedUser = Gson().fromJson(user,sType) as User

        val sharedPreferences_list = getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences_list.edit().remove(Constants.SELECTED_EXERCISE).apply();

        val viewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        viewModel.user = loggedUser
        main_title.text = "Welcome ${loggedUser.firstName}!"


        btn_bmi_calculator.setOnClickListener {
            // Launching the BMI Activity
            val intent = Intent(this, BMIActivity::class.java)
            startActivity(intent)
        }
        btn_history.setOnClickListener {
            //Launching the HistoryActivity
            val intent = Intent(this, HistoryActivity::class.java)
           // intent.putExtra(Constants.LOGGED_USER, loggedUser)
            startActivity(intent)
        }

        btn_workout.setOnClickListener {
            //Launching the WorkoutActivity
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        btn_get_inspired.setOnClickListener {
            //Launching the Inspiration Activity
            val intent = Intent(this, InspirationActivity::class.java)
            startActivity(intent)
        }

        btn_log_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Send the user to the splash activity
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        btn_reminder.setOnClickListener{
            alarmService = NotificationService(this)
            setAlarm() { alarmService.setRepetitiveAlarm(it) }
        }


//        updateExerciseDocument()

    }


    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                this@MainActivity,
                0,
                { _, year, month, day ->
                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)
                    TimePickerDialog(
                        this@MainActivity,
                        0,
                        { _, hour, minute ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, minute)
                            callback(this.timeInMillis)
                            Toast.makeText(this@MainActivity,"Successfully Scheduled a Recurring Reminder",Toast.LENGTH_SHORT).show()
                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }












    // UNUSED CODE

    private fun updateExerciseDocument(){
//        // Convert Workouts to json
//        val jsonArrayExercises = Gson().toJson(user.workoutList)
//        // Save json to local
//        editor.putString(
//                Constants.EXERCISES,
//                jsonArrayExercises
//        )

//        if (exerciseStringList.isNotEmpty()){
//            val sType = object : TypeToken<List<Exercise>>() { }.type
//            val exerciseList = Gson().fromJson<List<Exercise>>(exerciseStringList,sType) as ArrayList<Exercise>
//        }

        val exerciseList = ArrayList<Exercise>()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val randomExerciseAPI = retrofit.create(ExerciseService::class.java)
        randomExerciseAPI.getExerciseList(2, 250).enqueue(object : Callback<ExerciseData> {
            override fun onResponse(call: Call<ExerciseData>, response: Response<ExerciseData>) {
//                Log.d("Main", "OnResponse: $response")
                val body = response.body()
                if (body == null) {
                    Log.d("Main", "Invalid Response Found")
                    return
                }

                exerciseList.addAll(body.results)

                body.results.forEach {
                    FirestoreClass().addExerciseList(this@MainActivity, it)
                }


            }

            override fun onFailure(call: Call<ExerciseData>, t: Throwable) {
                Log.d("Main", "OnFailure: $t")
            }

        })

    }



}