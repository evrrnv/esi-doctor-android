package com.example.esiproject.utils

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.api.http.withHeader
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.http.HttpNetworkTransport

class ApolloClientManager {
    private val serverUrl = "https://1sc-project.moun3im.com/graphql"

    fun getApolloClient(token: String): ApolloClient {
        return ApolloClient(
            networkTransport = HttpNetworkTransport(
                serverUrl = serverUrl,
                interceptors = listOf(AuthorizationInterceptor(token))
            )
        )
    }
}

class AuthorizationInterceptor(private val token: String) : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        return chain.proceed(request.withHeader("Authorization", "Bearer $token"))
    }
}