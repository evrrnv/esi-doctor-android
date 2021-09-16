package com.example.esiproject.ui.rdv

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esiproject.R
import com.example.esiproject.databinding.ActivityRdvBinding
import com.example.esiproject.utils.AppAuthManager
import com.example.type.RendezVousInput
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

    private  var selectedDate: String? = null
    private  var selectedTime: String? = null

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

        val extras = intent.extras
        if (extras != null) {
            binding.bienvenue.text = extras.getString("bienvenueText")
        }

        val d1 = Day("Mer", 21)
        val d2 = Day("Jeu", 22)

        val daysList = ArrayList<Day>()
        daysList.add(d1)
        daysList.add(d2)

        viewModel.isAuth.observe(this, {
            viewModel.checkRdvAvailability(daysAfter(0))
            selectedDate = daysAfter(0)
        })

        viewModel.rdvAvailability.observe(this, {
            binding.chip1.isEnabled = it.r830 == false
            binding.chip2.isEnabled = it.r900 == false
            binding.chip3.isEnabled = it.r930 == false
            binding.chip4.isEnabled = it.r100 == false
            binding.chip5.isEnabled = it.r130 == false
            binding.chip6.isEnabled = it.r110 == false
            binding.chip7.isEnabled = it.r200 == false
            binding.chip8.isEnabled = it.r230 == false
            binding.chip9.isEnabled = it.r300 == false
            binding.chip10.isEnabled = it.r330 == false
            binding.chip11.isEnabled = it.r400 == false
            binding.chip12.isEnabled = it.r430 == false
        })

        val dayAdapter = RecyclerViewAdapter(getDateRange()) {
            viewModel.checkRdvAvailability(daysAfter(it))
            selectedDate = daysAfter(it)
        }

        binding.dayRv.adapter = dayAdapter

        binding.dayRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getDateRange()

        val now: Calendar = Calendar.getInstance()
        val format = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)
        binding.monthYear.text = format.format(now.time).replaceFirstChar { it.uppercase() }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.createRdvButton.isEnabled = checkedId != -1
            selectedTime = group.findViewById<Chip>(checkedId).text.toString()
        }

        binding.createRdvButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Confirmer")
                .setMessage("Voulez-vous vraiment envoyer une demande de rendez-vous ")
                .setPositiveButton("Oui", DialogInterface.OnClickListener {a, b ->
                    Log.i(tag, "$selectedDate $selectedTime")
                    val startDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRENCH).parse("$selectedDate $selectedTime")
                    val cal = Calendar.getInstance()
                    cal.time = startDate!!
                    cal.add(Calendar.MINUTE, 30)
                    val endDate = cal.time;
                    Log.i(tag, "$startDate $endDate" )
                    viewModel.demandezRdv(RendezVousInput(startDate = startDate, endDate = endDate))
                })
                .setNegativeButton("Non", null).
                show()
        }

        viewModel.rdvIsCreated.observe(this, {
            if (it == true) {
                finish()
            }
        })
    }

    private fun getDateRange(): ArrayList<Day> {
        val now: Calendar = Calendar.getInstance()

        val format = SimpleDateFormat("EE/dd", Locale.FRENCH)

        val days = ArrayList<Day>()
        val delta: Int =
            -now.get(GregorianCalendar.DAY_OF_WEEK) + 5

        now.add(Calendar.DAY_OF_MONTH, delta)
        for (i in 0..6) {
            val day = format.format(now.time).split('/')
            days.add(Day(day[0].dropLast(1), day[1].toInt()))
            now.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun daysAfter(number: Int) : String {
        val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, number)
        return simpleFormat.format(calendar.time)
    }

}