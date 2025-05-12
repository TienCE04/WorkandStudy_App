package com.example.workandstudy_app.todolist.todo_schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workandstudy_app.todolist.Entity.TasksData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModelTodo (private val repository:TaskRepository): ViewModel() {
    private val _selectedTasks = MutableLiveData<List<TasksData>>(emptyList())
    val selectedTasks: LiveData<List<TasksData>> = _selectedTasks

    var countTasks=0

    fun loadTasks(pattern:String)=viewModelScope.launch {
        val tasks=repository.getDsTask(pattern)
        countTasks=tasks.size
        _selectedTasks.postValue(tasks.sortedBy { it.timeTask })
    }

//    fun addTaskFromActivity(task: TasksData) =viewModelScope.launch {
//        withContext (Dispatchers.IO){
//            repository.insert(task)
//        }
//        val currentList=_selectedTasks.value.orEmpty()
//        _selectedTasks.postValue((currentList+task).sortedBy { it.timeTask })
//    }

    fun updateTaskFromDialog(taskUpdated: TasksData)=viewModelScope.launch {
        withContext (Dispatchers.IO){
            repository.update(taskUpdated)
        }
        val currentList=_selectedTasks.value.orEmpty()
        val newList=currentList.map { if(it.taskIdDate==taskUpdated.taskIdDate) taskUpdated else it }
        _selectedTasks.postValue(newList.sortedBy { it.timeTask })
    }
    fun deleteTask(task: TasksData)=viewModelScope.launch {
        withContext (Dispatchers.IO){
            repository.delete(task)
        }
        val currentList=_selectedTasks.value.orEmpty()
        _selectedTasks.postValue(currentList.filter { it.taskIdDate!=task.taskIdDate })
    }

}