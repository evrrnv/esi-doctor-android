package com.example.esiproject.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import net.openid.appauth.*

class AuthStateManager (context: Context) {

    private val TAG = this.javaClass.simpleName

    private val prefPath = "auth"
    private val stateKey = "stateJson"
    private val authPrefs: SharedPreferences = context.getSharedPreferences(prefPath, MODE_PRIVATE)

    var authState: AuthState? = null
        get() {
            return if (field != null) field else readAuthState()
        }
        set(authState) {
            field = authState ?: AuthState()
            writeAuthState(field!!)

        }

    private fun readAuthState(): AuthState {
        val stateJson = authPrefs.getString(stateKey, "{}")
        return if (stateJson != null) {
            AuthState.jsonDeserialize(stateJson)
        } else {
            AuthState()
        }
    }

    private fun writeAuthState(state: AuthState) {
        authPrefs.edit()
            .putString(stateKey, state.jsonSerializeString())
            .apply()
    }

    private fun replace(state: AuthState): AuthState {
        this.authState = state
        return state
    }

    fun updateAfterAuthorization(response: AuthorizationResponse?, ex: AuthorizationException?): AuthState {
        val current = authState!!
        current.update(response, ex)
        return replace(current)
    }

    fun updateAfterTokenResponse(response: TokenResponse?, ex: AuthorizationException?): AuthState {
        val current = authState!!
        current.update(response, ex)
        return replace(current)
    }
}