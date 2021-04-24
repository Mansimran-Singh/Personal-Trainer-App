package com.lemedebug.personaltrainer.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R

class AllWorkoutsAdapter(private var workoutList: ArrayList<WorkoutEntity>) : RecyclerView.Adapter<AllWorkoutsAdapter.AllWorkoutsAdapterViewHolder>() {

    inner class AllWorkoutsAdapterViewHolder(workoutView: View): RecyclerView.ViewHolder(workoutView){
        val name = workoutView.findViewById<TextView>(R.id.tv_workout_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllWorkoutsAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workout_short,
                parent,
                false
        )
        return AllWorkoutsAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllWorkoutsAdapterViewHolder, position: Int) {
        val currentItem = workoutList[position]
        holder.name.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }
}