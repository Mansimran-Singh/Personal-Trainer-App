package com.lemedebug.personaltrainer.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.TutorialItem

class TutorialsAdapter(private val tutorialList:ArrayList<TutorialItem>): RecyclerView.Adapter<TutorialsAdapter.TutorialsViewHolder>(){

    inner class TutorialsViewHolder(tutorialView:View): RecyclerView.ViewHolder(tutorialView){
        val textTitle:TextView = tutorialView.findViewById<TextView>(R.id.textTitle)
        val textDescription:TextView = tutorialView.findViewById<TextView>(R.id.textDescription)
        val imageTutorial:ImageView = tutorialView.findViewById<ImageView>(R.id.tutorialImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_container_tutorial,
            parent,
            false
        )
        return TutorialsViewHolder(view)
    }

    // tutorials display
    override fun onBindViewHolder(holder: TutorialsViewHolder, position: Int) {
        val activity: AppCompatActivity = holder.textTitle.context as AppCompatActivity

        val currentItem = tutorialList[position]
        holder.textTitle.text = currentItem.title
        holder.textDescription.text = currentItem.description
        holder.imageTutorial.setImageResource(currentItem.image)

    }

    override fun getItemCount(): Int {
        return tutorialList.size
    }
}