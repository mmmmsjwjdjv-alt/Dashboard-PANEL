package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoRed
import com.example.ui.theme.MaxoWhite
import com.example.ui.viewmodel.StatusLevel

@Composable
fun StatusPill(
    statusText: String,
    statusLevel: StatusLevel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "status_dot_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    val (dotColor, borderColor, textColor) = when (statusLevel) {
        StatusLevel.READY -> Triple(MaxoDim, MaxoLine, MaxoMuted)
        StatusLevel.LOADING -> Triple(MaxoWhite, MaxoLine, MaxoWhite)
        StatusLevel.OK -> Triple(MaxoGreen, MaxoLine, MaxoWhite)
        StatusLevel.ERROR -> Triple(MaxoRed, MaxoRed.copy(alpha = 0.5f), MaxoRed)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaxoBlack, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("status_pill")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(if (statusLevel == StatusLevel.LOADING || statusLevel == StatusLevel.OK) dotAlpha else 1f)
                    .background(dotColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = statusText,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.sp,
                color = textColor,
                lineHeight = 16.sp
            )
        }
    }
}
