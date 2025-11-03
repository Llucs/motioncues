package com.llucs.motioncues

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

data class Dot(
    val id: Int,
    var x: Float,
    var y: Float,
    val radius: Float,
    val color: Color,
    var velocityX: Float,
    var velocityY: Float,
    var accelerationX: Float = 0f,
    var accelerationY: Float = 0f
)

@Composable
fun DotOverlayView(
    dotColor: Long,
    dotCount: Int,
    dotSize: Int,
    isEffectActive: Boolean,
    sensorDetector: SensorDetector? = null
) {
    if (!isEffectActive) return

    val context = LocalContext.current
    val density = LocalDensity.current.density
    val dotRadiusDp = when (dotSize) {
        DotSize.SMALL.value -> 4.dp
        DotSize.MEDIUM.value -> 8.dp
        DotSize.LARGE.value -> 12.dp
        else -> 8.dp
    }
    val dotRadiusPx = with(LocalDensity.current) { dotRadiusDp.toPx() }
    val color = Color(dotColor.toULong())

    val dots = remember { mutableStateListOf<Dot>() }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    // Dados do sensor
    val accelerometerData by remember { mutableStateOf(FloatArray(3)) }
    var gyroAvailable by remember { mutableStateOf(true) }

    // Verificar sensores disponíveis
    LaunchedEffect(sensorDetector) {
        try {
            sensorDetector?.let {
                if (!it.isGyroAvailable() && !it.isAccelerometerAvailable()) {
                    gyroAvailable = false
                    Toast.makeText(context, "Nenhum sensor de movimento disponível", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao detectar sensores: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Inicializa as bolinhas
    LaunchedEffect(dotCount, canvasWidth, canvasHeight) {
        if (canvasWidth > 0 && canvasHeight > 0) {
            dots.clear()
            repeat(dotCount) {
                dots.add(
                    Dot(
                        id = it,
                        x = Random.nextFloat() * canvasWidth,
                        y = Random.nextFloat() * canvasHeight,
                        radius = dotRadiusPx,
                        color = color,
                        velocityX = Random.nextFloat() * 2f - 1f,
                        velocityY = Random.nextFloat() * 2f - 1f
                    )
                )
            }
        }
    }

    // Lógica de animação
    LaunchedEffect(isEffectActive) {
        while (isEffectActive) {
            delay(16) // ~60 FPS
            if (canvasWidth > 0 && canvasHeight > 0) {
                dots.forEach { dot ->
                    try {
                        // Aplicar aceleração baseada em sensores
                        val accelScale = 0.1f
                        val sensorData = sensorDetector?.getSensorData() ?: floatArrayOf(0f, 0f, 0f)
                        dot.accelerationX = (sensorData[0] / 10f) * accelScale
                        dot.accelerationY = (sensorData[1] / 10f) * accelScale

                        // Atualizar velocidade com aceleração
                        dot.velocityX += dot.accelerationX
                        dot.velocityY += dot.accelerationY

                        // Amortecimento
                        val damping = 0.95f
                        dot.velocityX *= damping
                        dot.velocityY *= damping

                        // Limitar velocidade máxima
                        val maxSpeed = 8f
                        val currentSpeed = sqrt(dot.velocityX * dot.velocityX + dot.velocityY * dot.velocityY)
                        if (currentSpeed > maxSpeed) {
                            val scale = maxSpeed / currentSpeed
                            dot.velocityX *= scale
                            dot.velocityY *= scale
                        }

                        // Atualizar posição
                        dot.x += dot.velocityX
                        dot.y += dot.velocityY

                        // Colisão com as bordas
                        if (dot.x - dot.radius < 0) {
                            dot.x = dot.radius
                            dot.velocityX = -dot.velocityX * 0.8f
                        } else if (dot.x + dot.radius > canvasWidth) {
                            dot.x = canvasWidth - dot.radius
                            dot.velocityX = -dot.velocityX * 0.8f
                        }
                        if (dot.y - dot.radius < 0) {
                            dot.y = dot.radius
                            dot.velocityY = -dot.velocityY * 0.8f
                        } else if (dot.y + dot.radius > canvasHeight) {
                            dot.y = canvasHeight - dot.radius
                            dot.velocityY = -dot.velocityY * 0.8f
                        }

                        // Pequena perturbação aleatória
                        dot.velocityX += Random.nextFloat() * 0.05f - 0.025f
                        dot.velocityY += Random.nextFloat() * 0.05f - 0.025f
                    } catch (e: Exception) {
                        // Se algum erro acontecer, não fecha o app
                        Toast.makeText(context, "Erro nas bolinhas: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        canvasWidth = size.width
        canvasHeight = size.height

        dots.forEach { dot ->
            drawCircle(
                color = dot.color,
                radius = dot.radius,
                center = Offset(dot.x, dot.y)
            )
        }
    }
}