package com.example.workandstudy_app.todolist.todo_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModelTodo::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModelTodo(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}