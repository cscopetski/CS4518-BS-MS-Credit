package com.example.mobilenetworkingretrofit.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilenetworkingretrofit.R
import com.example.mobilenetworkingretrofit.RetroFitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button
    private lateinit var nameText: TextView

    private var name: MutableLiveData<String> = MutableLiveData("Name")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logout_button)
        nameText = findViewById(R.id.name_display)

        logoutButton.setOnClickListener{
            logout(this)
        }

        name.observe(this) {
            it.let {
                nameText.text = it.toString()
            }
        }

        getName(this)
    }

    private fun logout(activity: Context){

        val call: Call<ResponseBody> =
            RetroFitClient.authorizationService.logout()

        call.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {

                if (response.isSuccessful) {

                    Toast.makeText(activity, "Logout ${name.value}", Toast.LENGTH_LONG).show()
                    RetroFitClient.deleteCookie()
                    switchActivity()
                } else {
                    Log.d(TAG, "Logout Failed")

                    Toast.makeText(activity, "Failed to Logout", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
                Log.d(TAG, "Logout: Error")
                Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                Log.d(TAG,t.toString())
            }
        })
    }

    private fun getName(activity: Context){

        val call: Call<String> =
            RetroFitClient.authorizationService.getName()

        call.enqueue(object : Callback<String?> {

            override fun onResponse(call: Call<String?>?, response: Response<String?>) {

                if (response.isSuccessful) {

                    name.postValue(response.body())

                } else {
                    Log.d(TAG, "Get Name Failed")
                    name.postValue("")
                    Toast.makeText(activity, "Failed to get name", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<String?>?, t: Throwable?) {
                Log.d(TAG, "Get Name: Error")
                name.postValue("")
                Toast.makeText(activity, "Get Name Error", Toast.LENGTH_LONG).show()

            }
        })
    }

    private fun switchActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}