package com.fb.myapplication

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.Date

class BondExpiryCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val smsManager: SmsManager by lazy {
        SmsManager.getDefault()
    }

    data class FidelityBond(
        val phoneNumber: String,
        val expiryDate: Date,
        val bondNumber: String
    )

    override fun doWork(): Result {
        try {
            // For testing, create some sample bonds
            val bonds = listOf(
                FidelityBond(
                    phoneNumber = "+1234567890",
                    expiryDate = Calendar.getInstance().apply { 
                        add(Calendar.DAY_OF_MONTH, -1) 
                    }.time,
                    bondNumber = "FB123456"
                ),
                FidelityBond(
                    phoneNumber = "+0987654321",
                    expiryDate = Calendar.getInstance().apply { 
                        add(Calendar.DAY_OF_MONTH, 1) 
                    }.time,
                    bondNumber = "FB789012"
                )
            )

            // Check each bond
            bonds.forEach { bond ->
                if (isBondExpired(bond)) {
                    sendExpiryNotification(bond)
                }
            }

            return Result.success()
        } catch (e: Exception) {
            Log.e("BondExpiryCheckWorker", "Error checking bonds: ${e.message}")
            return Result.failure()
        }
    }

    private fun isBondExpired(bond: FidelityBond): Boolean {
        val today = Calendar.getInstance().time
        return bond.expiryDate.before(today)
    }

    private fun sendExpiryNotification(bond: FidelityBond) {
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
            
            Log.i("BondExpiryCheckWorker", "SMS sent to ${bond.phoneNumber}")
        } catch (e: Exception) {
            Log.e("BondExpiryCheckWorker", "Failed to send SMS: ${e.message}")
        }
    }
} 