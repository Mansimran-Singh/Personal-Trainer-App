package com.lemedebug.personaltrainer.playworkout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.exercise.ExerciseViewModel
import com.lemedebug.personaltrainer.models.ExerciseModel
import com.lemedebug.personaltrainer.models.Workout
import com.lemedebug.personaltrainer.utils.Constants
import com.sevenminuteworkout.ExerciseStatusAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)


        val selectedStringWorkout = intent.getStringExtra(Constants.WORKOUT_TO_PLAY)
        val sType = object : TypeToken<Workout>() { }.type
        val selectedWorkout = Gson().fromJson<Workout>(selectedStringWorkout,sType) as Workout


        setSupportActionBar(toolbar_exercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Navigate the activity on click on back button of action bar.
        toolbar_exercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)

        exerciseList = defaultExerciseList(selectedWorkout)
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
            val exerciseModel = ExerciseModel(i,e!!.exercise.name,e.exercise.images,false,false)
            exList.add(exerciseModel)
            i++
        }

        return exList

    }

    /**
     * Here is Destroy function we will reset the rest timer to initial if it is running.
     */
    public override fun onDestroy() {
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
        super.onDestroy()
    }

    /**
     * This the TextToSpeech override function
     *
     * Called to signal the completion of the TextToSpeech engine initialization.
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
         * And played using MediaPlayer. MediaPlayer class can be used to control playback
         * of audio/video files and streams.
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

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].isSelected = true // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.

                setupExerciseView()
            }
        }.start()
    }

    /**
     * Function is used to set the progress of timer using the progress for Exercise View.
     */
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

        /**
         * Here current exercise name and image is set to exercise view.
         */
//        ivImage.setImageResource(exerciseList!![currentExercisePosition].image)
        if (exerciseList!![currentExercisePosition].images.isNotEmpty()){

            Picasso.get().load(exerciseList!![currentExercisePosition].images[0].image).into(iv_exercise_play_image)
            Picasso.get().load(exerciseList!![currentExercisePosition].images[1].image).into(iv_exercise_play_image_second)
            mainImageAnimation()

        }

        tvExerciseName.text = exerciseList!![currentExercisePosition].name

        speakOut(exerciseList!![currentExercisePosition].name)

        setExerciseProgressBar()
    }

    /**
     * Function is used to set the progress of timer using the progress for Exercise View for 30 Seconds
     */
    private fun setExerciseProgressBar() {

        progressBarExercise.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                progressBarExercise.progress = 30 - exerciseProgress
                tvExerciseTimer.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                exerciseList!![currentExercisePosition].isSelected = false // exercise is completed so selection is set to false
                exerciseList!![currentExercisePosition].isCompleted = true // updating in the list that this exercise is completed
                exerciseAdapter!!.notifyDataSetChanged() // Notifying to adapter class.

                if (currentExercisePosition < 11) {
                    setupRestView()
                } else {

                    val viewModel = ViewModelProvider(this@ExerciseActivity).get(ExerciseViewModel::class.java)
                    Toast.makeText(
                            this@ExerciseActivity,
                            "Congratulations! ${viewModel.user.firstName} You have completed ${viewModel.selectedWorkout?.name} workout.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
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
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener {
            finish()
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
}