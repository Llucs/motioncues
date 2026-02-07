package com.llucs.motioncues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    dataStore: SettingsDataStore,
    onNavigateHome: () -> Unit = {},
    onNavigateAbout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val dotColor by dataStore.dotColorFlow.collectAsState(initial = Constants.DEFAULT_DOT_COLOR.toInt())
    val dotCount by dataStore.dotCountFlow.collectAsState(initial = Constants.DEFAULT_DOT_COUNT)
    val dotSize by dataStore.dotSizeFlow.collectAsState(initial = Constants.DEFAULT_DOT_SIZE)
    val activationMode by dataStore.activationModeFlow.collectAsState(initial = Constants.DEFAULT_ACTIVATION_MODE)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                current = "settings",
                onHome = onNavigateHome,
                onSettings = {},
                onAbout = onNavigateAbout
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Configurações", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))

            Text("Modo de ativação", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.OFF.value) } }) { Text("Desligado") }
                Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.ON.value) } }) { Text("Ligado") }
                Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.AUTO.value) } }) { Text("Auto") }
            }

            Spacer(Modifier.height(10.dp))
            Text("Atual: ${activationMode.uppercase()}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(20.dp))
            Text("Quantidade de pontos: $dotCount", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = dotCount.toFloat(),
                onValueChange = { scope.launch { dataStore.saveDotCount(it.toInt()) } },
                valueRange = 1f..50f,
                steps = 48
            )

            Spacer(Modifier.height(20.dp))
            Text("Tamanho dos pontos", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { scope.launch { dataStore.saveDotSize(DotSize.SMALL.value) } }) { Text("Peq") }
                Button(onClick = { scope.launch { dataStore.saveDotSize(DotSize.MEDIUM.value) } }) { Text("Médio") }
                Button(onClick = { scope.launch { dataStore.saveDotSize(DotSize.LARGE.value) } }) { Text("Grande") }
            }
        }
    }
}