package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.model.FurnitureOrder
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<FurnitureOrder>> = orderDao.getAllOrders()

    fun searchOrders(query: String): Flow<List<FurnitureOrder>> {
        return orderDao.searchOrders(query.trim())
    }

    val dueOrders: Flow<List<FurnitureOrder>> = orderDao.getDueOrders()

    fun getOrderById(id: Long): Flow<FurnitureOrder?> {
        return orderDao.getOrderById(id)
    }

    suspend fun insert(order: FurnitureOrder): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun update(order: FurnitureOrder) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteById(id: Long) {
        orderDao.deleteOrderById(id)
    }

    suspend fun updateOrderStatus(id: Long, status: String) {
        orderDao.updateOrderStatus(id, status)
    }

    suspend fun recordPayment(id: Long, newAdvance: Double, newDue: Double, paymentStatus: String) {
        orderDao.updatePayment(id, newAdvance, newDue, paymentStatus)
    }
}
