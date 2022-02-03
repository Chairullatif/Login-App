package com.khoirullatif.loginapp.ui.signupphonenumber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.khoirullatif.loginapp.databinding.ActivitySignUpWithNumberResultBinding
import com.khoirullatif.loginapp.ui.login.LoginActivity

class SignUpWithNumberResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpWithNumberResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpWithNumberResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOk.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}