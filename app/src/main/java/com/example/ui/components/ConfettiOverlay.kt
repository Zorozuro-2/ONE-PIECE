package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberGreen
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class Particle(
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val alphaSpeed: Float = Random.nextFloat() * 0.02f + 0.005f
) {
    var alpha = 1f
    fun update() {
        y += speed
        alpha -= alphaSpeed
    }
}

@Composable
fun ConfettiOverlay(
    trigger: SharedFlow<Unit>,
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<Particle>() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = trigger) {
        scope.launch {
            trigger.collectLatest {
                // Generate a burst of 60 green cyber particles
                for (i in 0..60) {
                    val p = Particle(
                        x = Random.nextFloat() * 1000f, // initialized or adapted on size
                        y = -10f,
                        speed = Random.nextFloat() * 12f + 4f,
                        size = Random.nextFloat() * 16f + 4f,
                        color = Color(
                            red = 0f,
                            green = Random.nextFloat() * 0.4f + 0.6f, // beautiful shades of cyber green
                            blue = Random.nextFloat() * 0.3f,
                            alpha = 1f
                        )
                    )
                    particles.add(p)
                }
            }
        }
    }

    // High performance frame updater
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val frame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 1000,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame"
    )

    // Side effect to update positions and prune dead particles
    LaunchedEffect(frame) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.update()
            if (p.alpha <= 0f || p.y > 2500f) {
                iterator.remove()
            }
        }
    }

    if (particles.isNotEmpty()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val width = size.width
            particles.forEach { p ->
                // Ensure x fits on screen dynamically
                val scaledX = if (p.x > width) (p.x % width) else p.x
                drawCircle(
                    color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = p.size,
                    center = Offset(scaledX, p.y)
                )
            }
        }
    }
}

// Simple design scale approximation
private fun dpToPx(): Float = 3f
