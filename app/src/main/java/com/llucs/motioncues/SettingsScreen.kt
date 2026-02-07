package com.llucs.motioncues

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    dataStore: SettingsDataStore
) {
    val scope = rememberCoroutineScope()

    var dotColor by remember { mutableStateOf(0xFFFFFFFF) }
    var dotCount by remember { mutableStateOf(10) }
    var dotSize by remember { mutableStateOf(2) }

    LaunchedEffect(Unit) {
        dotColor = dataStore.getDotColor()
        dotCount = dataStore.getDotCount()
        dotSize = dataStore.getDotSize()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Configurações", style = MaterialTheme.typography.headlineSmall)

        Text("Modo de ativação", style = MaterialTheme.typography.titleMedium)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.OFF.value) } }) {
                Text("Desligado")
            }
            Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.ON.value) } }) {
                Text("Ligado")
            }
            Button(onClick = { scope.launch { dataStore.saveActivationMode(ActivationMode.AUTO.value) } }) {
                Text("Auto")
            }
        }

        Divider()

        Text("Quantidade de pontos: $dotCount", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = dotCount.toFloat(),
            onValueChange = { dotCount = it.toInt() },
            valueRange = 1f..100f,
            steps = 98
        )
        Button(onClick = { scope.launch { dataStore.saveDotCount(dotCount) } }) {
            Text("Salvar quantidade")
        }

        Divider()

        Text("Tamanho dos pontos: $dotSize", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = dotSize.toFloat(),
            onValueChange = { dotSize = it.toInt() },
            valueRange = 1f..3f,
            steps = 1
        )
        Button(onClick = { scope.launch { dataStore.saveDotSize(dotSize) } }) {
            Text("Salvar tamanho")
        }
    }
}