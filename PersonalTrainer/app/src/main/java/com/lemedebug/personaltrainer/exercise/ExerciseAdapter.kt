package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.squareup.picasso.Picasso

class ExerciseAdapter(private val exerciseList: ArrayList<ExerciseInfo>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(exerciseView: View):RecyclerView.ViewHolder(exerciseView){
        val expandedView: LinearLayout = exerciseView.findViewById(R.id.expanded_view)
        val exerciseName: TextView = exerciseView.findViewById(R.id.tv_name)
        val category: TextView = exerciseView.findViewById(R.id.tv_category)
        val equipment: TextView = exerciseView.findViewById(R.id.tv_equipment)
        val description: TextView = exerciseView.findViewById(R.id.tv_description)
        val image: ImageView = exerciseView.findViewById(R.id.iv_exercise_image)
        val comments: TextView = exerciseView.findViewById(R.id.tv_comments)
        val commentsLabel: TextView = exerciseView.findViewById(R.id.tv_comments_label)
        val muscleFront: ImageView = exerciseView.findViewById(R.id.iv_muscle_front)
        val muscleBack: ImageView = exerciseView.findViewById(R.id.iv_muscle_back)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.exercise_detailed,
            parent,
            false
        )
        return ExerciseViewHolder(view)
    }

    @ExperimentalStdlibApi
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {


        with(holder){
            with(exerciseList[position]){

                holder.comments.visibility = View.VISIBLE
                holder.commentsLabel.visibility = View.VISIBLE
                holder.image.visibility = View.VISIBLE

                val currentItem = exerciseList[position]

                holder.exerciseName.text = currentItem.name
                holder.category.text = "Category: ${currentItem.category}"
                holder.equipment.text = "Equipment: ${currentItem.equipment}"
                holder.description.text = Html.fromHtml(
                    currentItem.description,
                    Html.FROM_HTML_MODE_COMPACT
                )
                holder.comments.text = currentItem.comments.toString()

                if (currentItem.image?.size ?: 0 > 0){
                    Picasso.get().load(currentItem.image!![0]).into(holder.image)
                }else{
                    holder.image.visibility = View.GONE
                }

                if (currentItem.musclesMain?.size ?: 0 > 0){
                    val path =  "https://wger.de${currentItem.musclesMain!![0].muscleImage}"
                    Log.d("EXERCISE_ADAPTER", path)
                    if (musclesMain!![0].isFront){
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleFront)
//                        Picasso.get().load(path).into(holder.muscleFront)
                    }else{
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleBack)
//                        Picasso.get().load(path).into(holder.muscleBack)
                    }
                }

                if (currentItem.musclesSecondary?.size ?: 0 > 0){
                    val path =  "https://wger.de${currentItem.musclesSecondary!![0].muscleImage}"
                    Log.d("EXERCISE_ADAPTER", path)
                    if (musclesSecondary!![0].isFront){
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleFront)
//                        Picasso.get().load(path).into(holder.muscleFront)
                    }else{
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleBack)
//                        Picasso.get().load(path).into(holder.muscleBack)
                    }
                }



                if (currentItem.comments.isNullOrEmpty()){
                    holder.comments.visibility = View.GONE
                    holder.commentsLabel.visibility = View.GONE
                }
                Log.d("EXERCISE_ADAPTER", currentItem.name)


                // check if boolean property "extend" is true or false
                // if it is true make the "extendedView" Visible
                holder.exerciseName.setOnClickListener {
                    currentItem.expand = !currentItem.expand
                    notifyDataSetChanged()
                }

                if (currentItem.expand) {
                    holder.expandedView.visibility =  View.VISIBLE
                    currentItem.expand = !currentItem.expand
                } else{
                    holder.expandedView.visibility =  View.GONE
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }
}