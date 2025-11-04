package com.kelompok5.penjualan.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

/**
 * Utility class untuk formatting tanggal dan waktu
 * 
 * Digunakan untuk:
 * - Format tanggal transaksi
 * - Format waktu transaksi
 * - Display di receipt
 * - Display di history
 */
object DateUtils {
    
    // Format patterns
    private const val PATTERN_DATE = "dd/MM/yyyy"
    private const val PATTERN_TIME = "HH:mm:ss"
    private const val PATTERN_DATETIME = "dd/MM/yyyy HH:mm:ss"
    private const val PATTERN_DATE_LONG = "EEEE, dd MMMM yyyy"
    private const val PATTERN_TIME_SHORT = "HH:mm"
    
    // Locale Indonesia
    private val localeIndonesia = Locale("in", "ID")
    
    /**
     * Format timestamp ke format tanggal
     * 
     * @param timestamp Timestamp in milliseconds
     * @return String format tanggal (contoh: "15/01/2025")
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(PATTERN_DATE, localeIndonesia)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp ke format waktu
     * 
     * @param timestamp Timestamp in milliseconds
     * @return String format waktu (contoh: "14:30:25")
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(PATTERN_TIME, localeIndonesia)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp ke format tanggal dan waktu
     * 
     * @param timestamp Timestamp in milliseconds
     * @return String format datetime (contoh: "15/01/2025 14:30:25")
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(PATTERN_DATETIME, localeIndonesia)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp ke format tanggal panjang
     * 
     * @param timestamp Timestamp in milliseconds
     * @return String format tanggal panjang (contoh: "Senin, 15 Januari 2025")
     */
    fun formatDateLong(timestamp: Long): String {
        val sdf = SimpleDateFormat(PATTERN_DATE_LONG, localeIndonesia)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format timestamp ke format waktu pendek
     * 
     * @param timestamp Timestamp in milliseconds
     * @return String format waktu pendek (contoh: "14:30")
     */
    fun formatTimeShort(timestamp: Long): String {
        val sdf = SimpleDateFormat(PATTERN_TIME_SHORT, localeIndonesia)
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Get current timestamp
     * 
     * @return Current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Get timestamp untuk awal hari ini (00:00:00)
     * Berguna untuk query transaksi hari ini
     * 
     * @return Timestamp awal hari ini
     */
    fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Get timestamp untuk akhir hari ini (23:59:59)
     * 
     * @return Timestamp akhir hari ini
     */
    fun getEndOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    /**
     * Check apakah timestamp adalah hari ini
     * 
     * @param timestamp Timestamp yang akan dicek
     * @return true jika hari ini, false jika bukan
     */
    fun isToday(timestamp: Long): Boolean {
        val startOfDay = getStartOfDayTimestamp()
        val endOfDay = getEndOfDayTimestamp()
        return timestamp in startOfDay..endOfDay
    }
    
    /**
     * Get relative time string (contoh: "2 jam yang lalu")
     * 
     * @param timestamp Timestamp yang akan diformat
     * @return String relative time
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = getCurrentTimestamp()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Baru saja" // < 1 menit
            diff < 3_600_000 -> "${diff / 60_000} menit yang lalu" // < 1 jam
            diff < 86_400_000 -> "${diff / 3_600_000} jam yang lalu" // < 1 hari
            diff < 604_800_000 -> "${diff / 86_400_000} hari yang lalu" // < 1 minggu
            else -> formatDate(timestamp) // > 1 minggu, tampilkan tanggal
        }
    }
}
