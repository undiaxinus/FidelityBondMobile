package com.fb.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fb.myapplication.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup notification switch
        view.findViewById<SwitchMaterial>(R.id.notificationSwitch).setOnCheckedChangeListener { _, isChecked ->
            // TODO: Save notification preference
        }

        // Setup dark mode switch
        view.findViewById<SwitchMaterial>(R.id.darkModeSwitch).setOnCheckedChangeListener { _, isChecked ->
            // TODO: Implement dark mode
        }
    }
} 