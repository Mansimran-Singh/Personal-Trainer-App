package com.lemedebug.personaltrainer.models

data class User(
        val id: String = "",
        val firstName:String = "",
        val lastName:String = "",
        val email:String = "",
        var workoutList:ArrayList<Workout> = ArrayList(),
        var completedWorkoutList:ArrayList<CompletedWorkout> = ArrayList()
)

data class RandomQuote(
        val author:String,
        val quote:String
)

data class ExerciseData(
        val results: List<Exercise>
)

data class Workout(
        var name: String? = "",
        var listSelectedExercises: ArrayList<SelectedExercise?> = ArrayList()
)

data class CompletedWorkout(
        val workout: String? = "",
        val totalExercises: String? = "0",
        val date:String? =""
)

data class SelectedExercise(
        var exercise: Exercise = Exercise(),
        var reps: Int? = -1
)

data class ExerciseModel(
        var id: Int,
        var name: String,
        val description: String,
        val images:List<ExerciseImage>,
        val muscles: List<MainMuscles>,
        val muscles_secondary: List<SecondaryMuscles>,
        var isCompleted: Boolean,
        var isSelected: Boolean
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

data class Muscles(
        val image_url: String = "",
        val _front: Boolean? = null
)

data class MainMuscles(
        val image_url_main: String = "",
        val _front: Boolean?= null
)

data class SecondaryMuscles(
        val image_url_secondary: String = "",
        val _front: Boolean?= null
)

data class Equipment(
        val name:String = ""
)

data class ExerciseImage(
        val image: String = "",
        val _main: Boolean = true
)

data class Comment(
        val comment:String = ""
)