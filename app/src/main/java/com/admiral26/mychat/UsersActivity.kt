package com.admiral26.mychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.admiral26.mychat.adapter.UserAdapter
import com.admiral26.mychat.databinding.ActivityUsersBinding
import com.admiral26.mychat.listeners.UserListener
import com.admiral26.mychat.model.User
import com.admiral26.mychat.util.Constants
import com.admiral26.mychat.util.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UsersActivity : AppCompatActivity(), UserListener {
    private val binding by lazy { ActivityUsersBinding.inflate(layoutInflater) }
    private val adapter by lazy { UserAdapter() }
    private var preferenceManager: PreferenceManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        getUsers()
        setListener()
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showError() {
        binding.txtError.text = (String.format("Error %s", "No User Found"))
        binding.txtError.visibility = View.VISIBLE
    }

    private fun getUsers() {
        showLoading(true)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                showLoading(false)
                val currentUserId = preferenceManager?.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    val users =
                        ArrayList<User>()
                    for (documentSnapshot in task.result!!) {
                        if (documentSnapshot.id == currentUserId) {
                            continue
                        }
                        val user = User(
                            name = documentSnapshot.getString(Constants.KEY_NAME),
                            image = documentSnapshot.getString(Constants.KEY_IMAGE),
                            email = documentSnapshot.getString(Constants.KEY_EMAIL),
                            token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN),
                            id = documentSnapshot.id
                        )
                        users.add(user)
                    }
                    if (users.size > 0) {
                        binding.rvList.adapter = adapter
                        adapter.setData(users, this)
                        binding.rvList.visibility = View.VISIBLE
                    } else {
                        showError()
                    }
                } else {
                    showError()
                }
            }

    }

    override fun onUserClick(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }
}