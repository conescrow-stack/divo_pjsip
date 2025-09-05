package com.Fanuel.divo.data.model

import java.util.Date

data class CallHistory(
    val id: Long = 0,
    val contactName: String?,
    val phoneNumber: String,
    val callDate: Date,
    val duration: Long, // in seconds
    val isOutgoing: Boolean = true
)
