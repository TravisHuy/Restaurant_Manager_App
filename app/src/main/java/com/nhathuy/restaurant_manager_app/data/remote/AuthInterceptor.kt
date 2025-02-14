package com.nhathuy.restaurant_manager_app.data.remote

import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import okhttp3.Interceptor
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        if(request.url.encodedPath.contains("/auth")){
            return chain.proceed(request)
        }

        val token = tokenManager.getAccessToken()

        return if(token != null){
            val authenticatedRequest = request.newBuilder()
                .header("Authorization" ,"Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        }
        else{
            return chain.proceed(request)
        }


    }
}