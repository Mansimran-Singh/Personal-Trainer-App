package com.lemedebug.personaltrainer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.exercise.*
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.utils.Constants

class EditWorkoutFragment : Fragment() {

//    private var selectedExerciseList = ArrayList<SelectedExercise>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_workout, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_create_workout))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        activity.supportActionBar?.title = "EDIT WORKOUT"

        val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
        view.findViewById<TextView>(R.id.tv_workout_name_edit_fragment).text = viewModel.selectedWorkout?.name.toString()


        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_workout_specific_exercise_list)
        if (viewModel.selectedWorkout?.listSelectedExercises != null){
            val adapter = EditWorkoutAdapter(viewModel.selectedWorkout!!.listSelectedExercises)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }



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

            if (viewModel.selectedExercise?.exercise != null && viewModel.selectedExercise?.reps!=null){

                val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                val user = sharedPreferences.getString(Constants.LOGGED_USER,"")
                val sType = object : TypeToken<User>() { }.type
                val loggedUser = Gson().fromJson<User>(user,sType) as User

                for (w in loggedUser.workoutList){
                    if (w.name.equals(viewModel.selectedWorkout?.name)){
//                        selectedExerciseList = w.listSelectedExercises as ArrayList<SelectedExercise>
                        viewModel.selectedWorkout?.listSelectedExercises?.addAll(w.listSelectedExercises)
                        viewModel.selectedWorkout?.listSelectedExercises?.add(viewModel.selectedExercise)

                        viewModel.selectedWorkout?.let { it1 -> loggedUser.workoutList.add(it1) }

                        viewModel.user = loggedUser

                        Log.e("USER","$loggedUser")

                        FirestoreClass().updateUser(activity,loggedUser)

                    }
                }


//                selectedExerciseList.add(viewModel.selectedExercise)
//
//                val workoutInfo = loggedUser.workoutList as ArrayList<Workout>
//                workoutInfo.add(Workout(viewModel.selectedWorkout?.name,selectedExerciseList))


            }

            val textViewWorkOutName = view.findViewById<TextView>(R.id.tv_workout_name_edit_fragment)
            textViewWorkOutName.text = viewModel.selectedWorkout?.name

//            if(selectedExerciseList.isNotEmpty()){
                Toast.makeText(requireContext(),"Saved Successfully",Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, AllWorkoutsFragment())
                        .commit()
//            }



        }


        return view
    }

}