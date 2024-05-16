package com.casecode.data.service

import com.casecode.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AuthService {
    override val currentUserId: String
        get() =   /*auth.currentUser?.uid.orEmpty()*/  "gOGCYjEnHRYqBu9YVub7Be1xUU93"

    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth -> this.trySend(auth.currentUser) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override val hasUser: Boolean
        get() = auth.currentUser?.uid?.isNotBlank() == true
}