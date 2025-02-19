package com.fb.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fb.myapplication.R
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(
    private var messages: List<Message> = emptyList(),
    private val onMessageClick: (Message) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    fun getMessages(): List<Message> = messages

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val contentText: TextView = view.findViewById(R.id.messageContent)
        private val timestampText: TextView = view.findViewById(R.id.messageTimestamp)
        private val phoneNumberText: TextView = view.findViewById(R.id.phoneNumber)
        private val bondNumberText: TextView = view.findViewById(R.id.bondNumber)

        fun bind(message: Message, onMessageClick: (Message) -> Unit) {
            contentText.text = message.content
            timestampText.text = formatTimestamp(message.timestamp)
            phoneNumberText.text = message.phoneNumber ?: "N/A"
            bondNumberText.text = message.bondNumber ?: "N/A"

            // Apply read/unread styling
            itemView.alpha = if (message.isRead) 0.7f else 1.0f
            
            itemView.setOnClickListener { onMessageClick(message) }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position], onMessageClick)
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
} 