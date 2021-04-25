package com.lemedebug.personaltrainer.exercise


data class ExerciseData(
    val results: List<Exercise>
)

data class WorkoutEntity(
        var name: String = "",
        var exerciseList: List<Exercise>? = ArrayList<Exercise>()
)

data class RepsEntity(
        var reps : Int = -1,
        val workoutName : String = "",
        val exerciseID: String = ""
)


data class Exercise(
        val id:String = "",
        val name:String = "",
        val description:String = "",
        val category:Category = Category(""),
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
