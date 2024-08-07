package com.lukaslechner.coroutineusecasesonandroid.playground.testsource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FakeUserRepository : UserRepository() {
    private var users = listOf<String>()

    override suspend fun getAllUsers(): List<String> = users

    override suspend fun register(name: String) {
        delay(100L)
        users = users + name
        println("Registered $name")
    }
}

class UserState(
    private val userRepository: UserRepository,
    private val scope: CoroutineScope,
) {
    private val _users = MutableStateFlow(emptyList<String>())
    
    val users: StateFlow<List<String>> = _users.asStateFlow()

    fun registerUser(name: String) {
        scope.launch {
            userRepository.register(name)
            _users.update { userRepository.getAllUsers() }
        }
    }
}
