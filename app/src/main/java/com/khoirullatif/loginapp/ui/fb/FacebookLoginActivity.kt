package com.khoirullatif.loginapp.ui.fb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.databinding.ActivityFacebookLoginBinding

class FacebookLoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityFacebookLoginBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        val TAG = FacebookLoginActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacebookLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase auth
        auth = Firebase.auth
        binding.tvFbStatus.text = auth.currentUser?.displayName

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        //check status
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        //callbackManager
        callbackManager = CallbackManager.Factory.create()

        //facebookBtnLogin
        binding.btnFacebookLogin.setReadPermissions("email")
        binding.btnFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, "Facebook onSuccess: $result")
                if (result != null) {
                    handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
                Log.d(TAG, " Facebook onCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "Facebook onError", error)
            }

        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken: ${token.token}")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "handleFacebookAccessToken: success")
                    val user = auth.currentUser
                    binding.tvFbStatus.text = user?.displayName
                    Toast.makeText(this, user.displayName, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "handleFacebookAccessToken: ${task.exception}")
                    Toast.makeText(this, "Auth with facebook failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(this, currentUser.displayName, Toast.LENGTH_SHORT).show()
        }
    }
}