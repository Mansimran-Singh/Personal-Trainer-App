package com.lemedebug.personaltrainer

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.exercise.*
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants
import kotlin.collections.ArrayList

class AllWorkoutsFragment : Fragment() {

    lateinit var viewModel: ExerciseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.N)
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
        val user = sharedPreferences.getString(Constants.LOGGED_USER, "")
        val sType = object : TypeToken<User>() { }.type
        val loggedUser = Gson().fromJson<User>(user, sType) as User

        var workoutList = loggedUser.workoutList
        var adapter = AllWorkoutsAdapter(workoutList)

        viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
        viewModel.user = loggedUser
        var dummyWorkout:Workout = Workout()
        dummyWorkout.name = ""
        dummyWorkout.listSelectedExercises = ArrayList()
        viewModel.selectedWorkout = dummyWorkout

        var recyclerView = view.findViewById<RecyclerView>(R.id.rv_all_workouts)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_all_workouts).setNavigationOnClickListener {
            // Change it to required fragment back button
            requireActivity().finish()
        }


       // add new workout button code
        view.findViewById<FloatingActionButton>(R.id.btn_add_new_workout).setOnClickListener {
            showDialog(activity)

        }
       // tutorial for workout
        view.findViewById<FloatingActionButton>(R.id.btn_tutorial).setOnClickListener {
            val intent = Intent(activity.applicationContext, TutorialActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
        // get snapshot of live data from Firestore
        val docRef = FirestoreClass().mFireStore.collection(Constants.USERS).document(loggedUser.id)
        docRef.get()
        docRef.addSnapshotListener { snapshot, e ->
            if (snapshot != null) {

                var tworkoutList = snapshot.toObject(User::class.java)

                workoutList = tworkoutList!!.workoutList
                adapter = AllWorkoutsAdapter(workoutList)
                recyclerView.setAdapter(adapter)
                adapter.notifyDataSetChanged()
                loggedUser.workoutList = workoutList
                val sharedPreferences =
                        activity.getSharedPreferences(
                                Constants.PT_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonArrayLoggedUser = Gson().toJson(loggedUser)
                editor.putString(
                        Constants.LOGGED_USER,
                        jsonArrayLoggedUser
                )
                editor.apply()
            } else {

            }
        }


        return view
    }


    private fun showDialog(activity: AppCompatActivity) {
       // dialog for Create workout
        // check if name of workout already exists if so give message
        // if name does not exists then create new workout
        // save data to firestore
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edittext_workout_name, null)
        val editTextName = dialogLayout.findViewById<EditText>(R.id.et_workout_name)

        with(builder){
            setTitle("CREATE WORKOUT")
            setPositiveButton("PROCEED"){ dialog, which ->
                if (editTextName.text.trim().isEmpty()){
                    Toast.makeText(requireContext(), "Please enter a valid workout name", Toast.LENGTH_SHORT).show()
                }else{
                    val toFind = editTextName.text.toString()
                    var found: Boolean = false
                    val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                    val user = sharedPreferences.getString(Constants.LOGGED_USER, "")
                    val sType = object : TypeToken<User>() { }.type
                    val loggedUser = Gson().fromJson<User>(user, sType) as User
                    val sharedPreferencesList = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                    sharedPreferencesList.edit().remove(Constants.SELECTED_EXERCISE).apply();
                    val workoutList = loggedUser.workoutList
                    viewModelStore.clear()
                    viewModel.selectedWorkout!!.name = editTextName.text.toString()
                    viewModel.selectedWorkout!!.listSelectedExercises.clear()

                    for (n in workoutList){
                        if (toFind == n.name)
                        {
                            found = true
                            break
                        }
                    }
                    if (found)
                    {
                        Toast.makeText(requireContext(), "Please enter a different workout name. Entered workout already exists", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                                .commit()
                    }


                }
            }
            setNegativeButton("CANCEL"){ dialog, which->
                // DO NOTHING
                dialog.dismiss()
            }
        }

        // create the dialog and show it
        val dialog = builder.create()
        dialog.setView(dialogLayout)
        dialog.show()
    }



}