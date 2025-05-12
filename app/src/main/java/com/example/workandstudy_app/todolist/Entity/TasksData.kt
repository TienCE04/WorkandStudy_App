package com.example.workandstudy_app.todolist.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "data_tasks")
data class TasksData(
    @PrimaryKey val taskIdDate: String,
    var titleTask: String,
    var contentTask: String,
    var timeTask: String,
    var tick: Boolean = false,
    var flag:Boolean= false,
) {
}