package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FurnitureOrder
import com.example.ui.components.OrderStatusChip
import com.example.ui.components.PaymentStatusChip
import com.example.ui.components.VictorTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@Composable
fun SearchOrderScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "অর্ডার খুঁজুন",
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
            // Search Input Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("কাস্টমার, মোবাইল, অর্ডার নং বা ফার্নিচারের নাম...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "মুছে ফেলুন", tint = TextGray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("search_order_input")
            )

            // Search hint chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সার্চ ফলাফল: ${FormatUtils.toBengaliDigits(searchResults.size)} টি পাওয়া গেছে",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "❌ কোনো অর্ডার পাওয়া যায়নি",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "অনুগ্রহ করে কাস্টমারের নাম, ফোন নম্বর বা অর্ডার নম্বর মিলিয়ে দেখুন",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(searchResults, key = { it.id }) { order ->
                        SearchResultOrderCard(
                            order = order,
                            onEdit = { viewModel.selectedOrderForEdit.value = order },
                            onDelete = { viewModel.orderToDelete.value = order },
                            onMarkDelivery = { viewModel.markDeliveryCompleted(order.id) },
                            onPayDue = { viewModel.orderForPayment.value = order }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultOrderCard(
    order: FurnitureOrder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkDelivery: () -> Unit,
    onPayDue: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
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
                        text = "🆔 ${order.orderNumber}",
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "👤 ${order.customerName}",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    OrderStatusChip(status = order.orderStatus)
                    Spacer(modifier = Modifier.height(4.dp))
                    PaymentStatusChip(status = order.paymentStatus)
                }
            }

            Divider(color = DarkBorder.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 10.dp))

            // কাস্টমার ও ফার্নিচারের বিস্তারিত
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "📞 মোবাইল: ${FormatUtils.toBengaliDigits(order.customerPhone)}",
                    color = SkyBlue,
                    fontSize = 13.sp
                )
                if (order.customerAddress.isNotBlank()) {
                    Text(
                        text = "📍 ঠিকানা: ${order.customerAddress}",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = "🪑 ফার্নিচার: ${order.furnitureName} (${order.furnitureType})",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (order.furnitureDimensions.isNotBlank()) {
                    Text(
                        text = "📏 মাপ: ${FormatUtils.toBengaliDigits(order.furnitureDimensions)} | পরিমাণ: ${FormatUtils.toBengaliDigits(order.quantity)} টি",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
                if (order.designDescription.isNotBlank()) {
                    Text(
                        text = "🎨 ডিজাইন: ${order.designDescription}",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // টাকার হিসাব
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("💰 মোট দাম: ${FormatUtils.formatTaka(order.totalPrice)}", color = TextGray, fontSize = 13.sp)
                    Text("💵 জমা: ${FormatUtils.formatTaka(order.advancePaid)}", color = EmeraldLight, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "⚠️ বাকি টাকা: ${FormatUtils.formatTaka(order.dueAmount)}",
                        color = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("📅 অর্ডারের তারিখ: ${FormatUtils.formatBengaliDate(order.orderDate)}", color = TextGray, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("📦 ডেলিভারি: ${FormatUtils.formatBengaliDate(order.deliveryDate)}", color = SkyBlue, fontSize = 12.sp)
                    if (order.duePaymentDate.isNotBlank()) {
                        Text("টাকা দেওয়ার তারিখ: ${FormatUtils.formatBengaliDate(order.duePaymentDate)}", color = AmberDue, fontSize = 12.sp)
                    }
                }

                if (order.notes.isNotBlank()) {
                    Text(
                        text = "📝 নোট: ${order.notes}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Required Action Buttons:
            // 1. "✏️ তথ্য পরিবর্তন করুন"
            // 2. "🗑️ অর্ডার মুছে ফেলুন"
            // 3. "📦 ডেলিভারি সম্পন্ন করুন"
            // 4. "💰 টাকা পরিশোধ হয়েছে"
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (order.orderStatus != "ডেলিভারি সম্পন্ন") {
                        Button(
                            onClick = onMarkDelivery,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.weight(1f).testTag("search_card_delivery_btn")
                        ) {
                            Text("📦 ডেলিভারি সম্পন্ন করুন", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }

                    if (order.dueAmount > 0) {
                        Button(
                            onClick = onPayDue,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                            modifier = Modifier.weight(1f).testTag("search_card_pay_due_btn")
                        ) {
                            Text("💰 টাকা পরিশোধ হয়েছে", color = SkyBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        border = BorderStroke(1.dp, GoldPrimary),
                        modifier = Modifier.weight(1f).testTag("search_card_edit_btn")
                    ) {
                        Text("✏️ তথ্য পরিবর্তন করুন", color = GoldPrimary, fontSize = 12.sp, maxLines = 1)
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert.copy(alpha = 0.2f)),
                        border = BorderStroke(1.dp, CrimsonAlert),
                        modifier = Modifier.weight(1f).testTag("search_card_delete_btn")
                    ) {
                        Text("🗑️ অর্ডার মুছে ফেলুন", color = CrimsonAlert, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}
