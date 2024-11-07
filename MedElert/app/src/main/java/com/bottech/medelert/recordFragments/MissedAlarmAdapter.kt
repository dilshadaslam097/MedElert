package com.bottech.medelert.recordFragments

import com.bottech.medelert.data.format.GetMissedAlarmData
import AlarmDatabaseHelper
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R



class MissedAlarmAdapter(private val missedAlarmsList: List<GetMissedAlarmData>,
                         private val alarmDatabaseHelper: AlarmDatabaseHelper
) : RecyclerView.Adapter<MissedAlarmAdapter.MissedAlarmViewHolder>() {



    inner class MissedAlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationDateTime: TextView = itemView.findViewById(R.id.medication_time)
        val medicationName: TextView = itemView.findViewById(R.id.medication_name)
        val medicationDosage: TextView = itemView.findViewById(R.id.medication_dosage)
        val takenAlarm: LinearLayout = itemView.findViewById(R.id.takenLL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissedAlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.missed_alarms, parent, false)
        return MissedAlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissedAlarmViewHolder, position: Int) {
        val alarmAdapterView = missedAlarmsList[position]

        holder.medicationDateTime.text = alarmAdapterView.datesTimes
        holder.medicationName.text = alarmAdapterView.medicationName
        holder.medicationDosage.text = alarmAdapterView.medicationDosage
        holder.takenAlarm.setOnClickListener {
            val millisId = alarmAdapterView.millisId  // Extract millisId from GetMissedAlarmData
            updateAlarmStatus(millisId, true, false, false)
        }
    }

    override fun getItemCount(): Int = missedAlarmsList.size

    private fun updateAlarmStatus(millisId: Int, isTaken: Boolean, isMissed: Boolean, isSkipped: Boolean) {
        alarmDatabaseHelper.updateAlarmStatus(millisId, isTaken, isMissed, isSkipped)
    }
}