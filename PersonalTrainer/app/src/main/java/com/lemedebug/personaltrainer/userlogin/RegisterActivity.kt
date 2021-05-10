package com.lemedebug.personaltrainer.userlogin

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lemedebug.personaltrainer.R
import com.lemedebug.personaltrainer.models.User
import com.lemedebug.personaltrainer.firestore.FirestoreClass
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_buy_coffee.*

@Suppress("DEPRECATION")
class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_register.setOnClickListener {

            registerUser()
        }

        // START
        tv_login.setOnClickListener{

            onBackPressed()
        }

        tv_terms_condition.setOnClickListener {
            customDialogForTermsAndConditions()
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_confirm_password),
                        true
                )
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                    .trim { it <= ' ' } -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                        true
                )
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_agree_terms_and_condition),
                        true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to register the user with email and password using FirebaseAuth.
     */
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                // If the registration is successfully done
                                if (task.isSuccessful) {

                                    // Firebase registered user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    val user = User(
                                            firebaseUser.uid,
                                            et_first_name.text.toString().trim(){ it <= ' '},
                                            et_last_name.text.toString().trim(){ it <= ' '},
                                            et_email.text.toString().trim(){ it <= ' '}
                                    )

                                    FirestoreClass().registerUser(this@RegisterActivity, user)


//                                    FirebaseAuth.getInstance().signOut()
                                    // Finish the Register Screen
//                                    finish()
                                } else {
                                    hideProgressDialog()
                                    // If the registering is not successful then show error message.
                                    showErrorSnackBar(task.exception!!.message.toString(), true)
                                }
                            })
        }
    }

    fun userRegistrationSuccess(){

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
                this@RegisterActivity,
                resources.getString(R.string.register_success),
                Toast.LENGTH_SHORT
        ).show()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()

    }



    /**
     * Function is used to launch the terms and conditions.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun customDialogForTermsAndConditions() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.layout_buy_coffee)

        setSupportActionBar(customDialog.toolbar_buy_coffee)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="TERMS & CONDITIONS"

        customDialog.toolbar_buy_coffee.setNavigationOnClickListener {
            customDialog.dismiss()
        }


        customDialog.web_view.loadUrl("https://www.termsfeed.com/live/3d289b25-008b-4aaa-9004-ef4f46ce3b8f")
        customDialog.web_view.settings.javaScriptEnabled = true


        // If you want more control over where a clicked link loads,
        // create your own WebViewClient that overrides the shouldOverrideUrlLoading() method.
        customDialog.web_view.webViewClient = object : WebViewClient(){

            // shouldOverrideUrlLoading() checks whether the URL host matches a specific domain
            // If it matches, then the method returns false in order to not override the URL loading
            // (it allows the WebView to load the URL as usual).
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // show the progress bar
                customDialog.progress_bar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // hide the progress bar
                customDialog.progress_bar.visibility = View.GONE
            }
        }

        //Start the dialog and display it on screen.
        customDialog.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {
            web_view.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

}