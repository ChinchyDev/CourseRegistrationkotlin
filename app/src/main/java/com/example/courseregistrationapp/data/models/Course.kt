package com.example.courseregistrationapp.data.models

data class Course(
    val courseId: String,
    val title: String,
    val department: String = "Computer Science"
)

