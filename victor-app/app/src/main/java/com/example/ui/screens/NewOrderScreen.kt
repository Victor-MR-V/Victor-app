package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.example.ui.components.VictorTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.FurnitureViewModel
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
    viewModel: FurnitureViewModel,
    modifier: Modifier = Modifier
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }

    var furnitureName by remember { mutableStateOf("") }
    var furnitureType by remember { mutableStateOf("সেগুন কাঠ") }
    var quantityText by remember { mutableStateOf("1") }
    var dimensions by remember { mutableStateOf("") }
    var designDesc by remember { mutableStateOf("") }

    var totalPriceText by remember { mutableStateOf("") }
    var advancePaidText by remember { mutableStateOf("") }

    val todayDate = remember { FormatUtils.getTodayDateString() }
    val tomorrowDate = remember { FormatUtils.getTomorrowDateString() }

    var orderDate by remember { mutableStateOf(todayDate) }
    var deliveryDate by remember { mutableStateOf(tomorrowDate) }
    var duePaymentDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val woodTypes = listOf("সেগুন কাঠ", "মেহগনি", "কানাডিয়ান ওক", "বোর্ড / ভিনিয়ার", "মেটাল", "অন্যান্য")
    var woodDropdownExpanded by remember { mutableStateOf(false) }

    val calculatedDue by remember {
        derivedStateOf {
            val total = totalPriceText.toDoubleOrNull() ?: 0.0
            val advance = advancePaidText.toDoubleOrNull() ?: 0.0
            (total - advance).coerceAtLeast(0.0)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            VictorTopBar(
                title = "নতুন অর্ডার ফর্ম",
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("অর্ডার আইডি: ", color = TextGray, fontSize = 13.sp)
                    Text(
                        viewModel.generateNextOrderNumber(),
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // 1. কাস্টমারের তথ্য
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "👤 কাস্টমারের তথ্য",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("কাস্টমারের নাম *") },
                        placeholder = { Text("যেমন: করিম চৌধুরী") },
                        singleLine = true,
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_customer_name_input")
                    )

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("মোবাইল নম্বর *") },
                        placeholder = { Text("যেমন: 017xxxxxxxx") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_customer_phone_input")
                    )

                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("ঠিকানা") },
                        placeholder = { Text("যেমন: মিরপুর, ঢাকা") },
                        singleLine = true,
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_customer_address_input")
                    )
                }
            }

            // 2. ফার্নিচারের তথ্য
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🪑 ফার্নিচারের তথ্য",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = furnitureName,
                        onValueChange = { furnitureName = it },
                        label = { Text("ফার্নিচারের নাম *") },
                        placeholder = { Text("যেমন: সোফা সেট, খাট, ডাইনিং টেবিল") },
                        singleLine = true,
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_furniture_name_input")
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
                            colors = formTextFieldColors(),
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
                            colors = formTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("new_order_quantity_input")
                        )

                        OutlinedTextField(
                            value = dimensions,
                            onValueChange = { dimensions = it },
                            label = { Text("ফার্নিচারের মাপ") },
                            placeholder = { Text("যেমন: ৬x৭ ফুট") },
                            colors = formTextFieldColors(),
                            modifier = Modifier.weight(2f).testTag("new_order_dimensions_input")
                        )
                    }

                    OutlinedTextField(
                        value = designDesc,
                        onValueChange = { designDesc = it },
                        label = { Text("ডিজাইন / বিবরণ") },
                        placeholder = { Text("রং, নকশা বা অন্যান্য বিবরণ") },
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_design_desc_input")
                    )
                }
            }

            // 3. টাকার তথ্য (বাকি টাকা স্বয়ংক্রিয়ভাবে হিসাব)
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "💰 টাকার তথ্য",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalPriceText,
                            onValueChange = { totalPriceText = it },
                            label = { Text("মোট দাম (৳) *") },
                            placeholder = { Text("যেমন: 35000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = formTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("new_order_total_price_input")
                        )

                        OutlinedTextField(
                            value = advancePaidText,
                            onValueChange = { advancePaidText = it },
                            label = { Text("জমা টাকা (৳)") },
                            placeholder = { Text("যেমন: 15000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = formTextFieldColors(),
                            modifier = Modifier.weight(1f).testTag("new_order_advance_paid_input")
                        )
                    }

                    // Automatic Due Calculator Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkInput),
                        border = BorderStroke(1.dp, if (calculatedDue > 0) AmberDue else EmeraldLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "বাকি টাকা (স্বয়ংক্রিয় হিসাব):",
                                color = TextGray,
                                fontSize = 13.sp
                            )
                            Text(
                                text = FormatUtils.formatTaka(calculatedDue),
                                color = if (calculatedDue > 0) AmberDue else EmeraldLight,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. তারিখ
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📅 তারিখ সংক্রান্ত তথ্য",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = orderDate,
                        onValueChange = { orderDate = it },
                        label = { Text("অর্ডারের তারিখ (YYYY-MM-DD)") },
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_order_date_input")
                    )

                    OutlinedTextField(
                        value = deliveryDate,
                        onValueChange = { deliveryDate = it },
                        label = { Text("ডেলিভারির তারিখ (YYYY-MM-DD)") },
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_delivery_date_input")
                    )

                    OutlinedTextField(
                        value = duePaymentDate,
                        onValueChange = { duePaymentDate = it },
                        label = { Text("বাকি টাকা দেওয়ার তারিখ (ঐচ্ছিক)") },
                        placeholder = { Text("যেমন: 2026-09-30") },
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_due_date_input")
                    )
                }
            }

            // 5. অতিরিক্ত তথ্য
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📝 অতিরিক্ত তথ্য",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("অতিরিক্ত নোট") },
                        placeholder = { Text("যেমন: কাঁচের ফিটিংস, বিশেষ হ্যান্ডেল ইত্যাদি") },
                        colors = formTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("new_order_notes_input")
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val total = totalPriceText.toDoubleOrNull() ?: 0.0
                    val advance = advancePaidText.toDoubleOrNull() ?: 0.0
                    val qty = quantityText.toIntOrNull() ?: 1

                    viewModel.saveNewOrder(
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerAddress = customerAddress,
                        furnitureName = furnitureName,
                        furnitureType = furnitureType,
                        quantity = qty,
                        dimensions = dimensions,
                        designDesc = designDesc,
                        totalPrice = total,
                        advancePaid = advance,
                        orderDate = orderDate,
                        deliveryDate = deliveryDate,
                        duePaymentDate = duePaymentDate,
                        notes = notes
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_new_order_submit_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "💾 তথ্য সংরক্ষণ করুন",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = DarkBorder,
    focusedContainerColor = DarkInput,
    unfocusedContainerColor = DarkInput,
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextGray
)
