package com.lemedebug.personaltrainer.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.lemedebug.personaltrainer.models.Exercise
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.userlogin.LoginActivity
import com.lemedebug.personaltrainer.userlogin.RegisterActivity
import com.lemedebug.personaltrainer.utils.Constants

class FirestoreClass {

    val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
                // document UID
                .document(userInfo.id)
                // add whatever userInfo present
                .set(userInfo, SetOptions.merge())
                // On Success listener
                .addOnSuccessListener {
                    activity.userRegistrationSuccess()
                }
                // on fail
                .addOnFailureListener {
                    activity.hideProgressDialog()
                    Log.e("Register_Activity", "Error while registering")
                }
    }

    fun updateUser(activity: Activity, userInfo: User){
        mFireStore.collection(Constants.USERS)
                // document UID
                .document(userInfo.id)
                // add whatever userInfo present
                .set(userInfo, SetOptions.merge())
                // On Success listener
                .addOnSuccessListener {
                }
                // on fail
                .addOnFailureListener {
                    Log.e("Register_Activity", "Error while registering")
                }
    }

    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: AppCompatActivity){
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i("FirestoreClass", document.toString())
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences =
                            activity.getSharedPreferences(
                                    Constants.PT_PREFERENCES,
                                    Context.MODE_PRIVATE
                            )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
//                    editor.putString(
//                            Constants.LOGGED_IN_USERNAME,
//                            "${user.firstName} ${user.lastName}"
//                    )
//                    editor.apply()

//                    val viewModel = ViewModelProvider(activity).get(ExerciseViewModel::class.java)
////                    viewModel.user = user

                    // Convert Workouts to json
                    val jsonArrayLoggedUser = Gson().toJson(user)
                    editor.putString(
                            Constants.LOGGED_USER,
                            jsonArrayLoggedUser
                    )
                    editor.apply()

//
//                    Log.e("USER","${viewModel.user}")
//                    Log.e("USER","${user}")

                    // Convert Workouts to json
                    val jsonArrayWorkouts = Gson().toJson(user.workoutList)
                    // Save json to local
                    editor.putString(
                            Constants.WORKOUTS,
                            jsonArrayWorkouts
                    )
                    editor.apply()


                    when(activity){
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                    }

                }
    }

    fun getExerciseList(activity: Activity){

        val sharedPreferences = activity.getSharedPreferences(Constants.PT_PREFERENCES, Context.MODE_PRIVATE)
        val exerciseList = sharedPreferences.getString(Constants.EXERCISES, "")
        val exerciseInfoList = ArrayList<Exercise>()

        if (exerciseList.isNullOrEmpty()){

            mFireStore.collection(Constants.EXERCISES)
                    .orderBy("name")  // Here you can also use orderBy to sort the results based on a field such as id
                    //.orderBy("id", Query.Direction.DESCENDING)  // this would be used to orderBy in DESCENDING order
                    .get()
                    .addOnSuccessListener { documents ->

                        val buffer = StringBuffer()

                        // The result (documents) contains all the records in db, each of them is a document
                        for (document in documents) {
                            Log.d("EXERCISE_LIST", "${document.id} => $document")
                            val exercise = document.toObject(Exercise::class.java)
                            exerciseInfoList.add(exercise)
                        }


                        val jsonArrayExercises = Gson().toJson(exerciseInfoList)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(
                                Constants.EXERCISES,
                                jsonArrayExercises
                        )
                        editor.apply()

                    }

        }

    }


    fun addExerciseList(activity: Activity, exerciseInfo: Exercise){
        mFireStore.collection(Constants.EXERCISES)
                // document UID
                .document(exerciseInfo.id)
                // add whatever exerciseInfo present
                .set(exerciseInfo, SetOptions.merge())
                // On Success listener
                .addOnSuccessListener {
//                    Log.e("ExerciseList","Successfully added")
                }
                // on fail
                .addOnFailureListener {
//                    Log.e("ExerciseList","Failure adding")
                }
    }

    fun updateWorkoutList(activity: Activity, userInfo: User) {
        val docRef = mFireStore.collection(Constants.USERS).document(userInfo.id)
        docRef.update("workoutList",userInfo.workoutList)
                // On Success listener
                .addOnSuccessListener {
//                    Log.e("ExerciseList","Successfully added")
                }
                // on fail
                .addOnFailureListener {
//                    Log.e("ExerciseList","Failure adding")
                }
    }

    fun deleteWorkoutList(activity: Activity, userInfo: User) {
        val docRef = mFireStore.collection(Constants.USERS).document(userInfo.id)
        docRef.update("workoutList",userInfo.workoutList)
                // On Success listener
                .addOnSuccessListener {
//                    Log.e("ExerciseList","Successfully added")
                }
                // on fail
                .addOnFailureListener {
//                    Log.e("ExerciseList","Failure adding")
                }
        //docRef.update({
          //  ['workoutList.' + name]: firebase.firestore.FieldValue.delete()
        //});

        /*docRef.update("workoutList",userInfo.workoutList)
            // On Success listener
            .addOnSuccessListener {
//                    Log.e("ExerciseList","Successfully added")
            }
            // on fail
            .addOnFailureListener {
//                    Log.e("ExerciseList","Failure adding")
            }*/
    }


}