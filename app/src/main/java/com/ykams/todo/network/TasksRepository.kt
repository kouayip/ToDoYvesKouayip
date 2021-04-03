package com.ykams.todo.network

import com.ykams.todo.tasklist.Task

class TasksRepository {
    // Le web service requête le serveur
    private val webService = Api.tasksWebService

    suspend fun loadTasks(): List<Task>? {
        val response = webService.getTasks()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun removeTask(task: Task): Boolean {
        return webService.deleteTask(task.id).isSuccessful
    }

    suspend fun createTask(task: Task) : Task?{
        val createTaskResponse = webService.createTask(task)
        return if (createTaskResponse.isSuccessful) createTaskResponse.body() else null
    }

    suspend fun updateTask(task: Task) : Task?{
        val createTaskResponse = webService.updateTask(task)
        return if (createTaskResponse.isSuccessful) createTaskResponse.body() else null
    }
}