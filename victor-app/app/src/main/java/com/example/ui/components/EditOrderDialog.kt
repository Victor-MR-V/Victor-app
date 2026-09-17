package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FurnitureOrder
import com.example.ui.theme.*
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderDialog(
    order: FurnitureOrder?,
    onDismiss: () -> Unit,
    onSave: (FurnitureOrder) -> Unit
) {
    if (order == null) return

    var customerName by remember { mutableStateOf(order.customerName) }
    var customerPhone by remember { mutableStateOf(order.customerPhone) }
    var customerAddress by remember { mutableStateOf(order.customerAddress) }

    var furnitureName by remember { mutableStateOf(order.furnitureName) }
    var furnitureType by remember { mutableStateOf(order.furnitureType) }
    var quantityText by remember { mutableStateOf(order.quantity.toString()) }
    var dimensions by remember { mutableStateOf(order.furnitureDimensions) }
    var designDesc by remember { mutableStateOf(order.designDescription) }

    var totalPriceText by remember { mutableStateOf(order.totalPrice.toLong().toString()) }
    var advancePaidText by remember { mutableStateOf(order.advancePaid.toLong().toString()) }

    var orderDate by remember { mutableStateOf(order.orderDate) }
    var deliveryDate by remember { mutableStateOf(order.deliveryDate) }
    var duePaymentDate by remember { mutableStateOf(order.duePaymentDate) }
    var orderStatus by remember { mutableStateOf(order.orderStatus) }
    var notes by remember { mutableStateOf(order.notes) }

    val calculatedDue by remember {
        derivedStateOf {
            val total = totalPriceText.toDoubleOrNull() ?: 0.0
            val advance = advancePaidText.toDoubleOrNull() ?: 0.0
            (total - advance).coerceAtLeast(0.0)
        }
    }

    val scrollState = rememberScrollState()

    val statusOptions = listOf("নতুন অর্ডার", "কাজ চলছে", "প্রস্তুত", "ডেলিভারি বাকি", "ডেলিভারি সম্পন্ন", "বাতিল")
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    val woodTypes = listOf("সেগুন কাঠ", "মেহগনি", "কানাডিয়ান ওক", "বোর্ড / ভিনিয়ার", "মেটাল", "অন্যান্য")
    var woodDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
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
                            text = "অর্ডারের তথ্য পরিবর্তন",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = order.orderNumber,
                            color = GoldPrimary,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বাতিল", tint = TextGray)
                    }
                }

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("👤 কাস্টমারের তথ্য", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("কাস্টমারের নাম") },
                        singleLine = true,
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("edit_customer_name")
                    )

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("edit_customer_phone")
                    )

                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("ঠিকানা") },
                        singleLine = true,
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Text("🪑 ফার্নিচারের তথ্য", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = furnitureName,
                        onValueChange = { furnitureName = it },
                        label = { Text("ফার্নিচারের নাম") },
                        singleLine = true,
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("edit_furniture_name")
                    )

                    // Wood type exposed dropdown
                    ExposedDropdownMenuBox(
                        expanded = woodDropdownExpanded,
                        onExpandedChange = { woodDropdownExpanded = !woodDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = furnitureType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ফার্নিচারের ধরন / কাঠ") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = woodDropdownExpanded) },
                            colors = editTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = woodDropdownExpanded,
                            onDismissRequest = { woodDropdownExpanded = false },
                            modifier = Modifier.background(DarkSurfaceCard)
                        ) {
                            woodTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type, color = TextWhite) },
                                    onClick = {
                                        furnitureType = type
                                        woodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("পরিমাণ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = dimensions,
                            onValueChange = { dimensions = it },
                            label = { Text("মাপ (উদা: ৬x৭ ফুট)") },
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(2f)
                        )
                    }

                    OutlinedTextField(
                        value = designDesc,
                        onValueChange = { designDesc = it },
                        label = { Text("ডিজাইন / বিবরণ") },
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Text("💰 টাকার তথ্য", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalPriceText,
                            onValueChange = { totalPriceText = it },
                            label = { Text("মোট দাম (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("edit_total_price")
                        )
                        OutlinedTextField(
                            value = advancePaidText,
                            onValueChange = { advancePaidText = it },
                            label = { Text("জমা টাকা (৳)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("edit_advance_paid")
                        )
                    }

                    // Display calculated due
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, if (calculatedDue > 0) AmberDue else EmeraldLight)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("বাকি টাকা (স্বয়ংক্রিয়):", color = TextGray, fontSize = 13.sp)
                            Text(
                                FormatUtils.formatTaka(calculatedDue),
                                color = if (calculatedDue > 0) AmberDue else EmeraldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Text("📅 তারিখ ও অবস্থা", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = deliveryDate,
                            onValueChange = { deliveryDate = it },
                            label = { Text("ডেলিভারির তারিখ (YYYY-MM-DD)") },
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = duePaymentDate,
                            onValueChange = { duePaymentDate = it },
                            label = { Text("বাকি দেওয়ার তারিখ") },
                            colors = editTextFieldColors(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Order Status Dropdown
                    ExposedDropdownMenuBox(
                        expanded = statusDropdownExpanded,
                        onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = orderStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("অর্ডারের অবস্থা") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                            colors = editTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false },
                            modifier = Modifier.background(DarkSurfaceCard)
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status, color = TextWhite) },
                                    onClick = {
                                        orderStatus = status
                                        statusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("অতিরিক্ত নোট") },
                        colors = editTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Footer with Save Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("বাতিল", color = TextGray)
                    }

                    Button(
                        onClick = {
                            val total = totalPriceText.toDoubleOrNull() ?: order.totalPrice
                            val advance = advancePaidText.toDoubleOrNull() ?: order.advancePaid
                            val qty = quantityText.toIntOrNull() ?: order.quantity

                            val updated = order.copy(
                                customerName = customerName.trim(),
                                customerPhone = customerPhone.trim(),
                                customerAddress = customerAddress.trim(),
                                furnitureName = furnitureName.trim(),
                                furnitureType = furnitureType.trim(),
                                quantity = qty,
                                furnitureDimensions = dimensions.trim(),
                                designDescription = designDesc.trim(),
                                totalPrice = total,
                                advancePaid = advance,
                                deliveryDate = deliveryDate.trim(),
                                duePaymentDate = duePaymentDate.trim(),
                                orderStatus = orderStatus,
                                notes = notes.trim()
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        modifier = Modifier.weight(1f).testTag("save_edited_order_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সংরক্ষণ করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = DarkBorder,
    focusedContainerColor = DarkInput,
    unfocusedContainerColor = DarkInput,
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextGray
)
