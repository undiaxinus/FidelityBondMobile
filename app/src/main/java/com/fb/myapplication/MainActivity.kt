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
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 123
    private val smsManager: SmsManager by lazy {
        SmsManager.getDefault()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupUI()
        checkSmsPermission()
        
        // Start the BondExpiryService
        startService(Intent(this, BondExpiryService::class.java))
    }

    private fun setupUI() {
        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.bondsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // TODO: Set adapter for RecyclerView

        // Setup Add Bond Button
        findViewById<MaterialButton>(R.id.addBondButton).setOnClickListener {
            // TODO: Implement add bond functionality
            Toast.makeText(this, "Add Bond feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Update stats (these would normally come from your database)
        updateStats(activeBonds = 5, expiringBonds = 2)
    }

    private fun updateStats(activeBonds: Int, expiringBonds: Int) {
        findViewById<android.widget.TextView>(R.id.activeBondsCount).text = activeBonds.toString()
        findViewById<android.widget.TextView>(R.id.expiringBondsCount).text = expiringBonds.toString()
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
} 