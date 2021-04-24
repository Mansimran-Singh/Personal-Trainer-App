package com.lemedebug.personaltrainer.exercise

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.Instant

class Converters {

    @TypeConverter
    fun exerciseListToJson(value: List<Exercise>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToExercise(value: String?) = Gson().fromJson(value, Array<Exercise?>::class.java).toList()

    @TypeConverter
    fun categoryListToJson(value: List<Category>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToCategory(value: String) = Gson().fromJson(value, Array<Category>::class.java).toList()

    @TypeConverter
    fun fromInstant(value: Category?): String {
        return value.toString()
    }
    @TypeConverter
    fun toInstant(value: String): Category {
        return Category(value)
    }

    @TypeConverter
    fun mainMusclesListToJson(value: List<MainMuscles>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToMainMuscles(value: String) = Gson().fromJson(value, Array<MainMuscles>::class.java).toList()

    @TypeConverter
    fun SecondaryMusclesListToJson(value: List<SecondaryMuscles>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToSecondaryMuscles(value: String) = Gson().fromJson(value, Array<SecondaryMuscles>::class.java).toList()

    @TypeConverter
    fun EquipmentListToJson(value: List<Equipment>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToEquipment(value: String) = Gson().fromJson(value, Array<Equipment>::class.java).toList()

    @TypeConverter
    fun exerciseImageListToJson(value: List<ExerciseImage>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToExerciseImage(value: String) = Gson().fromJson(value, Array<ExerciseImage>::class.java).toList()

    @TypeConverter
    fun commentMusclesListToJson(value: List<Comment>?) = Gson().toJson(value)
    @TypeConverter
    fun jsonToComment(value: String) = Gson().fromJson(value, Array<Comment>::class.java).toList()


}