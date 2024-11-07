package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper
import java.util.Calendar


class HistoryFragment : Fragment() {
//    private lateinit var calendarView: CalendarView
//    private lateinit var weekBarContainer: LinearLayout
//
//    private lateinit var mondayFrame: LinearLayout
//    private lateinit var tuesdayFrame: LinearLayout
//    private lateinit var wednesdayFrame: LinearLayout
//    private lateinit var thursdayFrame: LinearLayout
//    private lateinit var fridayFrame: LinearLayout
//    private lateinit var saturdayFrame: LinearLayout
//    private lateinit var sundayFrame: LinearLayout
//
//    private lateinit var monday: TextView
//    private lateinit var tuesday: TextView
//    private lateinit var wednesday: TextView
//    private lateinit var thursday: TextView
//    private lateinit var friday: TextView
//    private lateinit var saturday: TextView
//    private lateinit var sunday: TextView
//
//    private lateinit var dateMonday: TextView
//    private lateinit var dateTuesday: TextView
//    private lateinit var dateWednesday: TextView
//    private lateinit var dateThursday: TextView
//    private lateinit var dateFriday: TextView
//    private lateinit var dateSaturday: TextView
//    private lateinit var dateSunday: TextView
//
//    private lateinit var calenderFrame: LinearLayout
//    private lateinit var calender_Iv: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_alarms, container, false)
        val alarmDatabaseHelper = AlarmDatabaseHelper(activity?.applicationContext!!)

        val sharedPreferencesHelper = SharedPreferencesHelper(activity?.applicationContext!!)
        val userId: Int = sharedPreferencesHelper.getUserId()!!

        val recyclerView = view?.findViewById<RecyclerView>(R.id.takenAlarmsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(context)
//        Log.d("DataCheck ----- ","-----get Rec--- "+alarmDatabaseHelper.getAllMedicationAlarms())
//        Log.d("CheckLogs ----- ", "AllAlarmsFragment = " + alarmDatabaseHelper.getAllAlarmData())
        val adapter = HistoryAlarmAdapter(alarmDatabaseHelper.getTakenAlarmData(userIdValue = userId)) // Replace with your list of reminders
        recyclerView?.adapter = adapter
        return view
    }


//        calendarView = view.findViewById(R.id.calendarView)
//        weekBarContainer = view.findViewById(R.id.week_bar_container)
//
//        mondayFrame = weekBarContainer.findViewById(R.id.mondayFrame)
//        tuesdayFrame = weekBarContainer.findViewById(R.id.tuesdayFrame)
//        wednesdayFrame = weekBarContainer.findViewById(R.id.wednesdayFrame)
//        thursdayFrame = weekBarContainer.findViewById(R.id.thursdayFrame)
//        fridayFrame = weekBarContainer.findViewById(R.id.fridayFrame)
//        saturdayFrame = weekBarContainer.findViewById(R.id.saturdayFrame)
//        sundayFrame = weekBarContainer.findViewById(R.id.sundayFrame)
//
//        monday = weekBarContainer.findViewById(R.id.monday)
//        tuesday = weekBarContainer.findViewById(R.id.tuesday)
//        wednesday = weekBarContainer.findViewById(R.id.wednesday)
//        thursday = weekBarContainer.findViewById(R.id.thursday)
//        friday = weekBarContainer.findViewById(R.id.friday)
//        saturday = weekBarContainer.findViewById(R.id.saturday)
//        sunday = weekBarContainer.findViewById(R.id.sunday)
//
//        dateMonday = weekBarContainer.findViewById(R.id.dateMonday)
//        dateTuesday = weekBarContainer.findViewById(R.id.dateTuesday)
//        dateWednesday = weekBarContainer.findViewById(R.id.dateWednesday)
//        dateThursday = weekBarContainer.findViewById(R.id.dateThursday)
//        dateFriday = weekBarContainer.findViewById(R.id.dateFriday)
//        dateSaturday = weekBarContainer.findViewById(R.id.dateSaturday)
//        dateSunday = weekBarContainer.findViewById(R.id.dateSunday)
//
//        calenderFrame = weekBarContainer.findViewById(R.id.calenderFrame)
//        calender_Iv = weekBarContainer.findViewById(R.id.calender_Iv)
//
//        // Set initial week in week bar
//        updateWeekBar(Calendar.getInstance())
//
//        // Handle button click to expand/collapse calendar (implement based on upArrow and downArrow)
//        calenderFrame.setOnClickListener { showDatePickerDialog(calenderFrame) }
////        setOnClickListener {
////            if (calendarView.getVisibility() == View.GONE) {
////                calendarView.setVisibility(View.VISIBLE);
////            } else {
////                calendarView.setVisibility(View.GONE);
////            }
////        }
//
//        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val selectedDate = Calendar.getInstance()
//            selectedDate.set(year, month, dayOfMonth)
//            updateWeekBar(selectedDate)
//        }
//
//        return view
//    }

//    private fun updateWeekBar(date: Calendar) {
//        // Calculate week start and end dates
//        val weekStart = Calendar.getInstance()
//        weekStart.time = date.time
//        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//
//        val weekEnd = Calendar.getInstance()
//        weekEnd.time = date.time
//        weekEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//
//        // Update text views in week bar with day names
//        val today = Calendar.getInstance()
//
//        // Update text views with day numbers
//        dateMonday.text = weekStart.get(Calendar.DAY_OF_MONTH).toString()
//        dateTuesday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 1).toString()
//        dateWednesday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 2).toString()
//        dateThursday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 3).toString()
//        dateFriday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 4).toString()
//        dateSaturday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 5).toString()
//        dateSunday.text = (weekStart.get(Calendar.DAY_OF_MONTH) + 6).toString()
//
//        // Highlight today's date frame
//        if (weekStart.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
//            weekStart.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
//            mondayFrame.setBackgroundColor(Color.parseColor("#F0F0F0"))
//        } else if ((weekStart.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) &&
//            (weekEnd.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
//                    weekEnd.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))) {
//            sundayFrame.setBackgroundColor(Color.parseColor("#F0F0F0"))
//        } else {
//            // Find the TextView for today's date and set its background color
//            val todayDate = today.get(Calendar.DAY_OF_WEEK)
//            val todayTextView: LinearLayout? = when (todayDate) {
//                Calendar.MONDAY -> mondayFrame
//                Calendar.TUESDAY -> tuesdayFrame
//                Calendar.WEDNESDAY -> wednesdayFrame
//                Calendar.THURSDAY -> thursdayFrame
//                Calendar.FRIDAY -> fridayFrame
//                Calendar.SATURDAY -> saturdayFrame
//                else -> null
//                // Handle potential edge cases (Sunday already handled)
//            }
//            todayTextView?.setBackgroundColor(Color.parseColor("#F0F0F0"))
//        }
//    }
//
//    private fun showDatePickerDialog(editText: LinearLayout) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//
//
//        val datePickerDialog = context?.let {
//            DatePickerDialog(it,
//            { _, year, month, dayOfMonth ->
//                val selectedDate = Calendar.getInstance()
//                selectedDate.set(year, month, dayOfMonth)
//                updateWeekBar(selectedDate)
//            }, year, month, dayOfMonth)
//        }
//        datePickerDialog?.show()
//    }

//    private fun showDatePickerDialog(editText: LinearLayout) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = context?.let {
//            DatePickerDialog(it, null, year, month, dayOfMonth)
//        }
//
//        // Set a listener to handle date changes
//        datePickerDialog?.setOnDateChangeListener { _, year, month, dayOfMonth ->
//            val selectedDate = Calendar.getInstance()
//            selectedDate.set(year, month, dayOfMonth)
//            updateWeekBar(selectedDate)
//            datePickerDialog.dismiss() // Dismiss the dialog after updating the week bar
//        }
//        datePickerDialog?.show()
//    }
}