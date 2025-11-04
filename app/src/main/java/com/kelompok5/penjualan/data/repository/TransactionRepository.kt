package com.kelompok5.penjualan.data.repository

import com.kelompok5.penjualan.data.dao.TransactionDao
import com.kelompok5.penjualan.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk Transaction
 * Abstraksi layer antara ViewModel dan Data Source (Room)
 */
class TransactionRepository(private val transactionDao: TransactionDao) {
    
    /**
     * Insert transaksi baru
     * Return: ID transaksi yang baru dibuat
     */
    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
    }
    
    /**
     * Get transaksi by ID
     */
    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDao.getTransactionById(id)
    }
    
    /**
     * Get transaksi terakhir
     */
    suspend fun getLastTransaction(): Transaction? {
        return transactionDao.getLastTransaction()
    }
    
    /**
     * Get semua transaksi
     */
    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
    
    /**
     * Get semua transaksi sebagai Flow
     */
    fun getAllTransactionsFlow(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsFlow()
    }
    
    /**
     * Get transaksi hari ini
     */
    suspend fun getTodayTransactions(): List<Transaction> {
        val startOfDay = getStartOfDayTimestamp()
        return transactionDao.getTodayTransactions(startOfDay)
    }
    
    /**
     * Get total penjualan hari ini
     */
    suspend fun getTodayTotalSales(): Double {
        val startOfDay = getStartOfDayTimestamp()
        return transactionDao.getTodayTotalSales(startOfDay) ?: 0.0
    }
    
    /**
     * Get jumlah transaksi hari ini
     */
    suspend fun getTodayTransactionCount(): Int {
        val startOfDay = getStartOfDayTimestamp()
        return transactionDao.getTodayTransactionCount(startOfDay)
    }
    
    /**
     * Helper: Get timestamp untuk awal hari ini (00:00:00)
     */
    private fun getStartOfDayTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
