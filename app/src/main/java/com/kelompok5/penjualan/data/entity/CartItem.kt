package com.kelompok5.penjualan.data.entity

/**
 * Data class CartItem (BUKAN Entity Room)
 * Digunakan untuk menyimpan item di keranjang belanja di ViewModel
 */
data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    /**
     * Hitung subtotal untuk item ini
     */
    fun getSubtotal(): Double = product.price * quantity
    
    /**
     * Increment quantity
     */
    fun incrementQuantity() {
        if (product.hasEnoughStock(quantity + 1)) {
            quantity++
        }
    }
    
    /**
     * Decrement quantity
     */
    fun decrementQuantity() {
        if (quantity > 1) {
            quantity--
        }
    }
    
    /**
     * Convert ke format JSON-friendly untuk disimpan di Transaction
     */
    fun toTransactionItem(): TransactionItem {
        return TransactionItem(
            productId = product.id,
            productName = product.name,
            price = product.price,
            quantity = quantity,
            subtotal = getSubtotal()
        )
    }
}

/**
 * Data class untuk menyimpan item transaksi dalam JSON
 */
data class TransactionItem(
    val productId: Int,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val subtotal: Double
)
