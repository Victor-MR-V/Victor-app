package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FurnitureOrder
import com.example.ui.components.OrderStatusChip
import com.example.ui.components.VictorTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@Composable
fun DeliveryManagementScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("📦 ডেলিভারি বাকি", "📅 আজকের ডেলিভারি", "✅ ডেলিভারি সম্পন্ন")
    val today = remember { FormatUtils.getTodayDateString() }
    val context = LocalContext.current

    val displayedOrders = remember(allOrders, selectedCategoryIndex) {
        when (selectedCategoryIndex) {
            0 -> allOrders.filter { it.orderStatus != "ডেলিভারি সম্পন্ন" } // ডেলিভারি বাকি
            1 -> allOrders.filter { it.deliveryDate == today } // আজকের ডেলিভারি
            2 -> allOrders.filter { it.orderStatus == "ডেলিভারি সম্পন্ন" } // ডেলিভারি সম্পন্ন
            else -> allOrders
        }
    }

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "ডেলিভারি ম্যানেজমেন্ট",
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
            // Category Tabs
            TabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = DarkSurfaceCard,
                contentColor = GoldPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedCategoryIndex == index) GoldPrimary else TextGray,
                                fontSize = 13.sp,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("delivery_tab_$index")
                    )
                }
            }

            // Category Count Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "মোট অর্ডার: ${FormatUtils.toBengaliDigits(displayedOrders.size)} টি",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            if (displayedOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "এই তালিকায় কোনো অর্ডার নেই",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(displayedOrders, key = { it.id }) { order ->
                        DeliveryCard(
                            order = order,
                            onCompleteDelivery = { viewModel.markDeliveryCompleted(order.id) },
                            onCall = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${order.customerPhone}")
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            onCardClick = { viewModel.selectedOrderForDetail.value = order }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    order: FurnitureOrder,
    onCompleteDelivery: () -> Unit,
    onCall: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Customer Name, Order ID, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "👤 ${order.customerName}",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🆔 ${order.orderNumber}",
                        color = GoldLight,
                        fontSize = 13.sp
                    )
                }
                OrderStatusChip(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details: Phone, Furniture, Delivery Date, Due
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCall() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "মোবাইল: ${FormatUtils.toBengaliDigits(order.customerPhone)}",
                    color = SkyBlue,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🪑 ফার্নিচার: ${order.furnitureName} (${order.furnitureType})",
                color = TextWhite,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📦 ডেলিভারির তারিখ: ${FormatUtils.formatBengaliDate(order.deliveryDate)}",
                    color = SkyBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "⚠️ বাকি: ${FormatUtils.formatTaka(order.dueAmount)}",
                    color = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Action Button: "✅ ডেলিভারি সম্পন্ন করুন"
            if (order.orderStatus != "ডেলিভারি সম্পন্ন") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCompleteDelivery,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("mark_delivery_completed_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✅ ডেলিভারি সম্পন্ন করুন",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
