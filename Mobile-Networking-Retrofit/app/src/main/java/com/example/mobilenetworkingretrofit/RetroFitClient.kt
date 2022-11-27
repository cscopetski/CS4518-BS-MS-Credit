package com.example.mobilenetworkingretrofit

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.mobilenetworkingretrofit.services.AuthorizationService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "RetroFitClient"

object RetroFitClient {

    private const val COOKIE_PREFERENCE_LOCATION = "COOKIE_STORE"
    private const val PREFS_KEY_PREFIX = "Cookies-"
    private const val HOST_NAME = "10.0.2.2"
    private const val BASE_URL = "http://$HOST_NAME:3000/"


    private val prefs: SharedPreferences = BaseApplication.getAppContext().getSharedPreferences(
        COOKIE_PREFERENCE_LOCATION,
        Context.MODE_PRIVATE
    )
    private val prefsEditor: SharedPreferences.Editor = prefs.edit()

    private val cookieJar: CookieJar = object : CookieJar {
        private val cookieStore = HashMap<String, List<Cookie>>()
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

            if (url.host().equals(HOST_NAME)) {
                val prefsKey = PREFS_KEY_PREFIX + url.host()

                if (!prefs.contains(prefsKey)) {
                    val json = Gson().toJson(cookies)

                    prefsEditor.putString(prefsKey, json)
                    prefsEditor.commit()
                } else {
                    val json = prefs.getString(prefsKey, "")
                    val listType = object : TypeToken<List<Cookie>>() {}.type
                    val cookiesList = Gson().fromJson<List<Cookie>>(json, listType)
                    var store = false;

                    for(newCookie in cookies){
                        for (oldCookie in cookiesList) {
                            if(oldCookie.expiresAt() < newCookie.expiresAt() || oldCookie.expiresAt() < System.currentTimeMillis()){
                                store = true;
                            }
                        }
                    }

                    if(store){
                        val json = Gson().toJson(cookies)

                        prefsEditor.putString(prefsKey, json)
                        prefsEditor.commit()
                    }
                }

                cookieStore[url.host()] = cookies
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {

            if (url.host().equals(HOST_NAME)) {

                val cookies = cookieStore[url.host()]
                val prefsKey = PREFS_KEY_PREFIX + url.host()

                if (cookies == null && prefs.contains(prefsKey)) {
                    val json = prefs.getString(prefsKey, "")

                    val listType = object : TypeToken<List<Cookie>>() {}.type
                    val cookiesList = Gson().fromJson<List<Cookie>>(json, listType)

                    return cookiesList;
                }
                return cookies ?: ArrayList()
            } else {
                return ArrayList()
            }

        }
    }

    private val httpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    fun checkCookie():Boolean {

        val prefsKey = PREFS_KEY_PREFIX + HOST_NAME

        if(prefs.contains(prefsKey)){
            val json = prefs.getString(prefsKey, "")
            val listType = object : TypeToken<List<Cookie>>() {}.type
            val cookiesList = Gson().fromJson<List<Cookie>>(json, listType)

            for (cookie in cookiesList) {
                if(cookie.expiresAt() < System.currentTimeMillis()){
                    return false
                }
            }

            return true
        }else{
            return false
        }
    }

    fun deleteCookie() {
        val prefsKey = PREFS_KEY_PREFIX + HOST_NAME

        if(prefs.contains(prefsKey)){
            prefsEditor.remove(prefsKey).commit()
        }
    }

    private fun retrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authorizationService: AuthorizationService by lazy {
        retrofit().create(AuthorizationService::class.java)
    }

}