package com.admiral26.mychat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Base64
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.admiral26.mychat.adapter.ChatAdapter
import com.admiral26.mychat.adapter.MultiAdapter
import com.admiral26.mychat.databinding.ActivityChatBinding
import com.admiral26.mychat.model.ChatMessage
import com.admiral26.mychat.model.User
import com.admiral26.mychat.util.Constants
import com.admiral26.mychat.util.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private var receiverUser: User? = null
    private var listMessage = ArrayList<ChatMessage>()
    private lateinit var multiAdapter: MultiAdapter
    private lateinit var chatAdapter: ChatAdapter
    private var preferenceManager: PreferenceManager? = null
    private var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadReceiverUser()
        setListeners()
        init()
        listenMessage()

    }

    private fun init() {
        preferenceManager = PreferenceManager(this)
        listMessage = ArrayList()
        multiAdapter = MultiAdapter(
            preferenceManager!!.getString(Constants.KEY_USER_ID),
            receiverUser?.image?.let { getBitmapFromEncodedString(it) },
            listMessage
        )
        binding.recyclerView.setAdapter(multiAdapter)
        firestore = FirebaseFirestore.getInstance()
    }

    private fun listenMessage() {
        firestore?.collection(Constants.KEY_COLLECTION_CHAT)
            ?.whereEqualTo(
                Constants.KEY_SENDER_ID,
                preferenceManager?.getString(Constants.KEY_USER_ID)
            )
            ?.whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser?.id)
            ?.addSnapshotListener(eventListener)
        firestore?.collection(Constants.KEY_COLLECTION_CHAT)
            ?.whereEqualTo(Constants.KEY_SENDER_ID, receiverUser?.id)
            ?.whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferenceManager?.getString(Constants.KEY_USER_ID)
            )
            ?.addSnapshotListener(eventListener)

    }

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                val count = listMessage.size
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val chatMessage = ChatMessage(
                            senderId = dc.document.getString(Constants.KEY_SENDER_ID)!!,
                            receiverId = dc.document.getString(Constants.KEY_RECEIVER_ID)!!,
                            text = dc.document.getString(Constants.KEY_MESSAGE)!!,
                            dataTime = getReadableDateTime(dc.document.getDate(Constants.KEY_TIMESTAMP)!!),
                            dataObject = dc.document.getDate(Constants.KEY_TIMESTAMP)!!
                        )
                        listMessage.add(chatMessage)
                    }
                }
                listMessage.sortWith(Comparator { o1, o2 ->
                    o1.dataObject?.compareTo(o2.dataObject)!!
                })
                if (count == 0) {
                    multiAdapter.notifyDataSetChanged()
                } else {
                    multiAdapter.setData(listMessage)
                    //multiAdapter.notifyItemRangeInserted(listMessage.size, listMessage.size)
                    binding.recyclerView.smoothScrollToPosition(listMessage.size - 1)
                }
                binding.recyclerView.visibility = VISIBLE
            }
            binding.progressBar.visibility = GONE
        }

    private fun getBitmapFromEncodedString(encodeImg: String): Bitmap {
        val bytes = Base64.decode(encodeImg, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setListeners() {
        val inputMessage: TextInputLayout = findViewById(R.id.textInputLayout)
        val customEndIcon: Drawable = resources.getDrawable(R.drawable.ic_send, null)
        inputMessage.endIconDrawable = customEndIcon
        inputMessage.setEndIconOnClickListener {
            sendMessage()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadReceiverUser() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.userName.text = receiverUser?.name
    }

    private fun sendMessage() {
        val message = HashMap<String, Any>()
        message[Constants.KEY_SENDER_ID] = preferenceManager?.getString(Constants.KEY_USER_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverUser?.id!!
        message[Constants.KEY_MESSAGE] = binding.textInputLayout.editText?.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        firestore?.collection(Constants.KEY_COLLECTION_CHAT)?.add(message)
        binding.textInputLayout.editText?.setText("")
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }
}