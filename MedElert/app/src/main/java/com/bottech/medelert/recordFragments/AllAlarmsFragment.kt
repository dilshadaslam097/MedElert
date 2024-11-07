package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper

class AllAlarmsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_alarms, container, false)
        val alarmDatabaseHelper = AlarmDatabaseHelper(activity?.applicationContext!!)

        val sharedPreferencesHelper = SharedPreferencesHelper(activity?.applicationContext!!)
        val userId: Int = sharedPreferencesHelper.getUserId()!!

        val recyclerView = view?.findViewById<RecyclerView>(R.id.allAlarmsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
//        Log.d("DataCheck ----- ","-----get Rec--- "+alarmDatabaseHelper.getAllMedicationAlarms())
//        Log.d("CheckLogs ----- ", "AllAlarmsFragment = " + alarmDatabaseHelper.getAllAlarmData())
        val adapter = AlarmAdapter(alarmDatabaseHelper.getAllAlarmData(userIdValue = userId), alarmDatabaseHelper) // Replace with your list of reminders
        recyclerView?.adapter = adapter
        return view
    }
}