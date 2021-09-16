package com.example.esiproject.ui.rdv

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.api.http.withHeader
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.network.http.*
import com.example.CheckRdvAvailabilityQuery
import com.example.DemandezRdvMutation
import com.example.esiproject.data.AuthRepository
import com.example.esiproject.utils.ApolloClientManager
import com.example.type.RendezVousInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import javax.inject.Inject

@HiltViewModel
class RdvViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val rdvAvailability: MutableLiveData<CheckRdvAvailabilityQuery.CheckRdvAvailability> by lazy {
        MutableLiveData<CheckRdvAvailabilityQuery.CheckRdvAvailability>()
    }

    val isAuth: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val rdvIsCreated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun checkAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        viewModelScope.launch {
            isAuth.value = authRepository.checkAuthorization(response, exception).isSuccess
        }
    }

    fun checkRdvAvailability(date: Any) {
        val token = authRepository.getAccessToken()
        if (token != null) {
            val apolloClient = ApolloClientManager().getApolloClient(token)
            viewModelScope.launch {
                val response = try {
                    apolloClient.query(CheckRdvAvailabilityQuery(date))
                } catch (e: ApolloException) {

                    return@launch
                }
                if (response.data?.checkRdvAvailability != null){
                    rdvAvailability.value = response.data!!.checkRdvAvailability
                    Log.i("RdvViewModel", response.data!!.checkRdvAvailability.toString())
                }
            }
        }
    }

    fun demandezRdv(data: RendezVousInput) {
        val token = authRepository.getAccessToken()
        if (token != null) {
            val apolloClient = ApolloClientManager().getApolloClient(token)
            viewModelScope.launch {
                val response = try {
                    apolloClient.mutate(DemandezRdvMutation(data))
                } catch (e: ApolloException) {
                    return@launch
                }
                Log.i("Rdv", data.endDate.toString())
                rdvIsCreated.value = true
            }
        }
    }
}