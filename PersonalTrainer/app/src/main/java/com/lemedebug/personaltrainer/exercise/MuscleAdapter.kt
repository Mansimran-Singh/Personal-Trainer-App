package com.lemedebug.personaltrainer.exercise


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.Muscles

class MuscleAdapter(private var muscleList: ArrayList<Muscles>) : RecyclerView.Adapter<MuscleAdapter.MuscleAdapterViewHolder>(){
    class MuscleAdapterViewHolder(muscleView: View): RecyclerView.ViewHolder(muscleView) {
        val muscleImage = muscleView.findViewById<FrameLayout>(R.id.muscle_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.exercise_image,
                parent,
                false
        )
        return MuscleAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MuscleAdapterViewHolder, position: Int) {
        val currentItem = muscleList[position]
        val path = "https://wger.de${currentItem.image_url}"
        Log.d("PATH",path)
        Utils.fetchSvg(holder.muscleImage.context, path, holder.muscleImage)
    }

    override fun getItemCount(): Int {
        return muscleList.size
    }
}