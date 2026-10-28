package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GitHubConnectionCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoCardBgStart
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun ShizukuScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState.value

    val linesCount = if (state.shizukuContent.isEmpty()) 0 else state.shizukuContent.split("\n").size
    val bytesCount = state.shizukuContent.toByteArray(Charsets.UTF_8).size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Shizuku",
                fontFamily = UnboundedFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                color = MaxoWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "View and replace the Shizuku file stored in your GitHub repository.",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.5.sp,
                color = MaxoMuted
            )
        }

        // Connection Card
        item {
            GitHubConnectionCard(
                config = state.shizukuConfig,
                isOpen = state.shizukuIsConfigOpen,
                onToggleOpen = { viewModel.toggleShizukuConfig(it) },
                onSave = { token, rawUrl -> viewModel.saveShizukuConfig(token, rawUrl) },
                onClear = { viewModel.clearShizukuConfig() },
                rawUrlPlaceholder = "https://raw.githubusercontent.com/USER/REPO/refs/heads/main/MAXO.txt"
            )
        }

        // Content Editor Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaxoCardBgStart, RoundedCornerShape(18.dp))
                    .border(1.dp, MaxoLine, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaxoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Shizuku content",
                            fontFamily = UnboundedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaxoWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "File content",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.5.sp,
                        color = MaxoMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Editor Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp, max = 280.dp)
                            .background(MaxoBlack, RoundedCornerShape(12.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = state.shizukuContent,
                            onValueChange = { viewModel.onShizukuContentChange(it) },
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.5.sp,
                                color = MaxoWhite,
                                lineHeight = 18.sp
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("textarea_shizuku_content"),
                            decorationBox = { innerTextField ->
                                if (state.shizukuContent.isEmpty()) {
                                    Text(
                                        text = "Enter the new Shizuku file content here...",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    // Metadata row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$linesCount ${if (linesCount == 1) "line" else "lines"}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = MaxoDim
                        )
                        Text(
                            text = "$bytesCount bytes",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = MaxoDim
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.viewShizukuFile() },
                            enabled = !state.isShizukuBusy,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaxoWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(MaxoLineLight)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_view_shizuku")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "View current",
                                    fontFamily = UnboundedFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.updateShizukuFile() },
                            enabled = !state.isShizukuBusy,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaxoWhite,
                                contentColor = MaxoBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_update_shizuku")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Update Shizuku",
                                    fontFamily = UnboundedFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    StatusPill(
                        statusText = state.shizukuStatusText,
                        statusLevel = state.shizukuStatusLevel
                    )

                    // Collapsible Current GitHub content
                    AnimatedVisibility(
                        visible = state.shizukuCurrentContent != null,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaxoBlack, RoundedCornerShape(14.dp))
                                    .border(1.dp, MaxoLine, RoundedCornerShape(14.dp))
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaxoCardBgStart)
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Current GitHub content",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            color = MaxoMuted
                                        )
                                        Text(
                                            text = "${state.shizukuCurrentSize ?: 0} bytes",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 11.sp,
                                            color = MaxoDim
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 200.dp)
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = state.shizukuCurrentContent ?: "",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 12.sp,
                                            color = MaxoWhite,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
