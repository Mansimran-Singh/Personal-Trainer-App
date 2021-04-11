package com.lemedebug.personaltrainer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import com.lemedebug.personaltrainer.exercise.ExerciseAdapter
import com.lemedebug.personaltrainer.exercise.ExerciseInfo
import com.lemedebug.personaltrainer.exercise.ExerciseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var exerciseList = ArrayList<ExerciseInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        exerciseList =
        generateExerciseData()
        // Initialize viewModel val from provider
//        val viewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
//
//        // Initialize viewModel value of animal to variable
//        viewModel.exerciseList = exerciseList

        tv_view_available_exercises.setOnClickListener {

            Log.d("EXERCISE_DATA", exerciseList.size.toString())

            rv_exercise_list.adapter = ExerciseAdapter(exerciseList)
            rv_exercise_list.layoutManager = LinearLayoutManager(this)

            Log.d("EXERCISE","Clicked")
        }


    }

    private fun generateExerciseData(){
        // REST Call
        Ion.with(this)
                .load("https://wger.de/api/v2/exerciseinfo/?language=2&limit=500")
                .asString()
                .setCallback { e, result ->
//                    Log.d("EXERCISE_DATA","$result")
                    parseExerciseData(result)
                }

    }

    private fun parseExerciseData(output: String?){

        val data = JSONObject(output)
        // Count of exercises
        val count = data.getString("count")
//        Log.d("EXERCISE_DATA","Total Exercise Count: $count")
        // Exercise Info
        val results = data.getJSONArray("results")
        // Iterate through Exercise Info
        for (i in 0 until (count).toInt()){

            val exercise = results.getJSONObject(i)

            // id
            val id = exercise.getString("id")
//            Log.d("EXERCISE_DATA","id: $id")

            // Name
            val name = exercise.getString("name")
//            Log.d("EXERCISE_DATA","Exercise Name: $name")

            // Category
            val categoryObj = exercise.getJSONObject("category")
            val category = categoryObj.getString("name")
//            Log.d("EXERCISE_DATA","Category: $category")

            // Equipment
            val equipmentArray = exercise.getJSONArray("equipment")
            var equipment:String = "None Required"
            if (equipmentArray.length()>0){
//                Log.d("EXERCISE_DATA","Equipment Size: ${equipmentArray.length()}")
                for (j in 0 until equipmentArray.length()){
                    val equipmentObj = equipmentArray.getJSONObject(j)
                    if (equipment != "None Required"){
                        equipment += ", ${equipmentObj.getString("name")}"
                    }else{
                        equipment = equipmentObj.getString("name")
                    }
                }
            }
//            Log.d("EXERCISE_DATA","Equipment: $equipment")

            // Description
            val description = exercise.getString("description")
//            Log.d("EXERCISE_DATA","Description: $description")

            // Images
            val imagesObjArray = exercise.getJSONArray("images")
            var imageArray = ArrayList<String>()
            if (equipmentArray.length()>0){
//                Log.d("EXERCISE_DATA","Equipment Size: ${equipmentArray.length()}")
                for (j in 0 until imagesObjArray.length()){
                    val imageObj = imagesObjArray.getJSONObject(j)
                    imageArray.add(imageObj.getString("image"))
                }
            }
//            Log.d("EXERCISE_DATA","Images Array: $imageArray")

            // Comments
            val commentsArray = exercise.getJSONArray("comments")
            var comments:String = ""
            if (commentsArray.length()>0){
//                Log.d("EXERCISE_DATA","Equipment Size: ${equipmentArray.length()}")
                for (j in 0 until commentsArray.length()){
                    val equipmentObj = commentsArray.getJSONObject(j)
                    if (comments != ""){
                        comments += "● ${equipmentObj.getString("comment")}\n"
                    }else{
                        comments = "● ${equipmentObj.getString("comment")}\n"
                    }
                }
            }
//            Log.d("EXERCISE_DATA","Comments: $comments")

            if (imageArray.isNotEmpty()){
                val exercise = ExerciseInfo(id,name,category,equipment,description,imageArray,comments)
                exerciseList.add(exercise)
            }

        }

        Log.d("EXERCISE_DATA", exerciseList.size.toString())
//        Log.d("EXERCISE_DATA", exercises.toString())
    }


}