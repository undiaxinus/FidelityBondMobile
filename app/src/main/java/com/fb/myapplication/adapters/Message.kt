package com.fb.myapplication.adapters

data class Message(
    val id: Long,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val phoneNumber: String? = null,
    val bondNumber: String? = null
)