package com.example.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).userDao()

    val users: StateFlow<List<User>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addUser(login: String, password: String, age: Int) {
        if (age in 1..120){
            if (login.isBlank() || password.isBlank()) return
            viewModelScope.launch {
                dao.insert(User(login = login, password = password, age = age))
            }
        }
    }
}