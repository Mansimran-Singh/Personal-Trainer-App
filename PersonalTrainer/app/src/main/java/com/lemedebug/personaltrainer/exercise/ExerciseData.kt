package com.lemedebug.personaltrainer.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ExerciseData(
    val results: List<Exercise>
)

@Entity(tableName = "workout_table")
data class WorkoutEntity(
        @PrimaryKey
        var name: String,
        var exerciseList: List<Exercise>?
)

@Entity(tableName = "reps_table")
data class RepsEntity(
        @PrimaryKey(autoGenerate = true)
        var id : Int,
        var reps : Int,
        val workoutName : String,
        val exerciseID: Int
)


@Entity(tableName = "exercise_table")
data class Exercise(
    @PrimaryKey
    val id:String,
    val name:String,
    val description:String,
    val category:Category,
    val muscles:List<MainMuscles>,
    val muscles_secondary:List<SecondaryMuscles>,
    val equipment:List<Equipment>,
    val images:List<ExerciseImage>,
    val comments:List<Comment>
)

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
    val is_main: Boolean
)

data class Comment(
    val comment:String
)
