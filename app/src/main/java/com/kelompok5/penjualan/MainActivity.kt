package com.kelompok5.penjualan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.kelompok5.penjualan.data.database.AppDatabase
import com.kelompok5.penjualan.data.repository.ProductRepository
import com.kelompok5.penjualan.data.repository.TransactionRepository
import com.kelompok5.penjualan.ui.adapter.CartAdapter
import com.kelompok5.penjualan.ui.adapter.ProductAdapter
import com.kelompok5.penjualan.utils.CurrencyUtils
import com.kelompok5.penjualan.viewmodel.MainViewModel
import com.kelompok5.penjualan.viewmodel.MainViewModelFactory

/**
 * MainActivity - Layar Utama Kasir
 * 
 * Features:
 * - Tampil daftar produk (2 kolom layout)
 * - Tambah ke keranjang
 * - Manage quantity
 * - Search/filter produk
 * - Checkout dengan payment dialog
 * - Navigate ke Receipt Activity
 * 
 * Architecture: MVVM Pattern
 */
class MainActivity : AppCompatActivity() {

    // ViewModel
    private lateinit var viewModel: MainViewModel
    
    // RecyclerView & Adapters
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    
    // Views
    private lateinit var searchView: SearchView
    private lateinit var textSubtotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnClearCart: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyProductsText: TextView
    private lateinit var emptyCartText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize ViewModel
        initViewModel()
        
        // Initialize Views
        initViews()
        
        // Setup RecyclerViews
        setupRecyclerViews()
        
        // Setup Observers
        setupObservers()
        
        // Setup Listeners
        setupListeners()
    }
    
    /**
     * Initialize ViewModel dengan Factory Pattern
     */
    private fun initViewModel() {
        // Get database instance
        val database = AppDatabase.getInstance(applicationContext)
        
        // Create repositories
        val productRepository = ProductRepository(database.productDao())
        val transactionRepository = TransactionRepository(database.transactionDao())
        
        // Create ViewModel with factory
        val factory = MainViewModelFactory(productRepository, transactionRepository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }
    
    /**
     * Initialize semua views
     */
    private fun initViews() {
        searchView = findViewById(R.id.searchView)
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)
        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        textSubtotal = findViewById(R.id.textSubtotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnClearCart = findViewById(R.id.btnClearCart)
        progressBar = findViewById(R.id.progressBar)
        emptyProductsText = findViewById(R.id.emptyProductsText)
        emptyCartText = findViewById(R.id.emptyCartText)
    }
    
    /**
     * Setup RecyclerViews dengan Adapters
     */
    private fun setupRecyclerViews() {
        // Product RecyclerView
        productAdapter = ProductAdapter { product ->
            // Handle add to cart
            viewModel.addToCart(product)
            Toast.makeText(this, "✓ ${product.name} ditambahkan", Toast.LENGTH_SHORT).show()
        }
        
        recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productAdapter
            setHasFixedSize(true)
        }
        
        // Cart RecyclerView
        cartAdapter = CartAdapter(
            onIncrementQuantity = { productId ->
                viewModel.incrementQuantity(productId)
            },
            onDecrementQuantity = { productId ->
                viewModel.decrementQuantity(productId)
            },
            onRemoveItem = { productId ->
                viewModel.removeFromCart(productId)
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
            }
        )
        
        recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cartAdapter
            setHasFixedSize(true)
        }
    }
    
    /**
     * Setup LiveData Observers
     */
    private fun setupObservers() {
        // Observe products list
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            
            // Show/hide empty state
            if (products.isEmpty()) {
                emptyProductsText.visibility = View.VISIBLE
                recyclerViewProducts.visibility = View.GONE
            } else {
                emptyProductsText.visibility = View.GONE
                recyclerViewProducts.visibility = View.VISIBLE
            }
        }
        
        // Observe cart items
        viewModel.cartItems.observe(this) { cartItems ->
            cartAdapter.submitList(cartItems)
            
            // Show/hide empty state
            if (cartItems.isEmpty()) {
                emptyCartText.visibility = View.VISIBLE
                recyclerViewCart.visibility = View.GONE
            } else {
                emptyCartText.visibility = View.GONE
                recyclerViewCart.visibility = View.VISIBLE
            }
            
            // Enable/disable buttons based on cart
            val isEmpty = cartItems.isEmpty()
            btnCheckout.isEnabled = !isEmpty
            btnClearCart.isEnabled = !isEmpty
            btnCheckout.alpha = if (isEmpty) 0.5f else 1.0f
            btnClearCart.alpha = if (isEmpty) 0.5f else 1.0f
        }
        
        // Observe subtotal
        viewModel.subtotal.observe(this) { subtotal ->
            textSubtotal.text = CurrencyUtils.formatRupiah(subtotal)
        }
        
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observe checkout success
        viewModel.checkoutSuccess.observe(this) { transactionId ->
            transactionId?.let {
                // TODO: Navigate to ReceiptActivity with transaction ID
                Toast.makeText(this, "✓ Transaksi berhasil! ID: $it", Toast.LENGTH_LONG).show()
                viewModel.resetCheckoutSuccess()
                
                // Note: ReceiptActivity akan dibuat di Phase 5
                // Intent akan ditambahkan nanti
            }
        }
    }
    
    /**
     * Setup semua click listeners
     */
    private fun setupListeners() {
        // SearchView listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchProducts(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.searchProducts(it) }
                return true
            }
        })
        
        // Clear cart button
        btnClearCart.setOnClickListener {
            showClearCartConfirmation()
        }
        
        // Checkout button
        btnCheckout.setOnClickListener {
            if (!viewModel.isCartEmpty()) {
                showPaymentDialog()
            } else {
                Toast.makeText(this, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Show confirmation dialog untuk clear cart
     */
    private fun showClearCartConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Kosongkan Keranjang?")
            .setMessage("Semua item di keranjang akan dihapus. Lanjutkan?")
            .setPositiveButton("Ya") { _, _ ->
                viewModel.clearCart()
                Toast.makeText(this, "Keranjang dikosongkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    /**
     * Show payment dialog untuk checkout
     */
    private fun showPaymentDialog() {
        // Inflate custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment, null)
        
        // Get views from dialog
        val textTotalAmount: TextView = dialogView.findViewById(R.id.textTotalAmount)
        val editMoneyPaid: TextInputEditText = dialogView.findViewById(R.id.editMoneyPaid)
        val textChangeAmount: TextView = dialogView.findViewById(R.id.textChangeAmount)
        val textErrorMessage: TextView = dialogView.findViewById(R.id.textErrorMessage)
        val btnQuick10k: Button = dialogView.findViewById(R.id.btnQuick10k)
        val btnQuick20k: Button = dialogView.findViewById(R.id.btnQuick20k)
        val btnQuick50k: Button = dialogView.findViewById(R.id.btnQuick50k)
        val btnQuick100k: Button = dialogView.findViewById(R.id.btnQuick100k)
        val btnExactAmount: Button = dialogView.findViewById(R.id.btnExactAmount)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
        val btnConfirmPayment: Button = dialogView.findViewById(R.id.btnConfirmPayment)
        
        // Get total amount from ViewModel
        val totalAmount = viewModel.subtotal.value ?: 0.0
        textTotalAmount.text = CurrencyUtils.formatRupiah(totalAmount)
        
        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        // Function to calculate change
        fun calculateChange() {
            val moneyPaidText = editMoneyPaid.text.toString()
            if (moneyPaidText.isNotEmpty()) {
                val moneyPaid = moneyPaidText.toDoubleOrNull() ?: 0.0
                val change = moneyPaid - totalAmount
                
                if (change >= 0) {
                    textChangeAmount.text = CurrencyUtils.formatRupiah(change)
                    textChangeAmount.setTextColor(getColor(R.color.oasis_green))
                    textErrorMessage.visibility = View.GONE
                    btnConfirmPayment.isEnabled = true
                } else {
                    textChangeAmount.text = CurrencyUtils.formatRupiah(0.0)
                    textChangeAmount.setTextColor(getColor(R.color.red_error))
                    textErrorMessage.visibility = View.VISIBLE
                    btnConfirmPayment.isEnabled = false
                }
            } else {
                textChangeAmount.text = CurrencyUtils.formatRupiah(0.0)
                textErrorMessage.visibility = View.GONE
            }
        }
        
        // Text change listener untuk real-time calculation
        editMoneyPaid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateChange()
            }
        })
        
        // Quick amount buttons
        btnQuick10k.setOnClickListener {
            editMoneyPaid.setText("10000")
            calculateChange()
        }
        
        btnQuick20k.setOnClickListener {
            editMoneyPaid.setText("20000")
            calculateChange()
        }
        
        btnQuick50k.setOnClickListener {
            editMoneyPaid.setText("50000")
            calculateChange()
        }
        
        btnQuick100k.setOnClickListener {
            editMoneyPaid.setText("100000")
            calculateChange()
        }
        
        btnExactAmount.setOnClickListener {
            editMoneyPaid.setText(totalAmount.toInt().toString())
            calculateChange()
        }
        
        // Cancel button
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        // Confirm payment button
        btnConfirmPayment.setOnClickListener {
            val moneyPaidText = editMoneyPaid.text.toString()
            if (moneyPaidText.isNotEmpty()) {
                val moneyPaid = moneyPaidText.toDoubleOrNull() ?: 0.0
                
                if (moneyPaid >= totalAmount) {
                    // Process checkout
                    viewModel.checkout(moneyPaid)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Uang tidak cukup!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Masukkan jumlah uang", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Show dialog
        dialog.show()
    }
}
