package com.lemedebug.personaltrainer.models

data class User(
    val id: String = "",
    val firstName:String = "",
    val lastName:String = "",
    val email:String = "",
    var workoutList:ArrayList<Workout> = ArrayList()
)


data class ExerciseData(
    val results: List<Exercise>
)

data class Workout(
        var name: String? = "",
        val listSelectedExercises: ArrayList<SelectedExercise?> = ArrayList()
)

data class SelectedExercise(
    var exercise: Exercise = Exercise(),
    var reps: Int? = -1
)


data class Exercise(
    val id:String = "",
    val name:String = "",
    val description:String = "",
    val category: Category = Category(),
    val muscles:List<MainMuscles> = ArrayList<MainMuscles>(),
    val muscles_secondary:List<SecondaryMuscles> = ArrayList<SecondaryMuscles>(),
    val equipment:List<Equipment> = ArrayList<Equipment>(),
    val images:List<ExerciseImage> = ArrayList<ExerciseImage>(),
    val comments:List<Comment> = ArrayList<Comment>()
)

data class Category(
    val name: String = ""
)

data class MainMuscles(
    val image_url_main: String = "",
    val is_front: Boolean = false
)

data class SecondaryMuscles(
    val image_url_secondary: String = "",
    val is_front: Boolean = false
)

data class Equipment(
    val name:String = ""
)

data class ExerciseImage(
    val image: String = "",
    val is_main: Boolean = false
)

data class Comment(
    val comment:String = ""
)
