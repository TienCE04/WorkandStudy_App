package com.example.workandstudy_app.document.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tai_lieu")
data class Documents(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monHocId: Int,
    val danhMuc: String,
    val phanLoai:String,
    val tenFile: String,
    val urlFile: String,
    val ngayTao: String,
    val isDefault: Int = 0
)