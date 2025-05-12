package com.example.workandstudy_app.document.DAO

import com.example.workandstudy_app.document.Entity.Subjects
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(monHoc: Subjects): Long

    @Query("SELECT * FROM mon_hoc")
    suspend fun getAll(): List<Subjects> // Giữ suspend và kiểm tra entity

    @Query("SELECT * FROM mon_hoc WHERE tenMonHoc = :tenMonHoc LIMIT 1")
    suspend fun getByName(tenMonHoc: String): Subjects?

    @Query("SELECT * FROM mon_hoc WHERE id = :id")
    suspend fun getById(id: Int): Subjects?

    @Delete
    suspend fun delete(monHoc: Subjects)
}