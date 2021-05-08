package com.lemedebug.personaltrainer.playworkout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.*
import com.lemedebug.personaltrainer.exercise.MuscleAdapter
import com.lemedebug.personaltrainer.exercise.MuscleDecoration
import com.lemedebug.personaltrainer.exercise.Utils
import com.lemedebug.personaltrainer.models.ExerciseModel
import com.lemedebug.personaltrainer.models.Muscles
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants
import com.sevenminuteworkout.ExerciseStatusAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import kotlinx.android.synthetic.main.exercise_image.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {

    private var restTimer: CountDownTimer? =
            null // Variable for Rest Timer and later on we will initialize it.
    private var restProgress =
            0 // Variable for timer progress. As initial the rest progress is set to 0. As we are about to start.

    private var exerciseTimer: CountDownTimer? = null // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseProgress = 0 // Variable for exercise timer progress. As initial the exercise progress is set to 0. As we are about to start.

    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.

    private var tts: TextToSpeech? = null // Variable for Text to Speech

    private var player: MediaPlayer? = null // Created a varible for Media Player to use later.

    // Declaring a exerciseAdapter object which will be initialized later.
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    private var selectedWorkout: Workout? = null
    private var isPaused = false
    private var resumeFromMillis:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val selectedStringWorkout = intent.getStringExtra(Constants.WORKOUT_TO_PLAY)
        val sType = object : TypeToken<Workout>() { }.type
        selectedWorkout = Gson().fromJson<Workout>(selectedStringWorkout,sType) as Workout

        setSupportActionBar(toolbar_exercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="EXERCISE"


        // Navigate the activity on click on back button of action bar.
        toolbar_exercise_activity.setNavigationOnClickListener {
            onBackPressed()
        }


        tts = TextToSpeech(this, this)

        exerciseList = defaultExerciseList(selectedWorkout!!)
//
//        val viewModel = ViewModelProvider(this@ExerciseActivity).get(ExerciseViewModel::class.java)
//        val selectedExerciseList = viewModel.selectedWorkout?.listSelectedExercises
//        Log.i("PLAY EXERCISE viewmodel",viewModel.selectedWorkout?.listSelectedExercises.toString())





        setupRestView() // REST View is set in this function

        // setting up the exercise recycler view
        setupExerciseStatusRecyclerView()
    }


    private fun defaultExerciseList(SelectedWork: Workout): ArrayList<ExerciseModel>{
        val exList = ArrayList<ExerciseModel>()
        val selectedExerciseList = SelectedWork.listSelectedExercises

        Log.i("PLAY EXERCISE",selectedExerciseList.toString())
        var i = 1
        for (e in selectedExerciseList){
            for (j in 1 ..e?.reps!!){
                val exerciseModel = ExerciseModel(i,
                    e.exercise.name,e.exercise.description,e.exercise.images,e.exercise.muscles,e.exercise.muscles_secondary,false,false)
                exList.add(exerciseModel)
                i++
            }
        }

        return exList

    }

    /**
     * Here is Destroy function we will reset the rest timer to initial if it is running.
     */
    public override fun onDestroy() {
        reset()
        super.onDestroy()
    }

    private fun reset(){
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player != null){
            player!!.stop()
        }
    }


    /**
     * This the TextToSpeech override function
     */
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    /**
     * Function is used to set the timer for REST.
     */
    private fun setupRestView() {

        /**
         * Here the sound file is added in to "raw" folder in resources.
         * And played using MediaPlayer
         */
        try {
            val soundURI =
                    Uri.parse("android.resource://com.lemedebug.personaltrainer/"+R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player!!.isLooping = false // Sets the player to be looping or non-looping.
            player!!.start() // Starts Playback.
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Here according to the view make it visible as this is Rest View so rest view is visible and exercise view is not.
        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE

        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }


        // Here we have set the upcoming exercise name to the text view
        // Here as the current position is -1 by default so to selected from the list it should be 0 so we have increased it by +1.
        tvUpcomingExerciseName.text = exerciseList!![currentExercisePosition + 1].name

        // This function is used to set the progress details.
        setRestProgressBar()
    }

    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setRestProgressBar() {

        progressBar.progress = restProgress // Sets the current progress to the specified value.

        /**
         * @param millisInFuture The number of millis in the future from the call
         *   to {#start()} until the countdown is done and {#onFinish()}
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {#onTick(long)} callbacks.
         */
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased to ascending order
                progressBar.progress = 10 - restProgress // Indicates progress bar progress
                tvTimer.text =
                        (10 - restProgress).toString()  // Current progress is set to text view in terms of seconds.
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].isSelected = true // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.

                resumeFromMillis =  30000
                exerciseProgress = 0
                setupExerciseView()
            }
        }.start()
    }

    /**
     * Function is used to set the progress of timer using the progress for Exercise View.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        llRestView.visibility = View.GONE
        llExerciseView.visibility = View.VISIBLE

        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }


        Utils.fetchSvg(body_back.context, "https://wger.de/static/images/muscles/muscular_system_back.svg", body_back)
        Utils.fetchSvg(body_front.context, "https://wger.de/static/images/muscles/muscular_system_front.svg", body_front)

        if (exerciseList!![currentExercisePosition].muscles.isNotEmpty()){
            for (i in exerciseList!![currentExercisePosition].muscles.indices){
                val path =  "https://wger.de${exerciseList!![currentExercisePosition].muscles[i].image_url_main}"
                if (exerciseList!![currentExercisePosition].muscles[i]._front == true){
                    Utils.fetchSvg(iv_muscle_front.context, path, iv_muscle_front)
//                        Picasso.get().load(path).into(holder.muscleFront)
                }else{
                    Utils.fetchSvg(iv_muscle_back.context, path, iv_muscle_back)
//                        Picasso.get().load(path).into(holder.muscleBack)
                }
            }
        }


        if (exerciseList!![currentExercisePosition].muscles_secondary.isNotEmpty()){
            val path =  "https://wger.de${exerciseList!![currentExercisePosition].muscles_secondary[0].image_url_secondary}"
            Log.d("EXERCISE_ADAPTER", path)
            if (exerciseList!![currentExercisePosition].muscles_secondary[0]._front == true){
                Utils.fetchSvg(iv_muscle_front.context, path, iv_muscle_front)
//                        Picasso.get().load(path).into(holder.muscleFront)
            }else{
                Utils.fetchSvg(iv_muscle_back.context, path, iv_muscle_back)
//                        Picasso.get().load(path).into(holder.muscleBack)
            }
        }


        // MUSCLE RECYCLER VIEW
        val arrayFrontMuscles = ArrayList<Muscles>()
        val arrayBackMuscles = ArrayList<Muscles>()

        for (i in exerciseList!![currentExercisePosition].muscles.indices) {
            val m = Muscles(exerciseList!![currentExercisePosition].muscles[i].image_url_main,exerciseList!![currentExercisePosition].muscles[i]._front)
            if (exerciseList!![currentExercisePosition].muscles[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        for (i in exerciseList!![currentExercisePosition].muscles_secondary.indices) {
            val m = Muscles( exerciseList!![currentExercisePosition].muscles_secondary[i].image_url_secondary, exerciseList!![currentExercisePosition].muscles_secondary[i]._front)
            if ( exerciseList!![currentExercisePosition].muscles_secondary[i]._front == true) {
                arrayFrontMuscles.add(m)
            } else {
                arrayBackMuscles.add(m)
            }
        }

        val frontMuscleAdapter  = MuscleAdapter(arrayFrontMuscles)
        frontMuscleAdapter.notifyDataSetChanged()

        val backMuscleAdapter  = MuscleAdapter(arrayBackMuscles)
        backMuscleAdapter.notifyDataSetChanged()

        iv_muscle_front.addItemDecoration(MuscleDecoration())
        iv_muscle_front.layoutManager = LinearLayoutManager(this@ExerciseActivity)

        iv_muscle_back.addItemDecoration(MuscleDecoration())
        iv_muscle_back.layoutManager = LinearLayoutManager(this@ExerciseActivity)

        iv_muscle_back.adapter = backMuscleAdapter
        iv_muscle_front.adapter = frontMuscleAdapter





        /**
         * Here current exercise name and image is set to exercise view.
         */
//        ivImage.setImageResource(exerciseList!![currentExercisePosition].image)
        if (exerciseList!![currentExercisePosition].images.isNotEmpty()){
            tv_play_exercise_description.visibility = View.INVISIBLE
            iv_exercise_play_image.visibility = View.VISIBLE
            iv_exercise_play_image_second.visibility = View.VISIBLE
            Picasso.get().load(exerciseList!![currentExercisePosition].images[0].image).into(iv_exercise_play_image)
            Picasso.get().load(exerciseList!![currentExercisePosition].images[1].image).into(iv_exercise_play_image_second)
            mainImageAnimation()

        }else{
            iv_exercise_play_image.visibility = View.INVISIBLE
            iv_exercise_play_image_second.visibility = View.INVISIBLE
            tv_play_exercise_description.visibility = View.VISIBLE
            tv_play_exercise_description.text = Html.fromHtml(
                exerciseList!![currentExercisePosition].description,
                Html.FROM_HTML_MODE_COMPACT
            )
        }

        tvExerciseName.text = exerciseList!![currentExercisePosition].name

        speakOut(exerciseList!![currentExercisePosition].name)

        resumeFromMillis =  30000
        exerciseProgress = 0

        setExerciseProgressBar()
    }

    /**
     * Function is used to set the progress of timer using the progress for Exercise View for 30 Seconds
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setExerciseProgressBar() {

        val millisInFuture:Long = 30000
        val countDownInterval:Long = 1000

        button_pause.isEnabled = true
        timer(millisInFuture,countDownInterval).start()
        isPaused = false
        button_pause.setOnClickListener(View.OnClickListener { v: View
         ->
            isPaused = true
            button_resume.isEnabled = true
            button_pause.isEnabled = false
                    Toast.makeText(
                    this@ExerciseActivity,
                    "Workout is paused.",
                    Toast.LENGTH_SHORT
            ).show()
        })

        button_resume.setOnClickListener(View.OnClickListener { v: View
            ->

            timer(resumeFromMillis,countDownInterval).start()
            isPaused = false
            button_pause.isEnabled = true
            button_resume.isEnabled = false
                    Toast.makeText(
                    this@ExerciseActivity,
                    "Workout is resumed.",
                    Toast.LENGTH_SHORT
            ).show()
            })
    }

    private fun timer(millisInFuture:Long,countDownInterval:Long):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {

                if (isPaused) {
                    resumeFromMillis = millisUntilFinished
                    cancel()
                } else {
                    exerciseProgress++
                    progressBarExercise.progress = 30 - exerciseProgress
                    tvExerciseTimer.text = (30 - exerciseProgress).toString()
                }

            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].isSelected = false // exercise is completed so selection is set to false
                exerciseList!![currentExercisePosition].isCompleted = true // updating in the list that this exercise is completed
                exerciseAdapter!!.notifyDataSetChanged() // Notifying to adapter class.

                if (currentExercisePosition < exerciseList!!.size - 1) {
                    setupRestView()
                } else {
                    Toast.makeText(
                            this@ExerciseActivity,
                            "Congratulations! You have completed the workout.",
                            Toast.LENGTH_SHORT
                    ).show()
                    val temp = currentExercisePosition + 1
                    val total: String = temp.toString()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    intent.putExtra(Constants.COMPLETED_WORKOUT, selectedWorkout!!.name)
                    intent.putExtra(Constants.TOTAL_EXERCISES, total)
                    startActivity(intent)
                    finish()
                }
            }


        }
    }

    /**
     * Function is used to speak the text what we pass to it.
     */
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    /**
     * Function is used to set up the recycler view to UI and assing the Layout Manager and Adapter Class is attached to it.
     */
    private fun setupExerciseStatusRecyclerView() {

        // Defining a layout manager to recycle view
        // Here we have used Linear Layout Manager with horizontal scroll.
        rvExerciseStatus.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // As the adapter expect the exercises list and context so initialize it passing it.
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)

        // Adapter class is attached to recycler view
        rvExerciseStatus.adapter = exerciseAdapter
    }

    /**
     * Function is used to launch the custom confirmation dialog.
     */
    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener {
            reset()
            player = null
            super.onBackPressed()
            customDialog.dismiss() // Dialog will be dismissed
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        customDialog.show()
    }

    /**
     * Function is used to animate main image
     */
    private fun mainImageAnimation(){

        val fadeIn = ObjectAnimator.ofFloat(iv_exercise_play_image, "alpha", 0f, 1f)
        fadeIn.duration = 4000
        val fadeOut = ObjectAnimator.ofFloat(iv_exercise_play_image, "alpha", 1f, 0f)
        fadeOut.duration = 2000
        val fadeInBehind = ObjectAnimator.ofFloat(iv_exercise_play_image_second, "alpha", 0f, 1f)
        fadeInBehind.duration = 4000
        val fadeOutBehind = ObjectAnimator.ofFloat(iv_exercise_play_image_second, "alpha", 1f, 0f)
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

    /**
     * Overriding on Back Pressed
     */
    override fun onBackPressed() {
        customDialogForBackButton()
    }
}