package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionConfig
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoCardBgEnd
import com.example.ui.theme.MaxoCardBgStart
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoRed
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GitHubConnectionCard(
    config: ConnectionConfig,
    isOpen: Boolean,
    onToggleOpen: (Boolean) -> Unit,
    onSave: (token: String, rawUrl: String) -> Unit,
    onClear: () -> Unit,
    tokenPlaceholder: String = "github_pat_...",
    rawUrlPlaceholder: String = "https://raw.githubusercontent.com/USER/REPO/refs/heads/main/file.txt",
    modifier: Modifier = Modifier
) {
    var tokenInput by remember(config.token) { mutableStateOf(config.token) }
    var rawUrlInput by remember(config.rawUrl) { mutableStateOf(config.rawUrl) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val isSaved = config.isSaved
    val chevronRotation by animateFloatAsState(targetValue = if (isOpen) 180f else 0f, label = "chevron")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaxoCardBgStart, RoundedCornerShape(18.dp))
            .border(1.dp, if (isSaved) MaxoLineLight else MaxoLine, RoundedCornerShape(18.dp))
            .padding(18.dp)
            .testTag("github_connection_card")
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onToggleOpen(!isOpen) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isSaved) MaxoGreen else MaxoDim, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "GitHub connection",
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaxoWhite
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isSaved) "Connected" else "Not saved",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = if (isSaved) MaxoWhite else MaxoMuted
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    tint = MaxoMuted,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(chevronRotation)
                )
            }

            AnimatedVisibility(
                visible = isOpen,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    // Token Field
                    Text(
                        text = "Access token",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaxoBlack, RoundedCornerShape(12.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = tokenInput,
                                onValueChange = { tokenInput = it },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 13.sp,
                                    color = MaxoWhite
                                ),
                                cursorBrush = SolidColor(MaxoWhite),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_connection_token"),
                                decorationBox = { innerTextField ->
                                    if (tokenInput.isEmpty()) {
                                        Text(
                                            text = tokenPlaceholder,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 13.sp,
                                            color = MaxoDim
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Token Visibility",
                                    tint = MaxoMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Raw URL Field
                    Text(
                        text = "Raw GitHub URL",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaxoBlack, RoundedCornerShape(12.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = rawUrlInput,
                            onValueChange = { rawUrlInput = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 13.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_connection_raw_url"),
                            decorationBox = { innerTextField ->
                                if (rawUrlInput.isEmpty()) {
                                    Text(
                                        text = rawUrlPlaceholder,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Dynamic parsed chips
                    val tempConfig = remember(rawUrlInput) { ConnectionConfig.from(tokenInput, rawUrlInput) }
                    if (rawUrlInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        if (tempConfig.parsedInfo != null) {
                            val info = tempConfig.parsedInfo
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ConnectionChip(label = "owner", value = info.owner)
                                ConnectionChip(label = "repo", value = info.repo)
                                ConnectionChip(label = "branch", value = info.branch)
                                ConnectionChip(label = "file", value = info.path)
                            }
                        } else if (tempConfig.parseError != null) {
                            Box(
                                modifier = Modifier
                                    .background(MaxoBlack, RoundedCornerShape(8.dp))
                                    .border(1.dp, MaxoRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tempConfig.parseError ?: "Invalid URL",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = MaxoRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onSave(tokenInput, rawUrlInput) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaxoWhite,
                                contentColor = MaxoBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_save_connection")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Save connection",
                                    fontFamily = UnboundedFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                tokenInput = ""
                                rawUrlInput = ""
                                onClear()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaxoMuted
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(MaxoLineLight)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_clear_connection")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clear",
                                    fontFamily = UnboundedFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Stored only on this device. The token needs Contents read and write permission.",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoDim,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionChip(
    label: String,
    value: String
) {
    Box(
        modifier = Modifier
            .background(MaxoBlack, RoundedCornerShape(100.dp))
            .border(1.dp, MaxoLineLight, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = MaxoDim
            )
            Text(
                text = value,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                color = MaxoWhite
            )
        }
    }
}
