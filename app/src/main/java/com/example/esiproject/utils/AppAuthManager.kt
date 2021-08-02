package com.example.esiproject.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import net.openid.appauth.*

class AppAuthManager (context: Context) {

    private val tag = this.javaClass.simpleName

    private val authEndpoint = "https://auth.moun3im.com/auth/realms/1sc-project/protocol/openid-connect/auth"
    private val tokenEndpoint = "https://auth.moun3im.com/auth/realms/1sc-project/protocol/openid-connect/token"
    private val redirectUri = Uri.parse("esiproject://oauth2redirect")
    private val clientId = "mobile"

    private val authStateManager: AuthStateManager = AuthStateManager(context)
    private val authService: AuthorizationService
    val authRequest: AuthorizationRequest

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
    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        if (isAuthorized()) {
            Log.i(tag, "Already authorized.")
            startRefreshAccessToken()
        } else {
            Log.i(tag, "Not yet authorized.")
            startAuthCodeExchange(response, exception)
        }
    }

    private fun startRefreshAccessToken() {
        authService.performTokenRequest(
            authStateManager.authState!!.createTokenRefreshRequest()
        ) { tokenResponse, authException ->
            handleCodeExchangeResponse(tokenResponse, authException)
        }
    }

    private fun startAuthCodeExchange(response: AuthorizationResponse?, exception: AuthorizationException?) {
        if (response != null || exception != null) {
            authStateManager.updateAfterAuthorization(response, exception)
        }

        when {
            response?.authorizationCode != null -> {
                authStateManager.updateAfterAuthorization(response, exception)
                exchangeAuthorizationCode(response)
            }
            exception != null -> {
                Log.e(tag, "Authorization flow failed: " + exception.message)
            }
            else -> {
                Log.e(tag, "No authorization state retained - reauthorization required")
            }
        }
    }

    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        authService.performTokenRequest(
                authorizationResponse.createTokenExchangeRequest()
                ) { tokenResponse, authException ->
                    handleCodeExchangeResponse(tokenResponse, authException)
                }
    }

    private fun handleCodeExchangeResponse(tokenResponse: TokenResponse?, authException: AuthorizationException?) {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException)
        Log.i(tag, "IsAuth: " + authStateManager.authState!!.isAuthorized)
        if (!authStateManager.authState!!.isAuthorized) {
            Log.e(tag, "failed to exchange authorization code")
        } else {
            Log.i(tag, "AccessToken: ${getAccessToken()}")
            Log.i(tag, "RefreshToken: ${authStateManager.authState?.refreshToken}")
        }
    }

    private fun isAuthorized(): Boolean {
        return authStateManager.authState!!.isAuthorized
    }

    fun getAccessToken(): String? {
        return authStateManager.authState?.accessToken
    }
}