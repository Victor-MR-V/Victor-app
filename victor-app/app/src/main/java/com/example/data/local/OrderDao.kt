package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FurnitureOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM furniture_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<FurnitureOrder>>

    @Query("SELECT * FROM furniture_orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: Long): Flow<FurnitureOrder?>

    @Query("""
        SELECT * FROM furniture_orders 
        WHERE customerName LIKE '%' || :query || '%' 
           OR customerPhone LIKE '%' || :query || '%' 
           OR orderNumber LIKE '%' || :query || '%' 
           OR furnitureName LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchOrders(query: String): Flow<List<FurnitureOrder>>

    @Query("SELECT * FROM furniture_orders WHERE dueAmount > 0 ORDER BY duePaymentDate ASC")
    fun getDueOrders(): Flow<List<FurnitureOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: FurnitureOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<FurnitureOrder>)

    @Update
    suspend fun updateOrder(order: FurnitureOrder)

    @Delete
    suspend fun deleteOrder(order: FurnitureOrder)

    @Query("DELETE FROM furniture_orders WHERE id = :id")
    suspend fun deleteOrderById(id: Long)

    @Query("UPDATE furniture_orders SET orderStatus = :newStatus WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, newStatus: String)

    @Query("""
        UPDATE furniture_orders 
        SET advancePaid = :newAdvance, 
            dueAmount = :newDue, 
            paymentStatus = :newPaymentStatus 
        WHERE id = :id
    """)
    suspend fun updatePayment(id: Long, newAdvance: Double, newDue: Double, newPaymentStatus: String)
}
