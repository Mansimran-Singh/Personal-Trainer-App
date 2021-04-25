package com.lemedebug.personaltrainer.exercise

import androidx.lifecycle.ViewModel
import com.lemedebug.personaltrainer.models.SelectedExercise
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.models.Workout

class ExerciseViewModel: ViewModel() {
    var user: User = User()
    var selectedExercise: SelectedExercise = SelectedExercise()
    var selectedWorkout: Workout? = null
}