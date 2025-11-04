package com.kelompok5.penjualan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kelompok5.penjualan.data.entity.CartItem
import com.kelompok5.penjualan.data.entity.Product
import com.kelompok5.penjualan.data.entity.Transaction
import com.kelompok5.penjualan.data.repository.ProductRepository
import com.kelompok5.penjualan.data.repository.TransactionRepository
import kotlinx.coroutines.launch

/**
 * MainViewModel - Core business logic untuk aplikasi kasir
 * 
 * Responsibilities:
 * - Manage product list dari database
 * - Manage cart state (keranjang belanja)
 * - Calculate subtotal otomatis
 * - Handle checkout process dengan stock update
 * - Search/filter products
 * 
 * MVVM Pattern:
 * View (Activity/Fragment) <- observe <- ViewModel <- Repository <- Database
 */
class MainViewModel(
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    // Gson untuk JSON serialization
    private val gson = Gson()
    
    // ============================================
    // LiveData - Reactive State Management
    // ============================================
    
    /**
     * List produk dari database
     * Observe ini di Activity untuk update RecyclerView
     */
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    
    /**
     * List item di keranjang belanja
     * Observe ini untuk update cart RecyclerView
     */
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems
    
    /**
     * Total harga keranjang (auto-calculate dari cartItems)
     */
    private val _subtotal = MutableLiveData<Double>(0.0)
    val subtotal: LiveData<Double> = _subtotal
    
    /**
     * Loading state
     */
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    /**
     * Error message
     */
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Checkout success dengan transaction ID
     */
    private val _checkoutSuccess = MutableLiveData<Long?>()
    val checkoutSuccess: LiveData<Long?> = _checkoutSuccess
    
    // ============================================
    // Initialization
    // ============================================
    
    init {
        // Load products saat ViewModel pertama kali dibuat
        loadProducts()
    }
    
    // ============================================
    // Product Operations
    // ============================================
    
    /**
     * Load semua produk dari database
     */
    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val productList = productRepository.getAllProducts()
                _products.value = productList
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Search products by name
     * 
     * @param query Search query (nama produk)
     */
    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val productList = if (query.isBlank()) {
                    productRepository.getAllProducts()
                } else {
                    productRepository.searchProducts(query)
                }
                _products.value = productList
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mencari produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // ============================================
    // Cart Operations
    // ============================================
    
    /**
     * Add produk ke cart atau increment quantity jika sudah ada
     * 
     * @param product Produk yang akan ditambahkan
     */
    fun addToCart(product: Product) {
        val currentCart = _cartItems.value?.toMutableList() ?: mutableListOf()
        
        // Check apakah produk sudah ada di cart
        val existingItem = currentCart.find { it.product.id == product.id }
        
        if (existingItem != null) {
            // Produk sudah ada, increment quantity
            if (existingItem.product.hasEnoughStock(existingItem.quantity + 1)) {
                existingItem.quantity++
            } else {
                _errorMessage.value = "Stock ${product.name} tidak cukup"
                return
            }
        } else {
            // Produk belum ada, tambahkan ke cart
            if (product.stock > 0) {
                currentCart.add(CartItem(product, 1))
            } else {
                _errorMessage.value = "Stock ${product.name} habis"
                return
            }
        }
        
        // Update cart dan recalculate subtotal
        _cartItems.value = currentCart
        calculateSubtotal()
    }
    
    /**
     * Remove produk dari cart
     * 
     * @param productId ID produk yang akan dihapus
     */
    fun removeFromCart(productId: Int) {
        val currentCart = _cartItems.value?.toMutableList() ?: return
        currentCart.removeAll { it.product.id == productId }
        _cartItems.value = currentCart
        calculateSubtotal()
    }
    
    /**
     * Update quantity item di cart
     * 
     * @param productId ID produk
     * @param newQuantity Quantity baru
     */
    fun updateQuantity(productId: Int, newQuantity: Int) {
        val currentCart = _cartItems.value?.toMutableList() ?: return
        val item = currentCart.find { it.product.id == productId } ?: return
        
        when {
            newQuantity <= 0 -> {
                // Quantity 0 atau negatif, remove from cart
                removeFromCart(productId)
            }
            item.product.hasEnoughStock(newQuantity) -> {
                // Stock cukup, update quantity
                item.quantity = newQuantity
                _cartItems.value = currentCart
                calculateSubtotal()
            }
            else -> {
                // Stock tidak cukup
                _errorMessage.value = "Stock ${item.product.name} tidak cukup (tersedia: ${item.product.stock})"
            }
        }
    }
    
    /**
     * Increment quantity item di cart
     * 
     * @param productId ID produk
     */
    fun incrementQuantity(productId: Int) {
        val currentCart = _cartItems.value ?: return
        val item = currentCart.find { it.product.id == productId } ?: return
        updateQuantity(productId, item.quantity + 1)
    }
    
    /**
     * Decrement quantity item di cart
     * 
     * @param productId ID produk
     */
    fun decrementQuantity(productId: Int) {
        val currentCart = _cartItems.value ?: return
        val item = currentCart.find { it.product.id == productId } ?: return
        updateQuantity(productId, item.quantity - 1)
    }
    
    /**
     * Clear semua item dari cart
     */
    fun clearCart() {
        _cartItems.value = emptyList()
        _subtotal.value = 0.0
    }
    
    /**
     * Calculate subtotal dari semua item di cart
     */
    private fun calculateSubtotal() {
        val total = _cartItems.value?.sumOf { it.getSubtotal() } ?: 0.0
        _subtotal.value = total
    }
    
    // ============================================
    // Checkout Operations (KRUSIAL!)
    // ============================================
    
    /**
     * Process checkout transaksi
     * 
     * Flow:
     * 1. Validasi cart tidak kosong
     * 2. Validasi uang yang dibayar >= subtotal
     * 3. Update stock untuk setiap item
     * 4. Convert cart to JSON
     * 5. Save transaction ke database
     * 6. Clear cart
     * 7. Return transaction ID
     * 
     * @param moneyPaid Uang yang dibayarkan customer
     * @return Result<Long> - Success dengan transaction ID atau Failure dengan error
     */
    fun checkout(moneyPaid: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Validasi 1: Cart tidak boleh kosong
                val currentCart = _cartItems.value
                if (currentCart.isNullOrEmpty()) {
                    _errorMessage.value = "Keranjang belanja kosong"
                    _isLoading.value = false
                    return@launch
                }
                
                // Validasi 2: Uang harus cukup
                val totalAmount = _subtotal.value ?: 0.0
                if (moneyPaid < totalAmount) {
                    _errorMessage.value = "Uang tidak cukup. Total: Rp ${totalAmount.toInt()}"
                    _isLoading.value = false
                    return@launch
                }
                
                // Validasi 3: Check stock availability untuk semua items
                for (cartItem in currentCart) {
                    val product = productRepository.getProductById(cartItem.product.id)
                    if (product == null || product.stock < cartItem.quantity) {
                        _errorMessage.value = "Stock ${cartItem.product.name} tidak mencukupi"
                        _isLoading.value = false
                        return@launch
                    }
                }
                
                // Step 3: Update stock untuk setiap item
                for (cartItem in currentCart) {
                    val product = cartItem.product
                    val newStock = product.stock - cartItem.quantity
                    productRepository.updateStock(product.id, newStock)
                }
                
                // Step 4: Convert cart to JSON string
                val transactionItems = currentCart.map { it.toTransactionItem() }
                val itemsJson = gson.toJson(transactionItems)
                
                // Step 5: Create dan save transaction
                val transaction = Transaction.create(
                    totalAmount = totalAmount,
                    itemsJson = itemsJson,
                    moneyPaid = moneyPaid
                )
                val transactionId = transactionRepository.insertTransaction(transaction)
                
                // Step 6: Clear cart
                clearCart()
                
                // Step 7: Reload products (untuk update stock di UI)
                loadProducts()
                
                // Notify success
                _checkoutSuccess.value = transactionId
                _errorMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Checkout gagal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reset checkout success state
     * Call setelah navigate ke Receipt Activity
     */
    fun resetCheckoutSuccess() {
        _checkoutSuccess.value = null
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    // ============================================
    // Helper Methods
    // ============================================
    
    /**
     * Get cart item count (total quantity semua items)
     */
    fun getCartItemCount(): Int {
        return _cartItems.value?.sumOf { it.quantity } ?: 0
    }
    
    /**
     * Check apakah cart kosong
     */
    fun isCartEmpty(): Boolean {
        return _cartItems.value.isNullOrEmpty()
    }
}
