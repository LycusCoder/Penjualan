package com.kelompok5.penjualan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kelompok5.penjualan.data.entity.Product
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk Product
 * Berisi operasi database untuk tabel products
 */
@Dao
interface ProductDao {
    
    /**
     * Insert produk baru
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)
    
    /**
     * Insert multiple products sekaligus
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)
    
    /**
     * Update produk yang sudah ada
     */
    @Update
    suspend fun update(product: Product)
    
    /**
     * Get semua produk
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProducts(): List<Product>
    
    /**
     * Get semua produk sebagai Flow (untuk observe realtime changes)
     */
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProductsFlow(): Flow<List<Product>>
    
    /**
     * Get produk by ID
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?
    
    /**
     * Update stock produk (KRUSIAL untuk checkout)
     */
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int)
    
    /**
     * Search produk by name
     */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchProducts(query: String): List<Product>
    
    /**
     * Get produk yang masih ada stock
     */
    @Query("SELECT * FROM products WHERE stock > 0 ORDER BY name ASC")
    suspend fun getAvailableProducts(): List<Product>
    
    /**
     * Delete semua produk (untuk testing)
     */
    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
