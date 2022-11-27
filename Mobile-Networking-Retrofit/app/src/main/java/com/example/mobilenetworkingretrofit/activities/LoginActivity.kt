package com.example.mobilenetworkingretrofit.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mobilenetworkingretrofit.R
import com.example.mobilenetworkingretrofit.RetroFitClient
import com.example.mobilenetworkingretrofit.services.AuthorizationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.login_button)
        usernameText = findViewById(R.id.email_editText)
        passwordText = findViewById(R.id.password_editText)

        loginButton.setOnClickListener {


            if (usernameText.text.isEmpty() || passwordText.text.isEmpty()) {
                loginButton.error = "Must fill out both fields"
                Toast.makeText(this, "Must fill out both fields", Toast.LENGTH_LONG).show()
            } else {
                loginButton.error = null
                login(usernameText.text.toString(), passwordText.text.toString(), this)
            }


        }

        if(RetroFitClient.checkCookie()){
            switchActivity()
        }

    }

    private fun login(username: String, password: String, activity: Context) {

        val call: Call<AuthorizationService.User> =
            RetroFitClient.authorizationService.login(
                AuthorizationService.LoginCredentials(
                    username,
                    password
                )
            )

        call.enqueue(object : Callback<AuthorizationService.User?> {

            override fun onResponse(call: Call<AuthorizationService.User?>?, response: Response<AuthorizationService.User?>) {

                if (response.isSuccessful) {
                    switchActivity()
                } else {
                    Log.d(TAG, "Incorrect Login")
                    loginButton.error = "Incorrect Email or Password"
                    Toast.makeText(
                        activity,
                        "Failed to Login:\nIncorrect Email or Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<AuthorizationService.User?>?, t: Throwable?) {
                Log.d(TAG, "Login: Error")
                Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                Log.d(TAG,t.toString())
            }
        })
    }

    private fun switchActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }
}