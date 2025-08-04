package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDatabase
import com.example.taskmanager.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        // Auto-load tasks whenever userId is set
        viewModelScope.launch {
            _userId.filterNotNull().flatMapLatest { userId ->
                repository.getTasksByUser(userId)
            }.collect { tasks ->
                _allTasks.value = tasks
            }
        }
    }

    fun setUserId(userId: String) {
        _userId.value = userId
        fetchUserName(userId)
    }

    private fun fetchUserName(userId: String) {
        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("name")
            .get()
            .addOnSuccessListener { snapshot ->
                _userName.value = snapshot.value as? String
            }
            .addOnFailureListener {
                _userName.value = null
            }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}
