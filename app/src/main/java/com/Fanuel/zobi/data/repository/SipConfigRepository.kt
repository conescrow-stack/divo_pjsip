package com.Fanuel.zobi.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.Fanuel.zobi.data.model.AudioCodec
import com.Fanuel.zobi.data.model.SipConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sipDataStore: DataStore<Preferences> by preferencesDataStore(name = "sip_config")

class SipConfigRepository(private val context: Context) {
    
    private val usernameKey = stringPreferencesKey("username")
    private val passwordKey = stringPreferencesKey("password")
    private val domainKey = stringPreferencesKey("domain")
    private val audioCodecKey = stringPreferencesKey("audio_codec")
    private val rememberMeKey = booleanPreferencesKey("remember_me")
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    
    suspend fun saveSipConfig(config: SipConfig) {
        context.sipDataStore.edit { preferences ->
            preferences[usernameKey] = config.username
            preferences[passwordKey] = config.password
            preferences[domainKey] = config.domain
            preferences[audioCodecKey] = config.audioCodec.name
            preferences[rememberMeKey] = config.rememberMe
            preferences[isLoggedInKey] = true
        }
    }
    
    suspend fun getSipConfig(): SipConfig {
        val preferences = context.sipDataStore.data.first()
        return SipConfig(
            username = preferences[usernameKey] ?: "",
            password = preferences[passwordKey] ?: "",
            domain = preferences[domainKey] ?: "",
            audioCodec = try {
                AudioCodec.valueOf(preferences[audioCodecKey] ?: AudioCodec.G711_PCMU.name)
            } catch (e: Exception) {
                AudioCodec.G711_PCMU
            },
            rememberMe = preferences[rememberMeKey] ?: true // Default to true
        )
    }
    
    fun getSipConfigFlow(): Flow<SipConfig> {
        return context.sipDataStore.data.map { preferences ->
            SipConfig(
                username = preferences[usernameKey] ?: "",
                password = preferences[passwordKey] ?: "",
                domain = preferences[domainKey] ?: "",
                audioCodec = try {
                    AudioCodec.valueOf(preferences[audioCodecKey] ?: AudioCodec.G711_PCMU.name)
                } catch (e: Exception) {
                    AudioCodec.G711_PCMU
                },
                rememberMe = preferences[rememberMeKey] ?: true // Default to true
            )
        }
    }
    
    suspend fun clearSipConfig() {
        context.sipDataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.sipDataStore.data.first()
        val isLoggedIn = preferences[isLoggedInKey] ?: false
        val username = preferences[usernameKey] ?: ""
        val domain = preferences[domainKey] ?: ""
        
        return isLoggedIn && username.isNotEmpty() && domain.isNotEmpty()
    }
}
