package com.khoirullatif.loginapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.R
import com.khoirullatif.loginapp.databinding.ActivityLoginBinding
import com.khoirullatif.loginapp.ui.OnBoardingActivity
import com.khoirullatif.loginapp.ui.fb.FacebookLoginActivity
import com.khoirullatif.loginapp.ui.firestrore.FirestoreActivity
import com.khoirullatif.loginapp.ui.signupphonenumber.SignUpPhoneNumberActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSingInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInOptions: GoogleSignInOptions

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        mGoogleSingInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val user = if (auth.currentUser != null) {
            "Login as: " + auth.currentUser?.displayName.toString()
        } else {
            "Your didn't login yet"
        }
        binding.tvStatus.text = user

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
//            if (auth.currentUser != null) {
//                auth.signOut()
//            }
            signIn(email, password)
        }

        binding.btnGoogleSignIn.setOnClickListener{
            googleSignIn()
        }

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
        }

        binding.btnPhone.setOnClickListener {
            val intent = Intent(this, SignUpPhoneNumberActivity::class.java)
            startActivity(intent)
        }

        binding.btnFacebook.setOnClickListener {
            val intent = Intent(this, FacebookLoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnFirestore.setOnClickListener {
            val intent = Intent(this, FirestoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Login Success",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Periksa kembali email dan password Anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun googleSignIn() {
        val googleSignInIntent = mGoogleSingInClient.signInIntent
        startActivityForResult(googleSignInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Toast.makeText(this, "Succesfull sign in with google", Toast.LENGTH_SHORT).show()
            val account = task.getResult(ApiException::class.java)!!
            authWithGoogleAccount(account.idToken!!)
        }
    }

    private fun authWithGoogleAccount(idToken: String) {
        Log.e("idToken", idToken)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(this, user.displayName!! + user.phoneNumber + user.email, Toast.LENGTH_SHORT).show()
                    binding.tvStatus.text = "Login sebagai: " + user.displayName
                    binding.btnLogin.visibility = View.GONE
                    binding.btnFirestore.visibility = View.VISIBLE
                    binding.btnRegister.visibility = View.GONE
                } else {
                    binding.tvStatus.text = "Failed login to google"
                }
            }
    }

    override fun onStart() {
        super.onStart()
        //current user bisa langsung detect all method when login (ex. google, facebook, nohp)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            Toast.makeText(this, "You have already sign in", Toast.LENGTH_SHORT).show()
            binding.btnLogin.visibility = View.GONE
            binding.btnRegister.visibility = View.GONE
        } else {
            binding.btnFirestore.visibility = View.GONE
//            binding.btnLogout.visibility = View.GONE
//            binding.btnLogin.visibility = View.VISIBLE
        }
    }
}