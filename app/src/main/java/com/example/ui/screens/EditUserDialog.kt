package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserLicense
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoCardBgStart
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily

@Composable
fun EditUserDialog(
    user: UserLicense,
    newKey: String,
    onNewKeyChange: (String) -> Unit,
    newDays: String,
    onNewDaysChange: (String) -> Unit,
    newOffline: Boolean?,
    onNewOfflineChange: (Boolean?) -> Unit,
    isSaving: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaxoCardBgStart, RoundedCornerShape(22.dp))
                .border(1.dp, MaxoLineLight, RoundedCornerShape(22.dp))
                .padding(22.dp)
                .testTag("modal_edit_user")
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit user",
                        fontFamily = UnboundedFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaxoWhite
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaxoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Any field left empty stays unchanged.",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.5.sp,
                    color = MaxoMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // User name (key)
                Text(
                    text = "User name (key)",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = MaxoMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = user.key,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = MaxoMuted,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaxoDim,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = newKey,
                            onValueChange = onNewKeyChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            decorationBox = { innerTextField ->
                                if (newKey.isEmpty()) {
                                    Text(
                                        text = "No change",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // License days
                Text(
                    text = "Extend license (days from today)",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = MaxoMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (user.daysLeft != null) "${user.daysLeft} days" else "Expired",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = MaxoMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaxoDim,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = newDays,
                            onValueChange = onNewDaysChange,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            decorationBox = { innerTextField ->
                                if (newDays.isEmpty()) {
                                    Text(
                                        text = "No change",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Offline access
                Text(
                    text = "Offline access",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = MaxoMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentVal = newOffline ?: user.allowOffline

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (currentVal) MaxoWhite else MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLineLight, RoundedCornerShape(10.dp))
                            .clickable { onNewOfflineChange(true) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Yes (true)",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.5.sp,
                            color = if (currentVal) MaxoBlack else MaxoMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (!currentVal) MaxoWhite else MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLineLight, RoundedCornerShape(10.dp))
                            .clickable { onNewOfflineChange(false) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No (false)",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.5.sp,
                            color = if (!currentVal) MaxoBlack else MaxoMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onConfirm,
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaxoWhite,
                        contentColor = MaxoBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_save_edit_user")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSaving) "Saving..." else "Confirm changes",
                            fontFamily = UnboundedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
