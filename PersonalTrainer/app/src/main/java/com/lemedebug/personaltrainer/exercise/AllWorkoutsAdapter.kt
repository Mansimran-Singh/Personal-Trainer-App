package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.EditWorkoutFragment
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.playworkout.ExerciseActivity
import com.lemedebug.personaltrainer.utils.Constants


class AllWorkoutsAdapter(private var workoutList: ArrayList<Workout>) : RecyclerView.Adapter<AllWorkoutsAdapter.AllWorkoutsAdapterViewHolder>() {

    private var workout:Workout? = null
    inner class AllWorkoutsAdapterViewHolder(workoutView: View): RecyclerView.ViewHolder(workoutView){
        val cv = workoutView.findViewById<CardView>(R.id.cv_workout_short)
        val name = workoutView.findViewById<TextView>(R.id.tv_workout_name)
        val exerciseCount = workoutView.findViewById<TextView>(R.id.tv_exercise_count)
        val playButton = workoutView.findViewById<LottieAnimationView>(R.id.btn_start_workout)
        val editButton = workoutView.findViewById<TextView>(R.id.btn_edit_workout)
        val deleteButton = workoutView.findViewById<TextView>(R.id.btn_delete_workout)
        val layoutUtilities = workoutView.findViewById<RelativeLayout>(R.id.rl_workout_options)
        val reminderButton = workoutView.findViewById<TextView>(R.id.btn_reminder_workout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllWorkoutsAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_short,
            parent,
            false
        )


        return AllWorkoutsAdapterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AllWorkoutsAdapterViewHolder, position: Int) {

        val activity: AppCompatActivity = holder.cv.context as AppCompatActivity

        val currentItem = workoutList[position]
        holder.name.text = currentItem.name
        holder.exerciseCount.text = currentItem.listSelectedExercises.size.toString() + " Exercises"


        if (workout != null && currentItem == workout){
            val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
            val user = sharedPreferences.getString(Constants.LOGGED_USER, "")
            val sType = object : TypeToken<User>() { }.type
            val loggedUser = Gson().fromJson<User>(user, sType) as User

            var workoutList = loggedUser.workoutList

            val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
            viewModel.selectedWorkout = currentItem
            Log.i("PLAY EXERCISE", "Setting workout to ${viewModel.selectedWorkout}")

            holder.cv.setCardBackgroundColor(Color.parseColor("#E3D5DA"))
            holder.playButton.visibility = View.VISIBLE
            holder.layoutUtilities.visibility = View.VISIBLE


            holder.playButton.setOnClickListener {
                // START PLAY WORKOUT ACTIVITY

                val intent = Intent(activity, ExerciseActivity::class.java)
                val listSerializedToJson = Gson().toJson(viewModel.selectedWorkout)
                intent.putExtra(Constants.WORKOUT_TO_PLAY,listSerializedToJson)
                activity.startActivity(intent)
            }

            holder.editButton.setOnClickListener {

                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, EditWorkoutFragment())
                        .commit()
            }

            holder.deleteButton.setOnClickListener {
                // CODE FOR DELETING WORKOUT
               loggedUser.workoutList.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                FirestoreClass().deleteWorkoutList(activity,loggedUser)

            }

            holder.reminderButton.setOnClickListener {
                // CODE FOR REMINDER WORKOUT
            }

        }else{
            holder.cv.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.playButton.visibility = View.INVISIBLE
            holder.layoutUtilities.visibility = View.INVISIBLE
        }

        holder.cv.setOnClickListener {
            workout = currentItem
            // val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
           // viewModel.selectedWorkout = workout
            notifyDataSetChanged()
        }



    }

    override fun getItemCount(): Int {
        return workoutList.size
    }
}