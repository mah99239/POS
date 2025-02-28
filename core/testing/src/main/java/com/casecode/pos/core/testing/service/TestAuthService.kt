package com.casecode.pos.core.testing.service

import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.model.data.LoginStateResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestAuthService
    @Inject
    constructor() : AuthService {
        override val loginData: Flow<LoginStateResult> = flowOf(LoginStateResult.NotSignIn)

        override suspend fun hasUser(): Boolean = true

    override suspend fun currentUserId(): String = "uidTest"

    override suspend fun currentNameLogin(): String {
        return "TestName"
    }

    override val currentUser: Flow<FirebaseUser?> = flowOf(null)

    override suspend fun hasEmployeeLogin(): Boolean = true
}