package com.lemedebug.personaltrainer.exercise

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.EditWorkoutFragment
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.playworkout.ExerciseActivity
import com.lemedebug.personaltrainer.utils.Constants
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import kotlin.collections.ArrayList


class AllWorkoutsAdapter(private var workoutList: ArrayList<Workout>) : RecyclerView.Adapter<AllWorkoutsAdapter.AllWorkoutsAdapterViewHolder>() {

    // variable declaration for workouts
    private var workout:Workout? = null
    inner class AllWorkoutsAdapterViewHolder(workoutView: View): RecyclerView.ViewHolder(workoutView){
        val cv = workoutView.findViewById<CardView>(R.id.cv_workout_short)
        val name = workoutView.findViewById<TextView>(R.id.tv_workout_name)
        val exerciseCount = workoutView.findViewById<TextView>(R.id.tv_exercise_count)
        val playButton = workoutView.findViewById<LottieAnimationView>(R.id.btn_start_workout)
        val editButton = workoutView.findViewById<TextView>(R.id.btn_edit_workout)
        val deleteButton = workoutView.findViewById<TextView>(R.id.btn_delete_workout)
        val layoutUtilities = workoutView.findViewById<RelativeLayout>(R.id.rl_workout_options)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllWorkoutsAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.workout_short,
            parent,
            false
        )


        return AllWorkoutsAdapterViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AllWorkoutsAdapterViewHolder, position: Int) {
       // get the selected workout in current item
        val activity: AppCompatActivity = holder.cv.context as AppCompatActivity

        val currentItem = workoutList[position]
        holder.name.text = currentItem.name
        holder.exerciseCount.text = currentItem.listSelectedExercises.size.toString() + " Exercises"

        // check if workout has any exercises
        // if null give message else play workout
        if (workout != null && currentItem == workout){
            val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
            val user = sharedPreferences.getString(Constants.LOGGED_USER, "")
            val sType = object : TypeToken<User>() { }.type
            val loggedUser = Gson().fromJson<User>(user, sType) as User

            val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
            viewModel.selectedWorkout = currentItem
            Log.i("PLAY EXERCISE", "Setting workout to ${viewModel.selectedWorkout}")

            holder.cv.setCardBackgroundColor(Color.parseColor("#E9EDF0"))
            holder.playButton.visibility = View.VISIBLE
            holder.layoutUtilities.visibility = View.VISIBLE


            holder.playButton.setOnClickListener {
                // START PLAY WORKOUT ACTIVITY

                if (viewModel.selectedWorkout == null || viewModel.selectedWorkout!!.listSelectedExercises.size < 1){
                    Toast.makeText(activity, "Cannot play empty workout",Toast.LENGTH_SHORT).show()
                }else{
                    val intent = Intent(activity, ExerciseActivity::class.java)
                    val listSerializedToJson = Gson().toJson(viewModel.selectedWorkout)
                    intent.putExtra(Constants.WORKOUT_TO_PLAY,listSerializedToJson)
                    activity.startActivity(intent)
                    activity.finish()
                }

            }


            // edit workout button press to selected edit workout
            holder.editButton.setOnClickListener {

                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.exercise_view_container, EditWorkoutFragment())
                        .commit()
            }

            // delete workout from recycler view and Firestore
            holder.deleteButton.setOnClickListener {
                // Launch Dialog
                val customDialog = Dialog(activity)
                customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
                customDialog.tv_confirmation_detail.text = "This will delete the workout permanently"
                customDialog.tvYes.setOnClickListener {
                    // Delete workout
                    loggedUser.workoutList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                    FirestoreClass().updateWorkoutList(activity,loggedUser)

                    customDialog.dismiss() // Dialog will be dismissed
                }
                customDialog.tvNo.setOnClickListener {
                    customDialog.dismiss()
                }
                //Start the dialog and display it on screen.
                customDialog.show()

            }

        }else{
            holder.cv.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.playButton.visibility = View.INVISIBLE
            holder.layoutUtilities.visibility = View.INVISIBLE
        }

        holder.cv.setOnClickListener {
            workout = currentItem
            notifyDataSetChanged()
        }



    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

}