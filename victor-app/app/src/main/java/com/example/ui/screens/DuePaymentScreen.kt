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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Paid
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
import com.example.ui.components.PaymentStatusChip
import com.example.ui.components.VictorTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@Composable
fun DuePaymentScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val totalDueSum by viewModel.totalDueSum.collectAsStateWithLifecycle()
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("সকল বাকি", "📅 আজ দেওয়ার কথা", "⚠️ সময় পার", "✅ পরিশোধিত")
    val today = remember { FormatUtils.getTodayDateString() }
    val context = LocalContext.current

    val displayedOrders = remember(allOrders, selectedCategoryIndex) {
        when (selectedCategoryIndex) {
            0 -> allOrders.filter { it.dueAmount > 0 } // সকল বাকি
            1 -> allOrders.filter { it.dueAmount > 0 && it.duePaymentDate == today } // আজ টাকা দেওয়ার কথা
            2 -> allOrders.filter { it.dueAmount > 0 && it.duePaymentDate.isNotBlank() && it.duePaymentDate < today } // সময় পার হয়ে গেছে
            3 -> allOrders.filter { it.dueAmount <= 0 } // টাকা পরিশোধ করা হয়েছে
            else -> allOrders
        }
    }

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "বাকি টাকার হিসাব",
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
            // Header Total Due Card: "💰 মোট বাকি টাকা"
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.5.dp, AmberDue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💰 মোট বাকি টাকা",
                            color = GoldLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatTaka(totalDueSum),
                            color = AmberDue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(AmberDue.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = AmberDue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 4 Category Tabs
            TabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = DarkSurfaceCard,
                contentColor = GoldPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedCategoryIndex == index) GoldPrimary else TextGray,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("due_tab_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "কাস্টমার সংখ্যা: ${FormatUtils.toBengaliDigits(displayedOrders.size)} জন",
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
                            imageVector = Icons.Default.Paid,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedCategoryIndex == 3) "কোনো পরিশোধিত অর্ডার নেই" else "কোনো বাকি টাকা নেই",
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
                        DueCustomerCard(
                            order = order,
                            onPayMoney = { viewModel.orderForPayment.value = order },
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
private fun DueCustomerCard(
    order: FurnitureOrder,
    onPayMoney: () -> Unit,
    onCall: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, if (order.dueAmount > 0) DarkBorder else EmeraldGreen.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
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
                PaymentStatusChip(status = order.paymentStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phone
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

            Spacer(modifier = Modifier.height(8.dp))

            // Price Details: মোট দাম, জমা টাকা, বাকি টাকা
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "💰 মোট দাম: ${FormatUtils.formatTaka(order.totalPrice)}", color = TextGray, fontSize = 13.sp)
                Text(text = "💵 জমা: ${FormatUtils.formatTaka(order.advancePaid)}", color = EmeraldLight, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️ বাকি টাকা: ${FormatUtils.formatTaka(order.dueAmount)}",
                    color = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                if (order.duePaymentDate.isNotBlank()) {
                    Text(
                        text = "তারিখ: ${FormatUtils.formatBengaliDate(order.duePaymentDate)}",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Button: "💵 টাকা পরিশোধ হয়েছে"
            if (order.dueAmount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onPayMoney,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("record_due_payment_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Paid,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💵 টাকা পরিশোধ হয়েছে",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
