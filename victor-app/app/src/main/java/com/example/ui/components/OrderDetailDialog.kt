package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FurnitureOrder
import com.example.ui.theme.*
import com.example.util.FormatUtils

@Composable
fun OrderDetailDialog(
    order: FurnitureOrder?,
    onDismiss: () -> Unit,
    onEdit: (FurnitureOrder) -> Unit,
    onDelete: (FurnitureOrder) -> Unit,
    onMarkDelivery: (Long) -> Unit,
    onPayDue: (FurnitureOrder) -> Unit
) {
    if (order == null) return
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceCard,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "অর্ডার বিস্তারিত",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = order.orderNumber,
                            color = GoldPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextGray
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OrderStatusChip(status = order.orderStatus)
                        PaymentStatusChip(status = order.paymentStatus)
                    }

                    // কাস্টমারের তথ্য কার্ড
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "👤 কাস্টমারের তথ্য",
                                color = GoldPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(label = "নাম", value = order.customerName)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DetailRow(label = "মোবাইল", value = FormatUtils.toBengaliDigits(order.customerPhone))
                                IconButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${order.customerPhone}")
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "কল করুন",
                                        tint = EmeraldLight
                                    )
                                }
                            }
                            if (order.customerAddress.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRow(label = "ঠিকানা", value = order.customerAddress)
                            }
                        }
                    }

                    // ফার্নিচারের তথ্য কার্ড
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🪑 ফার্নিচারের তথ্য",
                                color = GoldPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(label = "ফার্নিচারের নাম", value = order.furnitureName)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailRow(label = "কাঠ / ধরন", value = order.furnitureType)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailRow(label = "পরিমাণ", value = "${FormatUtils.toBengaliDigits(order.quantity)} টি")
                            if (order.furnitureDimensions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRow(label = "মাপ", value = FormatUtils.toBengaliDigits(order.furnitureDimensions))
                            }
                            if (order.designDescription.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRow(label = "ডিজাইন / বিবরণ", value = order.designDescription)
                            }
                        }
                    }

                    // টাকার তথ্য কার্ড
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "💰 টাকার হিসাব",
                                color = GoldPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(label = "মোট দাম", value = FormatUtils.formatTaka(order.totalPrice), valueColor = TextWhite)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailRow(label = "জমা টাকা", value = FormatUtils.formatTaka(order.advancePaid), valueColor = EmeraldLight)
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailRow(
                                label = "বাকি টাকা",
                                value = FormatUtils.formatTaka(order.dueAmount),
                                valueColor = if (order.dueAmount > 0) AmberDue else EmeraldLight,
                                isBold = true
                            )
                        }
                    }

                    // তারিখের তথ্য কার্ড
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📅 গুরুত্বপূর্ণ তারিখ",
                                color = GoldPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(label = "অর্ডারের তারিখ", value = FormatUtils.formatBengaliDate(order.orderDate))
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailRow(label = "ডেলিভারির তারিখ", value = FormatUtils.formatBengaliDate(order.deliveryDate))
                            if (order.duePaymentDate.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRow(label = "বাকি দেওয়ার তারিখ", value = FormatUtils.formatBengaliDate(order.duePaymentDate))
                            }
                        }
                    }

                    if (order.notes.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkInput),
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📝 অতিরিক্ত নোট",
                                    color = GoldPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = order.notes,
                                    color = TextWhite,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Action Buttons Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (order.orderStatus != "ডেলিভারি সম্পন্ন") {
                            Button(
                                onClick = {
                                    onMarkDelivery(order.id)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detail_mark_delivery_btn")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ডেলিভারি সম্পন্ন", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (order.dueAmount > 0) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onPayDue(order)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detail_pay_due_btn")
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("টাকা পরিশোধ", color = SkyBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onEdit(order)
                            },
                            border = BorderStroke(1.dp, GoldPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("detail_edit_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("তথ্য পরিবর্তন", color = GoldPrimary, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onDelete(order)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("detail_delete_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("মুছে ফেলুন", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = TextWhite,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
