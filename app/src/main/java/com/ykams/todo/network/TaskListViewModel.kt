package com.ykams.todo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ykams.todo.tasklist.Task
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel()  {
    private val repository = TasksRepository()
    private val _taskList = MutableLiveData<List<Task>>()
    public val taskList: LiveData<List<Task>> = _taskList

    private fun getMutableList() = _taskList.value.orEmpty().toMutableList()

    fun loadTasks() {
        viewModelScope.launch {
            val tasks = repository.loadTasks()
            _taskList.value = tasks.orEmpty()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if (repository.removeTask(task)) {
                _taskList.value = getMutableList().apply {
                    remove(task)
                }
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.createTask(task)?.let { task ->
                val newList = getMutableList()
                newList.add(task)
                _taskList.value = newList
            }
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)?.let { task ->
                _taskList.value = getMutableList().apply {
                    val position = indexOfFirst { task.id == it.id }
                    set(position, task)
                }
            }
        }
    }
}