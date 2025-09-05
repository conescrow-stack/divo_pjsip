package com.Fanuel.divo.data.repository

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.Fanuel.divo.data.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {
    
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        // Check if we have permission to read contacts
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) 
            != PackageManager.PERMISSION_GRANTED) {
            // Return demo contacts instead of throwing exception
            return@withContext getDemoContacts()
        }
        
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?"
        val selectionArgs = arrayOf("1")
        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                
                // Get phone numbers for this contact
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id.toString()),
                    null
                )
                
                phoneCursor?.use { phoneCursor ->
                    while (phoneCursor.moveToNext()) {
                        val phoneNumber = phoneCursor.getString(
                            phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        contacts.add(Contact(id, name, phoneNumber))
                    }
                }
            }
        }
        
        contacts
    }
    
    suspend fun searchContacts(query: String): List<Contact> = withContext(Dispatchers.IO) {
        // Check if we have permission to read contacts
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) 
            != PackageManager.PERMISSION_GRANTED) {
            // Return filtered demo contacts instead of throwing exception
            return@withContext getDemoContacts().filter { contact ->
                contact.displayName.contains(query, ignoreCase = true) ||
                contact.phoneNumber.contains(query)
            }
        }
        
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ? AND ${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?"
        val selectionArgs = arrayOf("%$query%", "1")
        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                
                // Get phone numbers for this contact
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id.toString()),
                    null
                )
                
                phoneCursor?.use { phoneCursor ->
                    while (phoneCursor.moveToNext()) {
                        val phoneNumber = phoneCursor.getString(
                            phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        contacts.add(Contact(id, name, phoneNumber))
                    }
                }
            }
        }
        
        contacts
    }
    
    fun hasContactsPermission(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }
    
    private fun getDemoContacts(): List<Contact> {
        return listOf(
            Contact(1L, "John Smith", "+1-555-0101"),
            Contact(2L, "Sarah Johnson", "+1-555-0102"),
            Contact(3L, "Michael Brown", "+1-555-0103"),
            Contact(4L, "Emily Davis", "+1-555-0104"),
            Contact(5L, "David Wilson", "+1-555-0105"),
            Contact(6L, "Lisa Anderson", "+1-555-0106"),
            Contact(7L, "Robert Taylor", "+1-555-0107"),
            Contact(8L, "Jennifer Martinez", "+1-555-0108"),
            Contact(9L, "William Garcia", "+1-555-0109"),
            Contact(10L, "Amanda Rodriguez", "+1-555-0110"),
            Contact(11L, "James Lopez", "+1-555-0111"),
            Contact(12L, "Michelle Gonzalez", "+1-555-0112"),
            Contact(13L, "Christopher Perez", "+1-555-0113"),
            Contact(14L, "Jessica Torres", "+1-555-0114"),
            Contact(15L, "Daniel Ramirez", "+1-555-0115")
        )
    }
}
