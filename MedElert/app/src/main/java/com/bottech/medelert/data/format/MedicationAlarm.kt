package com.bottech.medelert.data.format

data class GetAllAlarmData(
    val alarmId: Int,
    val medicationName: String,
    val dosageDescription: String,
    val creationDateTime: String,
    val modificationDateTime: String,
    val isAlarmCompleted: Boolean,
    val isDateCompleted: Boolean,
    val alarmDateList: ArrayList<String>,
    val alarmTimeList: ArrayList<String>,
    val isTaken: Boolean,
    val isMissed: Boolean,
    val isSkipped: Boolean
)

data class GetMissedAlarmData(
    val millisId: Int,
    val datesTimes: String,
    val medicationName: String,
    val medicationDosage: String
)

data class AlarmStatus(
    val isTaken: Boolean,
    val isMissed: Boolean,
    val isSkipped: Boolean,
    val isEnabled: Boolean
)

data class GetTakenAlarmData(
    val millisId: Int,
    val datesTimes: String,
    val medicationName: String,
    val medicationDosage: String
)

data class GetActiveAlarmData(
    val millisId: Int,
    val datesTimes: String,
    val medicationName: String,
    val medicationDosage: String,
    val isEnabled: Boolean
)







data class MedicationAlarmDetails(
    val userId: Int?,
    val medicationName: String,
    val dosageDescription: String,
    val creationDateTime: String,
    val modificationDateTime: String,
    val numberOfDays: Int = 1,
    val alarmDates: List<AlarmDate> = emptyList(),
    val alarmTimes: List<AlarmTime> = emptyList(),
    val dateTimeMillis: List<DateTimeMillis> = emptyList()
)

data class AlarmDate(
    val userId: Int?,
    val alarmDate: List<String>,
    val days: List<Int>,
    val months: List<Int>,
    val year: List<Int>
)

data class AlarmTime(
    val userId: Int?,
    val alarmTime: List<String>,
    val hours: List<Int>,
    val minutes: List<Int>,
    val timeOfDay: List<String>
)

data class DateTimeMillis(
    val millisId: List<Int>,
    val userId: Int?,
    val medicationName: String,
    val medicationDosage: String,
    val datesTimes: List<String>,
    val timeInMillis: List<Long>
)