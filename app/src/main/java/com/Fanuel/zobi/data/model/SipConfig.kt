package com.Fanuel.zobi.data.model

data class SipConfig(
    val username: String = "",
    val password: String = "",
    val domain: String = "",
    val audioCodec: AudioCodec = AudioCodec.G711_PCMU,
    val rememberMe: Boolean = false
)

enum class AudioCodec(val displayName: String, val codecName: String) {
    G711_PCMU("G.711 PCMU", "PCMU"),
    G711_PCMA("G.711 PCMA", "PCMA"),
    OPUS("Opus", "opus"),
    G729("G.729", "G729")
}
