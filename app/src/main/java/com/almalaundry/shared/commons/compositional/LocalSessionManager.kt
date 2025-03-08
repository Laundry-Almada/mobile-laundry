package com.almalaundry.shared.commons.compositional

import androidx.compose.runtime.compositionLocalOf
import com.almalaundry.shared.commons.session.SessionManager

val LocalSessionManager = compositionLocalOf<SessionManager> { error("No SessionManager provided") }