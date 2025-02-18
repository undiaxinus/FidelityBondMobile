package com.fb.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fb.myapplication.LoginActivity
import com.fb.myapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup edit profile button
        view.findViewById<MaterialButton>(R.id.editProfileButton).setOnClickListener {
            // TODO: Implement edit profile functionality
        }

        // Setup logout button
        view.findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes) { _, _ ->
                    // Clear any saved credentials/session
                    // TODO: Clear shared preferences, database sessions, etc.
                    
                    // Navigate to login screen
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }
} 