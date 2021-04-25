package com.lemedebug.personaltrainer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lemedebug.personaltrainer.exercise.Exercise
import com.lemedebug.personaltrainer.exercise.ExerciseData
import com.lemedebug.personaltrainer.exercise.ExerciseService
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.PT_PREFERENCES,Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")

        main_title.text = "Welcome $userName!"


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

//        updateExerciseDocument()

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
                    Log.d("Main", "OnResponse: $response")
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