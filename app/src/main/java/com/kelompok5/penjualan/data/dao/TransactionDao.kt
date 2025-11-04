package com.kelompok5.penjualan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kelompok5.penjualan.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk Transaction
 * Berisi operasi database untuk tabel transactions
 */
@Dao
interface TransactionDao {
    
    /**
     * Insert transaksi baru
     * Return: ID transaksi yang baru dibuat
     */
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long
    
    /**
     * Get transaksi terakhir (untuk ditampilkan di receipt)
     */
    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 1")
    suspend fun getLastTransaction(): Transaction?
    
    /**
     * Get transaksi by ID
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): Transaction?
    
    /**
     * Get semua transaksi (untuk history)
     * Diurutkan dari yang terbaru
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<Transaction>
    
    /**
     * Get semua transaksi sebagai Flow
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>
    
    /**
     * Get transaksi hari ini
     */
    @Query("SELECT * FROM transactions WHERE date >= :startOfDay ORDER BY date DESC")
    suspend fun getTodayTransactions(startOfDay: Long): List<Transaction>
    
    /**
     * Get total penjualan hari ini
     */
    @Query("SELECT SUM(totalAmount) FROM transactions WHERE date >= :startOfDay")
    suspend fun getTodayTotalSales(startOfDay: Long): Double?
    
    /**
     * Get jumlah transaksi hari ini
     */
    @Query("SELECT COUNT(*) FROM transactions WHERE date >= :startOfDay")
    suspend fun getTodayTransactionCount(startOfDay: Long): Int
    
    /**
     * Delete semua transaksi (untuk testing)
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
