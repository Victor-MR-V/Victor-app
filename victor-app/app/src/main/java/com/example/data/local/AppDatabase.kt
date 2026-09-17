package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.FurnitureOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(entities = [FurnitureOrder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "victor_furniture_orders.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                seedInitialData(database.orderDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: OrderDao) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val todayStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, 4)
            val nextWeekStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -10)
            val pastDueStr = dateFormat.format(calendar.time)

            val initialOrders = listOf(
                FurnitureOrder(
                    orderNumber = "ORD-2026-101",
                    customerName = "রহিম চৌধুরী",
                    customerPhone = "01712345678",
                    customerAddress = "মিরপুর-১০, ঢাকা",
                    furnitureName = "সেগুন কাঠের রাজকীয় সোফা সেট",
                    furnitureType = "সেগুন কাঠ",
                    quantity = 1,
                    furnitureDimensions = "৩+১+১ সিটার",
                    designDescription = "ভিক্টোরিয়ান খোদাই করা নকশা, ভেলভেট ফেব্রিক কুশন",
                    totalPrice = 55000.0,
                    advancePaid = 25000.0,
                    dueAmount = 30000.0,
                    orderDate = todayStr,
                    deliveryDate = todayStr, // Today's delivery!
                    duePaymentDate = todayStr, // Today's due date!
                    notes = "জরুরি ডেলিভারি, বিকেলে পাঠাতে হবে",
                    orderStatus = "প্রস্তুত",
                    paymentStatus = "আংশিক টাকা দেওয়া হয়েছে"
                ),
                FurnitureOrder(
                    orderNumber = "ORD-2026-102",
                    customerName = "করিম উল্লাহ",
                    customerPhone = "01823456789",
                    customerAddress = "উত্তরা সেক্টর ৭, ঢাকা",
                    furnitureName = "৬ সিটের মেহগনি ডাইনিং টেবিল",
                    furnitureType = "মেহগনি কাঠ",
                    quantity = 1,
                    furnitureDimensions = "৬ ফিট × ৩.৫ ফিট",
                    designDescription = "গ্লাস টপ সহ আধুনিক মসৃণ ফিনিশ",
                    totalPrice = 38000.0,
                    advancePaid = 38000.0,
                    dueAmount = 0.0,
                    orderDate = todayStr,
                    deliveryDate = todayStr,
                    duePaymentDate = "",
                    notes = "সম্পূর্ণ পেমেন্ট ক্যাশে জমা হয়েছে",
                    orderStatus = "ডেলিভারি সম্পন্ন",
                    paymentStatus = "সম্পূর্ণ টাকা দেওয়া হয়েছে"
                ),
                FurnitureOrder(
                    orderNumber = "ORD-2026-103",
                    customerName = "হাসান মাহমুদ",
                    customerPhone = "01934567890",
                    customerAddress = "ধানমন্ডি ২৭, ঢাকা",
                    furnitureName = "কিং সাইজ রাজকীয় খাট",
                    furnitureType = "সেগুন কাঠ",
                    quantity = 1,
                    furnitureDimensions = "৬ ফিট × ৭ ফিট",
                    designDescription = "হাই হেডবোর্ড কুশন ও বক্স স্টোরেজ",
                    totalPrice = 48000.0,
                    advancePaid = 18000.0,
                    dueAmount = 30000.0,
                    orderDate = todayStr,
                    deliveryDate = tomorrowStr, // Tomorrow's delivery!
                    duePaymentDate = tomorrowStr,
                    notes = "ফিটিংসের দক্ষ মিস্ত্রি পাঠাতে হবে",
                    orderStatus = "কাজ চলছে",
                    paymentStatus = "আংশিক টাকা দেওয়া হয়েছে"
                ),
                FurnitureOrder(
                    orderNumber = "ORD-2026-104",
                    customerName = "তানভীর আহমেদ",
                    customerPhone = "01645678901",
                    customerAddress = "বনশ্রী ব্লক সি, ঢাকা",
                    furnitureName = "৪ পাল্লার আধুনিক আলমারি",
                    furnitureType = "কানাডিয়ান ওক",
                    quantity = 1,
                    furnitureDimensions = "৬.৫ ফিট × ৬ ফিট",
                    designDescription = "ভেতরে সিক্রেট লকার এবং ফুল লেন্থ ড্রেসিং মিরর",
                    totalPrice = 42000.0,
                    advancePaid = 12000.0,
                    dueAmount = 30000.0,
                    orderDate = todayStr,
                    deliveryDate = nextWeekStr,
                    duePaymentDate = pastDueStr, // Overdue!
                    notes = "বাকি টাকা ফোনে তাগাদা দেওয়া হয়েছে",
                    orderStatus = "নতুন অর্ডার",
                    paymentStatus = "আংশিক টাকা দেওয়া হয়েছে"
                )
            )
            dao.insertAll(initialOrders)
        }
    }
}
