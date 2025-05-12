package com.example.workandstudy_app.school_schedule

import com.example.workandstudy_app.data_school_schedule.DataSubject

class SortListTimeSubject(private val list: MutableList<DataSubject>) {
    val timeBegin = list.map { it.time_Start }

}