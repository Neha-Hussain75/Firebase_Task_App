package com.example.taskmanager.repository

import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

  
    // New: Get tasks only for a specific user
    fun getTasksByUser(userId: String): Flow<List<Task>> {
        return taskDao.getTasksByUserId(userId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}
