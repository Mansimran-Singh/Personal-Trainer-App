package com.lemedebug.personaltrainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import com.lemedebug.personaltrainer.exercise.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"
    private val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val exerciseList = ArrayList<Exercise>()

        val adapter = ExerciseAdapter(exerciseList)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_exercise_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val randomExerciseAPI = retrofit.create(ExerciseService::class.java)
        randomExerciseAPI.getExerciseList(2, 200).enqueue(object : Callback<ExerciseData> {
            override fun onResponse(call: Call<ExerciseData>, response: Response<ExerciseData>) {
                Log.d(TAG, "OnResponse: $response")
                val body = response.body()
                if (body == null) {
                    Log.d(TAG, "Invalid Response Found")
                    return
                }

                Log.d(TAG, body.results[0].id)
                Log.d(TAG, body.results[0].name)
                Log.d(TAG, body.results[0].description)
                Log.d(TAG, body.results[0].category.name)
                Log.d(TAG, body.results[0].comments.toString())
                Log.d(TAG, body.results[0].equipment.toString())
                Log.d(TAG, body.results[0].images.toString())
                Log.d(TAG, body.results[0].muscles.toString())
                Log.d(TAG, body.results[0].muscles_secondary.toString())

                exerciseList.addAll(body.results)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ExerciseData>, t: Throwable) {
                Log.d(TAG, "OnFailure: $t")
            }

        })
    }


}