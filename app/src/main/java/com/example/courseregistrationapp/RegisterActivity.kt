package com.example.courseregistrationapp


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.courseregistrationapp.data.models.Student
import com.example.courseregistrationapp.utilities.DataManager
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var studentIdEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Hide action bar for cleaner UI
        supportActionBar?.hide()

        // Initialize data manager
        dataManager = DataManager(this)

        // Initialize UI components
        studentIdEditText = findViewById(R.id.registerStudentIdEditText)
        nameEditText = findViewById(R.id.registerNameEditText)
        emailEditText = findViewById(R.id.registerEmailEditText)
        passwordEditText = findViewById(R.id.registerPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginTextView = findViewById(R.id.loginTextView)

        // Set click listeners
        registerButton.setOnClickListener {
            registerStudent()
        }

        loginTextView.setOnClickListener {
            finish()  // Go back to login screen
        }
    }

    private fun registerStudent() {
        val studentId = studentIdEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate fields
        if (studentId.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fields_required), Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password match
        if (password != confirmPassword) {
            Toast.makeText(this, getString(R.string.error_passwords_dont_match), Toast.LENGTH_SHORT).show()
            return
        }

        // Create student object
        val student = Student(studentId, name, email, password)

        // Register student
        if (dataManager.registerStudent(student)) {
            Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show()

            // Navigate to login page
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_student_id_exists), Toast.LENGTH_SHORT).show()
        }
    }
}