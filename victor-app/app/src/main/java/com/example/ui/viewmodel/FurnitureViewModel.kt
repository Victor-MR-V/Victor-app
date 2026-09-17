package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.FurnitureOrder
import com.example.data.model.NotificationType
import com.example.data.repository.OrderRepository
import com.example.util.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    CUSTOMER_LIST,
    NEW_ORDER,
    SEARCH_ORDER,
    DELIVERY,
    DUE_PAYMENT,
    NOTIFICATIONS
}

class FurnitureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrderRepository

    val currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val searchQuery = MutableStateFlow("")

    val selectedOrderForDetail = MutableStateFlow<FurnitureOrder?>(null)
    val selectedOrderForEdit = MutableStateFlow<FurnitureOrder?>(null)
    val orderToDelete = MutableStateFlow<FurnitureOrder?>(null)
    val orderForPayment = MutableStateFlow<FurnitureOrder?>(null)

    val feedbackMessage = MutableStateFlow<String?>(null)
    val isErrorMessage = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = OrderRepository(database.orderDao())
    }

    val allOrders: StateFlow<List<FurnitureOrder>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchResults: StateFlow<List<FurnitureOrder>> = searchQuery
        .debounce(150)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allOrders
            } else {
                repository.searchOrders(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalOrdersCount: StateFlow<Int> = allOrders
        .combine(MutableStateFlow(Unit)) { orders, _ -> orders.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDueSum: StateFlow<Double> = allOrders
        .combine(MutableStateFlow(Unit)) { orders, _ ->
            orders.sumOf { it.dueAmount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayDeliveryCount: StateFlow<Int> = allOrders
        .combine(MutableStateFlow(Unit)) { orders, _ ->
            val today = FormatUtils.getTodayDateString()
            orders.count { it.deliveryDate == today }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notifications: StateFlow<List<AppNotification>> = allOrders
        .combine(MutableStateFlow(Unit)) { orders, _ ->
            val list = mutableListOf<AppNotification>()
            val today = FormatUtils.getTodayDateString()
            val tomorrow = FormatUtils.getTomorrowDateString()

            for (order in orders) {
                // 1. আজকের ডেলিভারি
                if (order.deliveryDate == today) {
                    val statusSuffix = if (order.orderStatus == "ডেলিভারি সম্পন্ন") " (সম্পন্ন)" else ""
                    list.add(
                        AppNotification(
                            id = "deliv_today_${order.id}",
                            type = NotificationType.TODAY_DELIVERY,
                            title = "📦 আজ ডেলিভারির তারিখ",
                            message = "আজ ${order.customerName}-এর ${order.furnitureName} ডেলিভারি$statusSuffix",
                            orderId = order.id,
                            orderNumber = order.orderNumber,
                            customerName = order.customerName,
                            dateText = FormatUtils.formatBengaliDate(order.deliveryDate)
                        )
                    )
                }

                // 2. আজ টাকা দেওয়ার তারিখ
                if (order.duePaymentDate == today && order.dueAmount > 0) {
                    list.add(
                        AppNotification(
                            id = "due_today_${order.id}",
                            type = NotificationType.TODAY_DUE,
                            title = "💰 আজ টাকা দেওয়ার তারিখ",
                            message = "${order.customerName}-এর ${FormatUtils.formatTaka(order.dueAmount)} আজ দেওয়ার কথা",
                            orderId = order.id,
                            orderNumber = order.orderNumber,
                            customerName = order.customerName,
                            dateText = FormatUtils.formatBengaliDate(order.duePaymentDate),
                            amountText = FormatUtils.formatTaka(order.dueAmount)
                        )
                    )
                }

                // 3. আগামী দিনের ডেলিভারি
                if (order.deliveryDate == tomorrow && order.orderStatus != "ডেলিভারি সম্পন্ন") {
                    list.add(
                        AppNotification(
                            id = "deliv_tomorrow_${order.id}",
                            type = NotificationType.TOMORROW_DELIVERY,
                            title = "📅 আগামী দিনের ডেলিভারি",
                            message = "আগামীকাল ${order.customerName}-এর ${order.furnitureName} ডেলিভারি দিতে হবে",
                            orderId = order.id,
                            orderNumber = order.orderNumber,
                            customerName = order.customerName,
                            dateText = FormatUtils.formatBengaliDate(order.deliveryDate)
                        )
                    )
                }

                // 4. সময় পার হয়ে যাওয়া বাকি টাকা
                if (order.duePaymentDate.isNotBlank() && order.duePaymentDate < today && order.dueAmount > 0) {
                    list.add(
                        AppNotification(
                            id = "overdue_${order.id}",
                            type = NotificationType.OVERDUE_PAYMENT,
                            title = "⚠️ সময় পার হয়ে গেছে",
                            message = "${order.customerName}-এর ${FormatUtils.formatTaka(order.dueAmount)} বাকি আছে (তারিখ ছিল: ${FormatUtils.formatBengaliDate(order.duePaymentDate)})",
                            orderId = order.id,
                            orderNumber = order.orderNumber,
                            customerName = order.customerName,
                            dateText = FormatUtils.formatBengaliDate(order.duePaymentDate),
                            amountText = FormatUtils.formatTaka(order.dueAmount)
                        )
                    )
                }
            }
            list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: AppScreen) {
        currentScreen.value = screen
    }

    fun goBack() {
        if (selectedOrderForDetail.value != null) {
            selectedOrderForDetail.value = null
            return
        }
        if (selectedOrderForEdit.value != null) {
            selectedOrderForEdit.value = null
            return
        }
        if (orderForPayment.value != null) {
            orderForPayment.value = null
            return
        }
        if (orderToDelete.value != null) {
            orderToDelete.value = null
            return
        }
        if (currentScreen.value != AppScreen.DASHBOARD) {
            currentScreen.value = AppScreen.DASHBOARD
        }
    }

    fun showFeedback(msg: String, isError: Boolean = false) {
        feedbackMessage.value = msg
        isErrorMessage.value = isError
    }

    fun clearFeedback() {
        feedbackMessage.value = null
    }

    fun generateNextOrderNumber(): String {
        val count = allOrders.value.size + 101
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        return "ORD-$year-$count"
    }

    fun saveNewOrder(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        furnitureName: String,
        furnitureType: String,
        quantity: Int,
        dimensions: String,
        designDesc: String,
        totalPrice: Double,
        advancePaid: Double,
        orderDate: String,
        deliveryDate: String,
        duePaymentDate: String,
        notes: String
    ): Boolean {
        if (customerName.isBlank() || customerPhone.isBlank() || furnitureName.isBlank() || totalPrice <= 0) {
            showFeedback("⚠️ অনুগ্রহ করে প্রয়োজনীয় তথ্য পূরণ করুন", isError = true)
            return false
        }

        val due = (totalPrice - advancePaid).coerceAtLeast(0.0)
        val pStatus = when {
            due <= 0 -> "সম্পূর্ণ টাকা দেওয়া হয়েছে"
            advancePaid > 0 -> "আংশিক টাকা দেওয়া হয়েছে"
            else -> "টাকা দেওয়া হয়নি"
        }

        val newOrder = FurnitureOrder(
            orderNumber = generateNextOrderNumber(),
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            customerAddress = customerAddress.trim(),
            furnitureName = furnitureName.trim(),
            furnitureType = furnitureType.ifBlank { "সেগুন কাঠ" },
            quantity = quantity.coerceAtLeast(1),
            furnitureDimensions = dimensions.trim(),
            designDescription = designDesc.trim(),
            totalPrice = totalPrice,
            advancePaid = advancePaid,
            dueAmount = due,
            orderDate = orderDate.ifBlank { FormatUtils.getTodayDateString() },
            deliveryDate = deliveryDate.ifBlank { FormatUtils.getTodayDateString() },
            duePaymentDate = if (due > 0) duePaymentDate else "",
            notes = notes.trim(),
            orderStatus = "নতুন অর্ডার",
            paymentStatus = pStatus
        )

        viewModelScope.launch {
            repository.insert(newOrder)
            showFeedback("✅ তথ্য সফলভাবে সংরক্ষণ হয়েছে", isError = false)
            currentScreen.value = AppScreen.CUSTOMER_LIST
        }
        return true
    }

    fun updateOrder(order: FurnitureOrder) {
        viewModelScope.launch {
            val due = (order.totalPrice - order.advancePaid).coerceAtLeast(0.0)
            val pStatus = when {
                due <= 0 -> "সম্পূর্ণ টাকা দেওয়া হয়েছে"
                order.advancePaid > 0 -> "আংশিক টাকা দেওয়া হয়েছে"
                else -> "টাকা দেওয়া হয়নি"
            }
            val updated = order.copy(
                dueAmount = due,
                paymentStatus = pStatus
            )
            repository.update(updated)
            selectedOrderForEdit.value = null
            showFeedback("✅ তথ্য সফলভাবে পরিবর্তন করা হয়েছে")
        }
    }

    fun confirmDeleteOrder() {
        val target = orderToDelete.value ?: return
        viewModelScope.launch {
            repository.deleteById(target.id)
            if (selectedOrderForDetail.value?.id == target.id) {
                selectedOrderForDetail.value = null
            }
            orderToDelete.value = null
            showFeedback("✅ অর্ডার মুছে ফেলা হয়েছে")
        }
    }

    fun markDeliveryCompleted(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "ডেলিভারি সম্পন্ন")
            showFeedback("✅ ডেলিভারি সম্পন্ন হিসেবে চিহ্নিত হয়েছে")
        }
    }

    fun recordPayment(orderId: Long, additionalPayment: Double) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId } ?: return@launch
            val newAdvance = order.advancePaid + additionalPayment
            val newDue = (order.totalPrice - newAdvance).coerceAtLeast(0.0)
            val newPaymentStatus = if (newDue <= 0.0) "সম্পূর্ণ টাকা দেওয়া হয়েছে" else "আংশিক টাকা দেওয়া হয়েছে"

            repository.recordPayment(orderId, newAdvance, newDue, newPaymentStatus)
            orderForPayment.value = null
            showFeedback("✅ টাকা পরিশোধ সফলভাবে জমা হয়েছে")
        }
    }
}
