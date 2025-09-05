package com.Fanuel.divo.data.model

data class CallState(
    val isInCall: Boolean = false,
    val phoneNumber: String = "",
    val contactName: String? = null,
    val callStatus: CallStatus = CallStatus.IDLE,
    val duration: Long = 0, // in seconds
    val isSpeakerOn: Boolean = false,
    val isMuted: Boolean = false
)

enum class CallStatus {
    IDLE,
    DIALING,
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    FAILED
}
