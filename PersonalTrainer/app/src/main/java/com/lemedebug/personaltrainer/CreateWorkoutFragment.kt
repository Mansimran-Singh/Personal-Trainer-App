package com.lemedebug.personaltrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lemedebug.personaltrainer.exercise.Exercise
import com.lemedebug.personaltrainer.exercise.ExerciseAdapter
import com.lemedebug.personaltrainer.exercise.ExerciseViewModel
import com.lemedebug.personaltrainer.exercise.PersonalTrainerDatabase
import kotlinx.android.synthetic.main.fragment_create_workout.*

class CreateWorkoutFragment : Fragment() {

    private val exerciseList = ArrayList<Exercise>()
    private val adapter = ExerciseAdapter(exerciseList)
    lateinit var db: PersonalTrainerDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_create_workout))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "EDIT WORKOUT"

        val viewModel = ViewModelProvider(requireActivity()).get(ExerciseViewModel::class.java)
        val textViewWorkOutName = view.findViewById<TextView>(R.id.et_workout_name_label)
        textViewWorkOutName.text = viewModel.selectedWorkoutID

        db = Room.databaseBuilder(
                activity.applicationContext,
                PersonalTrainerDatabase::class.java,
                "exercise.db"
        ).build()



        Thread{

        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_workout_specific_exercise_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_create_workout).setNavigationOnClickListener {
            // Change it to required fragment back button
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.exercise_view_container, AllWorkoutsFragment())
                .commit()
        }



        view.findViewById<FloatingActionButton>(R.id.btn_show_all_exercises).setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.exercise_view_container, ViewAllExercisesFragment())
                .commit()

        }


        view.findViewById<FloatingActionButton>(R.id.btn_save_workout).setOnClickListener {
            val editTextWorkoutName = view.findViewById<RecyclerView>(R.id.et_workout_name)

            if(editTextWorkoutName.toString().trim().isEmpty()){
                Toast.makeText(requireContext(),"Please enter a workout name to save",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Saved Successfully",Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, AllWorkoutsFragment())
                        .commit()
            }



        }


        return view
    }

}