package com.example.workandstudy_app.todolist.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.workandstudy_app.todolist.Entity.TasksData


@Dao
interface TasksDao {
    @Insert
    suspend fun insert(tasksData: TasksData): Long

    @Query("SELECT * FROM data_tasks")
    suspend fun getALL(): List<TasksData>

    @Query("SELECT * FROM data_tasks WHERE titleTask = :TitleTask LIMIT 1")
    suspend fun getByName(TitleTask: String): TasksData?

    @Query("SELECT * FROM data_tasks WHERE taskIdDate = :id")
    suspend fun getById(id: String): TasksData?

    @Delete
    suspend fun delete(task: TasksData)

    @Query("SELECT COUNT(*) FROM data_tasks WHERE taskIdDate LIKE :pattern")
    suspend fun getIdCount(pattern: String): Int

    @Query("SELECT * FROM data_tasks WHERE taskIdDate LIKE :pattern")
    suspend fun getListTasks(pattern: String): List<TasksData>

    @Update()
    suspend fun update(task: TasksData)

    @Query("SELECT COUNT(*) FROM data_tasks WHERE tick = :t ")
    suspend fun getCountTaskTick(t: Boolean): Int
}