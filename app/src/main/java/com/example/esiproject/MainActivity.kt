package com.example.esiproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.esiproject.utils.AppAuthManager
import net.openid.appauth.*
import net.openid.appauth.AuthorizationService


class MainActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private lateinit var appAuthManager: AppAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_EsiProject)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

//        appAuthManager = AppAuthManager(this)
//        doAuthorization(appAuthManager.authRequest)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            if (intent != null) {
                val response = AuthorizationResponse.fromIntent(intent)
                val exception = AuthorizationException.fromIntent(intent)
                appAuthManager.checkAuthorization(response, exception)
            }
        }
    }

    private fun doAuthorization(authRequest: AuthorizationRequest) {
        val authService = AuthorizationService(this)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        resultLauncher.launch(authIntent)
    }
}