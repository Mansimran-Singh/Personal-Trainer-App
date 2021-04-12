package com.lemedebug.personaltrainer.exercise

import androidx.lifecycle.ViewModel

class ExerciseViewModel:ViewModel() {
    var exerciseInfo:ExerciseInfo = ExerciseInfo("1",
                                                "Sample Exercise",
                                                "ARMS",
                                                null,
                                                "Lorem Ipsum Some Description",
                                                null,
                                                null,
                                                null,
                                                null)
}