package com.khoirullatif.loginapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.R
import com.khoirullatif.loginapp.databinding.ActivityMainBinding
import com.khoirullatif.loginapp.ui.login.LoginActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        val database = Firebase.database
        val myRef = database.getReference("username")

        if (user != null) {
            myRef.child(user.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name = snapshot.getValue<String>().toString()
                    binding.tvEmail.text = user.email?.toString()
                    binding.tvUsername.text = name
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MainActivity", "onCancelled: Failed read value")
                }

            })
        }

        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(p0: View) {

        when (p0.id) {
            R.id.btn_register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}