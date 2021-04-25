package com.lemedebug.personaltrainer

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.exercise.*
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants

class AllWorkoutsFragment : Fragment() {


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

        val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val user = sharedPreferences.getString(Constants.LOGGED_USER,"")
        val sType = object : TypeToken<User>() { }.type
        val loggedUser = Gson().fromJson<User>(user,sType) as User

        val workoutList = loggedUser.workoutList as ArrayList<Workout>
        val adapter = AllWorkoutsAdapter(workoutList)

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
                    viewModel.selectedWorkout?.name = editTextName.text.toString()
//                    Log.d("TEXTTT",viewModel.selectedWorkoutID.toString())

                    if (viewModel.selectedWorkout?.name.isNullOrEmpty()){

                        val activity = requireActivity() as AppCompatActivity

                    }

                    requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.exercise_view_container, EditWorkoutFragment())
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