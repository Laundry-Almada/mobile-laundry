package com.almalaundry.shared.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.almalaundry.shared.commons.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()