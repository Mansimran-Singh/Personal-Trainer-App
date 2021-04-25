package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.SelectedExercise

class EditWorkoutAdapter(private var selectedExerciseList: ArrayList<SelectedExercise?>) : RecyclerView.Adapter<EditWorkoutAdapter.EditWorkoutAdapterViewHolder>() {

    inner class EditWorkoutAdapterViewHolder(selectedExerciseView: View): RecyclerView.ViewHolder(selectedExerciseView){
        val name = selectedExerciseView.findViewById<TextView>(R.id.tv_exercise_name_edit_workout)
        val reps = selectedExerciseView.findViewById<TextView>(R.id.tv_exercise_reps_edit_workout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditWorkoutAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.selected_exercise_view,
                parent,
                false
        )

        return EditWorkoutAdapterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EditWorkoutAdapterViewHolder, position: Int) {
        val currentItem = selectedExerciseList[position]

        holder.name.text = currentItem?.exercise?.name
        holder.reps.text = currentItem?.reps.toString() + " Reps"
    }

    override fun getItemCount(): Int {
        return selectedExerciseList.size
    }
}