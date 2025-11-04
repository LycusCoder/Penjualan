package com.kelompok5.penjualan.data.repository

import com.kelompok5.penjualan.data.dao.ProductDao
import com.kelompok5.penjualan.data.entity.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk Product
 * Abstraksi layer antara ViewModel dan Data Source (Room)
 */
class ProductRepository(private val productDao: ProductDao) {
    
    /**
     * Get semua produk
     */
    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }
    
    /**
     * Get semua produk sebagai Flow (untuk observe perubahan)
     */
    fun getAllProductsFlow(): Flow<List<Product>> {
        return productDao.getAllProductsFlow()
    }
    
    /**
     * Get produk by ID
     */
    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }
    
    /**
     * Insert produk baru
     */
    suspend fun insert(product: Product) {
        productDao.insert(product)
    }
    
    /**
     * Update produk
     */
    suspend fun update(product: Product) {
        productDao.update(product)
    }
    
    /**
     * Update stock produk (KRUSIAL untuk checkout)
     */
    suspend fun updateStock(productId: Int, newStock: Int) {
        productDao.updateStock(productId, newStock)
    }
    
    /**
     * Search produk by name
     */
    suspend fun searchProducts(query: String): List<Product> {
        return productDao.searchProducts(query)
    }
    
    /**
     * Get produk yang available (stock > 0)
     */
    suspend fun getAvailableProducts(): List<Product> {
        return productDao.getAvailableProducts()
    }
}
