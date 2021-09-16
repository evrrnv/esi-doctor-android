package com.example.esiproject.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.esiproject.R
import com.example.esiproject.databinding.ActivityHomeBinding
import com.example.esiproject.ui.rdv.RdvActivity
import com.example.esiproject.utils.AppAuthManager
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    internal lateinit var appAuthManager: AppAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_EsiProject)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)
        viewModel.checkAuthorization(response, exception)

        binding.createRdvButton.setOnClickListener {
            val intent = Intent(this, RdvActivity::class.java)
            intent.putExtra("bienvenueText", binding.bienvenue.text)
            startActivity(intent)
        }

        viewModel.isAuth.observe(this, {
            viewModel.getHomeData()
        })

        viewModel.homeData.observe(this, {
            if (it.currentUser != null) {
                val bienvenueText = "Bienvenue ${it.currentUser.nom} ${it.currentUser.prenom}"
                binding.bienvenue.text = bienvenueText
                val pIsComplete = it.currentUser.isCompleted
                val bioIsComplete = it.currentUser.dossierMedicalsByUserId.nodes[0]?.biometriqueById?.isCompleted
                val prsIsComplete = it.currentUser.dossierMedicalsByUserId.nodes[0]?.antecedentsPersonnelleById?.isCompleted
                val chrIsComplete = it.currentUser.dossierMedicalsByUserId.nodes[0]?.antecedentsMedicoChirugicauxById?.isCompleted
                if (pIsComplete != null && bioIsComplete != null && prsIsComplete != null && chrIsComplete != null) {
                    if (pIsComplete) {
                        binding.textView7.background.setTint(Color.parseColor("#56C596"))
                        binding.textView7.text = "complet"
                    } else {
                        binding.textView7.background.setTint(Color.parseColor("#F63232"))
                        binding.textView7.text = "non complet"
                    }
                    if (bioIsComplete) {
                        binding.textView8.background.setTint(Color.parseColor("#56C596"))
                        binding.textView8.text = "complet"
                    } else {
                        binding.textView8.background.setTint(Color.parseColor("#F63232"))
                        binding.textView8.text = "non complet"
                    }
                    if (chrIsComplete) {
                        binding.textView10.background.setTint(Color.parseColor("#56C596"))
                        binding.textView10.text = "complet"
                    } else {
                        binding.textView10.background.setTint(Color.parseColor("#F63232"))
                        binding.textView10.text = "non complet"
                    }
                    if (prsIsComplete) {
                        binding.textView9.background.setTint(Color.parseColor("#56C596"))
                        binding.textView9.text = "complet"
                    } else {
                        binding.textView9.background.setTint(Color.parseColor("#F63232"))
                        binding.textView9.text = "non complet"
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cst.visibility = View.VISIBLE
                }
                Log.i(tag, it.toString())

                if (it.allRendezVous != null && it.allRendezVous.nodes.isNotEmpty()) {
                    val simpleFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH)
                    val mDate = simpleFormat.parse(it.allRendezVous.nodes[0]!!.startDate.toString().replace("T", " "))
                    val calendar = Calendar.getInstance()
                    val timeInMilliseconds = mDate.time - calendar.timeInMillis

                    if (timeInMilliseconds > 0) {
                        object : CountDownTimer(timeInMilliseconds, 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                val timeUnit = TimeUnit.MILLISECONDS
                                var delta = millisUntilFinished
                                val days = timeUnit.toDays(delta)
                                if (days > 0) {
                                    delta %= days * timeUnit.convert(1, TimeUnit.DAYS)
                                }
                                val hours = timeUnit.toHours(delta)
                                if (hours > 0) {
                                    delta %= hours * timeUnit.convert(1, TimeUnit.HOURS)
                                }
                                val minutes = timeUnit.toMinutes(delta)
                                if (minutes > 0) {
                                    delta %= minutes * timeUnit.convert(1, TimeUnit.MINUTES)
                                }

                                val daysText = "$days J"
                                val hoursText = "$hours H"
                                val minutesText = "$minutes M"
                                binding.jours.text = daysText
                                binding.hours.text = hoursText
                                binding.minutes.text = minutesText
                            }

                            override fun onFinish() {

                            }
                        }.start()
                    }
                }
            }
        })
    }
}