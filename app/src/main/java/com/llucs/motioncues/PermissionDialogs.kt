package com.llucs.motioncues

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun OverlayPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permissão necessária") },
        text = { Text("Este app precisa permissão para mostrar os pontos na tela.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Permitir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SensorPermissionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aviso") },
        text = { Text("Os sensores do dispositivo estão bloqueados. Ative para usar o app.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ok")
            }
        }
    )
}