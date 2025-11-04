package com.kelompok5.penjualan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Transaction untuk Room Database
 * Menyimpan riwayat transaksi penjualan
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val date: Long, // timestamp in milliseconds
    
    val totalAmount: Double,
    
    val itemsDetailJson: String, // JSON string berisi List<CartItem>
    
    val moneyPaid: Double = 0.0,
    
    val change: Double = 0.0
) {
    companion object {
        /**
         * Create transaction baru dari data checkout
         */
        fun create(
            totalAmount: Double,
            itemsJson: String,
            moneyPaid: Double
        ): Transaction {
            return Transaction(
                date = System.currentTimeMillis(),
                totalAmount = totalAmount,
                itemsDetailJson = itemsJson,
                moneyPaid = moneyPaid,
                change = moneyPaid - totalAmount
            )
        }
    }
}
