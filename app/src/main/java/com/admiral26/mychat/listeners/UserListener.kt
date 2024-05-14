package com.admiral26.mychat.listeners

import com.admiral26.mychat.model.User

interface UserListener {
    fun onUserClick(user: User)
}