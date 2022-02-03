package com.khoirullatif.loginapp.ui.otp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.R
import com.khoirullatif.loginapp.databinding.ActivityOtpBinding
import com.khoirullatif.loginapp.ui.login.LoginActivity
import com.khoirullatif.loginapp.ui.signupphonenumber.SignUpPhoneNumberActivity
import com.khoirullatif.loginapp.ui.signupphonenumber.SignUpWithNumberResultActivity
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var storedVerificationCode: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String

    private lateinit var binding: ActivityOtpBinding

    companion object {
        const val EXTRA_NUMBER = "extra_number"
        val TAG = OtpActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        auth.languageCode = "id"

        phoneNumber =
            "+62" + intent.getStringExtra(EXTRA_NUMBER).toString().replaceFirst("^0+(?!$)", "")
        Log.d(TAG, "onCreate: phoneNumb = $phoneNumber")


        // 1. Send verification code to user's phone
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks())
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.btnVerify.setOnClickListener {
            val code = binding.edtCode.text.toString()
            val credential = PhoneAuthProvider.getCredential(storedVerificationCode, code)
            Log.d(TAG, "onCreate: code = $storedVerificationCode")
            signInWithPhoneAuthCredential(credential)
        }

    }

    private fun callbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        val callback =
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(SignUpPhoneNumberActivity.TAG, "onVerificationCompleted:$credential")
                    val codeSms = credential.smsCode
                    if (codeSms != null) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(SignUpPhoneNumberActivity.TAG, "onVerificationFailed", e)
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this@OtpActivity, "Invalid request", Toast.LENGTH_SHORT)
                            .show()
                    } else if (e is FirebaseTooManyRequestsException) {
                        Toast.makeText(this@OtpActivity, "Too many request", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    Log.d(SignUpPhoneNumberActivity.TAG, "onCodeSent:$verificationId")

                    storedVerificationCode = verificationId
                    resendToken = token
                }
            }

        return callback
    }

    private fun signInWithPhoneAuthCredential(phoneCredential: PhoneAuthCredential) {
        auth.signInWithCredential(phoneCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "Sign Up with number success", Toast.LENGTH_SHORT).show()
                    val intent =
                        Intent(this@OtpActivity, SignUpWithNumberResultActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to sign up", Toast.LENGTH_SHORT).show()
                }
            }
    }


}