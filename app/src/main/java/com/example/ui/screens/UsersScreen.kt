package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserLicense
import com.example.ui.components.GitHubConnectionCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoCardBgStart
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoRed
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun UsersScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState.value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Users",
                fontFamily = UnboundedFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                color = MaxoWhite
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Manage license users stored as JSON on GitHub.",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.5.sp,
                color = MaxoMuted
            )
        }

        // Connection Card
        item {
            GitHubConnectionCard(
                config = state.usersConfig,
                isOpen = state.usersIsConfigOpen,
                onToggleOpen = { viewModel.toggleUsersConfig(it) },
                onSave = { token, rawUrl -> viewModel.saveUsersConfig(token, rawUrl) },
                onClear = { viewModel.clearUsersConfig() },
                rawUrlPlaceholder = "https://raw.githubusercontent.com/USER/REPO/refs/heads/main/User.json"
            )
        }

        // Sync Database Card
        item {
            val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
            val spinAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "sync_angle"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaxoCardBgStart, RoundedCornerShape(18.dp))
                    .border(1.dp, MaxoLine, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(MaxoBlack, RoundedCornerShape(12.dp))
                                .border(1.dp, MaxoLineLight, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = MaxoWhite,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (state.isUsersSyncing) spinAngle else 0f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Sync database",
                                fontFamily = UnboundedFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = MaxoWhite
                            )
                            Text(
                                text = "Pull the latest users from GitHub.",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.5.sp,
                                color = MaxoMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { viewModel.fetchUsers(silent = false) },
                        enabled = !state.isUsersSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaxoWhite,
                            contentColor = MaxoBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("btn_sync_users_now")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (state.isUsersSyncing) "Syncing..." else "Sync now",
                                fontFamily = UnboundedFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total users",
                    count = state.totalUsersCount,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active",
                    count = state.activeUsersCount,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Inactive/exp",
                    count = state.expiredUsersCount,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Add New User Card
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
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = MaxoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add new user",
                            fontFamily = UnboundedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaxoWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Device ID field
                    Text(
                        text = "Device ID",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = state.newDeviceId,
                            onValueChange = { viewModel.onNewDeviceIdChange(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.5.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_device_id"),
                            decorationBox = { innerTextField ->
                                if (state.newDeviceId.isEmpty()) {
                                    Text(
                                        text = "device_101",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.5.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // User name (key) field
                    Text(
                        text = "User name (key)",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = state.newUserKey,
                            onValueChange = { viewModel.onNewUserKeyChange(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.5.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_user_key"),
                            decorationBox = { innerTextField ->
                                if (state.newUserKey.isEmpty()) {
                                    Text(
                                        text = "Mahan",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.5.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // License days
                    Text(
                        text = "License days",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = MaxoMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaxoBlack, RoundedCornerShape(10.dp))
                            .border(1.dp, MaxoLine, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = state.newDaysCount,
                            onValueChange = { viewModel.onNewDaysCountChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.5.sp,
                                color = MaxoWhite
                            ),
                            cursorBrush = SolidColor(MaxoWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_new_days_count"),
                            decorationBox = { innerTextField ->
                                if (state.newDaysCount.isEmpty()) {
                                    Text(
                                        text = "30",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.5.sp,
                                        color = MaxoDim
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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
                        val isOffline = state.newAllowOffline
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isOffline) MaxoWhite else MaxoBlack, RoundedCornerShape(10.dp))
                                .border(1.dp, MaxoLineLight, RoundedCornerShape(10.dp))
                                .clickable { viewModel.onNewAllowOfflineChange(true) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Yes (true)",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = if (isOffline) MaxoBlack else MaxoMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (!isOffline) MaxoWhite else MaxoBlack, RoundedCornerShape(10.dp))
                                .border(1.dp, MaxoLineLight, RoundedCornerShape(10.dp))
                                .clickable { viewModel.onNewAllowOfflineChange(false) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No (false)",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp,
                                color = if (!isOffline) MaxoBlack else MaxoMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.addNewUser() },
                        enabled = !state.isAddingUser,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaxoWhite,
                            contentColor = MaxoBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_save_new_user")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isAddingUser) "Saving..." else "Save user",
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

        // Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaxoCardBgStart, RoundedCornerShape(14.dp))
                    .border(1.dp, MaxoLine, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaxoMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.5.sp,
                            color = MaxoWhite
                        ),
                        cursorBrush = SolidColor(MaxoWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_users"),
                        decorationBox = { innerTextField ->
                            if (state.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search by name or device...",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 12.5.sp,
                                    color = MaxoDim
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
        }

        // Users List
        val filtered = state.filteredUsers
        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaxoCardBgStart, RoundedCornerShape(16.dp))
                        .border(1.dp, MaxoLine, RoundedCornerShape(16.dp))
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.totalUsersCount == 0) "No users yet. Add one or press Sync now." else "No users match your search.",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        color = MaxoMuted
                    )
                }
            }
        } else {
            itemsIndexed(filtered, key = { _, u -> u.key + u.deviceId }) { _, user ->
                UserCardItem(
                    user = user,
                    onToggle = { viewModel.toggleUserDisabled(user) },
                    onEdit = { viewModel.openEditUserDialog(user) },
                    onDelete = { viewModel.openDeleteDialog(user) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Modal
    if (state.editingUser != null) {
        EditUserDialog(
            user = state.editingUser,
            newKey = state.editNewKey,
            onNewKeyChange = { viewModel.onEditKeyChange(it) },
            newDays = state.editNewDays,
            onNewDaysChange = { viewModel.onEditDaysChange(it) },
            newOffline = state.editNewOffline,
            onNewOfflineChange = { viewModel.onEditOfflineChange(it) },
            isSaving = state.isSavingEdit,
            onConfirm = { viewModel.saveEditUser() },
            onDismiss = { viewModel.dismissEditDialog() }
        )
    }

    // Delete Modal
    if (state.deletingUser != null) {
        ConfirmDialog(
            user = state.deletingUser,
            isDeleting = state.isDeletingUser,
            onConfirm = { viewModel.confirmDeleteUser() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaxoCardBgStart, RoundedCornerShape(14.dp))
            .border(1.dp, MaxoLine, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                color = MaxoMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontFamily = UnboundedFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = MaxoWhite
            )
        }
    }
}

@Composable
private fun UserCardItem(
    user: UserLicense,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaxoCardBgStart, RoundedCornerShape(16.dp))
            .border(1.dp, if (user.isDisabled) MaxoLine else MaxoLineLight, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("user_card_${user.key}")
    ) {
        Column {
            // Header: Name + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.key,
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (user.isDisabled) MaxoMuted else MaxoWhite
                )

                // Status Badge
                val (badgeBg, badgeBorder, badgeText, dotColor) = when {
                    user.isDisabled -> Quadruple(MaxoBlack, MaxoLine, "Paused", MaxoDim)
                    user.isActive -> Quadruple(MaxoBlack, MaxoLineLight, user.statusText, MaxoGreen)
                    else -> Quadruple(MaxoBlack, MaxoLine, "Expired", MaxoRed)
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(100.dp))
                        .border(1.dp, badgeBorder, RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(dotColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badgeText,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.5.sp,
                            color = if (user.isDisabled) MaxoMuted else MaxoWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Device", fontFamily = JetBrainsMonoFontFamily, fontSize = 10.5.sp, color = MaxoMuted)
                    Text(text = user.deviceId.ifBlank { "-" }, fontFamily = JetBrainsMonoFontFamily, fontSize = 11.5.sp, color = MaxoWhite)
                }
                Column {
                    Text(text = "Expires", fontFamily = JetBrainsMonoFontFamily, fontSize = 10.5.sp, color = MaxoMuted)
                    Text(text = user.expiryDate.ifBlank { "-" }, fontFamily = JetBrainsMonoFontFamily, fontSize = 11.5.sp, color = MaxoWhite)
                }
                Column {
                    Text(text = "Offline", fontFamily = JetBrainsMonoFontFamily, fontSize = 10.5.sp, color = MaxoMuted)
                    Text(text = if (user.allowOffline) "Yes" else "No", fontFamily = JetBrainsMonoFontFamily, fontSize = 11.5.sp, color = MaxoWhite)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions divider and buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaxoLine)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle Pause/Resume
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (user.isDisabled) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (user.isDisabled) "Enable" else "Pause",
                        tint = MaxoMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Edit
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaxoMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaxoRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
