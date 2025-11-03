package com.llucs.motioncues

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.llucs.motioncues.ui.theme.MotionCuesTheme
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@Composable
fun MainScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    dataStore: SettingsDataStore
) {
    val navController = rememberNavController()

    MotionCuesTheme {
        Scaffold(
            topBar = {
                // Top bar simples com o nome do app
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToAbout = { navController.navigate(Screen.About.route) },
                        dataStore = dataStore
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(dataStore = dataStore)
                }
                composable(Screen.About.route) {
                    AboutScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    dataStore: SettingsDataStore
) {
    val effectActive by dataStore.effectActiveFlow.collectAsState(initial = false)
    val activationMode by dataStore.activationModeFlow.collectAsState(initial = Constants.DEFAULT_ACTIVATION_MODE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status do Efeito
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.effect_status_active),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                val statusText = when (activationMode) {
                    ActivationMode.ON.value -> stringResource(R.string.mode_on)
                    ActivationMode.OFF.value -> stringResource(R.string.mode_off)
                    ActivationMode.AUTO.value -> stringResource(R.string.mode_auto)
                    else -> stringResource(R.string.mode_off)
                }
                Text(
                    text = "Modo: $statusText | Efeito: ${if (effectActive) "Ativo" else "Inativo"}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Botões de Navegação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onNavigateToSettings) {
                Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                Text(stringResource(R.string.settings))
            }
            Button(onClick = onNavigateToAbout) {
                Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about))
                Text(stringResource(R.string.about))
            }
        }
    }
}

@Composable
fun SettingsScreen(dataStore: SettingsDataStore) {
    val coroutineScope = rememberCoroutineScope()

    val activationMode by dataStore.activationModeFlow.collectAsState(initial = Constants.DEFAULT_ACTIVATION_MODE)
    val dotColor by dataStore.dotColorFlow.collectAsState(initial = Constants.DEFAULT_DOT_COLOR)
    val dotCount by dataStore.dotCountFlow.collectAsState(initial = Constants.DEFAULT_DOT_COUNT)
    val dotSize by dataStore.dotSizeFlow.collectAsState(initial = Constants.DEFAULT_DOT_SIZE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Modo de Ativação
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.mode_toggle), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveActivationMode(ActivationMode.OFF.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.mode_off))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveActivationMode(ActivationMode.ON.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text(stringResource(R.string.mode_on))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveActivationMode(ActivationMode.AUTO.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(stringResource(R.string.mode_auto))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Modo atual: $activationMode",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Cor das Bolinhas
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.dot_color), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                // Paleta de cores pré-definidas
                val colors = listOf(
                    0xFF0000FF to "Azul",      // Azul
                    0xFFFF0000 to "Vermelho",  // Vermelho
                    0xFF00FF00 to "Verde",     // Verde
                    0xFFFFFF00 to "Amarelo",   // Amarelo
                    0xFFFF00FF to "Magenta",   // Magenta
                    0xFF00FFFF to "Ciano",     // Ciano
                    0xFFFFFFFF to "Branco",    // Branco
                    0xFF808080 to "Cinza"      // Cinza
                )

                Column {
                    colors.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { (colorValue, colorName) ->
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            dataStore.saveDotColor(colorValue.toInt())
                                        }
                                    },
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(4.dp),
                                    shape = CircleShape
                                ) {
                                    // O botão mostra a cor
                                    Spacer(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(colorValue), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quantidade de Bolinhas
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.dot_count), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = dotCount.toFloat(),
                    onValueChange = { newValue ->
                        coroutineScope.launch {
                            dataStore.saveDotCount(newValue.toInt())
                        }
                    },
                    valueRange = 1f..50f,
                    steps = 48,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Quantidade: $dotCount bolinhas",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Tamanho das Bolinhas
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.dot_size), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveDotSize(DotSize.SMALL.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.size_small))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveDotSize(DotSize.MEDIUM.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text(stringResource(R.string.size_medium))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                dataStore.saveDotSize(DotSize.LARGE.value)
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text(stringResource(R.string.size_large))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val sizeText = when (dotSize) {
                    DotSize.SMALL.value -> stringResource(R.string.size_small)
                    DotSize.MEDIUM.value -> stringResource(R.string.size_medium)
                    DotSize.LARGE.value -> stringResource(R.string.size_large)
                    else -> stringResource(R.string.size_medium)
                }
                Text(
                    text = "Tamanho atual: $sizeText",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.about), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // Card com informações do app
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("MotionCues", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Vehicle Motion Visualizer",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "${stringResource(R.string.version)}: $versionName",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Um aplicativo que ajuda a reduzir o enjoo de movimento em veículos, fornecendo um efeito visual de bolinhas que se movem em sincronia com o movimento real.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Botão Código-fonte
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_REPO_URL))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(stringResource(R.string.source_code))
        }

        // Card com créditos
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.credits), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Desenvolvedor: ${stringResource(R.string.developer_name)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Licença: MIT",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Repositório: ${Constants.GITHUB_REPO_URL}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
