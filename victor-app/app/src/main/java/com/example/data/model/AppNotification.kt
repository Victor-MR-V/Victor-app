package com.example.data.model

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val orderId: Long,
    val orderNumber: String,
    val customerName: String,
    val dateText: String,
    val amountText: String = ""
)

enum class NotificationType(val banglaLabel: String) {
    TODAY_DELIVERY("আজকের ডেলিভারি"),
    TODAY_DUE("আজ টাকা দেওয়ার তারিখ"),
    TOMORROW_DELIVERY("আগামী দিনের ডেলিভারি"),
    OVERDUE_PAYMENT("সময় পার হয়ে যাওয়া বাকি টাকা")
}
