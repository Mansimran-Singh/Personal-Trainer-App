package com.lemedebug.personaltrainer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.lemedebug.personaltrainer.exercise.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewAllExercisesFragment : Fragment() {

    private val BASE_URL = "https://wger.de/api/v2/exerciseinfo/"
    private val TAG = "CREATE_WORKOUT_ACTIVITY"
    private val exerciseList = ArrayList<Exercise>()
    private val adapter = ExerciseAdapter(exerciseList)

    lateinit var db: PersonalTrainerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_all_exercises, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_view_all_exercises))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "ADD EXERCISE"

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_view_all_exercises).setNavigationOnClickListener {
            val viewModel = ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
            viewModel.exercise = null
            viewModel.reps = null
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                    .commit()
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_exercise_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return view
    }

    private fun getData(){

        val activity = requireActivity() as AppCompatActivity
        db = Room.databaseBuilder(
            activity.applicationContext,
            PersonalTrainerDatabase::class.java,
            "exercise.db"
        ).build()

        Thread{
            exerciseList.addAll(db.exerciseDAO().viewAllExercises())
            activity.runOnUiThread{
                adapter.notifyDataSetChanged()
            }

        }.start()
    }

}