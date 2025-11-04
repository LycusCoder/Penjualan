package com.kelompok5.penjualan.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kelompok5.penjualan.data.dao.ProductDao
import com.kelompok5.penjualan.data.dao.TransactionDao
import com.kelompok5.penjualan.data.entity.Product
import com.kelompok5.penjualan.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database untuk QuickSell PoS
 * 
 * Berisi 2 tabel:
 * - products: Menyimpan data produk
 * - transactions: Menyimpan riwayat transaksi
 */
@Database(
    entities = [Product::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get database instance (Singleton pattern)
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quicksell_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Callback untuk seed data awal saat database pertama kali dibuat
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                
                // Seed sample products saat database pertama kali dibuat
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.productDao())
                    }
                }
            }
        }
        
        /**
         * Seed sample products untuk testing
         */
        private suspend fun populateDatabase(productDao: ProductDao) {
            // Delete all first (untuk testing)
            productDao.deleteAll()
            
            // Insert sample products
            val sampleProducts = listOf(
                // Makanan & Minuman Instan
                Product(name = "Indomie Goreng", price = 3000.0, stock = 50),
                Product(name = "Indomie Soto", price = 3000.0, stock = 40),
                Product(name = "Mie Sedaap", price = 2500.0, stock = 45),
                Product(name = "Pop Mie", price = 5000.0, stock = 30),
                
                // Minuman
                Product(name = "Aqua 600ml", price = 5000.0, stock = 100),
                Product(name = "Teh Botol Sosro", price = 6000.0, stock = 50),
                Product(name = "Coca Cola", price = 8000.0, stock = 40),
                Product(name = "Fanta", price = 8000.0, stock = 35),
                Product(name = "Sprite", price = 8000.0, stock = 35),
                Product(name = "Pocari Sweat", price = 10000.0, stock = 25),
                
                // Snack
                Product(name = "Chitato", price = 10000.0, stock = 30),
                Product(name = "Taro", price = 8000.0, stock = 35),
                Product(name = "Oreo", price = 12000.0, stock = 25),
                Product(name = "Good Time", price = 11000.0, stock = 20),
                Product(name = "Pringles", price = 25000.0, stock = 15),
                
                // Roti & Kue
                Product(name = "Roti Tawar Sari Roti", price = 15000.0, stock = 20),
                Product(name = "Roti Sobek", price = 12000.0, stock = 15),
                Product(name = "Wafer Tango", price = 5000.0, stock = 40),
                
                // Kebutuhan Rumah Tangga
                Product(name = "Susu Ultra 1L", price = 18000.0, stock = 30),
                Product(name = "Gula Pasir 1kg", price = 15000.0, stock = 25),
                Product(name = "Minyak Goreng 1L", price = 17000.0, stock = 20),
                Product(name = "Beras 5kg", price = 75000.0, stock = 10),
                Product(name = "Telur 1kg", price = 28000.0, stock = 15),
                
                // Toiletries
                Product(name = "Sabun Lifebuoy", price = 5000.0, stock = 40),
                Product(name = "Shampoo Clear", price = 25000.0, stock = 20),
                Product(name = "Pasta Gigi Pepsodent", price = 12000.0, stock = 25)
            )
            
            productDao.insertAll(sampleProducts)
        }
    }
}
