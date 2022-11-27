package com.example.mobilenetworkingretrofit.services

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizationService {

    @POST("/login")
    fun login(@Body loginCredentials: LoginCredentials): Call<User>

    @POST("/logout")
    fun logout(): Call<ResponseBody>

    data class LoginCredentials(val username:String, val password:String)
    data class User(val name:String, val username:String)
}