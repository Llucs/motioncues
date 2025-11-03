package com.llucs.motioncues

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun AboutScreen(
    onNavigateHome: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val ctx = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                current = "about",
                onHome = onNavigateHome,
                onSettings = onNavigateSettings,
                onAbout = {}
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sobre", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(20.dp))

            Text(
                "MotionCues\nReduz enjoo de movimento com pontos sincronizados ao movimento real.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    ctx.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_REPO_URL))
                    )
                }
            ) { Text("Código no GitHub") }
        }
    }
}