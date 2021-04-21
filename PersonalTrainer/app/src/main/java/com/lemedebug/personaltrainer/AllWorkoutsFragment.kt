package com.lemedebug.personaltrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        activity.supportActionBar?.title = "AVAILABLE WORKOUTS"

        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_all_workouts).setNavigationOnClickListener {
            // Change it to required fragment back button
            activity.onBackPressed()
        }



        view.findViewById<FloatingActionButton>(R.id.btn_add_new_workout).setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                .commit()

        }

        return view
    }

}