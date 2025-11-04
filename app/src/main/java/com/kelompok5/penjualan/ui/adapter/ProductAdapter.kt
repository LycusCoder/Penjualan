package com.kelompok5.penjualan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kelompok5.penjualan.R
import com.kelompok5.penjualan.data.entity.Product
import com.kelompok5.penjualan.utils.CurrencyUtils

/**
 * RecyclerView Adapter untuk Product List
 * Menampilkan daftar produk yang bisa ditambahkan ke keranjang
 */
class ProductAdapter(
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view, onAddToCart)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder untuk Product Item
     */
    class ProductViewHolder(
        itemView: View,
        private val onAddToCart: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        private val textProductPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        private val textProductStock: TextView = itemView.findViewById(R.id.textProductStock)
        private val btnAddProduct: Button = itemView.findViewById(R.id.btnAddProduct)
        private val productIcon: TextView = itemView.findViewById(R.id.productIcon)

        fun bind(product: Product) {
            // Set product name
            textProductName.text = product.name

            // Set product price with currency formatting
            textProductPrice.text = CurrencyUtils.formatRupiah(product.price)

            // Set stock info
            textProductStock.text = "Stok: ${product.stock}"

            // Set icon based on product name (simple emoji mapping)
            productIcon.text = getProductIcon(product.name)

            // Enable/disable button based on stock
            btnAddProduct.isEnabled = product.stock > 0
            if (product.stock <= 0) {
                btnAddProduct.text = "Habis"
                btnAddProduct.alpha = 0.5f
                textProductStock.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                btnAddProduct.text = "+ Tambah"
                btnAddProduct.alpha = 1.0f
                textProductStock.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
            }

            // Set click listener for add to cart
            btnAddProduct.setOnClickListener {
                if (product.stock > 0) {
                    onAddToCart(product)
                }
            }

            // Optional: Make entire card clickable
            itemView.setOnClickListener {
                if (product.stock > 0) {
                    onAddToCart(product)
                }
            }
        }

        /**
         * Simple emoji mapping based on product name
         */
        private fun getProductIcon(productName: String): String {
            return when {
                productName.contains("Indomie", ignoreCase = true) -> "🍜"
                productName.contains("Aqua", ignoreCase = true) || 
                productName.contains("Air", ignoreCase = true) -> "💧"
                productName.contains("Roti", ignoreCase = true) -> "🍞"
                productName.contains("Susu", ignoreCase = true) || 
                productName.contains("Milk", ignoreCase = true) -> "🥛"
                productName.contains("Teh", ignoreCase = true) || 
                productName.contains("Tea", ignoreCase = true) -> "🍵"
                productName.contains("Kopi", ignoreCase = true) || 
                productName.contains("Coffee", ignoreCase = true) -> "☕"
                productName.contains("Snack", ignoreCase = true) || 
                productName.contains("Keripik", ignoreCase = true) -> "🍿"
                productName.contains("Coklat", ignoreCase = true) || 
                productName.contains("Chocolate", ignoreCase = true) -> "🍫"
                productName.contains("Permen", ignoreCase = true) || 
                productName.contains("Candy", ignoreCase = true) -> "🍬"
                productName.contains("Biskuit", ignoreCase = true) || 
                productName.contains("Biscuit", ignoreCase = true) -> "🍪"
                productName.contains("Mie", ignoreCase = true) -> "🍝"
                productName.contains("Nasi", ignoreCase = true) || 
                productName.contains("Rice", ignoreCase = true) -> "🍚"
                productName.contains("Sabun", ignoreCase = true) || 
                productName.contains("Soap", ignoreCase = true) -> "🧼"
                productName.contains("Shampo", ignoreCase = true) || 
                productName.contains("Shampoo", ignoreCase = true) -> "🧴"
                productName.contains("Pasta", ignoreCase = true) -> "🪥"
                productName.contains("Tissue", ignoreCase = true) || 
                productName.contains("Tisu", ignoreCase = true) -> "🧻"
                else -> "🛒"
            }
        }
    }

    /**
     * DiffUtil Callback untuk efisiensi update RecyclerView
     */
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}