package com.bottech.medelert.notificationReminder

import AlarmDatabaseHelper
import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper
import com.bottech.medelert.data.format.AlarmDate
import com.bottech.medelert.data.format.AlarmTime
import com.bottech.medelert.data.format.DateTimeMillis
import com.bottech.medelert.data.format.MedicationAlarmDetails
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddReminderDialog(context: Context) : Dialog(context) {

    private lateinit var medicationName: EditText
    private lateinit var dosageDescription: EditText
    private lateinit var alarmDate: TextView
    private lateinit var numbOfDays: TextView
    private lateinit var saveButton: Button

    private var currentRow = 1  // Track the number of time selection rows
    private lateinit var timeSelectionSection: LinearLayout
    private lateinit var addTimesLayout: LinearLayout
    private lateinit var noDaysLayout: LinearLayout
    private lateinit var plusButton: ImageButton
    private lateinit var subtractButton: ImageButton
    private var numberOfDays = 1

    private lateinit var addFirTime: LinearLayout
    private lateinit var addTimeRow: ImageButton
    private lateinit var alarmTime1: TextView
    private lateinit var addSecTime: LinearLayout
    private lateinit var removeTimeRow2: ImageButton
    private lateinit var alarmTime2: TextView
    private lateinit var addThiTime: LinearLayout
    private lateinit var removeTimeRow3: ImageButton
    private lateinit var alarmTime3: TextView

    private val alarmManagerHelper = AlarmManagerHelper()
    private val alarmReceiver = AlarmReceiver()
    private val sharedPreferencesHelper = SharedPreferencesHelper(context)
    private val alarmDatabaseHelper = AlarmDatabaseHelper(context)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reminder_add)

        medicationName = findViewById(R.id.medication_et)
        dosageDescription = findViewById(R.id.dosageDescription_et)
        alarmDate = findViewById(R.id.alarmDate_et)
        numbOfDays = findViewById(R.id.numbOfDays_tv)
        saveButton = findViewById(R.id.saveReminder)

        timeSelectionSection = findViewById(R.id.timeSelectionSection)
        addTimesLayout = findViewById(R.id.addTimes)
        noDaysLayout = findViewById(R.id.noDaysLayout)
        plusButton = findViewById(R.id.plus_button)
        subtractButton = findViewById(R.id.subtract_button)

        addFirTime = findViewById(R.id.addFirTime)
        alarmTime1 = findViewById(R.id.alarmTime_et1)
        addTimeRow = findViewById(R.id.add_time_btn)
        addSecTime = findViewById(R.id.addSecTime)
        alarmTime2 = findViewById(R.id.alarmTime_et2)
        removeTimeRow2 = findViewById(R.id.remove_time_btn1)
        addThiTime = findViewById(R.id.addThiTime)
        alarmTime3 = findViewById(R.id.alarmTime_et3)
        removeTimeRow3 = findViewById(R.id.remove_time_btn2)


        val window = this.window
        window?.let {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
        }


        // Set input filters to limit characters
        medicationName.filters = arrayOf<InputFilter>(LengthFilter(15))
        dosageDescription.filters = arrayOf<InputFilter>(LengthFilter(25))

        // Set click listeners
        addTimeRow.setOnClickListener { toggleTimeSection(addSecTime, addThiTime) }
        removeTimeRow2.setOnClickListener { hideTimeSection(addSecTime) }
        removeTimeRow3.setOnClickListener { hideTimeSection(addThiTime) }

        // Set initial visibility based on minimum requirement (by default it always visible)
        addTimesLayout.visibility = View.VISIBLE

        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1  // Month is 0-indexed
        val day = today.get(Calendar.DAY_OF_MONTH)

        alarmDate.text = String.format("%02d-%02d-%d", day, month, year)
        numbOfDays.text = numberOfDays.toString()

        // Set click listeners for alarmDate
        alarmDate.setOnClickListener { showDatePickerDialog(alarmDate) }
        plusButton.setOnClickListener {
            if (numberOfDays < 10) {
                numberOfDays++
                numbOfDays.text = numberOfDays.toString()
                subtractButton.isEnabled = true
            } else {
                Toast.makeText(context, "Maximum 10 days allowed!", Toast.LENGTH_SHORT).show()
            }
        }
        subtractButton.setOnClickListener {
            if (numberOfDays > 1) {
                numberOfDays--
                numbOfDays.text = numberOfDays.toString()
                if (numberOfDays == 1) {
                    subtractButton.isEnabled = false
                }
            }
        }

        // Set initial time by default for alarmTime1
        alarmTime1.text = "00:00 AM"
        alarmTime2.text = "00:00 AM"
        alarmTime3.text = "00:00 AM"

        // Set click listeners for alarmTime TextViews
        alarmTime1.setOnClickListener { showTimePickerDialog(alarmTime1) }
        alarmTime2.setOnClickListener { showTimePickerDialog(alarmTime2) }
        alarmTime3.setOnClickListener { showTimePickerDialog(alarmTime3) }


        saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Kindly allow notification permission in Settings.", Toast.LENGTH_LONG).show()
                } else
                    saveReminder()

            } else
                saveReminder()
            // Save reminder information using alarmRepository
        }

    }

    private fun toggleTimeSection(current: LinearLayout, next: LinearLayout) {
        if (current.visibility == View.GONE) {
            current.visibility = View.VISIBLE
        } else if (next.visibility == View.GONE) {
            next.visibility = View.VISIBLE
        } else if (currentRow < 3) { // Check if less than 3 rows are visible
            next.visibility = View.VISIBLE
            currentRow++
        } else {
            // Show toast message for exceeding maximum selections
            Toast.makeText(context, "Maximum 3 times per day!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideTimeSection(section: LinearLayout) {
        if (section.visibility == View.VISIBLE) {
            section.visibility = View.GONE
            currentRow-- // Decrement only if a section was hidden
        }
    }

    private fun showDatePickerDialog(editText: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
            editText.setText(selectedDate)
        }, year, month, dayOfMonth)

        // Set the minimum date to today
        val minDate = Calendar.getInstance()
        datePickerDialog.datePicker.minDate = minDate.timeInMillis

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
            val amPm = if (hourOfDay >= 12) "PM" else "AM"
            val adjustedHour = if (hourOfDay == 0 ||hourOfDay == 12) 12 else (hourOfDay % 12)
            val formattedTime = String.format("%02d:%02d %s", adjustedHour, minute, amPm)
            editText.setText(formattedTime)
        }, hour, minute, false) // Use false for 12-hour format
        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveReminder() {
        val medicationName = medicationName.text.toString()
        val dosageDescription = dosageDescription.text.toString()

        // Check if mandatory fields are empty
        if (medicationName.isEmpty() || dosageDescription.isEmpty()) {
            Toast.makeText(context, "Please fill in mandatory fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate alarmDates and numberOfDays
        val alarmDate = alarmDate
        val dateString = alarmDate.text.toString()
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) // Adjust format if needed
        val startDate = formatter.parse(dateString)
        val numberOfDays = numbOfDays.text.toString().toInt()
        val alarmDatesList = generateAlarmDates(startDate, numberOfDays)

        // Get selected times
        val time1 = alarmTime1.text.toString().trim()
        var time2: String? = null
        var time3: String? = null

        if (addSecTime.visibility == View.VISIBLE) {
            time2 = alarmTime2.text.toString().trim()
        }

        if (addThiTime.visibility == View.VISIBLE) {
            time3 = alarmTime3.text.toString().trim()
        }

        val alarmTimeList = listOf(time1, time2, time3).filterNotNull()
        val convertedTimeList = convertTimesTo24HourFormat(alarmTimeList)
        val numberOfTimes = alarmTimeList.size

        // Check if any alarm time is "00:00 AM"
        if (alarmTimeList.any { it == "00:00 AM" }) {
            Toast.makeText(context, "Please select a valid alarm time.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate timeOfDay based on formattedTime
        val parsedAlarmTimes = alarmTimeList.map { alarmTime ->
            parseAlarmTime(alarmTime)
        }

        // Parse each date string and extract day, month, and year
        val parsedDates = alarmDatesList.map { parseDateStringToComponents(it) }

        // Extract day, month, and year into separate lists
        val daysList = parsedDates.map { it.first }
        val monthsList = parsedDates.map { it.second }
        val yearsList = parsedDates.map { it.third }

        // Extract hours, minutes, and timeOfDay into separate lists
        val hoursList = parsedAlarmTimes.map { it.first }
        val minutesList = parsedAlarmTimes.map { it.second }
        val timeOfDayList = parsedAlarmTimes.map { it.third }

        val timeInMillisList = millisecondsOfDatesAndTimes(alarmDatesList, convertedTimeList)
        val datesTimesList = datesTimesCombinations(alarmDatesList, convertedTimeList)
        val millisIdList = generateMillisIdList(alarmDatesList, convertedTimeList)

        val currentTimestamp = System.currentTimeMillis()
        val formatIt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val creationTimestamp = formatIt.format(Date(currentTimestamp))

        val userId: Int? = sharedPreferencesHelper.getUserId()

        // Create a new alarm and save it to the database
        val alarmDetails = MedicationAlarmDetails(
            userId = userId,
            medicationName = medicationName,
            dosageDescription = dosageDescription,
            numberOfDays = numberOfDays,
            creationDateTime = creationTimestamp,
            modificationDateTime = ""
        )


        val alarmDates = AlarmDate(
            userId = alarmDetails.userId,
            alarmDate = alarmDatesList,
            days = daysList,
            months = monthsList,
            year = yearsList
        )

        val alarmTimes = AlarmTime(
            userId = alarmDetails.userId,
            alarmTime = alarmTimeList,
            hours = hoursList,
            minutes = minutesList,
            timeOfDay = timeOfDayList
        )

        val dateTimeMillis = DateTimeMillis(
            millisId = millisIdList,
            userId = alarmDetails.userId,
            medicationName = alarmDetails.medicationName,
            medicationDosage = alarmDetails.dosageDescription,
            datesTimes = datesTimesList,
            timeInMillis = timeInMillisList
        )


        // Pass all relevant objects to insertAlarm

        val inserted = alarmDatabaseHelper.insertAlarm(alarmDetails, alarmDates, alarmTimes, dateTimeMillis)

        if (inserted) {
            // If alarm insertion is successful, set up alarms
            setupAlarms(context, dateTimeMillis) // Pass DateTimeMillis instead of separate lists
            Toast.makeText(context, "Reminder saved successfully!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // ... update UI (optional) ...
    }

    fun setupAlarms(context: Context, dateTimeMillis: DateTimeMillis) {
        for (i in dateTimeMillis.millisId.indices) {
            val millisId = dateTimeMillis.millisId[i]
            val timeInMillis = dateTimeMillis.timeInMillis[i]
            alarmManagerHelper.setupAlarm(context, millisId, timeInMillis) // Pass both millisId and timeInMillis
        }
    }

    fun convertTimesTo24HourFormat(timeList: List<String>): List<String> {
        return timeList.map { convertTimeTo24HourFormat(it) }
    }

    fun convertTimeTo24HourFormat(time: String): String {
        val timePattern = Regex("""(\d{1,2}):(\d{2}) (AM|PM)""")
        val match = timePattern.matchEntire(time)

        if (match != null) {
            val hours = match.groupValues[1].toInt()
            val minutes = match.groupValues[2].toInt()
            val period = match.groupValues[3]

            return if (period == "PM" && hours != 12) {
                (hours + 12).toString().padStart(2, '0') + ":" + minutes.toString().padStart(2, '0')
            } else if (period == "AM" && hours == 12) {
                "00:${minutes.toString().padStart(2, '0')}"
            } else {
                hours.toString().padStart(2, '0') + ":" + minutes.toString().padStart(2, '0')
            }
        } else {
            throw IllegalArgumentException("Invalid time format: $time")
        }
    }

    fun parseDateStringToComponents(dateString: String): Triple<Int, Int, Int> {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = formatter.parse(dateString)
        return date?.let {
            val calendar = Calendar.getInstance()
            calendar.time = date
            Triple(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        } ?: Triple(0, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun millisecondsOfDatesAndTimes(alarmDatesList: List<String>, alarmTimeList: List<String>): List<Long> {
        val millisecondsList = mutableListOf<Long>()

        // Assuming alarmDatesList and alarmTimeList are in the format "DD-MM-YYYY" and "HH:MM" respectively
        for (date in alarmDatesList) {
            for (time in alarmTimeList) {
                // Parse date and time
                val dateTime = LocalDateTime.parse("$date $time", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

                // Convert to Pakistan Standard Time (PST)
                val pstDateTime = dateTime.atZone(ZoneId.of("Asia/Karachi"))

                // Calculate milliseconds since epoch
                val milliseconds = pstDateTime.toInstant().toEpochMilli()

                millisecondsList.add(milliseconds)
            }
        }

        return millisecondsList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateMillisIdList(alarmDatesList: List<String>, alarmTimeList: List<String>): List<Int> {
        val millisIdList = mutableListOf<Int>()

        // Get the last saved millisId from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        var lastMillisId = sharedPreferences.getInt("lastMillisId", 10000) // Default is 10000

        for (date in alarmDatesList) {
            for (time in alarmTimeList) {
                // Increment the lastMillisId to get a unique id
                lastMillisId++
                millisIdList.add(lastMillisId)
            }
        }

        // Save the last used millisId back to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("lastMillisId", lastMillisId)
        editor.apply()

        return millisIdList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun datesTimesCombinations(alarmDatesList: List<String>, alarmTimeList: List<String>): List<String> {
        val datesTimesList = mutableListOf<String>()

        // Assuming alarmDatesList and alarmTimeList are in the format "DD-MM-YYYY" and "HH:MM" respectively
        for (date in alarmDatesList) {
            for (time in alarmTimeList) {
                // Parse date and time
                val dateTime = LocalDateTime.parse("$date $time", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

                // Format the date and time as "dd-MM-yyyy HH:mm"
                val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

                datesTimesList.add(formattedDateTime)
            }
        }
        return datesTimesList
    }

    fun parseAlarmTime(alarmTime: String): Triple<Int, Int, String> {
        val formatter = SimpleDateFormat("HH:mm a", Locale.getDefault())
        val date = formatter.parse(alarmTime)

        return if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            val timeOfDay = alarmTime.substringAfterLast(" ") // Extract AM/PM from the alarm time string
            Triple(hours, minutes, timeOfDay)
        } else {
            Triple(-1, -1, "") // Handle invalid alarm time
        }
    }

    fun generateAlarmDates(startDate: Date, numberOfDays: Int): List<String> {
        val alarmDates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        for (i in 0 until numberOfDays) {
            val date = calendar.time
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
            alarmDates.add(formattedDate)
            calendar.add(Calendar.DATE, 1)
        }
        return alarmDates
    }

}


