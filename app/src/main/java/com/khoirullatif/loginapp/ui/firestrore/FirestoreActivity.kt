package com.khoirullatif.loginapp.ui.firestrore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.khoirullatif.loginapp.databinding.ActivityFirestoreBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class FirestoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirestoreBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var usersData: User

    companion object {
        private val TAG = FirestoreActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirestoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        db = Firebase.firestore
        val user = auth.currentUser

        //start listening data realtime
        getDataRealtime(user!!.uid)

        binding.btnSave.setOnClickListener {
            val id = user.uid
            val firstName = binding.edtFirstName.text.toString()
            val lastName = binding.edtLastName.text.toString()
            val age = binding.edtAge.text.toString().toInt()

            val userData = User(
                id,
                firstName,
                lastName,
                age
            )

            //begin to store data
            storeDataSameUser(userData)
        }

        binding.btnRefreshMethod.setOnClickListener {
            getData(user.uid)
        }
    }

    //get data from firestore
    private fun getData(userId: String) = CoroutineScope(IO).launch {
        try {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        usersData = document.toObject<User>()!!
                        initView(usersData)
                    }
                }
            withContext(Main) {
                Toast.makeText(this@FirestoreActivity, "Success to get data", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Main) {
                Toast.makeText(this@FirestoreActivity, "Failed to get data ${e.toString()}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "getData: failed $e")
            }
        }
    }

    //listen data from firebase realtime
    private fun getDataRealtime(userId: String) {
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    Log.d(TAG, "getDataRealtime: Listen Failed", error)
//                    return@addSnapshotListener
//                }
//                if (snapshot != null && snapshot.exists()) {
//                        usersData = snapshot.toObject<User>()!!
//                        initViewRealtime(usersData)
//                }

                //with .let() function, this called scope function
                error?.let {
                    Log.d(TAG, "getDataRealtime: Listen failed", it)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    usersData = it.toObject<User>()!!
                    initViewRealtime(usersData)
                }
            }
    }

    //store data to firestore when same user
    private fun storeDataSameUser(user: User) = CoroutineScope(IO).launch {
        try {
            // .set() .add() .doc() is equivalent
            db.collection("users").document(user.userId.toString())
                .set(user)
                .addOnCompleteListener {
                    Log.d(TAG, "storeData: success")
                }
                .addOnFailureListener {
                    Log.d(TAG, "storeData: failed")
                }
            withContext(Main){
                Toast.makeText(this@FirestoreActivity, "Success to store data", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Main){
                Toast.makeText(this@FirestoreActivity, "Failed to store data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView(usersData: User) {
        binding.apply {
            tvNameFromFirestore.text = usersData.firstName + " " + usersData.lastName
            tvAgeFromFirestore.text = usersData.age.toString()
        }
    }

    private fun initViewRealtime(userData: User) {
        binding.apply {
            tvNameFromFirestoreRealtime.text = userData.firstName + " " + userData.lastName
            tvAgeFromFirestoreRealtime.text = userData.age.toString()
        }
    }
}