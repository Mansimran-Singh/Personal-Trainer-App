package com.lemedebug.personaltrainer.exercise

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseService {
//    BASE = "https://wger.de/"
//    static/images/muscles/muscular_system_front.svg


    @GET(".")
    fun getExerciseList(@Query("language") language: Int,@Query("limit") count: Int): Call<ExerciseData>

}
