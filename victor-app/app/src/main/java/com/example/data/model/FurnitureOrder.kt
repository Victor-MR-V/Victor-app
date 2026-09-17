package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "furniture_orders")
data class FurnitureOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String = "",
    val furnitureName: String,
    val furnitureType: String = "সেগুন কাঠ",
    val quantity: Int = 1,
    val furnitureDimensions: String = "",
    val designDescription: String = "",
    val totalPrice: Double = 0.0,
    val advancePaid: Double = 0.0,
    val dueAmount: Double = 0.0,
    val orderDate: String,
    val deliveryDate: String,
    val duePaymentDate: String = "",
    val notes: String = "",
    val orderStatus: String = "নতুন অর্ডার",
    val paymentStatus: String = "টাকা দেওয়া হয়নি",
    val createdAt: Long = System.currentTimeMillis()
)
