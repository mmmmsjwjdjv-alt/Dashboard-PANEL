package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily
import kotlinx.coroutines.delay

@Composable
fun BootSplash(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)),
        modifier = modifier
    ) {
        val letters = listOf("M", "A", "X", "O")
        val letterAlphas = remember { letters.map { Animatable(0f) } }
        val barProgress = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            letterAlphas.forEachIndexed { index, anim ->
                delay(index * 90L)
                anim.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            }
            barProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaxoBlack)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .testTag("boot_splash"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Word MAXO
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    letters.forEachIndexed { idx, char ->
                        Text(
                            text = char,
                            fontFamily = UnboundedFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 64.sp,
                            color = MaxoWhite,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .alpha(letterAlphas[idx].value)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(2.dp)
                        .background(MaxoLineLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barProgress.value)
                            .height(2.dp)
                            .background(MaxoWhite)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.boot_subtitle),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = MaxoMuted
                )
            }
        }
    }
}
