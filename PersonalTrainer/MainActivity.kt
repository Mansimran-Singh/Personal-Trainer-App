package com.lemedebug.personaltrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.lemedebug.personaltrainer.exercise.ExerciseData
import com.lemedebug.personaltrainer.exercise.ExerciseService
import com.lemedebug.personaltrainer.exercise.PersonalTrainerDatabase
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"
    lateinit var db: PersonalTrainerDatabase

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

        getData()

    }


    private fun getData(){
        db = Room.databaseBuilder(
                applicationContext,
                PersonalTrainerDatabase::class.java,
                "exercise.db"
        ).build()

        Thread{
            val exerciseCount = db.exerciseDAO().getExerciseCount()
            if (exerciseCount < 0){
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


                        Thread{
                            body.results.forEach {
                                db.exerciseDAO().insertExercise(it)
                            }
                        }.start()

//                        exerciseList.addAll(body.results) CHANGE THIS TO ADD INTO TABLE
//                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<ExerciseData>, t: Throwable) {
                        Log.d("Main", "OnFailure: $t")
                    }

                })
            }
        }.start()
    }


}