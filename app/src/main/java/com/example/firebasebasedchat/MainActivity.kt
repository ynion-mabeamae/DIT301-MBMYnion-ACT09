package com.example.firebasebasedchat

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

// 1. Data model for the chat messages
data class ChatMessage(val text: String, val isMe: Boolean, val time: String)

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val messagesList = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("messages")

        val listView = findViewById<ListView>(R.id.messageList)
        val input = findViewById<EditText>(R.id.messageInput)
        val sendBtn = findViewById<Button>(R.id.sendBtn)

        // Setup Adapter
        chatAdapter = ChatAdapter(messagesList)
        listView.adapter = chatAdapter

        // Static Welcome Message
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        messagesList.add(ChatMessage("Stranger: Hello! Hope you have a great day!", false, currentTime))
        chatAdapter.notifyDataSetChanged()

        // Anonymous Authentication
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setupRealtimeListener()
            } else {
                Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Send Button Logic
        sendBtn.setOnClickListener {
            val text = input.text.toString().trim()
            val userId = auth.currentUser?.uid

            if (text.isNotEmpty() && userId != null) {
                val timeStamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

                val messageMap = mapOf(
                    "senderId" to userId,
                    "text" to text,
                    "time" to timeStamp
                )
                database.push().setValue(messageMap)
                input.setText("") // Clear the input field
            }
        }
    }

    private fun setupRealtimeListener() {
        val currentUserId = auth.currentUser?.uid

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                val senderId = snapshot.child("senderId").getValue(String::class.java)
                val text = snapshot.child("text").getValue(String::class.java)
                val time = snapshot.child("time").getValue(String::class.java) ?: ""

                if (text != null) {
                    val isMe = (senderId == currentUserId)
                    val displayName = if (isMe) "You: " else "Stranger: "

                    messagesList.add(ChatMessage(displayName + text, isMe, time))
                    chatAdapter.notifyDataSetChanged()

                    // Auto-scroll to the bottom of the list
                    findViewById<ListView>(R.id.messageList).post {
                        findViewById<ListView>(R.id.messageList).setSelection(messagesList.size - 1)
                    }
                }
            }
            override fun onChildChanged(s: DataSnapshot, p: String?) {}
            override fun onChildRemoved(s: DataSnapshot) {}
            override fun onChildMoved(s: DataSnapshot, p: String?) {}
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    // --- CUSTOM ADAPTER WITH ROUNDED BORDERS ---
    inner class ChatAdapter(private val messages: List<ChatMessage>) : BaseAdapter() {
        override fun getCount(): Int = messages.size
        override fun getItem(position: Int): Any = messages[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // Use the built-in two-line layout
            val view = convertView ?: LayoutInflater.from(this@MainActivity)
                .inflate(android.R.layout.simple_list_item_2, parent, false)

            val textMain = view.findViewById<TextView>(android.R.id.text1)
            val textTime = view.findViewById<TextView>(android.R.id.text2)
            val message = messages[position]

            // Style the message text
            textMain.text = message.text
            textMain.setTextColor(Color.BLACK)
            textMain.textSize = 16f

            // Style the timestamp
            textTime.text = message.time
            textTime.setTextColor(Color.parseColor("#757575")) // Dark Grey
            textTime.textSize = 11f

            // Create layout params to add vertical spacing between bubbles
            val params = AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
            )
            view.layoutParams = params

            if (message.isMe) {
                // ALIGN TO RIGHT (ME)
                textMain.gravity = Gravity.END
                textTime.gravity = Gravity.END

                // Apply the rounded drawable background
                view.setBackgroundResource(R.drawable.bubble_me)

                // Extra padding on the left pushes the bubble to the right side
                view.setPadding(100, 20, 30, 20)
            } else {
                // ALIGN TO LEFT (STRANGER)
                textMain.gravity = Gravity.START
                textTime.gravity = Gravity.START

                // Apply the rounded drawable background
                view.setBackgroundResource(R.drawable.bubble_stranger)

                // Extra padding on the right pushes the bubble to the left side
                view.setPadding(30, 20, 100, 20)
            }

            return view
        }
    }
}