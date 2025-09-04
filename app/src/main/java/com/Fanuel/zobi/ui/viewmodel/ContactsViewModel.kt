package com.Fanuel.zobi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Fanuel.zobi.data.model.Contact
import com.Fanuel.zobi.data.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val contactsRepository = ContactsRepository(application)
    
    private val _contactsState = MutableStateFlow(ContactsState())
    val contactsState: StateFlow<ContactsState> = _contactsState.asStateFlow()
    
    private var allContacts = listOf<Contact>()
    
    init {
        loadContacts()
    }
    
    fun loadContacts() {
        _contactsState.value = _contactsState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                allContacts = contactsRepository.getContacts()
                _contactsState.value = ContactsState(
                    contacts = allContacts,
                    isLoading = false,
                    error = null,
                    hasPermission = contactsRepository.hasContactsPermission(),
                    isDemoMode = !contactsRepository.hasContactsPermission()
                )
            } catch (e: Exception) {
                _contactsState.value = ContactsState(
                    contacts = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Failed to load contacts",
                    hasPermission = contactsRepository.hasContactsPermission(),
                    isDemoMode = !contactsRepository.hasContactsPermission()
                )
            }
        }
    }
    
    fun searchContacts(query: String) {
        if (query.isBlank()) {
            _contactsState.value = _contactsState.value.copy(
                contacts = allContacts,
                searchQuery = ""
            )
            return
        }
        
        viewModelScope.launch {
            try {
                val searchResults = contactsRepository.searchContacts(query)
                _contactsState.value = _contactsState.value.copy(
                    contacts = searchResults,
                    searchQuery = query
                )
            } catch (e: Exception) {
                _contactsState.value = _contactsState.value.copy(
                    contacts = emptyList(),
                    error = e.message ?: "Search failed"
                )
            }
        }
    }
    
    fun clearSearch() {
        _contactsState.value = _contactsState.value.copy(
            contacts = allContacts,
            searchQuery = ""
        )
    }
    
    fun refreshContacts() {
        loadContacts()
    }
}

data class ContactsState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val hasPermission: Boolean = true,
    val isDemoMode: Boolean = false
)
