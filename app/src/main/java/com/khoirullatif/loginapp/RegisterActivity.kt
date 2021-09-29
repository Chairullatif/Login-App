package com.khoirullatif.loginapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    companion object {
        private val TAG = RegisterActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordRepeat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database

        binding.btnRegister.setOnClickListener {
            name = binding.edtUsername.text.toString()
            email = binding.edtEmail.text.toString()
            password = binding.edtPassword.text.toString()
            passwordRepeat = binding.edtPasswordRepeat.text.toString()

            val check = checkColumn(name, email, password, passwordRepeat)
            if (check) {
                createAccount(name, email, password)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Toast.makeText(this, "You have already login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAccount(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.d(TAG, "createAccount: success")
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()

                    val myRef = database.getReference("username")
                    auth.uid?.let { it1 -> myRef.child(it1).setValue(name) }
                } else {
                    Log.d(TAG, "createAccount: failed")
                    Toast.makeText(this, "Register Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkColumn(name: String, email: String, password: String, passwordRepeat: String): Boolean {
        if (name.isEmpty()) {
            binding.edtUsername.error = "Nama harus diisi"
            binding.edtUsername.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            binding.edtEmail.error = "Email harus diisi"
            binding.edtEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Email tidak sesuai"
            binding.edtEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = "Password harus diisi"
            binding.edtPassword.requestFocus()
            return false
        }
        if (passwordRepeat.isEmpty()) {
            binding.edtPasswordRepeat.error = "Ulangi password harus diisi"
            binding.edtPasswordRepeat.requestFocus()
            return false
        }
        if (password != passwordRepeat) {
            binding.edtPasswordRepeat.error = "Password tidak sama"
            return false
        }

        return true
    }
}