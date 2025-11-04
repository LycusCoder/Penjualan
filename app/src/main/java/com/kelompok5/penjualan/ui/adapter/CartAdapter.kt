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
import com.kelompok5.penjualan.data.entity.CartItem
import com.kelompok5.penjualan.utils.CurrencyUtils

/**
 * RecyclerView Adapter untuk Cart Items
 * Menampilkan daftar item dalam keranjang dengan kontrol quantity
 */
class CartAdapter(
    private val onIncrementQuantity: (Int) -> Unit,
    private val onDecrementQuantity: (Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view, onIncrementQuantity, onDecrementQuantity, onRemoveItem)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder untuk Cart Item
     */
    class CartViewHolder(
        itemView: View,
        private val onIncrementQuantity: (Int) -> Unit,
        private val onDecrementQuantity: (Int) -> Unit,
        private val onRemoveItem: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textCartProductName: TextView = itemView.findViewById(R.id.textCartProductName)
        private val textCartUnitPrice: TextView = itemView.findViewById(R.id.textCartUnitPrice)
        private val textQuantity: TextView = itemView.findViewById(R.id.textQuantity)
        private val textCartItemTotal: TextView = itemView.findViewById(R.id.textCartItemTotal)
        private val btnIncrement: Button = itemView.findViewById(R.id.btnIncrement)
        private val btnDecrement: Button = itemView.findViewById(R.id.btnDecrement)
        private val btnRemove: Button = itemView.findViewById(R.id.btnRemove)

        fun bind(cartItem: CartItem) {
            // Set product name
            textCartProductName.text = cartItem.product.name

            // Set unit price
            textCartUnitPrice.text = "@${CurrencyUtils.formatRupiah(cartItem.product.price)}"

            // Set quantity
            textQuantity.text = cartItem.quantity.toString()

            // Calculate and set item total using getSubtotal()
            val itemTotal = cartItem.getSubtotal()
            textCartItemTotal.text = CurrencyUtils.formatRupiah(itemTotal)

            // Increment button click listener
            btnIncrement.setOnClickListener {
                onIncrementQuantity(cartItem.product.id)
            }

            // Decrement button click listener
            btnDecrement.setOnClickListener {
                onDecrementQuantity(cartItem.product.id)
            }

            // Remove button click listener
            btnRemove.setOnClickListener {
                onRemoveItem(cartItem.product.id)
            }

            // Disable decrement if quantity is 1 (will be removed instead)
            btnDecrement.isEnabled = true
            if (cartItem.quantity == 1) {
                btnDecrement.alpha = 0.7f
            } else {
                btnDecrement.alpha = 1.0f
            }
        }
    }

    /**
     * DiffUtil Callback untuk efisiensi update RecyclerView
     */
    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}