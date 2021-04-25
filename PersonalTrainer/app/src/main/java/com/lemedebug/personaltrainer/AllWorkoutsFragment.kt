package com.lemedebug.personaltrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lemedebug.personaltrainer.exercise.*
import kotlinx.android.synthetic.main.fragment_view_all_exercises.*

class AllWorkoutsFragment : Fragment() {


    private val workoutList = ArrayList<WorkoutEntity>()
    private val adapter = AllWorkoutsAdapter(workoutList)

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
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        activity.supportActionBar?.title = "AVAILABLE WORKOUTS"

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_all_workouts)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
//        getData()

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