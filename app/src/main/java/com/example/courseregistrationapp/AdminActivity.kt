package com.example.courseregistrationapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.courseregistrationapp.utilities.DataManager

class AdminActivity : AppCompatActivity() {

    private lateinit var registrationsListView: ListView
    private lateinit var adminLogoutButton: Button
    private lateinit var dataManager: DataManager
    private var registrationData: MutableList<RegistrationItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Hide action bar for cleaner UI
        supportActionBar?.hide()

        // Initialize data manager
        dataManager = DataManager(this)

        // Initialize UI components
        registrationsListView = findViewById(R.id.registrationsListView)
        adminLogoutButton = findViewById(R.id.adminLogoutButton)

        // Load registrations
        loadRegistrations()

        // Set adapter for ListView
        registrationsListView.adapter = RegistrationsAdapter()

        // Set click listener for logout button
        adminLogoutButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadRegistrations() {
        // Clear existing data
        registrationData.clear()

        // Get all students
        val students = dataManager.getStudents()

        // Get all courses
        val courses = dataManager.getCourses()

        // Get all registrations
        val registrations = dataManager.getRegistrations()

        // Process registrations
        registrations.forEach { registration ->
            val student = students.find { it.studentId == registration.studentId }
            val course = courses.find { it.courseId == registration.courseId }

            if (student != null && course != null) {
                registrationData.add(
                    RegistrationItem(
                        student.name,
                        student.studentId,
                        "${course.courseId} - ${course.title}"
                    )
                )
            }
        }
    }

    // Data class for registration item
    data class RegistrationItem(
        val studentName: String,
        val studentId: String,
        val courseName: String
    )

    // Adapter for registrations ListView
    inner class RegistrationsAdapter : BaseAdapter() {

        override fun getCount(): Int = registrationData.size

        override fun getItem(position: Int): Any = registrationData[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(this@AdminActivity)
                .inflate(R.layout.item_registration, parent, false)

            val item = registrationData[position]

            view.findViewById<TextView>(R.id.studentNameTextView).text = item.studentName
            view.findViewById<TextView>(R.id.studentIdTextView).text = getString(R.string.student_id_display, item.studentId)
            view.findViewById<TextView>(R.id.courseTextView).text = getString(R.string.course_display, item.courseName)

            return view
        }
    }
}