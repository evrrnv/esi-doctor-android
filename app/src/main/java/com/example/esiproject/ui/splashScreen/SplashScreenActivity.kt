package com.example.esiproject.ui.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.esiproject.ui.home.HomeActivity
import com.example.esiproject.ui.main.LoginActivity
import com.example.esiproject.utils.AppAuthManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()

    @Inject
    internal lateinit var appAuthManager: AppAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (appAuthManager.isAuthorized()) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}