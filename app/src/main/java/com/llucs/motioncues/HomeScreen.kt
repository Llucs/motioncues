package com.llucs.motioncues

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(dataStore: SettingsDataStore) {
    val effectActive = dataStore.effectActiveFlow.collectAsState(initial = false)
    val mode = dataStore.activationModeFlow.collectAsState(initial = "OFF")

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Status do Efeito", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Modo: ${mode.value} | Efeito: ${if (effectActive.value) "Ativo" else "Inativo"}")
            }
        }
    }
}