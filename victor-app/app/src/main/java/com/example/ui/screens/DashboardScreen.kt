package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.FurnitureOrder
import com.example.ui.components.OrderStatusChip
import com.example.ui.components.PaymentStatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@Composable
fun DashboardScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val totalOrdersCount by viewModel.totalOrdersCount.collectAsStateWithLifecycle()
    val totalDueSum by viewModel.totalDueSum.collectAsStateWithLifecycle()
    val todayDeliveryCount by viewModel.todayDeliveryCount.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Permanent User Profile Circular Frame at the top center
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo Frame with Gold Gradient Border
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .shadow(elevation = 14.dp, shape = CircleShape, ambientColor = GoldPrimary, spotColor = GoldPrimary)
                        .border(
                            BorderStroke(
                                3.dp,
                                Brush.sweepGradient(
                                    listOf(GoldPrimary, GoldLight, SkyBlue, GoldPrimary)
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_victor_profile),
                        contentDescription = "প্রোফাইল ছবি",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Victor App",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "ফার্নিচার অর্ডার ম্যানেজমেন্ট সিস্টেম",
                    color = GoldLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. Summary KPI Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryKpiCard(
                    title = "মোট অর্ডার",
                    value = FormatUtils.toBengaliDigits(totalOrdersCount),
                    icon = Icons.Default.Inventory,
                    accentColor = SkyBlue,
                    modifier = Modifier.weight(1f)
                )

                SummaryKpiCard(
                    title = "মোট বাকি টাকা",
                    value = FormatUtils.formatTaka(totalDueSum),
                    icon = Icons.Default.Paid,
                    accentColor = AmberDue,
                    modifier = Modifier.weight(1f)
                )

                SummaryKpiCard(
                    title = "আজ ডেলিভারি",
                    value = FormatUtils.toBengaliDigits(todayDeliveryCount),
                    icon = Icons.Default.LocalShipping,
                    accentColor = EmeraldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Section Title for Main 6 Buttons
        item {
            Text(
                text = "প্রধান মেনু",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
            )
        }

        // 4. Main 6 Buttons Grid
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: কাস্টমার লিস্ট & নতুন অর্ডার
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardMenuButton(
                        title = "কাস্টমার লিস্ট",
                        icon = Icons.Default.Group,
                        accentColor = SkyBlue,
                        badge = FormatUtils.toBengaliDigits(allOrders.size),
                        onClick = { viewModel.navigateTo(AppScreen.CUSTOMER_LIST) },
                        modifier = Modifier.weight(1f).testTag("menu_customer_list_btn")
                    )

                    DashboardMenuButton(
                        title = "নতুন অর্ডার",
                        icon = Icons.Default.AddCircle,
                        accentColor = GoldPrimary,
                        onClick = { viewModel.navigateTo(AppScreen.NEW_ORDER) },
                        modifier = Modifier.weight(1f).testTag("menu_new_order_btn")
                    )
                }

                // Row 2: অর্ডার খুঁজুন & ডেলিভারি সম্পন্ন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardMenuButton(
                        title = "অর্ডার খুঁজুন",
                        icon = Icons.Default.Search,
                        accentColor = Color(0xFF38BDF8),
                        onClick = { viewModel.navigateTo(AppScreen.SEARCH_ORDER) },
                        modifier = Modifier.weight(1f).testTag("menu_search_order_btn")
                    )

                    DashboardMenuButton(
                        title = "ডেলিভারি সম্পন্ন",
                        icon = Icons.Default.CheckCircle,
                        accentColor = EmeraldGreen,
                        badge = FormatUtils.toBengaliDigits(allOrders.count { it.orderStatus == "ডেলিভারি সম্পন্ন" }),
                        onClick = { viewModel.navigateTo(AppScreen.DELIVERY) },
                        modifier = Modifier.weight(1f).testTag("menu_delivery_btn")
                    )
                }

                // Row 3: বাকি টাকা & নোটিফিকেশন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardMenuButton(
                        title = "বাকি টাকা",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = AmberDue,
                        badge = FormatUtils.toBengaliDigits(allOrders.count { it.dueAmount > 0 }),
                        onClick = { viewModel.navigateTo(AppScreen.DUE_PAYMENT) },
                        modifier = Modifier.weight(1f).testTag("menu_due_money_btn")
                    )

                    DashboardMenuButton(
                        title = "নোটিফিকেশন",
                        icon = Icons.Default.Notifications,
                        accentColor = CrimsonAlert,
                        badge = if (notifications.isNotEmpty()) FormatUtils.toBengaliDigits(notifications.size) else null,
                        badgeColor = CrimsonAlert,
                        onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                        modifier = Modifier.weight(1f).testTag("menu_notifications_btn")
                    )
                }
            }
        }

        // 5. Recent Orders Preview Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সাম্প্রতিক অর্ডার সমূহ",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "সব দেখুন",
                    color = GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.CUSTOMER_LIST) }
                        .padding(4.dp)
                )
            }
        }

        val recentOrders = allOrders.take(3)
        if (recentOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "এখনো কোনো অর্ডার সংরক্ষিত নেই",
                        color = TextGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
            }
        } else {
            items(recentOrders, key = { it.id }) { order ->
                RecentOrderCard(
                    order = order,
                    onClick = { viewModel.selectedOrderForDetail.value = order }
                )
            }
        }
    }
}

@Composable
private fun SummaryKpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DashboardMenuButton(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeColor: Color = DarkBorder
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(106.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceCard
        ),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RecentOrderCard(
    order: FurnitureOrder,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.customerName,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.orderNumber,
                        color = GoldLight,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${order.furnitureName} (${order.furnitureType})",
                    color = TextGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                OrderStatusChip(status = order.orderStatus)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (order.dueAmount > 0) "বাকি: ${FormatUtils.formatTaka(order.dueAmount)}" else "পরিশোধ",
                    color = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
