package com.example.workandstudy_app.data_school_schedule

import java.io.Serializable

data class DataTKB(
    val id: String,
    var dateStart: String,
    val timeStart: String,
    val timeEnd: String,
    val classCode: String,
    val subjectName: String,
    val subjectHP: String,
    val timeInDay: String,
    val location: String,
    val week: String
) : Serializable