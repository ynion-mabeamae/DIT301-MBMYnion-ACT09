package com.example.firebasebasedchat

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

// NOTE: data class ChatMessage was removed from here because it already exists in MainActivity.kt

class ChatAdapter(private val context: Context, private val messages: List<ChatMessage>) : BaseAdapter() {

    override fun getCount(): Int = messages.size
    override fun getItem(position: Int): Any = messages[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        val message = messages[position]

        textView.text = message.text
        textView.setTextColor(Color.BLACK)
        textView.textSize = 16f

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (message.isMe) {
            textView.gravity = Gravity.END
            textView.setBackgroundColor(Color.parseColor("#E1FFC7")) // Green
        } else {
            textView.gravity = Gravity.START
            textView.setBackgroundColor(Color.parseColor("#F0F0F0")) // Grey
        }

        textView.setPadding(40, 20, 40, 20)

        return view
    }
}