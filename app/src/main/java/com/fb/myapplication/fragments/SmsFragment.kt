package com.fb.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fb.myapplication.R
import com.fb.myapplication.adapters.Message
import com.fb.myapplication.adapters.MessagesAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class SmsFragment : Fragment() {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var manageContactsButton: MaterialButton
    private lateinit var newMessageFab: ExtendedFloatingActionButton
    private lateinit var tabLayout: TabLayout

    // Sample data structures for contacts and groups
    private val contacts = mutableListOf<Contact>()
    private val groups = mutableListOf<ContactGroup>()

    data class Contact(
        val id: Long,
        val name: String,
        val phoneNumber: String,
        val rank: String,
        val unit: String
    )

    data class ContactGroup(
        val id: Long,
        val name: String,
        val contacts: MutableList<Contact>
    )

    companion object {
        fun newInstance() = SmsFragment()
        private const val TAB_ALL = 0
        private const val TAB_UNREAD = 1
        private const val TAB_SENT = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupTabs()
        setupNewMessageButton()
        setupContactManagement()
        loadSampleData()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.messagesRecyclerView)
        manageContactsButton = view.findViewById(R.id.manageContactsButton)
        newMessageFab = view.findViewById(R.id.newMessageFab)
        tabLayout = view.findViewById(R.id.tabLayout)
    }

    private fun setupTabs() {
        with(tabLayout) {
            addTab(newTab().setText("All"))
            addTab(newTab().setText("Unread"))
            addTab(newTab().setText("Sent"))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        TAB_ALL -> loadAllMessages()
                        TAB_UNREAD -> loadUnreadMessages()
                        TAB_SENT -> loadSentMessages()
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(emptyList()) { message ->
            showMessageDetails(message)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messagesAdapter
        }

        loadAllMessages()
    }

    private fun setupNewMessageButton() {
        newMessageFab.setOnClickListener {
            showNewMessageDialog()
        }
    }

    private fun setupContactManagement() {
        manageContactsButton.setOnClickListener {
            showContactManagementDialog()
        }
    }

    private fun showMessageDetails(message: Message) {
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Message Details")
                .setMessage("""
                    Content: ${message.content}
                    Phone: ${message.phoneNumber}
                    Bond: ${message.bondNumber}
                    Time: ${formatTimestamp(message.timestamp)}
                    Status: ${if (message.isRead) "Read" else "Unread"}
                """.trimIndent())
                .setPositiveButton("Close", null)
                .show()
        }
    }

    private fun showNewMessageDialog() {
        context?.let { ctx ->
            val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_new_message, null)
            
            // Setup contact/group selection
            val contactInput = dialogView.findViewById<AutoCompleteTextView>(R.id.contactInput)
            val recipients = mutableListOf<String>()
            recipients.addAll(contacts.map { "${it.name} (${it.phoneNumber})" })
            recipients.addAll(groups.map { "Group: ${it.name}" })
            
            val adapter = android.widget.ArrayAdapter(ctx, android.R.layout.simple_dropdown_item_1line, recipients)
            contactInput.setAdapter(adapter)

            MaterialAlertDialogBuilder(ctx)
                .setTitle("New Message")
                .setView(dialogView)
                .setPositiveButton("Send") { _, _ ->
                    val selectedRecipient = contactInput.text.toString()
                    val messageInput = dialogView.findViewById<TextInputEditText>(R.id.messageInput)
                    val message = messageInput.text.toString()
                    
                    if (message.isNotEmpty() && selectedRecipient.isNotEmpty()) {
                        if (selectedRecipient.startsWith("Group:")) {
                            // Send to group
                            val groupName = selectedRecipient.substringAfter("Group: ")
                            val group = groups.find { it.name == groupName }
                            group?.contacts?.forEach { contact ->
                                sendMessage(message, contact)
                            }
                        } else {
                            // Send to individual contact
                            val phoneNumber = selectedRecipient.substringAfter("(").substringBefore(")")
                            val contact = contacts.find { it.phoneNumber == phoneNumber }
                            contact?.let { sendMessage(message, it) }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun sendMessage(content: String, contact: Contact) {
        val newMessage = Message(
            id = System.currentTimeMillis(),
            content = content,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            phoneNumber = contact.phoneNumber,
            bondNumber = null // TODO: Add bond selection
        )
        
        val currentMessages = messagesAdapter.getMessages().toMutableList()
        currentMessages.add(0, newMessage)
        messagesAdapter.updateMessages(currentMessages)
    }

    private fun showContactManagementDialog() {
        context?.let { ctx ->
            val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_manage_contacts, null)
            val dialog = MaterialAlertDialogBuilder(ctx)
                .setView(dialogView)
                .create()

            // Initialize views
            val contactsLayout = dialogView.findViewById<View>(R.id.contactsLayout)
            val groupsLayout = dialogView.findViewById<View>(R.id.groupsLayout)
            val tabLayout = dialogView.findViewById<TabLayout>(R.id.contactTabLayout)
            
            // Setup tab selection
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            contactsLayout.visibility = View.VISIBLE
                            groupsLayout.visibility = View.GONE
                        }
                        1 -> {
                            contactsLayout.visibility = View.GONE
                            groupsLayout.visibility = View.VISIBLE
                        }
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            // Setup contact list
            val contactsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.contactsRecyclerView)
            setupContactsList(contactsRecyclerView)

            // Setup groups list
            val groupsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.groupsRecyclerView)
            setupGroupsList(groupsRecyclerView)

            // Setup buttons
            dialogView.findViewById<MaterialButton>(R.id.addContactButton).setOnClickListener {
                showAddContactDialog()
            }

            dialogView.findViewById<MaterialButton>(R.id.createGroupButton).setOnClickListener {
                showCreateGroupDialog()
            }

            dialog.show()
        }
    }

    private fun showCreateGroupDialog() {
        context?.let { ctx ->
            val dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_create_group, null)
            val groupNameInput = dialogView.findViewById<TextInputEditText>(R.id.groupNameInput)
            val contactsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.selectContactsRecyclerView)

            // Setup contacts selection list
            setupContactsSelectionList(contactsRecyclerView)

            MaterialAlertDialogBuilder(ctx)
                .setTitle("Create Group")
                .setView(dialogView)
                .setPositiveButton("Create") { _, _ ->
                    val groupName = groupNameInput.text.toString()
                    if (groupName.isNotEmpty()) {
                        // TODO: Get selected contacts and create group
                        val newGroup = ContactGroup(
                            System.currentTimeMillis(),
                            groupName,
                            mutableListOf()
                        )
                        groups.add(newGroup)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupContactsList(recyclerView: RecyclerView) {
        // TODO: Implement contacts list adapter and setup
    }

    private fun setupGroupsList(recyclerView: RecyclerView) {
        // TODO: Implement groups list adapter and setup
    }

    private fun setupContactsSelectionList(recyclerView: RecyclerView) {
        // TODO: Implement contacts selection list adapter and setup
    }

    private fun showAddContactDialog() {
        // TODO: Implement add contact dialog
    }

    private fun loadAllMessages() {
        val sampleMessages = listOf(
            Message(
                id = 1,
                content = "Your bond FB-123456 is about to expire in 30 days",
                timestamp = System.currentTimeMillis() - 86400000,
                isRead = true,
                phoneNumber = "+1234567890",
                bondNumber = "FB-123456"
            ),
            Message(
                id = 2,
                content = "Bond FB-789012 has expired",
                timestamp = System.currentTimeMillis() - 172800000,
                isRead = false,
                phoneNumber = "+9876543210",
                bondNumber = "FB-789012"
            )
        )
        messagesAdapter.updateMessages(sampleMessages)
    }

    private fun loadUnreadMessages() {
        val unreadMessages = messagesAdapter.getMessages().filter { !it.isRead }
        messagesAdapter.updateMessages(unreadMessages)
    }

    private fun loadSentMessages() {
        // TODO: Implement sent messages loading
        messagesAdapter.updateMessages(emptyList())
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return android.text.format.DateFormat.getDateFormat(context)
            .format(date) + " " + android.text.format.DateFormat.getTimeFormat(context)
            .format(date)
    }

    private fun loadSampleData() {
        // Sample contacts
        contacts.addAll(listOf(
            Contact(1, "John Smith", "+1234567890", "PCPT", "NAGA CPO"),
            Contact(2, "Sarah Johnson", "+9876543210", "PMAJ", "ALBAY PPO"),
            Contact(3, "Mike Brown", "+1122334455", "PLTCOL", "MASBATE PPO")
        ))

        // Sample groups
        groups.addAll(listOf(
            ContactGroup(1, "CPO Officers", mutableListOf(contacts[0])),
            ContactGroup(2, "PPO Chiefs", mutableListOf(contacts[1], contacts[2]))
        ))
    }
} 