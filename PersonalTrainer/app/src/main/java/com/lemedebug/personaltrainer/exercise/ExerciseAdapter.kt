package com.lemedebug.personaltrainer.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lemedebug.personaltrainer.CreateWorkoutFragment
import com.lemedebug.personaltrainer.R
import com.squareup.picasso.Picasso


class ExerciseAdapter(private val exerciseList: ArrayList<Exercise>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private var exercise:Exercise? = null

    inner class ExerciseViewHolder(exerciseView: View):RecyclerView.ViewHolder(exerciseView){
        val expandedView: LinearLayout = exerciseView.findViewById(R.id.expanded_view)
        val exerciseName: TextView = exerciseView.findViewById(R.id.tv_name)
        val category: TextView = exerciseView.findViewById(R.id.tv_category)
        val equipment: TextView = exerciseView.findViewById(R.id.tv_equipment)
        val description: TextView = exerciseView.findViewById(R.id.tv_description)

        val image: ImageView = exerciseView.findViewById(R.id.iv_exercise_image)
        val imageSecond: ImageView = exerciseView.findViewById(R.id.iv_exercise_image_second)
        val imageThird: ImageView = exerciseView.findViewById(R.id.iv_exercise_image_third)
        val imageFourth: ImageView = exerciseView.findViewById(R.id.iv_exercise_image_fourth)

        val comments: TextView = exerciseView.findViewById(R.id.tv_comments)
        val commentsLabel: TextView = exerciseView.findViewById(R.id.tv_comments_label)
        val muscleFront: ImageView = exerciseView.findViewById(R.id.iv_muscle_front)
        val muscleBack: ImageView = exerciseView.findViewById(R.id.iv_muscle_back)

        val bodyFront: FrameLayout = exerciseView.findViewById(R.id.body_front)
        val bodyBack: FrameLayout = exerciseView.findViewById(R.id.body_back)
        val cardView: CardView = exerciseView.findViewById(R.id.cv_exercise)
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

        holder.comments.visibility = View.VISIBLE
        holder.commentsLabel.visibility = View.VISIBLE
        holder.image.visibility = View.VISIBLE
        holder.imageSecond.visibility = View.VISIBLE

        Utils.fetchSvg(holder.bodyBack.context, "https://wger.de/static/images/muscles/muscular_system_back.svg", holder.bodyBack)
        Utils.fetchSvg(holder.bodyFront.context, "https://wger.de/static/images/muscles/muscular_system_front.svg", holder.bodyFront)

        val currentItem = exerciseList[position]

        holder.exerciseName.text = currentItem.name
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

            if (currentItem.images[0].is_main){

            }
            Picasso.get().load(currentItem.images[0].image).into(holder.image)
            Picasso.get().load(currentItem.images[1].image).into(holder.imageSecond)
            Picasso.get().load(currentItem.images[2].image).into(holder.imageThird)
            Picasso.get().load(currentItem.images[3].image).into(holder.imageFourth)

        }else{
            holder.image.visibility = View.GONE
            holder.imageSecond.visibility = View.GONE
        }

            if (currentItem.muscles.isNotEmpty()){
                for (i in currentItem.muscles.indices){
                    val path =  "https://wger.de${currentItem.muscles[i].image_url_main}"
                    Log.d("EXERCISE_ADAPTER", path)
                    if (currentItem.muscles[i].is_front){
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleFront)
//                        Picasso.get().load(path).into(holder.muscleFront)
                    }else{
                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleBack)
//                        Picasso.get().load(path).into(holder.muscleBack)
                    }
                }
            }

//                    val path =  "https://wger.de${currentItem.muscles[0].image_url_main}"
//                    Log.d("EXERCISE_ADAPTER", path)
//                    if (currentItem.muscles[0].is_front){
//                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleFront)
////                        Picasso.get().load(path).into(holder.muscleFront)
//                    }else{
//                        Utils.fetchSvg(holder.muscleFront.context, path, holder.muscleBack)
////                        Picasso.get().load(path).into(holder.muscleBack)
//                    }
//                }

        if (currentItem.muscles_secondary.isNotEmpty()){
            val path =  "https://wger.de${currentItem.muscles_secondary[0].image_url_secondary}"
            Log.d("EXERCISE_ADAPTER", path)
            if (currentItem.muscles_secondary[0].is_front){
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

//#F0EEFE

        val activity: AppCompatActivity = holder.expandedView.context as AppCompatActivity
        val selectedItemName: TextView = activity.findViewById(R.id.tv_selected_item)

        holder.exerciseName.setOnClickListener {
            notifyDataSetChanged()
            selectedItemName.text = currentItem.name
            selectedItemName.visibility = View.VISIBLE
            exercise = exerciseList[position]
        }

        // add to view model here

        if (exercise == exerciseList[position]){
            holder.expandedView.visibility =  View.VISIBLE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#e6ffe6"))

//            holder.image.animate().alpha(0F).setDuration(6000)
//            holder.imageBehind.animate().alpha(1F).setDuration(6000)




        }else{
            holder.expandedView.visibility =  View.GONE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val addButton = activity.findViewById<FloatingActionButton>(R.id.btn_add_selected)
        addButton.setOnClickListener {
            if(exercise==null){
                Toast.makeText(activity.applicationContext, "Please select an exercise to add", Toast.LENGTH_SHORT).show()
            }else{

                val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)

                viewModel.exercise = exercise

                showDialog(activity)

                mainImageAnimation(holder)

            }

        }

    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    private fun showDialog(activity: AppCompatActivity) {

        val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
        val name = viewModel.exercise?.name
        val repsData = arrayOf("1x $name", "2x $name", "3x $name", "4x $name")
        var selectedReps:Int = 0

        // Create an alertdialog builder object,
        // then set attributes that you want the dialog to have
        val builder = AlertDialog.Builder(activity)
        builder.setIcon(android.R.drawable.ic_menu_help)
        builder.setTitle("Reps")

        // Set the button actions, optional
        builder.setPositiveButton("Add"){ dialog, which ->
            if (selectedReps<1){
                Toast.makeText(activity.applicationContext, "Please select Reps to add", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.reps = selectedReps

                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, CreateWorkoutFragment())
                        .commit()


            }
        }
        builder.setNeutralButton("Cancel"){ dialog, which ->
            viewModel.reps = null
        }



        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
        builder.setSingleChoiceItems(repsData, -1,
                DialogInterface.OnClickListener { dialog, which ->
                    selectedReps = which + 1
                })

        // create the dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

    private fun handleImageView(view:View, holder: ExerciseViewHolder,images:Array<ExerciseImage>){
        val layout:RelativeLayout? = view.findViewById(R.id.image_layout)
        for (i in images.indices){
            val imageView = ImageView(holder.image.context)

            if (images[i].is_main){
                // setting height and width of imageview
                imageView.layoutParams= RelativeLayout.LayoutParams(120,150)
                Picasso.get().load(images[i].image).into(imageView)
                // Add ImageView to LinearLayout
                layout?.addView(imageView) // adding image to the layout
            }else{

            }
        }
    }

    private fun mainImageAnimation(holder: ExerciseViewHolder){

        val fadeOut = ObjectAnimator.ofFloat(holder.image, "alpha", 1f, 0f)
        fadeOut.duration = 2000
        val fadeIn = ObjectAnimator.ofFloat(holder.image, "alpha", 0f, 1f)
        fadeIn.duration = 4000
        val fadeInBehind = ObjectAnimator.ofFloat(holder.imageSecond, "alpha", 0f, 1f)
        fadeInBehind.duration = 4000
        val fadeOutBehind = ObjectAnimator.ofFloat(holder.imageSecond, "alpha", 1f, 0f)
        fadeOutBehind.duration = 5500

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