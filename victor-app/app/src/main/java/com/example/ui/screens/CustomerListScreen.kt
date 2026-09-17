package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun CustomerListScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    var searchFilter by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredOrders = remember(allOrders, searchFilter) {
        if (searchFilter.isBlank()) {
            allOrders
        } else {
            val q = searchFilter.trim().lowercase()
            allOrders.filter {
                it.customerName.lowercase().contains(q) ||
                it.customerPhone.contains(q) ||
                it.orderNumber.lowercase().contains(q) ||
                it.furnitureName.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "কাস্টমার লিস্ট",
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
            // Search Input
            OutlinedTextField(
                value = searchFilter,
                onValueChange = { searchFilter = it },
                placeholder = { Text("🔍 কাস্টমার বা ফোন নম্বর দিয়ে খুঁজুন...", color = TextGray) },
                singleLine = true,
                trailingIcon = {
                    if (searchFilter.isNotBlank()) {
                        IconButton(onClick = { searchFilter = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "মুছে ফেলুন", tint = TextGray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("customer_list_search_input")
            )

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "মোট কাস্টমার: ${FormatUtils.toBengaliDigits(filteredOrders.size)} জন",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchFilter.isNotBlank()) "❌ কোনো কাস্টমার পাওয়া যায়নি" else "কোনো কাস্টমার সংরক্ষিত নেই",
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
                    items(filteredOrders, key = { it.id }) { order ->
                        CustomerCard(
                            order = order,
                            onViewDetails = { viewModel.selectedOrderForDetail.value = order },
                            onEdit = { viewModel.selectedOrderForEdit.value = order },
                            onDelete = { viewModel.orderToDelete.value = order },
                            onCall = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${order.customerPhone}")
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerCard(
    order: FurnitureOrder,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
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
            // Header Row: Customer Name, Phone call, Order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = order.customerName,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCall() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = SkyBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = FormatUtils.toBengaliDigits(order.customerPhone),
                            color = SkyBlue,
                            fontSize = 13.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "🆔 ${order.orderNumber}",
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OrderStatusChip(status = order.orderStatus)
                }
            }

            Divider(color = DarkBorder.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 10.dp))

            // Order Details Grid
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🪑 ${order.furnitureName}",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "📅 ${FormatUtils.formatBengaliDate(order.orderDate)}",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "💰 মোট: ${FormatUtils.formatTaka(order.totalPrice)}",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "💵 জমা: ${FormatUtils.formatTaka(order.advancePaid)}",
                        color = EmeraldLight,
                        fontSize = 13.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ বাকি: ${FormatUtils.formatTaka(order.dueAmount)}",
                        color = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "📦 ডেলিভারি: ${FormatUtils.formatBengaliDate(order.deliveryDate)}",
                        color = SkyBlue,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row: বিস্তারিত দেখুন, তথ্য পরিবর্তন করুন, মুছে ফেলুন
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    border = BorderStroke(1.dp, SkyBlue),
                    modifier = Modifier.weight(1f).testTag("customer_card_view_details_btn")
                ) {
                    Text(text = "👁️ বিস্তারিত", color = SkyBlue, fontSize = 12.sp, maxLines = 1)
                }

                OutlinedButton(
                    onClick = onEdit,
                    border = BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier.weight(1f).testTag("customer_card_edit_btn")
                ) {
                    Text(text = "✏️ পরিবর্তন", color = GoldPrimary, fontSize = 12.sp, maxLines = 1)
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, CrimsonAlert),
                    modifier = Modifier.weight(1f).testTag("customer_card_delete_btn")
                ) {
                    Text(text = "🗑️ মুছুন", color = CrimsonAlert, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}
