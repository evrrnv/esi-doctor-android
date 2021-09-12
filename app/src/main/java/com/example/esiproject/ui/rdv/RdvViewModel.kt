package com.example.esiproject.ui.rdv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esiproject.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@HiltViewModel
class RdvViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        viewModelScope.launch {
            authRepository.checkAuthorization(response, exception)
        }
    }
}