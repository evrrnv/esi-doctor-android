package com.example.esiproject.data

import com.example.esiproject.utils.AppAuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(private val appAuthManager: AppAuthManager) {

    suspend fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?): Result<String> {
        return withContext(Dispatchers.IO) {
            appAuthManager.checkAuthorization(response, exception)
        }
    }

    fun getAuthRequest(): AuthorizationRequest = appAuthManager.getAuthRequest()
}