package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoWhite
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val initialX: Float, // 0..1 ratio
    val initialY: Float, // 0..1 ratio
    val radius: Float,
    val speedX: Float,
    val speedY: Float,
    val alphaBase: Float,
    val pulsePhase: Float
)

@Composable
fun CyberParticleBackground(
    modifier: Modifier = Modifier
) {
    // Generate stable particles
    val particles = remember {
        val rand = Random(42)
        List(48) {
            Particle(
                initialX = rand.nextFloat(),
                initialY = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 0.8f,
                speedX = (rand.nextFloat() - 0.5f) * 0.04f,
                speedY = -(rand.nextFloat() * 0.08f + 0.02f), // drift upward
                alphaBase = rand.nextFloat() * 0.45f + 0.2f,
                pulsePhase = rand.nextFloat() * 6.28f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles_engine")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Subtle Digital Grid lines
        val gridSize = 48f
        val gridAlpha = 0.045f
        var x = 0f
        while (x < w) {
            drawLine(
                color = MaxoLine.copy(alpha = gridAlpha),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += gridSize
        }
        var y = 0f
        while (y < h) {
            drawLine(
                color = MaxoLine.copy(alpha = gridAlpha),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            y += gridSize
        }

        // Calculate current particle positions
        val coords = particles.map { p ->
            val currX = ((p.initialX + p.speedX * time) % 1f + 1f) % 1f * w
            val currY = ((p.initialY + p.speedY * time) % 1f + 1f) % 1f * h
            val pulse = (sin((time * 3f + p.pulsePhase).toDouble()).toFloat() + 1f) / 2f
            val alpha = (p.alphaBase * (0.6f + 0.4f * pulse)).coerceIn(0f, 1f)
            Triple(currX, currY, alpha)
        }

        // 2. Draw subtle constellation connections between close particles
        val maxDistSq = 85f * 85f
        for (i in coords.indices) {
            val (x1, y1, a1) = coords[i]
            for (j in i + 1 until coords.size) {
                val (x2, y2, a2) = coords[j]
                val dx = x1 - x2
                val dy = y1 - y2
                val distSq = dx * dx + dy * dy
                if (distSq < maxDistSq) {
                    val lineAlpha = (1f - (distSq / maxDistSq)) * 0.12f * (a1 + a2) / 2f
                    drawLine(
                        color = MaxoWhite.copy(alpha = lineAlpha),
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 0.8f
                    )
                }
            }
        }

        // 3. Draw glowing particle points
        coords.forEachIndexed { idx, (px, py, pa) ->
            val p = particles[idx]
            // Outer faint glow
            drawCircle(
                color = MaxoWhite.copy(alpha = pa * 0.35f),
                radius = p.radius * 2.2f,
                center = Offset(px, py)
            )
            // Core particle
            drawCircle(
                color = MaxoWhite.copy(alpha = pa),
                radius = p.radius,
                center = Offset(px, py)
            )
        }
    }
}
