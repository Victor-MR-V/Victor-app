package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FurnitureOrder
import com.example.ui.theme.*
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VictorTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("top_bar_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "পেছনে যান",
                        tint = GoldPrimary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg,
            navigationIconContentColor = GoldPrimary,
            titleContentColor = TextWhite
        ),
        modifier = modifier
    )
}

@Composable
fun OrderStatusChip(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        "ডেলিভারি সম্পন্ন" -> Color(0xFF134E39) to EmeraldLight
        "প্রস্তুত" -> Color(0xFF1E3A5F) to SkyBlue
        "কাজ চলছে" -> Color(0xFF4A3712) to GoldLight
        "বাতিল" -> Color(0xFF4C1D24) to Color(0xFFFCA5A5)
        else -> Color(0xFF26333F) to Color(0xFFCBD5E1) // নতুন অর্ডার
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PaymentStatusChip(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        "সম্পূর্ণ টাকা দেওয়া হয়েছে" -> Color(0xFF134E39) to EmeraldLight
        "আংশিক টাকা দেওয়া হয়েছে" -> Color(0xFF4A3712) to AmberDue
        else -> Color(0xFF4C1D24) to CrimsonAlert // টাকা দেওয়া হয়নি
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    order: FurnitureOrder?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (order == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceCard,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = CrimsonAlert,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "আপনি কি এই তথ্যটি মুছে ফেলতে চান?",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "অর্ডার: ${order.orderNumber}",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "কাস্টমার: ${order.customerName} (${order.furnitureName})",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "তথ্য মুছে ফেললে তা আর ফেরত পাওয়া যাবে না।",
                    color = CrimsonAlert.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAlert),
                modifier = Modifier.testTag("dialog_confirm_delete_btn")
            ) {
                Text(
                    text = "হ্যাঁ, মুছে ফেলুন",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, DarkBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                modifier = Modifier.testTag("dialog_cancel_delete_btn")
            ) {
                Text(text = "না", color = TextWhite)
            }
        }
    )
}

@Composable
fun PaymentRecordDialog(
    order: FurnitureOrder?,
    onRecord: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    if (order == null) return

    var paymentAmountInput by remember { mutableStateOf(order.dueAmount.toLong().toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceCard,
        icon = {
            Icon(
                imageVector = Icons.Default.Paid,
                contentDescription = null,
                tint = EmeraldLight,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "টাকা পরিশোধ গ্রহণ",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "কাস্টমার: ${order.customerName}",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "মোট দাম: ${FormatUtils.formatTaka(order.totalPrice)}",
                    color = TextGray,
                    fontSize = 13.sp
                )
                Text(
                    text = "পূর্বের জমা: ${FormatUtils.formatTaka(order.advancePaid)}",
                    color = TextGray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "বর্তমানে বাকি: ${FormatUtils.formatTaka(order.dueAmount)}",
                    color = AmberDue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = paymentAmountInput,
                    onValueChange = {
                        paymentAmountInput = it
                        errorMessage = null
                    },
                    label = { Text("পরিশোধের পরিমাণ (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage != null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmeraldLight,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkInput,
                        unfocusedContainerColor = DarkInput,
                        focusedLabelColor = EmeraldLight,
                        unfocusedLabelColor = TextGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = CrimsonAlert,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Full Pay button
                Button(
                    onClick = {
                        paymentAmountInput = order.dueAmount.toLong().toString()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "পুরো বাকি টাকা (${FormatUtils.formatTaka(order.dueAmount)}) পরিশোধ",
                        color = SkyBlue,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = paymentAmountInput.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMessage = "অনুগ্রহ করে সঠিক পরিমাণ লিখুন"
                    } else if (amt > order.dueAmount) {
                        errorMessage = "পরিশোধের পরিমাণ বাকি টাকার চেয়ে বেশি হতে পারে না"
                    } else {
                        onRecord(amt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.testTag("confirm_payment_btn")
            ) {
                Text(
                    text = "টাকা জমা করুন",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Text(text = "বাতিল", color = TextGray)
            }
        }
    )
}
