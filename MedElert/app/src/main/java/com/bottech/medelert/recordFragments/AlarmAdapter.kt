package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.data.format.GetAllAlarmData


class AlarmAdapter(private var alarmDataList: List<GetAllAlarmData>,
                   private val alarmDatabaseHelper: AlarmDatabaseHelper,
    ) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationName: TextView = itemView.findViewById(R.id.medication_name)
        val medicationDosage: TextView = itemView.findViewById(R.id.medication_dosage)
        val alarmDate1: TextView = itemView.findViewById(R.id.alarmDate_1)
        val alarmDate2: TextView = itemView.findViewById(R.id.alarmDate_2)
        val alarmDate3: TextView = itemView.findViewById(R.id.alarmDate_3)
        val alarmTime1: TextView = itemView.findViewById(R.id.alarmTime_1)
        val alarmTime2: TextView = itemView.findViewById(R.id.alarmTime_2)
        val alarmTime3: TextView = itemView.findViewById(R.id.alarmTime_3)
//        val removeButton: ImageView = itemView.findViewById(R.id.removeAlarm)
        val enableDisableAlarmSwitch: Switch = itemView.findViewById(R.id.enableDisableAlarm)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarmAdapterView = alarmDataList[position]
        val alarmId = alarmAdapterView.alarmId

        // Check individual alarm enabled status from database
        var isAlarmCancelled = alarmDatabaseHelper.getAlarmCancelStatus(alarmId)

        holder.medicationName.text = alarmAdapterView.medicationName
        holder.medicationDosage.text = alarmAdapterView.dosageDescription
//        Log.d("CheckLogs ----- ", "alarmAdapterView = " + alarmAdapterView)
        val alarmDates = alarmAdapterView.alarmDateList

        // Handle cases based on the number of alarm dates
        when (alarmDates.size) {
            0 -> {
                // No alarm dates, hide all date TextViews (optional)
                holder.alarmDate1.visibility = View.GONE
                holder.alarmDate2.visibility = View.GONE
                holder.alarmDate3.visibility = View.GONE
            }
            1 -> {
                holder.alarmDate1.text = alarmDates[0]
                holder.alarmDate2.visibility = View.GONE
                holder.alarmDate3.visibility = View.GONE
            }
            2 -> {
                holder.alarmDate1.text = alarmDates[0]
                holder.alarmDate2.text = alarmDates[1]
                holder.alarmDate3.visibility = View.GONE
            }
            3 -> {
                holder.alarmDate1.text = alarmDates[0]
                holder.alarmDate2.text = alarmDates[1]
                holder.alarmDate3.text = alarmDates[2]
            }
            else -> {
                holder.alarmDate1.text = alarmDates[0]
                holder.alarmDate2.text = "till"
                holder.alarmDate3.text = alarmDates.last()
            }
        }

        // Handle multiple alarm times based on list size
        val alarmTimes = alarmAdapterView.alarmTimeList
        val numAlarmTimes = alarmTimes.size
        when (numAlarmTimes) {
            0 -> {
                // No alarm times, hide all time TextViews
                holder.alarmTime1.visibility = View.GONE
                holder.alarmTime2.visibility = View.GONE
                holder.alarmTime3.visibility = View.GONE
            }
            1 -> {
                // One alarm time, show it in alarmTime1 and hide others
                holder.alarmTime1.text = alarmTimes[0]
                holder.alarmTime1.visibility = View.VISIBLE
                holder.alarmTime2.visibility = View.GONE
                holder.alarmTime3.visibility = View.GONE
            }
            2 -> {
                // Two alarm times, show them in alarmTime1 and alarmTime2 and hide the third
                holder.alarmTime1.text = alarmTimes[0]
                holder.alarmTime1.visibility = View.VISIBLE
                holder.alarmTime2.text = alarmTimes[1]
                holder.alarmTime2.visibility = View.VISIBLE
                holder.alarmTime3.visibility = View.GONE
            }
            3 -> {
                // Three alarm times, show them all
                holder.alarmTime1.text = alarmTimes[0]
                holder.alarmTime1.visibility = View.VISIBLE
                holder.alarmTime2.text = alarmTimes[1]
                holder.alarmTime2.visibility = View.VISIBLE
                holder.alarmTime3.text = alarmTimes[2]
                holder.alarmTime3.visibility = View.VISIBLE
            }
            else -> {
                // Handle unexpected number of alarm times (optional: log or throw an exception)
                Log.w("AlarmAdapter", "Unexpected number of alarm times: ${numAlarmTimes}")
                // Or: throw IllegalArgumentException("Alarm data has too many alarm times")
            }
        }

        // Set click listeners for removal
//        holder.removeButton.setOnClickListener {
//            onRemoveAlarm(position)
//        }

        // Set initial state of the switch based on isAlarmCancelled
        holder.enableDisableAlarmSwitch.isChecked = !isAlarmCancelled

        holder.enableDisableAlarmSwitch.setOnClickListener {
            val newCancelState = !isAlarmCancelled // Toggle the cancel state
            alarmDatabaseHelper.updateAlarmCancelStatus(alarmId, newCancelState);

            // Update UI and switch state based on new cancellation status
            isAlarmCancelled = newCancelState;
            holder.enableDisableAlarmSwitch.isChecked = !isAlarmCancelled;
            holder.enableDisableAlarmSwitch.isEnabled = !isAlarmCancelled; // Enable switch interaction when alarm is active
        }
    }

    private fun onRemoveAlarm(position: Int) {
        val updatedAlarmList = alarmDataList.toMutableList()
        updatedAlarmList.removeAt(position)
        alarmDataList = updatedAlarmList.toList()
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = alarmDataList.size
}