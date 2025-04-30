package com.example.courseregistrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.courseregistrationapp.data.models.Course
import com.example.courseregistrationapp.utilities.DataManager
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var courseSpinner: Spinner
    private lateinit var studentIdEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var viewRegistrationsTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var dataManager: DataManager
    private lateinit var studentId: String
    private lateinit var courses: List<Course>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide action bar
        supportActionBar?.hide()

        // Get student ID from intent
        studentId = intent.getStringExtra("studentId") ?: ""
        if (studentId.isEmpty()) {
            // No student ID, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize data manager
        dataManager = DataManager(this)

        // Get courses
        courses = dataManager.getCourses()

        // Get student data
        val student = dataManager.getStudentById(studentId)

        // Initialize UI components
        welcomeTextView = findViewById(R.id.welcomeTextView)
        courseSpinner = findViewById(R.id.courseSpinner)
        studentIdEditText = findViewById(R.id.studentIdEditText)
        registerButton = findViewById(R.id.registerButton)
        viewRegistrationsTextView = findViewById(R.id.viewRegistrationsTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Set welcome message with student name
        welcomeTextView.text = getString(R.string.welcome_message, student?.name ?: "Student")

        // Set student ID in EditText
        studentIdEditText.setText(studentId)

        // Set up spinner with courses
        setupCourseSpinner()

        // Set click listeners
        registerButton.setOnClickListener {
            registerForCourse()
        }

        viewRegistrationsTextView.setOnClickListener {
            showRegistrations()
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun setupCourseSpinner() {
        val courseNames = courses.map { "${it.courseId} - ${it.title}" }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter
    }

    private fun registerForCourse() {
        val selectedPosition = courseSpinner.selectedItemPosition
        if (selectedPosition >= 0 && selectedPosition < courses.size) {
            val selectedCourse = courses[selectedPosition]

            if (dataManager.registerCourse(studentId, selectedCourse.courseId)) {
                // Registration successful, navigate to confirmation
                val intent = Intent(this, ConfirmationActivity::class.java)
                intent.putExtra("courseTitle", selectedCourse.title)
                intent.putExtra("studentId", studentId)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_already_registered), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.error_registration_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRegistrations() {
        val registeredCourses = dataManager.getStudentRegistrations(studentId)

        if (registeredCourses.isEmpty()) {
            Toast.makeText(this, "You are not registered for any courses", Toast.LENGTH_SHORT).show()
            return
        }

        val coursesList = registeredCourses.joinToString("\n") { "• ${it.courseId} - ${it.title}" }

        AlertDialog.Builder(this)
            .setTitle("Your Registrations")
            .setMessage(coursesList)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun logout() {
        // Clear shared preferences
        getSharedPreferences("CourseRegistrationPrefs", MODE_PRIVATE)
            .edit() {
                remove("studentId")
            }

        // Navigate to login screen
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}