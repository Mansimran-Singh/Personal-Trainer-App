package com.lemedebug.personaltrainer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_create_workout.*

class CreateWorkoutFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_workout, container, false)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(view.findViewById(R.id.toolbar_create_workout))
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "CREATE NEW WORKOUT"

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

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.exercise_view_container, ViewAllExercisesFragment())
                .commit()

        }


        return view
    }

}