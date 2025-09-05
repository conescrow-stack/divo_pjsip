package com.Fanuel.divo.data.model

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val displayName: String = name
)
