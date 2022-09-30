package com.example.app_notes_securty_as.domain.models

import com.google.firebase.firestore.DocumentId

data class User(
    var idAuth: String? = null,
    var name: String? = null,
    var email: String? = null,
) {
}