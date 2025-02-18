package com.fb.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fb.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BondsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bonds, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.bondsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // TODO: Set adapter for RecyclerView

        // Setup FAB
        view.findViewById<FloatingActionButton>(R.id.addBondFab).setOnClickListener {
            // TODO: Show add bond dialog
        }
    }
} 