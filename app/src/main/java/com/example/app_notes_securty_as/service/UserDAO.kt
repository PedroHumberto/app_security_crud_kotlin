package com.example.app_notes_securty_as.service

import android.content.ContentValues
import android.util.Log
import com.example.app_notes_securty_as.domain.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDAO {


    private val COLLECTION = "users"
    private val db = Firebase.firestore


    fun addUser(user: User){
        db.collection(COLLECTION).document(user.idAuth!!).set(user)
            .addOnSuccessListener {documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference}")

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun getUser(idAuth: String): Task<DocumentSnapshot> {
        return db.collection(COLLECTION).document(idAuth).get()
    }

}