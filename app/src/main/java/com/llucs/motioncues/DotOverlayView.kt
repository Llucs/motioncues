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
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
    val density = LocalDensity.current

    val sizePx = with(density) { (dotSize * 4).toDp().toPx() }
    val dots = remember { mutableStateListOf<Dot>() }

    LaunchedEffect(dotCount) {
        dots.clear()
        repeat(dotCount.coerceIn(1, 200)) {
            dots.add(
                Dot(
                    position = Offset(
                        Random.nextFloat() * 1080f,
                        Random.nextFloat() * 2400f
                    ),
                    velocity = Offset(
                        Random.nextFloat() * 2f - 1f,
                        Random.nextFloat() * 2f - 1f
                    )
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val tilt = sensorDetector?.getTilt() ?: 0f

            dots.forEachIndexed { index, dot ->
                val speedFactor = 1.2f + (tilt * 0.8f)

                val newX = dot.position.x + dot.velocity.x * speedFactor
                val newY = dot.position.y + dot.velocity.y * speedFactor

                dot.position = Offset(newX, newY)

                if (dot.position.x <= 0f || dot.position.x >= 1080f) {
                    dot.velocity = Offset(-dot.velocity.x, dot.velocity.y)
                }
                if (dot.position.y <= 0f || dot.position.y >= 2400f) {
                    dot.velocity = Offset(dot.velocity.x, -dot.velocity.y)
                }

                dots[index] = dot
            }

            delay(16L)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        dots.forEach { dot ->
            drawCircle(
                color = Color(dotColor),
                radius = sizePx,
                center = dot.position
            )
        }
    }
}

data class Dot(
    var position: Offset,
    var velocity: Offset
)