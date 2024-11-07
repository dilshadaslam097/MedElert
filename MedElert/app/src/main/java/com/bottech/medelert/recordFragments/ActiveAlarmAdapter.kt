package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.data.format.GetActiveAlarmData
import com.bottech.medelert.data.format.GetMissedAlarmData

class ActiveAlarmAdapter (private val activeAlarmsList: List<GetActiveAlarmData>,
                          private val alarmDatabaseHelper: AlarmDatabaseHelper
) : RecyclerView.Adapter<ActiveAlarmAdapter.ActiveAlarmViewHolder>() {

    inner class ActiveAlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationDateTime: TextView = itemView.findViewById(R.id.medication_time)
        val medicationName: TextView = itemView.findViewById(R.id.medication_name)
        val medicationDosage: TextView = itemView.findViewById(R.id.medication_dosage)
        val skipAlarm: LinearLayout = itemView.findViewById(R.id.skipLL)
        val takenAlarm: LinearLayout = itemView.findViewById(R.id.takenLL)
        val enableDisableAlarmSwitch: Switch = itemView.findViewById(R.id.enableDisableAlarm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveAlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notifications_all, parent, false)
        return ActiveAlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveAlarmViewHolder, position: Int) {
        val alarmAdapterView = activeAlarmsList[position]
        val millisId = alarmAdapterView.millisId

        // Check individual alarm enabled status from database
        var isAlarmEnabled = alarmDatabaseHelper.getAlarmEnabledStatus(millisId)

        holder.medicationDateTime.text = alarmAdapterView.datesTimes
        holder.medicationName.text = alarmAdapterView.medicationName
        holder.medicationDosage.text = alarmAdapterView.medicationDosage

        // Set initial state of the switch
        holder.enableDisableAlarmSwitch.isChecked = isAlarmEnabled

        holder.enableDisableAlarmSwitch.setOnClickListener {
            val newIsEnabledState = !isAlarmEnabled
            alarmDatabaseHelper.updateAlarmEnabledStatus(millisId, newIsEnabledState)

            // Update the UI to reflect the new state
            isAlarmEnabled = newIsEnabledState
            holder.enableDisableAlarmSwitch.isEnabled = !isAlarmEnabled
            holder.enableDisableAlarmSwitch.isEnabled = isAlarmEnabled
        }

        // Set click listeners for delete and update buttons
        holder.skipAlarm.setOnClickListener {
            val millisId = alarmAdapterView.millisId  // Extract millisId from GetMissedAlarmData
            alarmDatabaseHelper.updateAlarmStatus(millisId, false, false, true)
            notifyDataSetChanged()
        }
        holder.takenAlarm.setOnClickListener {
            val millisId = alarmAdapterView.millisId  // Extract millisId from GetMissedAlarmData
            alarmDatabaseHelper.updateAlarmStatus(millisId, true, false, false)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = activeAlarmsList.size

}