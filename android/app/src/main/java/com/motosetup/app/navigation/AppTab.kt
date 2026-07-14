package com.motosetup.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Setup("Setup", Icons.Filled.Search),
    ConsigliAI("Consigli AI", Icons.AutoMirrored.Filled.Chat),
    Profilo("Profilo", Icons.Filled.Person),
}
