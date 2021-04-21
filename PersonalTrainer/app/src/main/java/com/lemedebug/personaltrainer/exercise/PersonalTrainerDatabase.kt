package com.lemedebug.personaltrainer.exercise

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WorkoutEntity::class, Exercise::class, RepsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class PersonalTrainerDatabase: RoomDatabase() {

    abstract fun workoutDAO() : WorkoutDAO
    abstract fun exerciseDAO() : ExerciseDAO
    abstract fun repsDAO() : RepsDAO

}