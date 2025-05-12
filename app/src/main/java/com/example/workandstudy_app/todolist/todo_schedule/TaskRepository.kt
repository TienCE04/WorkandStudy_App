package com.example.workandstudy_app.todolist.todo_schedule

import com.example.workandstudy_app.todolist.DAO.TasksDao
import com.example.workandstudy_app.todolist.Entity.TasksData

class TaskRepository(private val dao: TasksDao) {

    suspend fun delete(task: TasksData){
        dao.delete(task)
    }

    suspend fun update(task: TasksData){
        dao.update(task)
    }

    suspend fun insert(task: TasksData){
        dao.insert(task)
    }

    suspend fun getDsTask(pattern:String): List<TasksData>{
        return dao.getListTasks(pattern)
    }

    suspend fun countTasks(pattern: String): Int{
        return dao.getIdCount(pattern)
    }

}