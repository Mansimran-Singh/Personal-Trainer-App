package com.lemedebug.personaltrainer.exercise

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.squareup.picasso.Picasso

class ExerciseAdapter(val exerciseList: ArrayList<ExerciseInfo>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(exerciseView: View):RecyclerView.ViewHolder(exerciseView){
        val exerciseName: TextView = exerciseView.findViewById(R.id.tv_name)
        val category: TextView = exerciseView.findViewById(R.id.tv_category)
        val equipment: TextView = exerciseView.findViewById(R.id.tv_equipment)
        val description: TextView = exerciseView.findViewById(R.id.tv_description)
        val image: ImageView = exerciseView.findViewById(R.id.iv_exercise_image)
        val comments: TextView = exerciseView.findViewById(R.id.tv_comments)
        val commentsLabel: TextView = exerciseView.findViewById(R.id.tv_comments_label)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_detailed, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {

        holder.comments.visibility = View.VISIBLE
        holder.commentsLabel.visibility = View.VISIBLE

        val currentItem = exerciseList[position]

        holder.exerciseName.text = currentItem.name
        holder.category.text = "Category: ${currentItem.category}"
        holder.equipment.text = "Equipment: ${currentItem.equipment}"
        holder.description.text = currentItem.description
        holder.comments.text = currentItem.comments.toString()

        if (currentItem.image?.size ?: 0 > 0){
            Picasso.get().load(currentItem.image!![0]).into(holder.image)
        }



        if (currentItem.comments.isNullOrEmpty()){
            holder.comments.visibility = View.GONE
            holder.commentsLabel.visibility = View.GONE
        }



        Log.d("EXERCISE_ADAPTER", currentItem.name)

    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }
}