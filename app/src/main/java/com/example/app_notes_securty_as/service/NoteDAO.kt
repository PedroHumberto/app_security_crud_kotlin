package com.example.app_notes_securty_as.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.app_notes_securty_as.domain.models.Note
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteDAO {

    private val COLLECTION = "note"
    private val db = Firebase.firestore

    fun addNote(note: Note){
        db.collection(COLLECTION).add(note)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    //apenas notas feitas pelo usuario com idAuth pode ter acesso aos itens.
    fun getNotes(idAuth: String): Task<QuerySnapshot>{
        return db.collection(COLLECTION).whereEqualTo("idAuth", idAuth).get()
    }
}