package com.llucs.motioncues

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

    // Observar dados do acelerômetro se disponível
    val accelerometerData by remember { mutableStateOf(FloatArray(3)) }

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

    // Lógica de animação com resposta a sensores
    LaunchedEffect(isEffectActive) {
        while (isEffectActive) {
            delay(16) // Aproximadamente 60 FPS

            if (canvasWidth > 0 && canvasHeight > 0) {
                dots.forEach { dot ->
                    // Aplicar aceleração baseada em sensores (se disponível)
                    val accelScale = 0.1f
                    dot.accelerationX = (accelerometerData[0] / 10f) * accelScale
                    dot.accelerationY = (accelerometerData[1] / 10f) * accelScale

                    // Atualizar velocidade com base na aceleração
                    dot.velocityX += dot.accelerationX
                    dot.velocityY += dot.accelerationY

                    // Aplicar amortecimento (fricção)
                    val damping = 0.95f
                    dot.velocityX *= damping
                    dot.velocityY *= damping

                    // Limitar a velocidade máxima
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