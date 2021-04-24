package com.lemedebug.personaltrainer.firestore

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.userlogin.LoginActivity
import com.lemedebug.personaltrainer.userlogin.RegisterActivity
import com.lemedebug.personaltrainer.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

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
                    Log.e("Register_Activity","Error while registering")
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

    fun getUserDetails(activity: Activity){
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.i("FirestoreClass",document.toString())
                    val user = document.toObject(User::class.java)!!

                    when(activity){
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                    }

                }
    }
}