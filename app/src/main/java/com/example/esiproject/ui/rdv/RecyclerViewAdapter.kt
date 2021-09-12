package com.example.esiproject.ui.rdv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.esiproject.R

class RecyclerViewAdapter(private val dataSet: ArrayList<Day>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private var selectedItemPosition: Int = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayName: TextView = view.findViewById(R.id.dayName830)
        val dayNumber: TextView = view.findViewById(R.id.dayNumber)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.date_card, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.dayName.text = dataSet[position].dayName
        viewHolder.dayNumber.text = dataSet[position].dayNumber.toString()
        viewHolder.cardView.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
        }
        if (selectedItemPosition == position) {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#24A9E2"))
            viewHolder.dayName.setTextColor(Color.WHITE)
            viewHolder.dayNumber.setTextColor(Color.WHITE)
        }
        else {
            viewHolder.cardView.setCardBackgroundColor(Color.WHITE)
            viewHolder.dayName.setTextColor(Color.BLACK)
            viewHolder.dayNumber.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = dataSet.size

}