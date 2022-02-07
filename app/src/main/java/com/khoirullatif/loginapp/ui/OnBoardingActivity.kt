package com.khoirullatif.loginapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.databinding.ActivityOnBoardingBinding
import com.khoirullatif.loginapp.ui.login.LoginActivity

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
//        if (auth.currentUser != null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }
    }
}