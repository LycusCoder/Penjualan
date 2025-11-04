package com.kelompok5.penjualan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Product untuk Room Database
 * Menyimpan informasi produk yang dijual di kasir
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val name: String,
    
    val price: Double,
    
    val stock: Int
) {
    /**
     * Check apakah produk masih tersedia (stock > 0)
     */
    fun isAvailable(): Boolean = stock > 0
    
    /**
     * Check apakah stock cukup untuk quantity tertentu
     */
    fun hasEnoughStock(quantity: Int): Boolean = stock >= quantity
}
