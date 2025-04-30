package com.example.courseregistrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var confirmationTextView: TextView
    private lateinit var courseInfoTextView: TextView
    private lateinit var backToMainButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Hide action bar for cleaner UI
        supportActionBar?.hide()

        // Get data from intent
        val courseTitle = intent.getStringExtra("courseTitle") ?: ""
        val studentId = intent.getStringExtra("studentId") ?: ""

        // Initialize UI components
        confirmationTextView = findViewById(R.id.confirmationTextView)
        courseInfoTextView = findViewById(R.id.courseInfoTextView)
        backToMainButton = findViewById(R.id.backToMainButton)

        // set the text for confirmationTextView
        confirmationTextView.text = "Thank you for registering"

        // Set course information
        courseInfoTextView.text = getString(R.string.text_course_info, courseTitle)

        // Set click listener for button
        backToMainButton.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("studentId", studentId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)  // Clear back stack
            startActivity(intent)
            finish()
        }
    }
}