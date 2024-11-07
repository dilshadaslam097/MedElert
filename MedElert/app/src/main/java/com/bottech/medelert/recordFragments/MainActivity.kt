package com.bottech.medelert.recordFragments

import AlarmDatabaseHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
//import androidx.activity.EdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bottech.medelert.ChangePasswordDialogFragment
import com.bottech.medelert.R
import com.bottech.medelert.SharedPreferencesHelper
import com.bottech.medelert.UpdateProfileDialogFragment
import com.bottech.medelert.basics.WelcomeActivity
import com.bottech.medelert.data.DatabaseHelper
import com.bottech.medelert.notificationReminder.AddReminderDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var addRemButton: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navViewSettings: NavigationView
    private lateinit var navViewProfile: NavigationView
    private lateinit var toggleLeftMenu: ImageButton
    private lateinit var toggleRightProfile: ImageButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var sharedPreferences: SharedPreferences

    private val historyFragment = HistoryFragment()
    private val missedAlarmsFragment = MissedAlarmsFragment()
    private val allAlarmsFragment = AllAlarmsFragment()
    private val activeAlarmsFragment = ActiveAlarmsFragment()
    private val alarmDatabaseHelper = AlarmDatabaseHelper(this)

    private var backPressedTime: Long = 0L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        EdgeToEdge.enable(this)
        setContentView(R.layout.activity_main)

        // Check Android version
//        val currentapiVersion = Build.VERSION.SDK_INT
//        if (currentapiVersion < Build.VERSION_CODES.O) {
//            Toast.makeText(this, "Some features may not work on your device due to unsupported Android version.", Toast.LENGTH_LONG).show()
//        } else {
            // Check and request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            checkPermissions()
//        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
//        navViewSettings = findViewById(R.id.nav_view_settings)
        navViewProfile = findViewById(R.id.nav_view_profile)

//        toggleLeftMenu = findViewById(R.id.toggleLeftMenu)
        toggleRightProfile = findViewById(R.id.toggleRightProfile)

        bottomNavigationView = findViewById(R.id.bottom_Navigation)
        fragmentContainer = findViewById(R.id.fragment_container)
        addRemButton = findViewById(R.id.addReminder_btn)
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val databaseHelper = DatabaseHelper(this)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, allAlarmsFragment).commit()

//        val badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_missed)
//        badgeDrawable.isVisible = true
//        badgeDrawable.number = 5

//        toggleLeftMenu.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }

        toggleRightProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

//        navViewSettings.setNavigationItemSelectedListener { item ->
//            val itemId = item.itemId
//            when (itemId) {
//                R.id.nav_notification -> Toast.makeText(this, "Notification Tone, Time", Toast.LENGTH_SHORT).show()
//                R.id.nav_theme -> Toast.makeText(this, "Light Mode vs Dark Mode", Toast.LENGTH_SHORT).show()
//                R.id.nav_backup -> Toast.makeText(this, "Backup and Restore", Toast.LENGTH_SHORT).show()
//                R.id.nav_feedback -> Toast.makeText(this, "Feedback Form", Toast.LENGTH_SHORT).show()
//                R.id.nav_contactUs -> Toast.makeText(this, "Contact Us Form", Toast.LENGTH_SHORT).show()
//                R.id.nav_rating -> Toast.makeText(this, "Play Store Rating", Toast.LENGTH_SHORT).show()
//                R.id.nav_share -> {
//
////                    Toast.makeText(this, "Play Store Rating", Toast.LENGTH_SHORT).show()
//
////                    val intent = Intent(Intent.ACTION_SEND)
////                    intent.type = "text/plain"
////                    intent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app! Download it here: https://drive.google.com/file/d/1VFKvsf4kwTORVDOQmGvfUoCXtwrNM4yM/view?usp=sharing")
//
//                    // Add Google Drive share option
//                    val driveIntent = Intent(Intent.ACTION_SEND)
//                    driveIntent.type = "text/plain"
//                    driveIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app! Download it here: https://drive.google.com/file/d/1VFKvsf4kwTORVDOQmGvfUoCXtwrNM4yM/view?usp=sharing")
//                    driveIntent.data = Uri.parse("https://drive.google.com/file/d/1VFKvsf4kwTORVDOQmGvfUoCXtwrNM4yM/view?usp=sharing")
////                    intent.putExtra(Intent.EXTRA_STREAM, driveIntent)
//
//                    startActivity(Intent.createChooser(driveIntent, "Share App"))
//                }
//
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            false
//        }

        navViewProfile.setNavigationItemSelectedListener { item ->
            val itemId = item.itemId

            when (itemId) {
                R.id.nav_updateProfile -> {
                    val dialogFragment = UpdateProfileDialogFragment()
                    dialogFragment.show(supportFragmentManager, "ChangePasswordDialog")
                }
                R.id.nav_changePassword -> {
                    val dialogFragment = ChangePasswordDialogFragment()
                    dialogFragment.show(supportFragmentManager, "ChangePasswordDialog")
                }
                R.id.nav_deleteAccount -> {
                    // Show confirmation dialog before deleting the account
                    val confirmationDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Confirm Account Deletion")
                        .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                        .setPositiveButton("Yes") { dialog, which ->
                            // Delete account logic here
                            val sharedPreferencesHelper = SharedPreferencesHelper(this)
                            val cnic = sharedPreferencesHelper.getCnic().toString()  // Assuming your method returns a String
                            val newCNIC = cnic.replace("-", "")
                            Log.d("Checklogs", "checkCNICforDeletion $newCNIC")

                            if (cnic != null && databaseHelper.deleteAccount(newCNIC)) {
                                sharedPreferencesHelper.clearLoginData()
                                alarmDatabaseHelper.disableAllAlarmsForUser(getUserId())
                                Toast.makeText(this, "Account Deleted Successfully!", Toast.LENGTH_SHORT).show()

                                // Navigate to WelcomeActivity
                                val intent = Intent(this, WelcomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)

                                // Finish current task after starting WelcomeActivity
                                finishAffinity()
                            } else {
                                Toast.makeText(this, "Account Deletion Failed!", Toast.LENGTH_SHORT).show()
                            }

                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()

                    confirmationDialog.show()
                }

                R.id.nav_logout -> {
                    val confirmationDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to logout? This will disable all active alarms.")
                        .setPositiveButton("Logout") { dialog, which ->
                            sharedPreferencesHelper.clearLoginData()
                            alarmDatabaseHelper.disableAllAlarmsForUser(getUserId())
                            Toast.makeText(this, "Logout Successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, WelcomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finishAffinity()

                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()

                    confirmationDialog.show()
                }
                R.id.nav_share -> {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this MedElert app! Download it here: https://drive.google.com/drive/folders/1kCgzAQL8IYmd4l4M_Cmo6jLmbxwonIbb?usp=drive_link")  // Replace with your actual app store link
                    startActivity(Intent.createChooser(shareIntent, "Share App"))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.END)
            false
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_active -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, allAlarmsFragment).commit()
                    true
                }
                R.id.navigation_all_active -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, activeAlarmsFragment).commit()
                    true
                }
                R.id.navigation_missed -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, missedAlarmsFragment).commit()
                    true
                }
                R.id.navigation_history -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, historyFragment).commit()
                    true
                }
                else -> false
            }
        }

        addRemButton.setOnClickListener {
            // Show the dialog when needed
            val addReminderDialog = AddReminderDialog(this)
            addReminderDialog.show()

            
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )


        val permissionsToRequest = mutableListOf<String>()
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, proceed with app functionality
                // ...
            } else {
                Toast.makeText(this, "Some permissions are required for the app to function properly. Please consider granting them in Settings.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getUserId(): Int {
        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        return sharedPreferencesHelper.getUserId() ?: -1 // Handle cases where user ID is not found
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }
}
private const val REQUEST_CODE_PERMISSIONS = 101 // Choose a suitable request code