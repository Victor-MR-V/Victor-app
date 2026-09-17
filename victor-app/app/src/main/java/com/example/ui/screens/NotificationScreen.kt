package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.ui.components.VictorTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@Composable
fun NotificationScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("সব নোটিফিকেশন", "📦 ডেলিভারি", "💰 বাকি টাকা", "⚠️ সতর্কবার্তা")

    val filteredList = remember(notifications, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> notifications.filter { it.type == NotificationType.TODAY_DELIVERY || it.type == NotificationType.TOMORROW_DELIVERY }
            2 -> notifications.filter { it.type == NotificationType.TODAY_DUE }
            3 -> notifications.filter { it.type == NotificationType.OVERDUE_PAYMENT }
            else -> notifications
        }
    }

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "নোটিফিকেশন ও অ্যালার্ট",
                onBack = { viewModel.goBack() }
            )
        },
        containerColor = DarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Reminder Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "অফলাইন স্বয়ংক্রিয় রিমাইন্ডার",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "আজকের তারিখ: ${FormatUtils.formatBengaliDate(FormatUtils.getTodayDateString())}",
                            color = GoldLight,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Filter Chips
            ScrollableTabRow(
                selectedTabIndex = selectedFilterIndex,
                containerColor = DarkBg,
                contentColor = GoldPrimary,
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                filters.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilterIndex == index,
                        onClick = { selectedFilterIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedFilterIndex == index) GoldPrimary else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "মোট বিজ্ঞপ্তি: ${FormatUtils.toBengaliDigits(filteredList.size)} টি",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোনো নতুন নোটিফিকেশন নেই",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আজ বা আগামী দিনের কোনো জরুরি ডেলিভারি বা তাগাদা নেই",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        val targetOrder = allOrders.find { it.id == item.orderId }
                        NotificationItemCard(
                            notification = item,
                            onClick = {
                                if (targetOrder != null) {
                                    viewModel.selectedOrderForDetail.value = targetOrder
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val (cardBorderColor, iconColor, iconVector) = when (notification.type) {
        NotificationType.TODAY_DELIVERY -> Triple(EmeraldLight, EmeraldLight, Icons.Default.LocalShipping)
        NotificationType.TODAY_DUE -> Triple(AmberDue, AmberDue, Icons.Default.Paid)
        NotificationType.TOMORROW_DELIVERY -> Triple(SkyBlue, SkyBlue, Icons.Default.CalendarMonth)
        NotificationType.OVERDUE_PAYMENT -> Triple(CrimsonAlert, CrimsonAlert, Icons.Default.Warning)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, cardBorderColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = iconColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = notification.orderNumber,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📅 ${notification.dateText}",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "ট্যাপ করে বিস্তারিত দেখুন ➔",
                        color = GoldLight,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
