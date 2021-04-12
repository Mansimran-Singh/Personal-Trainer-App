package com.lemedebug.personaltrainer.exercise

data class ExerciseInfo(
        val id: String,
        val name:String,
        val category: String,
        val equipment: String?,
        val description:String,
        val image: ArrayList<String>?,
        val comments: String?,
        val musclesMain: ArrayList<MuscleInfo>?,
        val musclesSecondary: ArrayList<MuscleInfo>?
) {var expand : Boolean = false}