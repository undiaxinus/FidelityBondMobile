package com.fb.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.card.MaterialCardView
import android.widget.EditText
import android.widget.ImageView
import de.hdodenhof.circleimageview.CircleImageView
import com.fb.myapplication.adapters.Message
import com.fb.myapplication.adapters.MessagesAdapter
import java.util.*

class SmsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newMessageFab: FloatingActionButton
    private lateinit var searchCard: MaterialCardView
    private lateinit var searchInput: EditText
    private lateinit var menuButton: ImageView
    private lateinit var profileImage: CircleImageView
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.messagesRecyclerView)
        newMessageFab = view.findViewById(R.id.newMessageFab)
        searchCard = view.findViewById(R.id.searchCard)
        searchInput = view.findViewById(R.id.searchInput)
        menuButton = view.findViewById(R.id.menuButton)
        profileImage = view.findViewById(R.id.profileImage)

        // Setup RecyclerView
        messagesAdapter = MessagesAdapter { message ->
            // Handle message click
            Toast.makeText(context, "Clicked: ${message.contactName}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter
        }

        // Load sample data
        loadSampleMessages()

        // Setup click listeners
        newMessageFab.setOnClickListener {
            // TODO: Implement new message action
            Toast.makeText(context, "New Message", Toast.LENGTH_SHORT).show()
        }

        menuButton.setOnClickListener {
            // Open navigation drawer
            (activity as? MainActivity)?.openDrawer()
        }

        searchInput.setOnClickListener {
            // TODO: Implement search action
            Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
        }

        profileImage.setOnClickListener {
            // TODO: Implement profile action
            Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSampleMessages() {
        val sampleMessages = listOf(
            Message(
                "John Smith",
                "Hey, I need to renew my bond. Can you help me with the process?",
                Calendar.getInstance().apply { add(Calendar.MINUTE, -5) }.time
            ),
            Message(
                "Mary Johnson",
                "Thank you for the bond renewal reminder!",
                Calendar.getInstance().apply { add(Calendar.HOUR, -2) }.time
            ),
            Message(
                "David Wilson",
                "I've submitted the required documents for my new bond application.",
                Calendar.getInstance().apply { add(Calendar.HOUR, -4) }.time
            ),
            Message(
                "Sarah Brown",
                "When will my bond certificate be ready for pickup?",
                Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time
            ),
            Message(
                "Michael Davis",
                "Please send me the list of requirements for bond application.",
                Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time
            )
        )
        messagesAdapter.submitList(sampleMessages)
    }

    companion object {
        fun newInstance() = SmsFragment()
    }
} 