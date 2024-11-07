package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.data.format.GetMissedAlarmData
import com.bottech.medelert.data.format.GetTakenAlarmData

class HistoryAlarmAdapter(private val takenAlarmsList: List<GetTakenAlarmData>) : RecyclerView.Adapter<HistoryAlarmAdapter.HistoryAlarmViewHolder>() {

    inner class HistoryAlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationDateTime: TextView = itemView.findViewById(R.id.medication_time)
        val medicationName: TextView = itemView.findViewById(R.id.medication_name)
        val medicationDosage: TextView = itemView.findViewById(R.id.medication_dosage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taken_alarms, parent, false)
        return HistoryAlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryAlarmViewHolder, position: Int) {
        val alarmAdapterView = takenAlarmsList[position]

        holder.medicationDateTime.text = alarmAdapterView.datesTimes
        holder.medicationName.text = alarmAdapterView.medicationName
        holder.medicationDosage.text = alarmAdapterView.medicationDosage

    }

    override fun getItemCount(): Int = takenAlarmsList.size

}