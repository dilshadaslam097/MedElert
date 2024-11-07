import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.bottech.medelert.data.format.AlarmDate
import com.bottech.medelert.data.format.AlarmStatus
import com.bottech.medelert.data.format.AlarmTime
import com.bottech.medelert.data.format.DateTimeMillis
import com.bottech.medelert.data.format.GetActiveAlarmData
import com.bottech.medelert.data.format.GetAllAlarmData
import com.bottech.medelert.data.format.GetMissedAlarmData
import com.bottech.medelert.data.format.GetTakenAlarmData
import com.bottech.medelert.data.format.MedicationAlarmDetails


class AlarmDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "alarmDatabase.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL(
            """
        CREATE TABLE medication_alarms (
          alarmId INTEGER PRIMARY KEY AUTOINCREMENT,
          userId INTEGER NOT NULL,
          medicationName TEXT NOT NULL,
          dosageDescription TEXT NOT NULL,
          creationDateTime TEXT NOT NULL,
          modificationDateTime TEXT,
          numberOfDays INTEGER NOT NULL DEFAULT 1,
          isAlarmCompleted BOOLEAN NOT NULL DEFAULT 0,
          isCancelled BOOLEAN NOT NULL DEFAULT 0
        );
        """
        )
        db.execSQL(
            """
        CREATE TABLE alarm_dates (
          dateId INTEGER PRIMARY KEY AUTOINCREMENT,
          alarmId TEXT NOT NULL REFERENCES medication_alarms(alarmId),
          userId INTEGER NOT NULL,
          alarmDate TEXT NOT NULL DEFAULT (strftime('%Y-%m-%d')),
          days INTEGER NOT NULL DEFAULT (strftime('%d')),
          months INTEGER NOT NULL DEFAULT (strftime('%m')),
          year INTEGER NOT NULL DEFAULT (strftime('%Y')),
          isDateCompleted BOOLEAN NOT NULL DEFAULT 0
        );
        """
        )
        db.execSQL(
            """
        CREATE TABLE alarm_times (
          timeId INTEGER PRIMARY KEY AUTOINCREMENT,
          alarmId TEXT NOT NULL REFERENCES medication_alarms(alarmId),
          dateId TEXT NOT NULL REFERENCES alarm_dates(dateId),
          userId INTEGER NOT NULL,
          alarmTime TEXT NOT NULL DEFAULT (strftime('%H:%M %p')),
          hours INTEGER NOT NULL DEFAULT (strftime('%H')),
          minutes INTEGER NOT NULL DEFAULT (strftime('%M')),
          timeOfDay TEXT NOT NULL DEFAULT (strftime('%p')),
          isTaken BOOLEAN NOT NULL DEFAULT 0,
          isMissed BOOLEAN NOT NULL DEFAULT 0,
          isSkipped BOOLEAN NOT NULL DEFAULT 0,
          isEnabled BOOLEAN NOT NULL DEFAULT 1
        );
        """
        )
        db.execSQL(
            """
        CREATE TABLE alarm_millis (
          millisId INTEGER PRIMARY KEY,
          alarmId TEXT NOT NULL REFERENCES medication_alarms(alarmId),
          dateId TEXT NOT NULL REFERENCES alarm_dates(dateId),
          timeId TEXT NOT NULL REFERENCES alarm_times(timeId),
          userId INTEGER NOT NULL,
          medicationName TEXT NOT NULL,
          dosageDescription TEXT NOT NULL,
          datesTimes NOT NULL,
          timeInMillis INTEGER NOT NULL DEFAULT 0,
          isTaken BOOLEAN NOT NULL DEFAULT 0,
          isMissed BOOLEAN NOT NULL DEFAULT 0,
          isSkipped BOOLEAN NOT NULL DEFAULT 0,
          isEnabled BOOLEAN NOT NULL DEFAULT 1
        );
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
//        db.execSQL("DROP TABLE IF EXISTS medication_alarms")
//        db.execSQL("DROP TABLE IF EXISTS alarm_dates")
//        db.execSQL("DROP TABLE IF EXISTS alarm_times")
//        db.execSQL("DROP TABLE IF EXISTS alarm_millis")
//        onCreate(db)
    }

    fun disableAllAlarmsForUser(userId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isEnabled", 0)
        }
        db.update("alarm_millis", values, "userId = ?", arrayOf(userId.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getAlarmCancelStatus(alarmId: Int): Boolean {
        val db = readableDatabase

        val cursor = db.query(
            "medication_alarms", // Table name
            arrayOf("isCancelled"), // Column to retrieve (only isEnabled)
            "alarmId = ?", // Selection criteria
            arrayOf(alarmId.toString()), // Selection arguments
            null, // Group by
            null, // Having
            null // Order by
        )

        val isCancelled = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex("isCancelled")) == 1
        } else {
            false // Default value if no record found
        }
        cursor.close()
        db.close()
        return isCancelled
    }

    @SuppressLint("Range")
    fun getAlarmEnabledStatus(millisId: Int): Boolean {
        val db = readableDatabase

        val cursor = db.query(
            "alarm_millis", // Table name
            arrayOf("isEnabled"), // Column to retrieve (only isEnabled)
            "millisId = ?", // Selection criteria
            arrayOf(millisId.toString()), // Selection arguments
            null, // Group by
            null, // Having
            null // Order by
        )

        val isEnabled = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex("isEnabled")) == 1
        } else {
            false // Default value if no record found
        }
        cursor.close()
        db.close()
        return isEnabled
    }

    fun updateAlarmCancelStatus(alarmId: Int, isCancelled: Boolean) {
        val db = writableDatabase

        // Update the isCancelled flag in the medication_alarms table
        val values = ContentValues().apply {
            put("isCancelled", if (isCancelled) 1 else 0)
        }
        db.update("medication_alarms", values, "alarmId = ?", arrayOf(alarmId.toString()))

        // Update the isEnabled flag in the alarm_millis table for the given alarmId
        val newIsEnabledValue = if (isCancelled) 0 else 1
        val updateMillisQuery = "UPDATE alarm_millis SET isEnabled = ? WHERE alarmId = ?"
        val args = arrayOf(newIsEnabledValue.toString(), alarmId.toString())
        db.execSQL(updateMillisQuery, args)

        db.close()
    }

    fun cancelAlarm(alarmId: Int): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("isCancelled", true)
        }
        val rowsUpdated = db.update("medication_alarms", contentValues, "alarmId = ?", arrayOf(alarmId.toString()))
        db.close()
        return rowsUpdated > 0 // Return true if at least one row was updated (alarm cancelled)
    }

    fun updateAlarmEnabledStatus(millisId: Int, isEnabled: Boolean) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("isEnabled", if (isEnabled) 1 else 0)
        }

        // Update the table with the new value for isEnabled
        db.update("alarm_millis", values, "millisId = ?", arrayOf(millisId.toString()))
        db.close()
    }


    fun getAlarmStatus(millisId: Int): AlarmStatus {
        val db = writableDatabase

        val cursor = db.query(
            "alarm_millis", // Table name
            arrayOf("isTaken", "isMissed", "isSkipped", "isEnabled"), // Columns to retrieve
            "millisId = ?", // Selection criteria
            arrayOf(millisId.toString()), // Selection arguments
            null, // Group by
            null, // Having
            null // Order by
        )

        if (cursor.moveToFirst()) {
            val isTaken = cursor.getInt(0) == 1
            val isMissed = cursor.getInt(1) == 1
            val isSkipped = cursor.getInt(2) == 1
            val isEnabled = cursor.getInt(3) == 1
            cursor.close()
            return AlarmStatus(isTaken, isMissed, isSkipped, isEnabled)
        } else {
            cursor.close()
            return AlarmStatus(false, false, false, true) // Default values if no record found
        }
    }

    fun updateAlarmStatus(millisId: Int, isTaken: Boolean, isMissed: Boolean, isSkipped: Boolean) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("isTaken", if (isTaken) 1 else 0)
            put("isMissed", if (isMissed) 1 else 0)
            put("isSkipped", if (isSkipped) 1 else 0)
        }

        // Update the table with the new values
        db.update("alarm_millis", values, "millisId = ?", arrayOf(millisId.toString()))
        db.close() // Close the database after the operation
    }




//    fun getMissedAlarmData(): List<GetMissedAlarmData> {
//        val db = readableDatabase
//        val query = """
//    SELECT medication_alarms.alarmId, medication_alarms.medicationName, alarm_dates.alarmDate, alarm_times.alarmTime
//    FROM medication_alarms
//    INNER JOIN alarm_dates ON medication_alarms.alarmId = alarm_dates.alarmId
//    INNER JOIN alarm_times ON medication_alarms.alarmId = alarm_times.alarmId
//    WHERE alarm_times.isMissed = 1
//    ORDER BY alarm_dates.alarmDate DESC, alarm_times.alarmTime DESC
//"""
//        val cursor = db.rawQuery(query, null)
//
//        val alarmDataList = List<GetMissedAlarmData>
//        val alarmMap = mutableMapOf<Int, GetAllAlarmData>()
//
//        cursor.close()
//        db.close()
//        return alarmDataList
//    }

    @SuppressLint("Range")
    fun getTakenAlarmData(userIdValue : Int): List<GetTakenAlarmData> {

        val db = this.readableDatabase
        val query = "SELECT * FROM alarm_millis WHERE alarm_millis.userId = ? AND isTaken = 1 AND isMissed = 0 AND isSkipped = 0 ORDER BY datesTimes ASC"
        val cursor = db.rawQuery(query, arrayOf(userIdValue.toString()))
//        Log.d("CheckLogs ----- ", "queryMissed = " + cursor)

        val takenAlarmsList = ArrayList<GetTakenAlarmData>()

        if (cursor.moveToFirst()) {
            do {
                val millisId = cursor.getInt(cursor.getColumnIndex("millisId"))
                val datesTimes = cursor.getString(cursor.getColumnIndex("datesTimes"))
                val medicationName = cursor.getString(cursor.getColumnIndex("medicationName"))
                val medicationDosage = cursor.getString(cursor.getColumnIndex("dosageDescription"))
                val takenAlarmData = GetTakenAlarmData(millisId, datesTimes, medicationName, medicationDosage)
                takenAlarmsList.add(takenAlarmData)

                // Log the fetched data here:
//                Log.d("CheckLogs ----- ", "Fetched data: millisId = $millisId, datesTimes = $datesTimes")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return takenAlarmsList
    }

    @SuppressLint("Range")
    fun getActiveAlarmData(userIdValue : Int): List<GetActiveAlarmData> {

        val db = this.readableDatabase
        val query = "SELECT * FROM alarm_millis WHERE alarm_millis.userId = ? AND isTaken = 0 AND isMissed = 0 AND isSkipped = 0 ORDER BY datesTimes ASC"
        val cursor = db.rawQuery(query, arrayOf(userIdValue.toString()))
//        Log.d("CheckLogs ----- ", "queryMissed = " + cursor)

        val activeAlarmsList = ArrayList<GetActiveAlarmData>()

        if (cursor.moveToFirst()) {
            do {
                val millisId = cursor.getInt(cursor.getColumnIndex("millisId"))
                val datesTimes = cursor.getString(cursor.getColumnIndex("datesTimes"))
                val medicationName = cursor.getString(cursor.getColumnIndex("medicationName"))
                val medicationDosage = cursor.getString(cursor.getColumnIndex("dosageDescription"))
                val isEnabled = cursor.getInt(cursor.getColumnIndex("isEnabled")) == 1
                val activeAlarmData = GetActiveAlarmData(millisId, datesTimes, medicationName, medicationDosage, isEnabled)
                activeAlarmsList.add(activeAlarmData)

                // Log the fetched data here:
//                Log.d("CheckLogs ----- ", "Fetched data: millisId = $millisId, datesTimes = $datesTimes")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return activeAlarmsList
    }

    @SuppressLint("Range")
    fun getMissedAlarmData(userIdValue : Int): List<GetMissedAlarmData> {

        val db = this.readableDatabase
        val query = "SELECT * FROM alarm_millis WHERE alarm_millis.userId = ? AND isTaken = 0 AND (isMissed = 1 OR isSkipped = 1) ORDER BY datesTimes ASC"
        val cursor = db.rawQuery(query, arrayOf(userIdValue.toString()))
//        Log.d("CheckLogs ----- ", "queryMissed = " + cursor)

        val missedAlarmsList = ArrayList<GetMissedAlarmData>()

        if (cursor.moveToFirst()) {
            do {
                val millisId = cursor.getInt(cursor.getColumnIndex("millisId"))
                val datesTimes = cursor.getString(cursor.getColumnIndex("datesTimes"))
                val medicationName = cursor.getString(cursor.getColumnIndex("medicationName"))
                val medicationDosage = cursor.getString(cursor.getColumnIndex("dosageDescription"))
                val missedAlarmData = GetMissedAlarmData(millisId, datesTimes, medicationName, medicationDosage)
                missedAlarmsList.add(missedAlarmData)

                // Log the fetched data here:
//                Log.d("CheckLogs ----- ", "Fetched data: millisId = $millisId, datesTimes = $datesTimes")
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return missedAlarmsList
    }

    @SuppressLint("Range")
    fun getAllAlarmData(userIdValue : Int): List<GetAllAlarmData> {

        Log.d("CheckUserId -----","----"+userIdValue)
        val db = readableDatabase
        val query = "SELECT * FROM medication_alarms INNER JOIN alarm_dates ON medication_alarms.alarmId = alarm_dates.alarmId INNER JOIN alarm_times ON medication_alarms.alarmId = alarm_times.alarmId WHERE medication_alarms.userId = ? AND medication_alarms.isAlarmCompleted = 0 AND alarm_dates.isDateCompleted = 0 AND alarm_times.isTaken = 0 AND alarm_times.isMissed = 0 AND alarm_times.isSkipped = 0 AND alarm_times.isEnabled = 1 ORDER BY medication_alarms.creationDateTime DESC"
        val cursor = db.rawQuery(query, arrayOf(userIdValue.toString()))
//        Log.d("CheckLogs", "Query: $query")
//        Log.d("CheckLogs", "Cursor: $cursor")

        val alarmDataList = mutableListOf<GetAllAlarmData>()
        val alarmMap = mutableMapOf<Int, GetAllAlarmData>()

        while (cursor.moveToNext()) {
            val alarmId = cursor.getInt(cursor.getColumnIndex("alarmId"))
            val alarmDate = cursor.getString(cursor.getColumnIndex("alarmDate"))
            val alarmTime = cursor.getString(cursor.getColumnIndex("alarmTime"))

            // Check if the alarmId exists in the map
            val alarmData = alarmMap[alarmId] ?: GetAllAlarmData(
                alarmId,
                cursor.getString(cursor.getColumnIndex("medicationName")),
                cursor.getString(cursor.getColumnIndex("dosageDescription")),
                cursor.getString(cursor.getColumnIndex("creationDateTime")),
                cursor.getString(cursor.getColumnIndex("modificationDateTime")),
                false, // isAlarmCompleted
                false, // isDateCompleted
                ArrayList<String>(),
                ArrayList<String>(),
                false, // isTaken
                false, // isMissed
                false // isSkipped
            )

            // Add unique alarm date and time to the lists
            if (!alarmData.alarmDateList.contains(alarmDate)) {
                alarmData.alarmDateList.add(alarmDate)
            }
            if (!alarmData.alarmTimeList.contains(alarmTime)) {
                alarmData.alarmTimeList.add(alarmTime)
            }

            // Update the map with the updated alarm data
            alarmMap[alarmId] = alarmData.copy(
                alarmDateList = ArrayList(alarmData.alarmDateList),
                alarmTimeList = ArrayList(alarmData.alarmTimeList)
            )
//            Log.d("CheckLogs", "Fetched alarmId: $alarmId, alarmDate: $alarmDate, alarmTime: $alarmTime")
        }

        // Add all alarm data from the map to the list
        alarmDataList.addAll(alarmMap.values)

//        Log.d("CheckLogs", "Final alarmDataList: $alarmDataList")

        cursor.close()
        db.close()
        return alarmDataList
    }

    fun insertAlarm(alarmDetails: MedicationAlarmDetails, alarmDates: AlarmDate, alarmTimes: AlarmTime, dateTimeMillis: DateTimeMillis): Boolean {
        val db = writableDatabase

        // Insert medication alarm details
        val medicationAlarmValues = ContentValues().apply {
            put("userId", alarmDetails.userId)
            put("medicationName", alarmDetails.medicationName)
            put("dosageDescription", alarmDetails.dosageDescription)
            put("numberOfDays", alarmDetails.numberOfDays)
            put("creationDateTime", alarmDetails.creationDateTime)
            put("modificationDateTime", alarmDetails.modificationDateTime)
        }

        val medicationAlarmId =
            db.insert("medication_alarms", null, medicationAlarmValues)
        if (medicationAlarmId == -1L) {
            db.close()
            return false // Error inserting medication alarm details
        }

        // Insert medication alarm dates
        var medicationDateId: Long = 0
        for (i in alarmDates.alarmDate.indices) {
            val alarmDateValues = ContentValues().apply {
                put("alarmId", medicationAlarmId)
                put("userId", alarmDates.userId)
                put("alarmDate", alarmDates.alarmDate[i])
                put("days", alarmDates.days[i])
                put("months", alarmDates.months[i])
                put("year", alarmDates.year[i])
            }
            medicationDateId =
                db.insert("alarm_dates", null, alarmDateValues)
            if (medicationDateId == -1L) {
                db.close()
                return false // Error inserting alarm date
            }
        }

        // Insert medication alarm times
        var medicationTimeId: Long = 0
        for (i in alarmTimes.alarmTime.indices) {
            val alarmTimeValues = ContentValues().apply {
                put("alarmId", medicationAlarmId)
                put("dateId", medicationDateId)
                put("userId", alarmTimes.userId)
                put("alarmTime", alarmTimes.alarmTime[i])
                put("hours", alarmTimes.hours[i])
                put("minutes", alarmTimes.minutes[i])
                put("timeOfDay", alarmTimes.timeOfDay[i])
            }
            medicationTimeId =
                db.insert("alarm_times", null, alarmTimeValues)
            if (medicationTimeId == -1L) {
                db.close()
                return false // Error inserting alarm time
            }
        }

//         Insert medication alarm times
        for (i in dateTimeMillis.timeInMillis.indices) {
            val alarmMilliseconds = ContentValues().apply {
                put("millisId", dateTimeMillis.millisId[i])
                put("timeId", medicationTimeId)
                put("dateId", medicationDateId)
                put("alarmId", medicationAlarmId)
                put("userId", dateTimeMillis.userId)
                put("medicationName", dateTimeMillis.medicationName)
                put("dosageDescription", dateTimeMillis.medicationDosage)
                put("datesTimes", dateTimeMillis.datesTimes[i])
                put("timeInMillis", dateTimeMillis.timeInMillis[i])
            }
            val millisecondsId =
                db.insert("alarm_millis", null, alarmMilliseconds)
            if (millisecondsId == -1L) {
                db.close()
                return false // Error inserting alarm milliseconds
            }
        }

        db.close()
        return true // All data inserted successfully
    }

//    fun updateAlarm(alarmDetails: MedicationAlarmDetails, alarmDates: AlarmDate, alarmTimes: AlarmTime, dateTimeMillis: DateTimeMillis): Boolean {
//        val db = writableDatabase
//
//        // Insert medication alarm details
//        val medicationAlarmValues = ContentValues().apply {
////            put("alarmId", alarmDetails.alarmId)
//            put("medicationName", alarmDetails.medicationName)
//            put("dosageDescription", alarmDetails.dosageDescription)
//            put("numberOfDays", alarmDetails.numberOfDays)
//            put("creationDateTime", alarmDetails.creationDateTime)
//            put("modificationDateTime", alarmDetails.modificationDateTime)
//            put("isAlarmCompleted", alarmDetails.isAlarmCompleted)
//            put("isCancelled", alarmDetails.isCancelled)
//        }
//
//        val medicationAlarmId = db.insert("medication_alarms", null, medicationAlarmValues)
//        if (medicationAlarmId == -1L) {
//            db.close()
//            return false // Error inserting medication alarm details
//        }
//
//        // Insert medication alarm dates
//        var medicationDateId: Long = 0
//        for (i in alarmDates.alarmDate.indices) {
//            val alarmDateValues = ContentValues().apply {
////                put("dateId", alarmDates.dateId)
//                put("alarmId", medicationAlarmId)
//                put("alarmDate", alarmDates.alarmDate[i])
//                put("days", alarmDates.days[i])
//                put("months", alarmDates.months[i])
//                put("year", alarmDates.year[i])
//                put("isDateCompleted", alarmDates.isDateCompleted)
//            }
//            medicationDateId = db.insert("alarm_dates", null, alarmDateValues)
//            if (medicationDateId == -1L) {
//                db.close()
//                return false // Error inserting alarm date
//            }
//        }
//
//        // Insert medication alarm times
//        var medicationTimeId: Long = 0
//        for (i in alarmTimes.alarmTime.indices) {
//            val alarmTimeValues = ContentValues().apply {
////                put("timeId", alarmTimes.timeId)
//                put("alarmId", medicationAlarmId)
//                put("dateId", medicationDateId)
//                put("hours", alarmTimes.hours[i])
//                put("minutes", alarmTimes.minutes[i])
//                put("alarmTime", alarmTimes.alarmTime[i])
//                put("timeOfDay", alarmTimes.timeOfDay[i])
//                put("isTaken", alarmTimes.isTaken)
//                put("isMissed", alarmTimes.isMissed)
//                put("isSkipped", alarmTimes.isSkipped)
//                put("skippedTime", alarmTimes.skippedTime)
//            }
//            medicationTimeId = db.insert("alarm_times", null, alarmTimeValues)
//            if (medicationTimeId == -1L) {
//                db.close()
//                return false // Error inserting alarm time
//            }
//        }
//
////         Insert medication alarm times
//        for (i in dateTimeMillis.timeInMillis.indices) {
//            val alarmMilliseconds = ContentValues().apply {
////                put("millisId", dateTimeMillis.millisId)
//                put("timeId", medicationTimeId)
//                put("dateId", medicationDateId)
//                put("alarmId", medicationAlarmId)
//                put("timeInMillis", dateTimeMillis.timeInMillis[i])
//            }
//            val millisecondsId = db.insert("alarm_millis", null, alarmMilliseconds)
//            if (millisecondsId == -1L) {
//                db.close()
//                return false // Error inserting alarm milliseconds
//            }
//        }
//
//        db.close()
//        return true // All data inserted successfully
//    }
}