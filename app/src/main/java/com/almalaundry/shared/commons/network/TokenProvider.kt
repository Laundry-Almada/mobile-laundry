package com.almalaundry.shared.commons.network

import com.almalaundry.shared.commons.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProvider @Inject constructor(
    sessionManager: SessionManager
) {
    private val _tokenFlow = MutableStateFlow<String?>(null)
    val tokenFlow = _tokenFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            sessionManager.sessionFlow.collect { session ->
                _tokenFlow.value = session?.token
            }
        }
    }

    fun getToken(): String? = _tokenFlow.value
}