package com.kelompok5.penjualan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kelompok5.penjualan.data.repository.ProductRepository
import com.kelompok5.penjualan.data.repository.TransactionRepository

/**
 * Factory untuk create MainViewModel dengan dependency injection
 * 
 * ViewModel membutuhkan repositories yang tidak bisa dipassing langsung
 * di constructor, jadi kita pakai Factory pattern untuk inject dependencies
 * 
 * Usage di Activity:
 * ```
 * val viewModelFactory = MainViewModelFactory(productRepository, transactionRepository)
 * val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
 * ```
 */
class MainViewModelFactory(
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(productRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
