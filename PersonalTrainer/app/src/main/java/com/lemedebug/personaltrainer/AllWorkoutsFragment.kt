package com.lemedebug.personaltrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lemedebug.personaltrainer.exercise.*

class AllWorkoutsFragment : Fragment() {

    lateinit var db: PersonalTrainerDatabase

    private val workoutList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_all_workouts, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_all_workouts))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "AVAILABLE WORKOUTS"

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_all_workouts)
        val lvTasksAdapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, workoutList)

        //CREATE AN ADAPTER CLASS
//        val inflater = layoutInflater
//        val workoutShort = inflater.inflate(R.layout.workout_short,null)
//        val editTextName = workoutShort.findViewById<EditText>(R.id.tv_workout_name)

//        recyclerView.adapter = lvTasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_all_workouts).setNavigationOnClickListener {
            // Change it to required fragment back button
            activity.onBackPressed()
        }



        view.findViewById<FloatingActionButton>(R.id.btn_add_new_workout).setOnClickListener {
            showDialog(activity)
        }

        return view
    }


    private fun showDialog(activity: AppCompatActivity) {

        val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)

        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edittext_workout_name,null)
        val editTextName = dialogLayout.findViewById<EditText>(R.id.et_workout_name)

        with(builder){
            setTitle("CREATE WORKOUT")
            setPositiveButton("PROCEED"){dialog,which ->
                if (editTextName.text.trim().isEmpty()){
                    Toast.makeText(requireContext(),"Please enter a valid workout name",Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.selectedWorkoutID = editTextName.text.toString()
//                    Log.d("TEXTTT",viewModel.selectedWorkoutID.toString())

                    if (!viewModel.selectedWorkoutID.isNullOrEmpty()){

                        val activity = requireActivity() as AppCompatActivity
                        db = Room.databaseBuilder(
                                activity.applicationContext,
                                PersonalTrainerDatabase::class.java,
                                "exercise.db"
                        ).build()
                        Thread{
                            val workoutEntity = WorkoutEntity(viewModel.selectedWorkoutID.toString(),null)
                            db.workoutDAO().insertWorkout(workoutEntity)
                        }.start()
                    }

                    requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                            .commit()


                }
            }
            setNegativeButton("CANCEL"){dialog,which->
                // DO NOTHING
            }
        }

        // create the dialog and show it
        val dialog = builder.create()
        dialog.setView(dialogLayout)
        dialog.show()
    }

}