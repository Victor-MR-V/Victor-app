package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.EditOrderDialog
import com.example.ui.components.OrderDetailDialog
import com.example.ui.components.PaymentRecordDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FurnitureViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FurnitureViewModel = viewModel()
                VictorAppMain(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VictorAppMain(viewModel: FurnitureViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedDetail by viewModel.selectedOrderForDetail.collectAsStateWithLifecycle()
    val selectedEdit by viewModel.selectedOrderForEdit.collectAsStateWithLifecycle()
    val targetDelete by viewModel.orderToDelete.collectAsStateWithLifecycle()
    val targetPayment by viewModel.orderForPayment.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val isErrorMessage by viewModel.isErrorMessage.collectAsStateWithLifecycle()

    // Handle back button smoothly
    BackHandler(
        enabled = currentScreen != AppScreen.DASHBOARD ||
                selectedDetail != null ||
                selectedEdit != null ||
                targetDelete != null ||
                targetPayment != null
    ) {
        viewModel.goBack()
    }

    // Auto-clear feedback after 3 seconds
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(3000)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBg,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            // Main Screen Routing
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.CUSTOMER_LIST -> CustomerListScreen(viewModel = viewModel)
                AppScreen.NEW_ORDER -> NewOrderScreen(viewModel = viewModel)
                AppScreen.SEARCH_ORDER -> SearchOrderScreen(viewModel = viewModel)
                AppScreen.DELIVERY -> DeliveryManagementScreen(viewModel = viewModel)
                AppScreen.DUE_PAYMENT -> DuePaymentScreen(viewModel = viewModel)
                AppScreen.NOTIFICATIONS -> NotificationScreen(viewModel = viewModel)
            }

            // Floating Feedback Banner (Bengali Alerts)
            AnimatedVisibility(
                visible = feedbackMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                feedbackMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isErrorMessage) CrimsonAlert else EmeraldGreen,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isErrorMessage) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isErrorMessage) Color.White else Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                color = if (isErrorMessage) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dialogs
            if (selectedDetail != null) {
                OrderDetailDialog(
                    order = selectedDetail,
                    onDismiss = { viewModel.selectedOrderForDetail.value = null },
                    onEdit = { order ->
                        viewModel.selectedOrderForDetail.value = null
                        viewModel.selectedOrderForEdit.value = order
                    },
                    onDelete = { order ->
                        viewModel.selectedOrderForDetail.value = null
                        viewModel.orderToDelete.value = order
                    },
                    onMarkDelivery = { id -> viewModel.markDeliveryCompleted(id) },
                    onPayDue = { order ->
                        viewModel.selectedOrderForDetail.value = null
                        viewModel.orderForPayment.value = order
                    }
                )
            }

            if (selectedEdit != null) {
                EditOrderDialog(
                    order = selectedEdit,
                    onDismiss = { viewModel.selectedOrderForEdit.value = null },
                    onSave = { updatedOrder -> viewModel.updateOrder(updatedOrder) }
                )
            }

            if (targetDelete != null) {
                DeleteConfirmationDialog(
                    order = targetDelete,
                    onConfirm = { viewModel.confirmDeleteOrder() },
                    onDismiss = { viewModel.orderToDelete.value = null }
                )
            }

            if (targetPayment != null) {
                PaymentRecordDialog(
                    order = targetPayment,
                    onRecord = { amountPaid ->
                        targetPayment?.let { order ->
                            viewModel.recordPayment(order.id, amountPaid)
                        }
                    },
                    onDismiss = { viewModel.orderForPayment.value = null }
                )
            }
        }
    }
}
