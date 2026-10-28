package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoRed
import com.example.ui.theme.MaxoWhite
import com.example.ui.viewmodel.ToastMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ToastHost(
    toastFlow: SharedFlow<ToastMessage>,
    modifier: Modifier = Modifier
) {
    var currentToast by remember { mutableStateOf<ToastMessage?>(null) }

    LaunchedEffect(toastFlow) {
        toastFlow.collect { toast ->
            currentToast = toast
            delay(3200)
            if (currentToast?.id == toast.id) {
                currentToast = null
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = currentToast != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
        ) {
            val toast = currentToast
            if (toast != null) {
                val icon = if (toast.isError) Icons.Default.Warning else Icons.Default.Check
                val tint = if (toast.isError) MaxoRed else MaxoGreen
                val borderCol = if (toast.isError) MaxoRed.copy(alpha = 0.5f) else MaxoLineLight

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaxoBlack.copy(alpha = 0.95f), RoundedCornerShape(14.dp))
                        .border(1.dp, borderCol, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("toast_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = toast.text,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = MaxoWhite,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
