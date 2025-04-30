package com.example.courseregistrationapp.data.models

data class Registration(
    val registrationId: String,
    val studentId: String,
    val courseId: String,
    val timestamp: Long = System.currentTimeMillis()
)