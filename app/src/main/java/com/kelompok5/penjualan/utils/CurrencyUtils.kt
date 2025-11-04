package com.kelompok5.penjualan.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility class untuk formatting mata uang Rupiah
 * 
 * Digunakan untuk:
 * - Format harga produk
 * - Format subtotal
 * - Format total transaksi
 * - Format kembalian
 */
object CurrencyUtils {
    
    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    
    /**
     * Format angka ke format Rupiah
     * 
     * @param amount Jumlah yang akan diformat
     * @return String format Rupiah (contoh: "Rp 15.000")
     * 
     * Example:
     * ```
     * formatRupiah(15000.0) // Output: "Rp 15.000"
     * formatRupiah(1500000.0) // Output: "Rp 1.500.000"
     * ```
     */
    fun formatRupiah(amount: Double): String {
        return rupiahFormat.format(amount)
    }
    
    /**
     * Format angka ke format Rupiah tanpa simbol Rp
     * Useful untuk input field atau display tertentu
     * 
     * @param amount Jumlah yang akan diformat
     * @return String format angka dengan separator (contoh: "15.000")
     */
    fun formatNumber(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        formatter.maximumFractionDigits = 0
        formatter.minimumFractionDigits = 0
        return formatter.format(amount)
    }
    
    /**
     * Parse string Rupiah ke Double
     * Berguna untuk parsing input user
     * 
     * @param rupiahString String format Rupiah atau angka
     * @return Double value atau 0.0 jika parsing gagal
     * 
     * Example:
     * ```
     * parseRupiah("Rp 15.000") // Output: 15000.0
     * parseRupiah("15000") // Output: 15000.0
     * parseRupiah("15.000") // Output: 15000.0
     * ```
     */
    fun parseRupiah(rupiahString: String): Double {
        return try {
            // Remove "Rp", spaces, and dots
            val cleanString = rupiahString
                .replace("Rp", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
            cleanString.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }
    
    /**
     * Validate apakah string adalah format Rupiah yang valid
     * 
     * @param rupiahString String yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    fun isValidRupiahFormat(rupiahString: String): Boolean {
        return try {
            parseRupiah(rupiahString) > 0
        } catch (e: Exception) {
            false
        }
    }
}
