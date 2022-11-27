package com.example.mobilenetworkingretrofit.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.mobilenetworkingretrofit.R
import com.example.mobilenetworkingretrofit.RetroFitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        logoutButton = findViewById(R.id.logout_button)

        logoutButton.setOnClickListener{
            logout(this)
        }
    }

    private fun logout(activity: Context){

        val call: Call<ResponseBody> =
            RetroFitClient.authorizationService.logout()

        call.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>?, response: Response<ResponseBody?>) {

                if (response.isSuccessful) {

                    Toast.makeText(activity, "Logout User", Toast.LENGTH_LONG).show()
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
            }
        })
    }

    private fun switchActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}