package com.fb.myapplication.models

import java.util.UUID
import java.util.Date

data class ContactGroup(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val members: MutableList<String>,
    val adminId: String,
    val createdAt: Date = Date()
) {
    fun addMember(contactName: String) {
        if (!members.contains(contactName)) {
            members.add(contactName)
        }
    }

    fun removeMember(contactName: String) {
        members.remove(contactName)
    }

    fun isMember(contactName: String): Boolean {
        return members.contains(contactName)
    }
}