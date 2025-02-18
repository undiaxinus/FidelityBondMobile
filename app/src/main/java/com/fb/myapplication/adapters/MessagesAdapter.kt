package com.fb.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fb.myapplication.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val contactName: String,
    val messagePreview: String,
    val timestamp: Date,
    val contactImageResId: Int = R.drawable.ic_person
)

class MessagesAdapter(
    private val onMessageClick: (Message) -> Unit
) : ListAdapter<Message, MessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage: CircleImageView = view.findViewById(R.id.contactImage)
        val contactName: TextView = view.findViewById(R.id.contactName)
        val messagePreview: TextView = view.findViewById(R.id.messagePreview)
        val messageTime: TextView = view.findViewById(R.id.messageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        
        holder.contactImage.setImageResource(message.contactImageResId)
        holder.contactName.text = message.contactName
        holder.messagePreview.text = message.messagePreview
        
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        holder.messageTime.text = timeFormat.format(message.timestamp)

        holder.itemView.setOnClickListener {
            onMessageClick(message)
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.contactName == newItem.contactName && 
                   oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
} 