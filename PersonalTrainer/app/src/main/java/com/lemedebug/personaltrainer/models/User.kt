package com.lemedebug.personaltrainer.models

import com.lemedebug.personaltrainer.exercise.WorkoutEntity

data class User(
        val id: String = "",
        val firstName:String = "",
        val lastName:String = "",
        val email:String = "",
        val workoutList:List<WorkoutEntity> = ArrayList()
)


