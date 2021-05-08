package com.lemedebug.personaltrainer.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.Exercise
import com.lemedebug.personaltrainer.models.Muscles
import com.lemedebug.personaltrainer.models.SelectedExercise
import com.squareup.picasso.Picasso

class EditWorkoutAdapter(private var exerciseList: ArrayList<SelectedExercise?>) : RecyclerView.Adapter<EditWorkoutAdapter.EditWorkoutAdapterViewHolder>() {

    private var exercise: Exercise? = null

    inner class EditWorkoutAdapterViewHolder(exerciseView: View): RecyclerView.ViewHolder(exerciseView){
        val removeButton = exerciseView.findViewById<TextView>(R.id.btn_remove_selected)
        val name = exerciseView.findViewById<TextView>(R.id.tv_exercise_name_edit_workout)
        val reps = exerciseView.findViewById<TextView>(R.id.tv_exercise_reps_edit_workout)
        val expandedView: LinearLayout = exerciseView.findViewById(R.id.expanded_view)
        val exerciseLinearLayout: RelativeLayout = exerciseView.findViewById(R.id.ll_name)
        val category: TextView = exerciseView.findViewById(R.id.tv_category)
        val equipment: TextView = exerciseView.findViewById(R.id.tv_equipment)
        val description: TextView = exerciseView.findViewById(R.id.tv_description)

        val image: ImageView = exerciseView.findViewById(R.id.iv_exercise_image)
        val imageSecond: ImageView = exerciseView.findViewById(R.id.iv_exercise_image_second)

        val comments: TextView = exerciseView.findViewById(R.id.tv_comments)
        val commentsLabel: TextView = exerciseView.findViewById(R.id.tv_comments_label)
        val muscleFront: RecyclerView = exerciseView.findViewById(R.id.iv_muscle_front)
        val muscleBack: RecyclerView = exerciseView.findViewById(R.id.iv_muscle_back)

        val bodyFront: FrameLayout = exerciseView.findViewById(R.id.body_front)
        val bodyBack: FrameLayout = exerciseView.findViewById(R.id.body_back)
        val cardView: CardView = exerciseView.findViewById(R.id.cv_exercise)
        val exerciseImageLayout: RelativeLayout = exerciseView.findViewById(R.id.image_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditWorkoutAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.selected_exercise_view,
                parent,
                false
        )


        return EditWorkoutAdapterViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EditWorkoutAdapterViewHolder, position: Int) {
        val currentItem = exerciseList[position]!!.exercise

        holder.comments.visibility = View.VISIBLE
        holder.commentsLabel.visibility = View.VISIBLE

        Utils.fetchSvg(holder.bodyBack.context, "https://wger.de/static/images/muscles/muscular_system_back.svg", holder.bodyBack)
        Utils.fetchSvg(holder.bodyFront.context, "https://wger.de/static/images/muscles/muscular_system_front.svg", holder.bodyFront)


        holder.name.text = currentItem.name
        holder.category.text = "Category: ${currentItem.category.name}"

        var equip:String = ""
        if (currentItem.equipment.isNotEmpty()) {
            for (i in currentItem.equipment.indices){
                if (equip != ""){
                    equip += ", ${currentItem.equipment[i].name}"
                }else{
                    equip = currentItem.equipment[i].name
                }
            }
        }
        holder.equipment.text = "Equipment: $equip"

        holder.description.text = Html.fromHtml(
                currentItem.description,
                Html.FROM_HTML_MODE_COMPACT
        )

        var comments:String = ""
        if (currentItem.comments.isNotEmpty()){
            for (i in currentItem.comments.indices){
                if (comments != ""){
                    comments += "● ${currentItem.comments[i].comment}\n"
                }else{
                    comments = "● ${currentItem.comments[i].comment}\n"
                }
            }
        }

        holder.comments.text = comments.toString()

        if (currentItem.images.isNotEmpty()){

            Picasso.get().load(currentItem.images[0].image).into(holder.image)
            Picasso.get().load(currentItem.images[1].image).into(holder.imageSecond)

        }


        val activity: AppCompatActivity = holder.expandedView.context as AppCompatActivity
        // MUSCLE RECYCLER VIEW
        val arrayFrontMuscles = ArrayList<Muscles>()
        val arrayBackMuscles = ArrayList<Muscles>()

        for (i in currentItem.muscles.indices) {
            val m = Muscles(currentItem.muscles[i].image_url_main,currentItem.muscles[i]._front)
            if (currentItem.muscles[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        for (i in currentItem.muscles_secondary.indices) {
            val m = Muscles(currentItem.muscles_secondary[i].image_url_secondary,currentItem.muscles_secondary[i]._front)
            if (currentItem.muscles_secondary[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        val frontMuscleAdapter  = MuscleAdapter(arrayFrontMuscles)
        frontMuscleAdapter.notifyDataSetChanged()

        val backMuscleAdapter  = MuscleAdapter(arrayBackMuscles)
        backMuscleAdapter.notifyDataSetChanged()


        holder.muscleFront.addItemDecoration(MuscleDecoration())
        holder.muscleFront.layoutManager = LinearLayoutManager(holder.muscleFront.context)

        holder.muscleBack.addItemDecoration(MuscleDecoration())
        holder.muscleBack.layoutManager = LinearLayoutManager(holder.muscleBack.context)

        holder.muscleBack.adapter = backMuscleAdapter
        holder.muscleFront.adapter = frontMuscleAdapter



        if (currentItem.comments.isNullOrEmpty()){
            holder.comments.visibility = View.GONE
            holder.commentsLabel.visibility = View.GONE
        }

//#F0EEFE



        holder.exerciseLinearLayout.setOnClickListener {
            notifyDataSetChanged()
            exercise = exerciseList[position]?.exercise
        }



        if (exercise == exerciseList[position]?.exercise){
            holder.expandedView.visibility =  View.VISIBLE
            holder.removeButton.visibility = View.VISIBLE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

//            holder.image.animate().alpha(0F).setDuration(6000)
//            holder.imageBehind.animate().alpha(1F).setDuration(6000)
            if (currentItem.images.isNotEmpty()){
                holder.exerciseImageLayout.visibility = View.VISIBLE
                mainImageAnimation(holder)
            }else{
                holder.exerciseImageLayout.visibility = View.GONE
            }
        }
        else{
            holder.expandedView.visibility =  View.GONE
            holder.removeButton.visibility = View.GONE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E9EDF0"))
        }




//        holder.name.text = currentItem?.exercise?.name
        holder.reps.text = exerciseList[position]!!.reps.toString() + " Reps"
        holder.removeButton.setOnClickListener {
            val selectedItem = position
            exerciseList.removeAt(selectedItem)
            notifyItemRemoved(selectedItem)
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    private fun mainImageAnimation(holder: EditWorkoutAdapterViewHolder){

        val fadeIn = ObjectAnimator.ofFloat(holder.image, "alpha", 0f, 1f)
        fadeIn.duration = 4000
        val fadeOut = ObjectAnimator.ofFloat(holder.image, "alpha", 1f, 0f)
        fadeOut.duration = 2000
        val fadeInBehind = ObjectAnimator.ofFloat(holder.imageSecond, "alpha", 0f, 1f)
        fadeInBehind.duration = 4000
        val fadeOutBehind = ObjectAnimator.ofFloat(holder.imageSecond, "alpha", 1f, 0f)
        fadeOutBehind.duration = 4001

        val mAnimationSet = AnimatorSet()

        mAnimationSet.play(fadeIn).after(fadeOutBehind).after(fadeInBehind).after(fadeOut)


        mAnimationSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mAnimationSet.start()
            }
        })
        mAnimationSet.start()


    }
}