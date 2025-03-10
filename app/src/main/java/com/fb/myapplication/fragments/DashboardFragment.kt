package com.fb.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fb.myapplication.R
import com.google.android.material.button.MaterialButton
import java.util.*

class DashboardFragment : Fragment() {
    private val units = listOf(
        "RMFB5", "AVSEU5", "NAGA CPO", "ALBAY PPO", "CAMARINES NORTE PPO",
        "CAMARINES SUR PPO", "CATANDUANES PPO", "MASBATE PPO", "SORSOGON PPO"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStatCards(view)
        setupCalendar(view)
        setupContactList(view)
    }

    private fun setupStatCards(view: View) {
        // Set click listeners for "More info" buttons
        view.findViewById<MaterialButton>(R.id.grandTotalMoreInfo).setOnClickListener {
            // TODO: Show grand total details
        }
        view.findViewById<MaterialButton>(R.id.validMoreInfo).setOnClickListener {
            // TODO: Show valid bonds details
        }
        view.findViewById<MaterialButton>(R.id.expiringMoreInfo).setOnClickListener {
            // TODO: Show expiring bonds details
        }
        view.findViewById<MaterialButton>(R.id.expiredMoreInfo).setOnClickListener {
            // TODO: Show expired bonds details
        }

        // Update stats
        updateStats(
            grandTotal = 246,
            valid = 141,
            expiring = 23,
            expired = 82
        )
    }

    private fun updateStats(grandTotal: Int, valid: Int, expiring: Int, expired: Int) {
        view?.apply {
            findViewById<TextView>(R.id.grandTotalCount).text = grandTotal.toString()
            findViewById<TextView>(R.id.validCount).text = valid.toString()
            findViewById<TextView>(R.id.expiringCount).text = expiring.toString()
            findViewById<TextView>(R.id.expiredCount).text = expired.toString()
        }
    }

    private fun setupCalendar(view: View) {
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        
        calendarView.apply {
            // Set calendar colors programmatically
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface))
            
            // Set date selection listener
            setOnDateChangeListener { _, year, month, dayOfMonth ->
                // Handle date selection
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                // TODO: Handle the selected date
            }
        }
    }

    private fun setupContactList(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.contactsRecyclerView)
        val messageInput = view.findViewById<EditText>(R.id.messageInput)
        val sendButton = view.findViewById<MaterialButton>(R.id.sendMessage)

        recyclerView.layoutManager = LinearLayoutManager(context)
        // TODO: Set up RecyclerView adapter for contacts list

        // Populate contacts list with units
        // TODO: Create and set adapter with units list

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                // TODO: Send message to selected contacts
                messageInput.text.clear()
            }
        }
    }
} 