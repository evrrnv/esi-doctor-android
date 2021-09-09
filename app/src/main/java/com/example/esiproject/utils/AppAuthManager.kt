package com.example.esiproject.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import net.openid.appauth.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppAuthManager (context: Context) {

    private val tag = this.javaClass.simpleName

    private val authEndpoint = "https://auth.moun3im.com/auth/realms/1sc-project/protocol/openid-connect/auth"
    private val tokenEndpoint = "https://auth.moun3im.com/auth/realms/1sc-project/protocol/openid-connect/token"
    private val redirectUri = Uri.parse("esiproject://oauth2redirect")
    private val clientId = "web"

    private val authStateManager: AuthStateManager = AuthStateManager(context)
    private val authService: AuthorizationService
    private val authRequest: AuthorizationRequest

    init {
        if (authStateManager.authState!!.authorizationServiceConfiguration == null) {
            val serviceConfig = AuthorizationServiceConfiguration(
                Uri.parse(authEndpoint),
                Uri.parse(tokenEndpoint)
            )
            authStateManager.authState = AuthState(serviceConfig)
        }
        val authRequestBuilder = AuthorizationRequest.Builder(
            authStateManager.authState!!.authorizationServiceConfiguration!!,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
        authRequest = authRequestBuilder.build()
        authService = AuthorizationService(context)
    }

    suspend fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?): Result<String> {
        return if (isAuthorized()) {
            Log.i(tag, "Already authorized.")
            if (isAccessTokenExpired()) {
                startRefreshAccessToken()
            } else {
                Result.success(getAccessToken()!!)
            }
        } else {
            Log.i(tag, "Not yet authorized.")
            startAuthCodeExchange(response, exception)
        }
    }

    private suspend fun startRefreshAccessToken(): Result<String> {
        return suspendCoroutine {cont ->
            authService.performTokenRequest(
                authStateManager.authState!!.createTokenRefreshRequest()
            ) { tokenResponse, authException ->
                cont.resume(handleCodeExchangeResponse(tokenResponse, authException))
            }
        }
    }

    private suspend fun startAuthCodeExchange(response: AuthorizationResponse?, exception: AuthorizationException?): Result<String> {
        if (response != null || exception != null) {
            authStateManager.updateAfterAuthorization(response, exception)
        }

        return when {
            response?.authorizationCode != null -> {
                authStateManager.updateAfterAuthorization(response, exception)
                return exchangeAuthorizationCode(response)
            }
            exception != null -> {
                Result.failure(Exception("Authorization flow failed: " + exception.message))
            }
            else -> {
                Result.failure(Exception("Authorization flow failed: " + exception?.message))
            }
        }
    }

    private suspend fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse): Result<String> {
        return suspendCoroutine { cont ->
            authService.performTokenRequest(
                authorizationResponse.createTokenExchangeRequest()
            ) { tokenResponse, authException ->
                cont.resume(handleCodeExchangeResponse(tokenResponse, authException))
            }
        }
    }

    private fun handleCodeExchangeResponse(tokenResponse: TokenResponse?, authException: AuthorizationException?): Result<String> {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException)
        Log.i(tag, "IsAuth: " + authStateManager.authState!!.isAuthorized)
        return if (!authStateManager.authState!!.isAuthorized) {
            Result.failure(Exception("failed to exchange authorization code"))
        } else {
            Log.i(tag, "RefreshToken: ${authStateManager.authState?.refreshToken}")
            Log.i(tag, "AccessToken: ${getAccessToken()}")
            Result.success(getAccessToken()!!)
        }
    }

    fun isAuthorized(): Boolean {
        return authStateManager.authState!!.isAuthorized
    }

    private fun isAccessTokenExpired(): Boolean {
        return authStateManager.authState!!.needsTokenRefresh
    }

    private fun getAccessToken(): String? {
        return authStateManager.authState?.accessToken
    }

    fun getAuthRequest(): AuthorizationRequest {
        return this.authRequest
    }
}