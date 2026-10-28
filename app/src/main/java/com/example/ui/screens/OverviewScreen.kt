package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MaxoBlack
import com.example.ui.theme.MaxoCardBgStart
import com.example.ui.theme.MaxoDim
import com.example.ui.theme.MaxoGreen
import com.example.ui.theme.MaxoLine
import com.example.ui.theme.MaxoLineLight
import com.example.ui.theme.MaxoMuted
import com.example.ui.theme.MaxoWhite
import com.example.ui.theme.UnboundedFontFamily
import com.example.ui.viewmodel.DashboardTab
import com.example.ui.viewmodel.DashboardUiState

@Composable
fun OverviewScreen(
    state: DashboardUiState,
    onNavigate: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Live Header Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(MaxoGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MAXO control panel",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = MaxoWhite
                )
            }

            // Hero Word "DASHBOARD"
            Text(
                text = "DASHBOARD",
                fontFamily = UnboundedFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                color = MaxoWhite,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("overview_hero_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Edit your GitHub-hosted files, links and user licenses from one place.",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.5.sp,
                color = MaxoMuted,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Profile / System Card ("پروفایل برنامه")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaxoCardBgStart, RoundedCornerShape(20.dp))
                    .border(1.dp, MaxoLineLight, RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("profile_banner_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar with double glowing ring
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaxoWhite, CircleShape)
                            .background(MaxoBlack)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_maxo_avatar),
                            contentDescription = "MAXO Core Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(MaxoGreen, CircleShape)
                                .border(2.dp, MaxoBlack, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MAXO Core",
                                fontFamily = UnboundedFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaxoWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(MaxoBlack, RoundedCornerShape(100.dp))
                                    .border(1.dp, MaxoLineLight, RoundedCornerShape(100.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ROOT",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.sp,
                                    color = MaxoWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "GitHub Control System",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.5.sp,
                            color = MaxoMuted
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaxoGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "ENCRYPTED • CLIENT-SIDE DIRECT",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                color = MaxoGreen
                            )
                        }
                    }
                }
            }
        }

        // Shizuku Module Card
        item {
            OverviewModuleCard(
                title = "Shizuku",
                description = "Edit the Shizuku file content on GitHub.",
                icon = Icons.Default.Description,
                isConnected = state.shizukuConfig.isSaved,
                metaText = null,
                onClick = { onNavigate(DashboardTab.SHIZUKU) },
                testTag = "overview_card_shizuku"
            )
        }

        // Link Module Card
        item {
            OverviewModuleCard(
                title = "Link",
                description = "Change the online link file.",
                icon = Icons.Default.Link,
                isConnected = state.linkConfig.isSaved,
                metaText = null,
                onClick = { onNavigate(DashboardTab.LINK) },
                testTag = "overview_card_link"
            )
        }

        // Users Module Card
        item {
            val meta = if (state.usersLoaded) "${state.totalUsersCount} users" else "Not synced"
            OverviewModuleCard(
                title = "Users",
                description = "Add, extend, pause and remove licenses.",
                icon = Icons.Default.Group,
                isConnected = state.usersConfig.isSaved,
                metaText = meta,
                onClick = { onNavigate(DashboardTab.USERS) },
                testTag = "overview_card_users"
            )
        }

        // Session Activity Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaxoCardBgStart, RoundedCornerShape(18.dp))
                    .border(1.dp, MaxoLine, RoundedCornerShape(18.dp))
                    .padding(18.dp)
                    .testTag("overview_activity_card")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = MaxoMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Session activity",
                            fontFamily = UnboundedFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaxoWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (state.sessionLogs.isEmpty()) {
                        Text(
                            text = "Nothing yet. Actions you run in this session will show up here.",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = MaxoMuted,
                            lineHeight = 17.sp
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            state.sessionLogs.forEach { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.formattedTime,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 11.sp,
                                        color = MaxoDim,
                                        modifier = Modifier.width(65.dp)
                                    )
                                    Text(
                                        text = log.text,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 12.sp,
                                        color = MaxoWhite,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
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

@Composable
private fun OverviewModuleCard(
    title: String,
    description: String,
    icon: ImageVector,
    isConnected: Boolean,
    metaText: String?,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaxoCardBgStart, RoundedCornerShape(18.dp))
            .border(1.dp, MaxoLine, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(18.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaxoBlack, RoundedCornerShape(12.dp))
                        .border(1.dp, MaxoLineLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaxoWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        fontFamily = UnboundedFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaxoWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        color = MaxoMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(if (isConnected) MaxoGreen else MaxoDim, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "Connected" else "Not connected",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (isConnected) MaxoWhite else MaxoMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (metaText != null) {
                        Text(
                            text = metaText,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = MaxoDim,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to $title",
                        tint = MaxoMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
