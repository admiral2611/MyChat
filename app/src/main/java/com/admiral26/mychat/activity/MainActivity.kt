package com.admiral26.mychat.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.admiral26.mychat.UsersActivity
import com.admiral26.mychat.databinding.ActivityMainBinding
import com.admiral26.mychat.util.Constants
import com.admiral26.mychat.util.PreferenceManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var preferenceManager: PreferenceManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        loadUserDetail()
        getToken()
        setListener()
    }

    private fun setListener() {
        binding.ivCancel.setOnClickListener {
            signOut()
        }
        binding.fabAddChat.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }
    }

    private fun loadUserDetail() {
        binding.tvName.text = preferenceManager?.getString(Constants.KEY_NAME)
        val byte = Base64.decode(preferenceManager?.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        binding.imgUser.setImageBitmap(BitmapFactory.decodeByteArray(byte, 0, byte.size))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            updateToken(it)
        }
    }

    private fun updateToken(token: String) {
        val firestore = FirebaseFirestore.getInstance()
        val documentReference = firestore.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager?.getString(Constants.KEY_USER_ID)!!
        )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
                showToast("Token Updated")
            }
            .addOnFailureListener {
                showToast(it.message.toString())
            }
    }

    private fun signOut() {
        showToast("Sign Out")
        val firestore = FirebaseFirestore.getInstance()
        val documentReference = preferenceManager?.getString(Constants.KEY_USER_ID)?.let {
            firestore.collection(
                Constants.KEY_COLLECTION_USERS
            ).document(
                it
            )
        }
        val hashMap = HashMap<String, Any>()
        hashMap[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference?.update(hashMap)
            ?.addOnSuccessListener {
                preferenceManager?.clear()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            ?.addOnFailureListener {
                showToast(it.message.toString())
            }
    }
}