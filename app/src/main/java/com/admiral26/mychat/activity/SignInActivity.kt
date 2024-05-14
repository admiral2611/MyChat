package com.admiral26.mychat.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.admiral26.mychat.databinding.ActivitySignInBinding
import com.admiral26.mychat.util.Constants
import com.admiral26.mychat.util.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignInBinding.inflate(layoutInflater) }
    private var preferenceManager: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager?.getBoolean(Constants.KEY_IS_SIGNED_IN) == true) {
            val intentMy = Intent(applicationContext, MainActivity::class.java)
            startActivity(intentMy)
            finish()
        }
        setListeners()
    }

    private fun setListeners() {
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.signInButton.setOnClickListener {
            if (isValidateSignIn()) {
                signIn()
            }
        }
    }

    private fun signIn() {
        showProgressBar(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString()).get()
            .addOnCompleteListener {
                showProgressBar(false)
                if (it.isSuccessful && it.result != null && it.result!!.documents.size > 0) {
                    val documentSnapshot = it.result!!.documents[0]
                    preferenceManager?.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager?.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager?.putString(
                        Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME)
                    )
                    preferenceManager?.putString(
                        Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE)
                    )
                    val intentMy = (Intent(this, MainActivity::class.java))
                    intentMy.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intentMy)
                } else {
                    showToast("Unable to sign in")

                }
            }
    }

    private fun showProgressBar(boolean: Boolean) {
        if (boolean) {
            binding.signInButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.signInButton.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidateSignIn(): Boolean {
        if (binding.inputEmail.text.toString().isEmpty()) {
            showToast("Please enter your email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Please enter a valid email")
            return false
        } else if (binding.inputPassword.text.toString().isEmpty()) {
            showToast("Please enter your password")
            return false
        } else {
            return true
        }
    }
}