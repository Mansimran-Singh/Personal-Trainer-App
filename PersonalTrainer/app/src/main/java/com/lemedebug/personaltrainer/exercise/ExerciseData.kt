package com.lemedebug.personaltrainer.exercise

data class ExerciseData(
    val results: List<Exercise>
)


data class Exercise(
    val id:String,
    val name:String,
    val description:String,
    val category:Category,
    val muscles:List<MainMuscles>,
    val muscles_secondary:List<SecondaryMuscles>,
    val equipment:List<Equipment>,
    val images:List<ExerciseImage>,
    val comments:List<Comment>
){var expand : Boolean = false}

data class Category(
    val name: String
)

data class MainMuscles(
    val image_url_main: String,
    val is_front: Boolean
)

data class SecondaryMuscles(
    val image_url_secondary: String,
    val is_front: Boolean
)

data class Equipment(
    val name:String
)

data class ExerciseImage(
    val image: String,
    val is_main:String
)

data class Comment(
    val comment:String
)
