package com.fb.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import android.view.View
import android.view.ViewGroup
import android.app.DatePickerDialog
import android.widget.TextView
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import android.graphics.Color
import com.fb.myapplication.fragments.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private val PERMISSION_REQUEST_CODE = 123
    private val smsManager: SmsManager by lazy {
        SmsManager.getDefault()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupNavigationDrawer()
        
        // Show dashboard fragment by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }
        
        checkSmsPermission()
        
        // Start the BondExpiryService
        startService(Intent(this, BondExpiryService::class.java))
    }

    private fun setupNavigationDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, DashboardFragment())
                    .commit()
            }
            R.id.nav_bonds -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, BondsFragment())
                    .commit()
            }
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ProfileFragment())
                    .commit()
            }
            R.id.nav_sms -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SmsFragment.newInstance())
                    .commit()
            }
            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            setupSmsNotifications()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupSmsNotifications()
                } else {
                    Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSmsNotifications() {
        // Example FidelityBond data class
        data class FidelityBond(
            val phoneNumber: String,
            val expiryDate: Date,
            val bondNumber: String
        )

        // Function to check if bond is expired
        fun isBondExpired(bond: FidelityBond): Boolean {
            val today = Calendar.getInstance().time
            return bond.expiryDate.before(today)
        }

        // Function to send SMS notification
        fun sendExpiryNotification(bond: FidelityBond) {
            try {
                val message = """
                    Dear Customer,
                    Your Fidelity Bond (${bond.bondNumber}) has expired.
                    Please contact our office for renewal.
                    Thank you.
                """.trimIndent()

                smsManager.sendTextMessage(
                    bond.phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
                
                Toast.makeText(
                    this,
                    "Expiry notification sent to ${bond.phoneNumber}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Failed to send SMS: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Example usage:
        // You would typically get this data from a database
        val sampleBond = FidelityBond(
            phoneNumber = "+1234567890",  // Replace with actual phone number
            expiryDate = Calendar.getInstance().apply { 
                add(Calendar.DAY_OF_MONTH, -1) // Yesterday
            }.time,
            bondNumber = "FB123456"
        )

        if (isBondExpired(sampleBond)) {
            sendExpiryNotification(sampleBond)
        }
    }

    private fun showAddBondDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_bond, null)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        // Setup rank dropdown
        val ranks = arrayOf(
            "Pat",
            "PCpl",
            "PSSg",
            "PMSg",
            "PSMS",
            "PCMS",
            "PEMS",
            "PLT",
            "PCPT",
            "PMAJ",
            "PLTCOL",
            "PCOL",
            "PBGEN",
            "PMGEN",
            "PLTGEN",
            "PGEN",
            "NUP"
        )
        
        // Setup units dropdown
        val units = arrayOf(
            "RMFB5",
            "AVSEU5",
            "NAGA CPO",
            "ALBAY PPO",
            "CAMARINES NORTE PPO",
            "CAMARINES SUR PPO",
            "CATANDUANES PPO",
            "MASBATE PPO",
            "SORSOGON PPO",
            "RHQ",
            "SDO/DO",
            "SUPPLY ACCOUNTABLE OFFICERS",
            "RIASS",
            "RHFPU5",
            "RMU5",
            "CIDG RFU5",
            "RIU5",
            "RFU5",
            "RACU5",
            "SAF"
        )

        // Setup rank dropdown
        val rankInput = dialogView.findViewById<AutoCompleteTextView>(R.id.rankInput)
        val rankAdapter = ArrayAdapter(this, R.layout.dropdown_item, ranks)
        rankInput.setAdapter(rankAdapter)

        // Setup unit dropdown
        val unitInput = dialogView.findViewById<AutoCompleteTextView>(R.id.unitInput)
        val unitAdapter = ArrayAdapter(this, R.layout.dropdown_item, units)
        unitInput.setAdapter(unitAdapter)

        // Setup date pickers and status fields
        val effectiveDateInput = dialogView.findViewById<TextInputEditText>(R.id.effectiveDateInput)
        val expirationDateInput = dialogView.findViewById<TextInputEditText>(R.id.expirationDateInput)
        val statusInput = dialogView.findViewById<TextInputEditText>(R.id.statusInput)
        val daysRemainingInput = dialogView.findViewById<TextInputEditText>(R.id.daysRemainingInput)
        
        setupDatePicker(effectiveDateInput)
        setupDatePicker(expirationDateInput)

        // Update status and days remaining when expiration date changes
        expirationDateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                    expirationDateInput.setText(dateFormat.format(calendar.time))
                    
                    // Calculate and update status and days remaining
                    updateStatusAndDaysRemaining(calendar.time, statusInput, daysRemainingInput)
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Setup buttons
        dialogView.findViewById<MaterialButton>(R.id.cancelButton)?.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.submitButton)?.setOnClickListener {
            // TODO: Validate and save bond data
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        editText.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun updateStatusAndDaysRemaining(
        expiryDate: Date,
        statusInput: TextInputEditText,
        daysRemainingInput: TextInputEditText
    ) {
        val today = Calendar.getInstance().time
        val daysRemaining = ((expiryDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
        
        val status = when {
            daysRemaining < 0 -> "EXPIRED"
            daysRemaining <= 30 -> "ABOUT TO EXPIRE"
            else -> "VALID"
        }
        
        val statusColor = when (status) {
            "EXPIRED" -> Color.RED
            "ABOUT TO EXPIRE" -> Color.parseColor("#FFA500") // Orange
            else -> Color.parseColor("#008000") // Green
        }
        
        statusInput.setText(status)
        statusInput.setTextColor(statusColor)
        
        daysRemainingInput.setText(if (daysRemaining < 0) "Expired" else "$daysRemaining days")
        daysRemainingInput.setTextColor(statusColor)
    }
} 