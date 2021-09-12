package com.example.esiproject.ui.rdv

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esiproject.R
import com.example.esiproject.databinding.ActivityRdvBinding
import com.example.esiproject.utils.AppAuthManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class RdvActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private lateinit var binding: ActivityRdvBinding

    private val viewModel: RdvViewModel by viewModels()

    private var dayCardSelected: String? = null

    @Inject
    internal lateinit var appAuthManager: AppAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_EsiProject)

        binding = ActivityRdvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)
        viewModel.checkAuthorization(response, exception)

        val d1 = Day("Mer", 21)
        val d2 = Day("Jeu", 22)

        val daysList = ArrayList<Day>()
        daysList.add(d1)
        daysList.add(d2)

        val dayAdapter = RecyclerViewAdapter(getDateRange())

        binding.dayRv.adapter = dayAdapter

        binding.dayRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getDateRange()

        val now: Calendar = Calendar.getInstance()
        val format = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
        binding.monthYear.text = format.format(now.time).replaceFirstChar { it.uppercase() }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.createRdvButton.isEnabled = checkedId != -1
        }

    }

    private fun getDateRange(): ArrayList<Day> {
        val now: Calendar = Calendar.getInstance()

        val format = SimpleDateFormat("EE/dd", Locale.FRENCH)

        val days = ArrayList<Day>()
        val delta: Int =
            -now.get(GregorianCalendar.DAY_OF_WEEK) + 2

        now.add(Calendar.DAY_OF_MONTH, delta)
        for (i in 0..6) {
            val day = format.format(now.time).split('/')
            days.add(Day(day[0].dropLast(1), day[1].toInt()))
            now.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

}