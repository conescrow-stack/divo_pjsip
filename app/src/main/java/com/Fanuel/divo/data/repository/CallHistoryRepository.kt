package com.Fanuel.divo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.Fanuel.divo.data.model.CallHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "call_history")

class CallHistoryRepository(private val context: Context) {
    
    private val callHistoryKey = stringPreferencesKey("call_history")
    
    suspend fun addCallHistory(callHistory: CallHistory) {
        val currentHistory = getCallHistory().toMutableList()
        currentHistory.add(0, callHistory) // Add to beginning
        
        // Keep only last 100 calls
        if (currentHistory.size > 100) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        context.dataStore.edit { preferences ->
            val historyJson = currentHistory.joinToString("|") { call ->
                "${call.id},${call.contactName ?: ""},${call.phoneNumber},${call.callDate.time},${call.duration},${call.isOutgoing}"
            }
            preferences[callHistoryKey] = historyJson
        }
    }
    
    suspend fun getCallHistory(): List<CallHistory> {
        val preferences = context.dataStore.data.first()
        val historyJson = preferences[callHistoryKey] ?: return emptyList()
        
        return if (historyJson.isNotEmpty()) {
            historyJson.split("|").mapNotNull { callString ->
                try {
                    val parts = callString.split(",")
                    if (parts.size >= 6) {
                        CallHistory(
                            id = parts[0].toLongOrNull() ?: 0L,
                            contactName = if (parts[1].isNotEmpty()) parts[1] else null,
                            phoneNumber = parts[2],
                            callDate = Date(parts[3].toLong()),
                            duration = parts[4].toLongOrNull() ?: 0L,
                            isOutgoing = parts[5].toBoolean()
                        )
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        } else {
            emptyList()
        }
    }
    
    suspend fun clearCallHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(callHistoryKey)
        }
    }
    
    fun getCallHistoryFlow(): Flow<List<CallHistory>> {
        return context.dataStore.data.map { preferences ->
            val historyJson = preferences[callHistoryKey] ?: return@map emptyList()
            
            if (historyJson.isNotEmpty()) {
                historyJson.split("|").mapNotNull { callString ->
                    try {
                        val parts = callString.split(",")
                        if (parts.size >= 6) {
                            CallHistory(
                                id = parts[0].toLongOrNull() ?: 0L,
                                contactName = if (parts[1].isNotEmpty()) parts[1] else null,
                                phoneNumber = parts[2],
                                callDate = Date(parts[3].toLong()),
                                duration = parts[4].toLongOrNull() ?: 0L,
                                isOutgoing = parts[5].toBoolean()
                            )
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            } else {
                emptyList()
            }
        }
    }
}
