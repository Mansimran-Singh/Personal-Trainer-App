package com.lemedebug.personaltrainer.exercise

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.EditWorkoutFragment
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.*
import com.lemedebug.personaltrainer.utils.Constants
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


class ExerciseAdapter(private var exerciseList: ArrayList<Exercise>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>(),Filterable {
    // variable declaration
    private var exercise: Exercise? = null
    private var tempexerciseList: ArrayList<Exercise> = exerciseList
    private var arrayFrontMuscles = ArrayList<Muscles>()
    private var arrayBackMuscles = ArrayList<Muscles>()

    // get data from view items
    inner class ExerciseViewHolder(exerciseView: View):RecyclerView.ViewHolder(exerciseView){
        val expandedView: LinearLayout = exerciseView.findViewById(R.id.expanded_view)
        val exerciseName: TextView = exerciseView.findViewById(R.id.tv_name)
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


        // display front and back muscles, details, equipment, comments of selected exercise
        holder.comments.visibility = View.VISIBLE
        holder.commentsLabel.visibility = View.VISIBLE

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
            Picasso.get().load(currentItem.images[0].image).into(holder.image)
            Picasso.get().load(currentItem.images[1].image).into(holder.imageSecond)
        }

        if (currentItem.comments.isNullOrEmpty()){
            holder.comments.visibility = View.GONE
            holder.commentsLabel.visibility = View.GONE
        }
        Log.d("EXERCISE_ADAPTER", currentItem.name)

        val activity: AppCompatActivity = holder.expandedView.context as AppCompatActivity
        val selectedItemName: TextView = activity.findViewById(R.id.tv_selected_item)
        val viewModel_item = ViewModelProvider(activity).get(ExerciseViewModel::class.java)


        // MUSCLE RECYCLER VIEW
        arrayFrontMuscles = ArrayList<Muscles>()
        arrayBackMuscles = ArrayList<Muscles>()

        for (i in currentItem.muscles.indices) {
            val m = Muscles(currentItem.muscles[i].image_url_main,currentItem.muscles[i]._front)
            Log.d("PATH_Main",currentItem.toString())
            if (currentItem.muscles[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        for (i in currentItem.muscles_secondary.indices) {
            val m = Muscles(currentItem.muscles_secondary[i].image_url_secondary,currentItem.muscles_secondary[i]._front)
            Log.d("PATH_Secondary",currentItem.toString())
            if (currentItem.muscles_secondary[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        // display muscles SVGs
        val frontMuscleAdapter  = MuscleAdapter(arrayFrontMuscles)
        frontMuscleAdapter.notifyDataSetChanged()

        val backMuscleAdapter  = MuscleAdapter(arrayBackMuscles)
        backMuscleAdapter.notifyDataSetChanged()


        Log.d("PATH",arrayFrontMuscles.toString())
        Log.d("PATH",arrayBackMuscles.toString())
        holder.muscleFront.addItemDecoration(MuscleDecoration())
        holder.muscleFront.layoutManager = LinearLayoutManager(holder.muscleFront.context)

        holder.muscleBack.addItemDecoration(MuscleDecoration())
        holder.muscleBack.layoutManager = LinearLayoutManager(holder.muscleBack.context)

        holder.muscleBack.adapter = backMuscleAdapter
        holder.muscleFront.adapter = frontMuscleAdapter



        holder.exerciseName.setOnClickListener {
            notifyDataSetChanged()
            selectedItemName.text = currentItem.name
            selectedItemName.visibility = View.VISIBLE
            exercise = exerciseList[position]
        }

        // add to view model here

        if (exercise == exerciseList[position]){
            holder.expandedView.visibility =  View.VISIBLE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

            if (currentItem.images.isNotEmpty()){
                holder.exerciseImageLayout.visibility = View.VISIBLE
                mainImageAnimation(holder)
            }else{
                holder.exerciseImageLayout.visibility = View.GONE
            }
        }
        else{
            holder.expandedView.visibility =  View.GONE
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E9EDF0"))
        }

        // add exercise to created or selected list when add button pressed
        val addButton = activity.findViewById<FloatingActionButton>(R.id.btn_add_selected)
        addButton.setOnClickListener {
            if(exercise==null){
                Toast.makeText(activity.applicationContext, "Please select an exercise to add", Toast.LENGTH_SHORT).show()
            }else{
                val sharedPreferences_list = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
                sharedPreferences_list.edit().remove(Constants.SELECTED_EXERCISE).commit();
                val selExercise: SelectedExercise = SelectedExercise()
                selExercise.exercise = exercise as Exercise

                val sharedPreferences =
                        activity.getSharedPreferences(
                                Constants.PT_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonArraySelectedExercises = Gson().toJson(selExercise)
                editor.putString(
                        Constants.SELECTED_EXERCISE,
                        jsonArraySelectedExercises
                )
                editor.apply()

                showDialog(activity)

            }

        }

    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    private fun showDialog(activity: AppCompatActivity) {

        var selectedReps:Int = 0

        val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val selectedexercise = sharedPreferences.getString(Constants.SELECTED_EXERCISE,"")
        val sTypeExer = object : TypeToken<SelectedExercise>() { }.type
        val selExercise = Gson().fromJson<SelectedExercise>(selectedexercise,sTypeExer) as SelectedExercise
        val name = selExercise.exercise.name
        val repsData = arrayOf("1x $name", "2x $name", "3x $name", "4x $name")
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

                selExercise.reps = selectedReps

                val sharedPreferences =
                        activity.getSharedPreferences(
                                Constants.PT_PREFERENCES,
                                Context.MODE_PRIVATE
                        )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val jsonArraySelectedExercises = Gson().toJson(selExercise)
                editor.putString(
                        Constants.SELECTED_EXERCISE,
                        jsonArraySelectedExercises
                )
                editor.apply()

                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, EditWorkoutFragment())
                        .commit()
            }
        }
        builder.setNeutralButton("Cancel"){ dialog, which ->

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


    private fun mainImageAnimation(holder: ExerciseViewHolder){

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var tempExerciseList = ArrayList<Exercise>()
                tempExerciseList = tempexerciseList
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    tempExerciseList = tempexerciseList
                } else {
                    val resultList = ArrayList<Exercise>()
                    var search: String



                    for (row in tempExerciseList) {

                        when(charSearch){
                            "Shoulders" -> search = row.category.name
                            "Back" -> search = row.category.name
                            "Chest" -> search = row.category.name
                            "Arms" -> search = row.category.name
                            "Abs" -> search = row.category.name
                            "Legs" -> search = row.category.name
                            "Calves" -> search = row.category.name
                            else -> search = row.name
                        }

                        if (search.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }

                    }
                    tempExerciseList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = tempExerciseList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                exerciseList = results?.values as ArrayList<Exercise>
                notifyDataSetChanged()
            }

        }
    }

}