package com.lemedebug.personaltrainer

import android.annotation.SuppressLint
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
import com.lemedebug.personaltrainer.models.Exercise
import com.lemedebug.personaltrainer.models.SelectedExercise
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants

class EditWorkoutFragment : Fragment() {

//    private var selectedExerciseList = ArrayList<SelectedExercise>()

    @SuppressLint("CutPasteId")
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

        val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val user = sharedPreferences.getString(Constants.LOGGED_USER,"")
        val sType = object : TypeToken<User>() { }.type
        val loggedUser = Gson().fromJson<User>(user,sType) as User


        if (user != null) {
            Log.i("LOGGED USER",user)
        }else{
            Log.i("LOGGED USER","NOT FOUND")
        }
        lateinit var adapter: EditWorkoutAdapter
        val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
        var selExercise: SelectedExercise = SelectedExercise()
        val selectedexercise = sharedPreferences.getString(Constants.SELECTED_EXERCISE,"")
        val sTypeExer = object : TypeToken<SelectedExercise>() { }.type
        if (selectedexercise!!.isNotEmpty()) {

            selExercise = Gson().fromJson<SelectedExercise>(selectedexercise,sTypeExer) as SelectedExercise
        }


        view.findViewById<TextView>(R.id.tv_workout_name_edit_fragment).text = viewModel.selectedWorkout?.name.toString()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_workout_specific_exercise_list)
        if (viewModel.selectedWorkout?.listSelectedExercises != null){
            if (selExercise.exercise != null && selExercise.reps!! > 0){

                viewModel.selectedWorkout?.listSelectedExercises?.add(selExercise)
            }
            adapter = EditWorkoutAdapter(viewModel.selectedWorkout!!.listSelectedExercises)
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

            // if (viewModel.selectedExercise?.exercise != null && viewModel.selectedExercise?.reps!=null){

            //  if (viewModel.selectedExercise?.exercise != null && viewModel.selectedExercise?.reps!=null){
            if(selExercise.exercise != null && selExercise.reps!=null){
                if (loggedUser.workoutList.isEmpty()){
                    //viewModel.selectedWorkout?.listSelectedExercises?.add(selExercise)
                    viewModel.selectedWorkout?.let { it1 -> loggedUser.workoutList.add(it1) }

                    viewModel.user = loggedUser

                    Log.e("USER", "$loggedUser")

                    FirestoreClass().updateUser(activity,loggedUser)
                    val sharedPreferences_list = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                    sharedPreferences_list.edit().remove(Constants.SELECTED_EXERCISE).commit();

                }
                else {
                    viewModel.selectedWorkout?.let { it1 -> loggedUser.workoutList.add(it1) }

                    viewModel.user = loggedUser

                    Log.e("USER", "$loggedUser")
                    FirestoreClass().updateWorkoutList(activity,loggedUser)
                    val sharedPreferences_list = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                    sharedPreferences_list.edit().remove(Constants.SELECTED_EXERCISE).commit();

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

        val docRef = FirestoreClass().mFireStore.collection(Constants.USERS).document(loggedUser.id)
        docRef.get()
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                loggedUser.workoutList = snapshot.get("workoutList") as ArrayList<Workout>
                // val adapter = AllWorkoutsAdapter(workoutList)
                adapter.notifyDataSetChanged()

                Log.d("onstart", "DocumentSnapshot data: ${snapshot.data}")
            } else {
                //Log.d(TAG, "No such document")
            }
        }


        return view
    }

}