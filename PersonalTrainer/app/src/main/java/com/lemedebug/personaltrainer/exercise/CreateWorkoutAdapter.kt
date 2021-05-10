package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.SelectedExercise
class CreateWorkoutAdapter (private var selectedExerciseList: ArrayList<SelectedExercise?>) : RecyclerView.Adapter<CreateWorkoutAdapter.CreateWorkoutAdapterViewHolder>() {
    // variables declaration for exercise name and repetation
    inner class CreateWorkoutAdapterViewHolder(selectedExerciseView: View): RecyclerView.ViewHolder(selectedExerciseView){
        val name = selectedExerciseView.findViewById<TextView>(R.id.tv_exercise_name_edit_workout)
        val reps = selectedExerciseView.findViewById<TextView>(R.id.tv_exercise_reps_edit_workout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateWorkoutAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.selected_exercise_view,
            parent,
            false
        )

        return CreateWorkoutAdapterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CreateWorkoutAdapterViewHolder, position: Int) {
        val currentItem = selectedExerciseList[position]

        holder.name.text = currentItem?.exercise?.name
        holder.reps.text = currentItem?.reps.toString() + " Reps"

        holder.itemView.setOnLongClickListener {

            val selectedItem = position
            selectedExerciseList.removeAt(selectedItem)
            notifyItemRemoved(selectedItem)
            notifyDataSetChanged()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return selectedExerciseList.size
    }
}