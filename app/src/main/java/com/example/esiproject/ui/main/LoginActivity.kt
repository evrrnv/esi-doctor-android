package com.example.esiproject.ui.main

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.esiproject.R
import com.example.esiproject.databinding.ActivityLoginBinding
import com.example.esiproject.ui.home.HomeActivity
import com.example.esiproject.utils.AppAuthManager
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()

    @Inject
    internal lateinit var appAuthManager: AppAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_EsiProject)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        binding.createRdvButton.setOnClickListener {
            startAuthorization()
        }
    }

    private fun startAuthorization() {
        val authService = AuthorizationService(this)

        val okIntent = Intent(this, HomeActivity::class.java)
        okIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val cancelIntent = Intent(this, LoginActivity::class.java)

        authService.performAuthorizationRequest(
            appAuthManager.getAuthRequest(),
            PendingIntent.getActivity(this, 0, okIntent, 0),
            PendingIntent.getActivity(this, 0, cancelIntent, 0)
        )
    }
}