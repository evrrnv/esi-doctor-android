package com.example.esiproject.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esiproject.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        viewModelScope.launch {
            val result = authRepository.checkAuthorization(response, exception)

            if (result.isSuccess) {

            }
        }
    }
}