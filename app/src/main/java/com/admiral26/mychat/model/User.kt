package com.admiral26.mychat.model

import java.io.Serializable

data class User(
    val name: String?,
    val image: String?,
    val email: String?,
    val token: String?,
    val id: String?
) : Serializable

