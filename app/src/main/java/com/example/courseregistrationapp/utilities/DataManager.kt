package com.example.courseregistrationapp.utilities
import android.content.Context
import android.util.Log
import com.example.courseregistrationapp.data.models.Course
import com.example.courseregistrationapp.data.models.Registration
import com.example.courseregistrationapp.data.models.Student
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class DataManager(private val context: Context) {

    private val STUDENTS_FILE = "students.json"
    private val COURSES_FILE = "courses.json"
    private val REGISTRATIONS_FILE = "registrations.json"

    // Initialize with default courses if the file doesn't exist
    init {
        if (!File(context.filesDir, COURSES_FILE).exists()) {
            initializeCourses()
        }
    }

    private fun initializeCourses() {
        val courses = listOf(
            Course("CS101", "Introduction to Programming"),
            Course("CS201", "Data Structures"),
            Course("CS301", "Database Systems"),
            Course("CS401", "Software Engineering"),
            Course("CS501", "Artificial Intelligence"),
            Course("CS601", "Mobile App Development")
        )

        val jsonArray = JSONArray()
        courses.forEach { course ->
            val jsonObject = JSONObject().apply {
                put("courseId", course.courseId)
                put("title", course.title)
                put("department", course.department)
            }
            jsonArray.put(jsonObject)
        }

        context.openFileOutput(COURSES_FILE, Context.MODE_PRIVATE).use {
            it.write(jsonArray.toString().toByteArray())
        }
    }

    // Student Methods
    fun registerStudent(student: Student): Boolean {
        try {
            val students = getStudents().toMutableList()

            // Check if student ID already exists
            if (students.any { it.studentId == student.studentId }) {
                return false
            }

            students.add(student)
            saveStudents(students)
            return true
        } catch (e: Exception) {
            Log.e("DataManager", "Error registering student", e)
            return false
        }
    }

    fun authenticateStudent(studentId: String, password: String): Boolean {
        val students = getStudents()
        return students.any { it.studentId == studentId && it.password == password }
    }

    fun getStudents(): List<Student> {
        try {
            val file = File(context.filesDir, STUDENTS_FILE)
            if (!file.exists()) {
                return emptyList()
            }

            val content = file.readText()
            val jsonArray = JSONArray(content)
            val students = mutableListOf<Student>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                students.add(
                    Student(
                        jsonObject.getString("studentId"),
                        jsonObject.getString("name"),
                        jsonObject.getString("email"),
                        jsonObject.getString("password")
                    )
                )
            }

            return students
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading students", e)
            return emptyList()
        }
    }

    private fun saveStudents(students: List<Student>) {
        val jsonArray = JSONArray()
        students.forEach { student ->
            val jsonObject = JSONObject().apply {
                put("studentId", student.studentId)
                put("name", student.name)
                put("email", student.email)
                put("password", student.password)
            }
            jsonArray.put(jsonObject)
        }

        context.openFileOutput(STUDENTS_FILE, Context.MODE_PRIVATE).use {
            it.write(jsonArray.toString().toByteArray())
        }
    }

    // Course Methods
    fun getCourses(): List<Course> {
        try {
            val file = File(context.filesDir, COURSES_FILE)
            if (!file.exists()) {
                return emptyList()
            }

            val content = file.readText()
            val jsonArray = JSONArray(content)
            val courses = mutableListOf<Course>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                courses.add(
                    Course(
                        jsonObject.getString("courseId"),
                        jsonObject.getString("title"),
                        jsonObject.getString("department")
                    )
                )
            }

            return courses
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading courses", e)
            return emptyList()
        }
    }

    // Registration Methods
    fun registerCourse(studentId: String, courseId: String): Boolean {
        try {
            val registrations = getRegistrations().toMutableList()

            // Check if student is already registered for this course
            if (registrations.any { it.studentId == studentId && it.courseId == courseId }) {
                return false
            }

            val registration = Registration(
                UUID.randomUUID().toString(),
                studentId,
                courseId
            )

            registrations.add(registration)
            saveRegistrations(registrations)
            return true
        } catch (e: Exception) {
            Log.e("DataManager", "Error registering course", e)
            return false
        }
    }

    fun getRegistrations(): List<Registration> {
        try {
            val file = File(context.filesDir, REGISTRATIONS_FILE)
            if (!file.exists()) {
                return emptyList()
            }

            val content = file.readText()
            val jsonArray = JSONArray(content)
            val registrations = mutableListOf<Registration>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                registrations.add(
                    Registration(
                        jsonObject.getString("registrationId"),
                        jsonObject.getString("studentId"),
                        jsonObject.getString("courseId"),
                        jsonObject.getLong("timestamp")
                    )
                )
            }

            return registrations
        } catch (e: Exception) {
            Log.e("DataManager", "Error loading registrations", e)
            return emptyList()
        }
    }

    private fun saveRegistrations(registrations: List<Registration>) {
        val jsonArray = JSONArray()
        registrations.forEach { registration ->
            val jsonObject = JSONObject().apply {
                put("registrationId", registration.registrationId)
                put("studentId", registration.studentId)
                put("courseId", registration.courseId)
                put("timestamp", registration.timestamp)
            }
            jsonArray.put(jsonObject)
        }

        context.openFileOutput(REGISTRATIONS_FILE, Context.MODE_PRIVATE).use {
            it.write(jsonArray.toString().toByteArray())
        }
    }

    fun getStudentRegistrations(studentId: String): List<Course> {
        val registrations = getRegistrations().filter { it.studentId == studentId }
        val courses = getCourses()

        return registrations.mapNotNull { registration ->
            courses.find { it.courseId == registration.courseId }
        }
    }

    fun getAllRegistrations(): Map<String, List<Course>> {
        val result = mutableMapOf<String, List<Course>>()
        val students = getStudents()

        students.forEach { student ->
            result[student.studentId] = getStudentRegistrations(student.studentId)
        }

        return result
    }

    fun getStudentById(studentId: String): Student? {
        return getStudents().find { it.studentId == studentId }
    }
}