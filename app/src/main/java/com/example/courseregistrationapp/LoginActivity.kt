package com.example.courseregistrationapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.courseregistrationapp.utilities.DataManager
import com.google.android.material.textfield.TextInputEditText
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var studentIdEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var studentLoginButton: Button
    private lateinit var adminLoginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var dataManager: DataManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide action bar for cleaner UI
        supportActionBar?.hide()

        // Initialize data manager
        dataManager = DataManager(this)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("CourseRegistrationPrefs", MODE_PRIVATE)

        // Initialize UI components
        studentIdEditText = findViewById(R.id.studentIdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        studentLoginButton = findViewById(R.id.studentLoginButton)
        adminLoginButton = findViewById(R.id.adminLoginButton)
        registerTextView = findViewById(R.id.registerTextView)

        // Set click listeners
        studentLoginButton.setOnClickListener {
            loginAsStudent()
        }

        adminLoginButton.setOnClickListener {
            loginAsAdmin()
        }

        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Check if user is already logged in
        checkExistingLogin()
    }

    private fun checkExistingLogin() {
        val studentId = sharedPreferences.getString("studentId", null)
        if (!studentId.isNullOrEmpty()) {
            // Student is already logged in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
            finish()
        }
    }

    private fun loginAsStudent() {
        val studentId = studentIdEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fields_required), Toast.LENGTH_SHORT).show()
            return
        }

        if (dataManager.authenticateStudent(studentId, password)) {
            // Save login status
            sharedPreferences.edit() { putString("studentId", studentId) }

            /* Navigate to MainActivity */
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("studentId", studentId)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginAsAdmin() {
        val studentId = studentIdEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (studentId == getString(R.string.admin_id) && password == getString(R.string.admin_password)) {
            // Navigate to AdminActivity
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
        }
    }
}