package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily

@Composable
fun MaxoTopBar(
    clockString: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaxoBlack)
            .statusBarsPadding()
    ) {
        // Top shimmer progress line
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "top_progress")
            val offsetRatio by infiniteTransition.animateFloat(
                initialValue = -0.5f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1100, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "top_progress_offset"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Transparent)
                    .drawBehind {
                        val barWidth = size.width * 0.45f
                        val startX = size.width * offsetRatio
                        drawLine(
                            color = MaxoWhite,
                            start = Offset(startX, size.height / 2),
                            end = Offset(startX + barWidth, size.height / 2),
                            strokeWidth = size.height
                        )
                    }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar with rim border & online status dot
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaxoWhite, CircleShape)
                        .background(MaxoBlack)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_maxo_avatar),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    // Mini online status dot
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(MaxoGreen, CircleShape)
                            .border(1.5.dp, MaxoBlack, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "MAXO",
                        fontFamily = UnboundedFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = MaxoWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Control Center",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        color = MaxoMuted
                    )
                }
            }

            // Live Clock with monospace numbers
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("top_bar_clock")
            ) {
                Text(
                    text = if (clockString.isNotBlank()) clockString else "--:--:--",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaxoWhite,
                    letterSpacing = 1.sp
                )
            }
        }

        // Bottom border line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaxoLine)
        )
    }
}
