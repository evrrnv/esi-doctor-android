package com.example.esiproject.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.CheckRdvAvailabilityQuery
import com.example.HomeDataQuery
import com.example.esiproject.data.AuthRepository
import com.example.esiproject.utils.ApolloClientManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val homeData: MutableLiveData<HomeDataQuery.Data> by lazy {
        MutableLiveData<HomeDataQuery.Data>()
    }

    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        viewModelScope.launch {
            authRepository.checkAuthorization(response, exception)
        }
    }

    fun getHomeData() {
        val token = authRepository.getAccessToken()
        if (token != null) {
            viewModelScope.launch {
                val apolloClient = ApolloClientManager().getApolloClient(token)
                val response = try {
                    apolloClient.query(HomeDataQuery())
                } catch (e: ApolloException) {
                    return@launch
                }
                homeData.value = response.data
            }
        }
    }
}