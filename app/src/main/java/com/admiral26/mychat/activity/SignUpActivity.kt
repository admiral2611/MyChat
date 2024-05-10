package com.admiral26.mychat.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.admiral26.mychat.databinding.ActivitySignUpBinding
import com.admiral26.mychat.util.Constants
import com.admiral26.mychat.util.Constants.KEY_EMAIL
import com.admiral26.mychat.util.Constants.KEY_IMAGE
import com.admiral26.mychat.util.Constants.KEY_IS_SIGNED_IN
import com.admiral26.mychat.util.Constants.KEY_NAME
import com.admiral26.mychat.util.Constants.KEY_PASSWORD
import com.admiral26.mychat.util.Constants.KEY_USER_ID
import com.admiral26.mychat.util.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }
    private var enCodedImage: String = ""
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListeners()

    }

    private fun signUp() {
        showProgress(true)
        val firebaseUser = FirebaseFirestore.getInstance()
        val user: HashMap<String, Any> = HashMap()
        user[KEY_NAME] = binding.inputName.text.toString()
        user[KEY_EMAIL] = binding.inputEmail.text.toString()
        user[KEY_PASSWORD] = binding.inputPassword.text.toString()
        user[KEY_IMAGE] = enCodedImage
        firebaseUser.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener { documentReference ->
                showProgress(false)
                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(KEY_USER_ID, documentReference.id)
                preferenceManager.putString(KEY_NAME, binding.inputName.text.toString())
                preferenceManager.putString(KEY_IMAGE, enCodedImage)
                val intentMy = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMy)
            }
            .addOnFailureListener { e ->
                showProgress(false)
                Log.d("error11", "signUp: ${e.message}")
                e.message?.let { showToast(it) }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignUpDetails(): Boolean {
        if (enCodedImage.isEmpty()) {
            showToast("Please select an image")
            return false
        } else if (binding.inputName.text.toString().isEmpty()) {
            showToast("Please enter your name")
            return false
        } else if (binding.inputEmail.text.toString().isEmpty()) {
            showToast("Please enter your email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Please enter a valid email address")
            return false
        } else if (binding.inputPassword.text.toString().isEmpty()) {
            showToast("Please enter your password")
            return false
        } else if (binding.inputConfirmPassword.text.toString().isEmpty()) {
            showToast("Please confirm your password")
            return false
        } else if (binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()) {
            showToast("Password and confirm password do not match")
            return false
        } else {
            return true
        }
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener {
            onBackPressed()
        }
        binding.signUpButton.setOnClickListener {
            if (isValidSignUpDetails()) {
                signUp()
            }
        }
        binding.imgLayout.setOnClickListener {
            checkPermission()
        }
    }

    private fun showProgress(isLoading: Boolean) {
        if (isLoading) {
            binding.signUpButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.signUpButton.visibility = View.VISIBLE
        }
    }

    private fun enCodedImage(bitmap: Bitmap): String? {
        val previewWith = 150
        val previewHeight = bitmap.height * previewWith / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWith, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val imageUri: Uri? = result.data!!.data
                try {
                    val inputStream =
                        contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageAvatar.setImageBitmap(bitmap)
                    enCodedImage = enCodedImage(bitmap).toString()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
// Inside your activity or fragment

    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission has already been granted
            openImagePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to open image picker
                openImagePicker()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        // Your code to open the image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(intent)


    }


}