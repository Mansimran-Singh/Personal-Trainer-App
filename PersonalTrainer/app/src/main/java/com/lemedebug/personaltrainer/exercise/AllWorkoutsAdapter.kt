package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lemedebug.personaltrainer.EditWorkoutFragment
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.Workout

class AllWorkoutsAdapter(private var workoutList: ArrayList<Workout>) : RecyclerView.Adapter<AllWorkoutsAdapter.AllWorkoutsAdapterViewHolder>() {

    private var workout:Workout? = null
    inner class AllWorkoutsAdapterViewHolder(workoutView: View): RecyclerView.ViewHolder(workoutView){
        val cv = workoutView.findViewById<CardView>(R.id.cv_workout_short)
        val name = workoutView.findViewById<TextView>(R.id.tv_workout_name)
        val exerciseCount = workoutView.findViewById<TextView>(R.id.tv_exercise_count)
        val playButton = workoutView.findViewById<LottieAnimationView>(R.id.btn_start_workout)
        val editButton = workoutView.findViewById<FloatingActionButton>(R.id.btn_edit_workout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllWorkoutsAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workout_short,
                parent,
                false
        )


        return AllWorkoutsAdapterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AllWorkoutsAdapterViewHolder, position: Int) {

        val activity: AppCompatActivity = holder.cv.context as AppCompatActivity

        val currentItem = workoutList[position]
        holder.name.text = currentItem.name
        holder.exerciseCount.text = currentItem.listSelectedExercises.size.toString() + " Exercises"


        if (workout != null && currentItem == workout){
            holder.cv.setCardBackgroundColor(Color.parseColor("#e6ffe6"))
            holder.playButton.visibility = View.VISIBLE
            holder.editButton.visibility = View.VISIBLE

            holder.playButton.setOnClickListener {
                // START PLAY WORKOUT ACTIVITY
            }

            holder.editButton.setOnClickListener {
                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, EditWorkoutFragment())
                        .commit()
            }

        }else{
            holder.cv.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.playButton.visibility = View.GONE
            holder.editButton.visibility = View.GONE
        }

        holder.cv.setOnClickListener {
            workout = currentItem
            val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
            viewModel.selectedWorkout = workout
            notifyDataSetChanged()
        }



    }

    override fun getItemCount(): Int {
        return workoutList.size
    }
}