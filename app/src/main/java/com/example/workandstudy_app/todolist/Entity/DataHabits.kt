package com.example.workandstudy_app.todolist.Entity


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataHabits(
    val id: Int = 0,
    var tenHabit: String = "",
    val modeRepeat: String = "",
    var thoiGian: String = ""
) {
    // Constructor không tham số cho Firebase
    constructor() : this(0, "", "Hằng ngày", "")
}
