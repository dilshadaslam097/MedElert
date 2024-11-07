package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper

class ActiveAlarmsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_active_alarms, container, false)
        val alarmDatabaseHelper = AlarmDatabaseHelper(activity?.applicationContext!!)

        val sharedPreferencesHelper = SharedPreferencesHelper(activity?.applicationContext!!)
        val userId: Int = sharedPreferencesHelper.getUserId()!!
        
        val recyclerView = view?.findViewById<RecyclerView>(R.id.activeAlarmsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
//        Log.d("DataCheck ----- ","-----get Rec--- "+alarmDatabaseHelper.getAllMedicationAlarms())
//        Log.d("CheckLogs ----- ", "AllAlarmsFragment = " + alarmDatabaseHelper.getAllAlarmData())
        val adapter = ActiveAlarmAdapter(alarmDatabaseHelper.getActiveAlarmData(userIdValue = userId), alarmDatabaseHelper) // Replace with your list of reminders
        recyclerView?.adapter = adapter
        return view
    }
}