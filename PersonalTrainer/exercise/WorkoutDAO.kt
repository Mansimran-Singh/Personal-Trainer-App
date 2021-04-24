package com.lemedebug.personaltrainer.exercise

import androidx.room.*

@Dao
interface WorkoutDAO {
    // OnConflictStrategy is optional, but good idea to specify
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWorkout(workout: WorkoutEntity)

    @Update
    fun updateWorkout(workout: WorkoutEntity)

    @Delete
    fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workout_table ")
    fun viewAllWorkouts() : List<WorkoutEntity>

    @Query("SELECT * FROM workout_table WHERE name LIKE :workoutName")
    fun findWorkout(workoutName: String) : WorkoutEntity
}

@Dao
interface ExerciseDAO {
    // OnConflictStrategy is optional, but good idea to specify
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(exercise: Exercise)

    @Update
    fun updateExercise(exercise: Exercise)

    @Delete
    fun deleteExercise(exercise: Exercise)

    @Query("SELECT * FROM exercise_table ")
    fun viewAllExercises() : List<Exercise>

    @Query("SELECT COUNT(*) FROM exercise_table ")
    fun getExerciseCount() : Int

    @Query("SELECT * FROM exercise_table WHERE id LIKE :exerciseId")
    fun findExercise(exerciseId: String) : Exercise

}
@Dao
interface RepsDAO {
    // OnConflictStrategy is optional, but good idea to specify
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReps(reps: RepsEntity)

    @Update
    fun updateReps(reps: RepsEntity)

    @Delete
    fun deleteReps(reps: RepsEntity)

    @Query("SELECT * FROM reps_table ")
    fun viewAllReps() : List<RepsEntity>

    @Query("SELECT * FROM reps_table WHERE workoutName LIKE :workoutName AND exerciseID LIKE :exerciseID")
    fun findReps(workoutName: String,exerciseID: Int) : RepsEntity

}