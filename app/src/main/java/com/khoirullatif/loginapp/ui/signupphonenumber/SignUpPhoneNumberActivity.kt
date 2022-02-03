package com.khoirullatif.loginapp.ui.signupphonenumber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.khoirullatif.loginapp.databinding.ActivitySignUpPhoneNumberBinding
import com.khoirullatif.loginapp.ui.otp.OtpActivity

class SignUpPhoneNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpPhoneNumberBinding

    companion object {
        val TAG = SignUpPhoneNumberActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val phoneNumber = binding.edtPhone.text.toString()
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra(OtpActivity.EXTRA_NUMBER, phoneNumber)
            Log.d(TAG, "onCreate: phone = $phoneNumber")
            startActivity(intent)
        }
    }
}