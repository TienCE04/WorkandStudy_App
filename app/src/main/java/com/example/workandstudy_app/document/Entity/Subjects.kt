package com.example.workandstudy_app.document.Entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mon_hoc")
data class Subjects(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenMonHoc: String,
    val ngayThem: String,
    val isDefault: Int = 0 // 1 = mặc định, 0 = người dùng thêm
)