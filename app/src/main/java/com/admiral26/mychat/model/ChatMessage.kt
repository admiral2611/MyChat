package com.admiral26.mychat.model

import java.util.Date

data class ChatMessage(
    val senderId: String?,
    val receiverId: String?,
    val text: String?,
    val dataTime: String?,
    val dataObject: Date?
)
