# 📱 RENCANA APLIKASI QUICKSELL POS

## 📋 Informasi Proyek

**Nama Aplikasi**: QuickSell PoS (Point of Sale / Mini Kasir)  
**Mata Kuliah**: Pemrograman Bergerak - Ujian Tengah Semester  
**Kelompok**: 5

### 👥 Tim Pengembang
- **Ketua**: Muhammad Affif (Pembuat/Developer)
- **Anggota**:
  - Muhamad Fahren Andrean Rangkuti
  - Sigit Pratama
  - Djafar Ilya
  - Septina Asti Nabila
  - Afsani

---

## 🎯 Tujuan Aplikasi

Membuat aplikasi Point of Sale (PoS) sederhana untuk kasir yang dapat:
- Menampilkan daftar produk
- Menambahkan produk ke keranjang belanja
- Melakukan checkout transaksi
- Update stok produk otomatis
- Menyimpan riwayat transaksi
- Menampilkan struk digital

---

## 🛠️ Teknologi & Arsitektur

### Tech Stack
- **Bahasa**: Kotlin (100%)
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **UI Framework**: Jetpack Compose & XML (Hybrid)
- **Database**: Room Persistence Library
- **Async**: Kotlin Coroutines
- **Reactive**: LiveData / Flow

### Arsitektur: MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────┐
│           VIEW LAYER                │
│   (Activity/Fragment/Compose)       │
│   - MainActivity                    │
│   - ReceiptActivity                 │
└──────────────┬──────────────────────┘
               │ observe
               ↓
┌─────────────────────────────────────┐
│        VIEWMODEL LAYER              │
│   - MainViewModel                   │
│   - Manage Cart State               │
│   - Business Logic                  │
└──────────────┬──────────────────────┘
               │ call
               ↓
┌─────────────────────────────────────┐
│       REPOSITORY LAYER              │
│   - ProductRepository               │
│   - TransactionRepository           │
└──────────────┬──────────────────────┘
               │ access
               ↓
┌─────────────────────────────────────┐
│      DATA SOURCE LAYER              │
│   - Room Database                   │
│   - DAO (Data Access Object)        │
│   - Entity (Data Classes)           │
└─────────────────────────────────────┘
```

---

## 📊 Database Schema

### 1. Product Entity

```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int
)
```

**Kolom**:
- `id`: Primary Key, auto-increment
- `name`: Nama produk
- `price`: Harga produk (Double)
- `stock`: Jumlah stok (harus di-update saat checkout)

### 2. Transaction Entity

```kotlin
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val date: Long,              // timestamp
    val totalAmount: Double,
    val itemsDetailJson: String  // JSON string
)
```

**Kolom**:
- `id`: Primary Key, auto-increment
- `date`: Waktu transaksi (System.currentTimeMillis())
- `totalAmount`: Total harga transaksi
- `itemsDetailJson`: Detail items dalam format JSON

---

## 🔌 Data Access Objects (DAO)

### ProductDao

```kotlin
@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product)
    
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
    
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int)
}
```

### TransactionDao

```kotlin
@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long
    
    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 1")
    suspend fun getLastTransaction(): Transaction?
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<Transaction>
}
```

---

## 🎨 User Interface Design

### MainActivity (Layar Kasir)

Layout: **2 Kolom (Split Screen)**

```
┌─────────────────────────────────────────────┐
│  QuickSell PoS          [Search Box]        │
├──────────────────┬──────────────────────────┤
│  DAFTAR PRODUK   │   KERANJANG BELANJA      │
│                  │                          │
│ ┌──────────────┐ │ ┌────────────────────┐  │
│ │ 🛒 Indomie   │ │ │ Indomie      x2    │  │
│ │ Rp 3,000     │ │ │ [−] 2 [+]          │  │
│ └──────────────┘ │ │ Rp 6,000           │  │
│                  │ └────────────────────┘  │
│ ┌──────────────┐ │                          │
│ │ 💧 Aqua      │ │ ┌────────────────────┐  │
│ │ Rp 5,000     │ │ │ Aqua         x1    │  │
│ └──────────────┘ │ │ [−] 1 [+]          │  │
│                  │ │ Rp 5,000           │  │
│ ┌──────────────┐ │ └────────────────────┘  │
│ │ 🍞 Roti      │ │                          │
│ │ Rp 10,000    │ │ ──────────────────────  │
│ └──────────────┘ │ Subtotal: Rp 11,000    │
│                  │                          │
│                  │ [    CHECKOUT    ]       │
└──────────────────┴──────────────────────────┘
```

**Komponen**:
- RecyclerView (Kiri): Daftar produk dengan SearchView
- RecyclerView (Kanan): Keranjang dengan tombol +/-
- Bottom Section: Subtotal & Checkout button

### ReceiptActivity (Layar Struk)

Layout: **Thermal Receipt Style**

```
┌─────────────────────────────────────┐
│     ════════════════════════        │
│        QUICKSELL POS                │
│     ════════════════════════        │
│                                     │
│  Tanggal: 15/01/2025                │
│  Waktu  : 14:30:25                  │
│  No. Trx: #00123                    │
│                                     │
│  ─────────────────────────────────  │
│  ITEM              QTY      HARGA   │
│  ─────────────────────────────────  │
│  Indomie            2      Rp 6,000 │
│  Aqua               1      Rp 5,000 │
│  ─────────────────────────────────  │
│                                     │
│  TOTAL                   Rp 11,000  │
│  BAYAR                   Rp 20,000  │
│  KEMBALI                  Rp 9,000  │
│                                     │
│  ═════════════════════════════════  │
│    TERIMA KASIH SUDAH BERBELANJA    │
│  ═════════════════════════════════  │
│                                     │
│  Kelompok 5 - Muhammad Affif (Ketua)│
│  Pemrograman Bergerak - UTS         │
│                                     │
│  [   KEMBALI KE KASIR   ]           │
└─────────────────────────────────────┘
```

---

## 🔄 Alur Aplikasi (Flow)

### 1. Alur Menambah ke Keranjang

```
User tap Product
    ↓
Check if Product in Cart?
    ├─ YES → Increment quantity
    └─ NO  → Add new CartItem (qty=1)
    ↓
Update cartItems (LiveData)
    ↓
Recalculate subtotal
    ↓
Update UI (RecyclerView)
```

### 2. Alur Checkout (KRUSIAL!)

```
User tap CHECKOUT button
    ↓
Show Payment Dialog
    ↓
User input money paid
    ↓
Validate: moneyPaid >= totalAmount?
    ├─ NO  → Show error "Uang tidak cukup"
    └─ YES → Continue
        ↓
    FOR EACH item in Cart:
        ├─ Get current stock from DB
        ├─ Calculate: newStock = oldStock - quantity
        ├─ Update stock in Room DB
        └─ (using Coroutine)
        ↓
    Convert Cart to JSON string
        ↓
    Create Transaction object
        ↓
    Save Transaction to Room DB
        ↓
    Get Transaction ID
        ↓
    Clear Cart (cartItems)
        ↓
    Navigate to ReceiptActivity
        ↓
    Pass Transaction ID via Intent
```

### 3. Alur Tampil Struk

```
ReceiptActivity started
    ↓
Get Transaction ID from Intent
    ↓
Query Transaction from Room DB
    ↓
Parse itemsDetailJson to List
    ↓
Calculate change (kembalian)
    ↓
Display all data in thermal style
    ↓
User tap "Kembali ke Kasir"
    ↓
Finish Activity → back to MainActivity
```

---

## 📦 Dependencies yang Digunakan

```gradle
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// ViewModel & LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// RecyclerView
implementation("androidx.recyclerview:recyclerview:1.3.2")

// GSON (untuk JSON parsing)
implementation("com.google.code.gson:gson:2.10.1")

// Material Design
implementation("com.google.android.material:material:1.11.0")
```

---

## ✅ Fitur Utama

### Wajib Ada (Core Features)
1. ✅ **Product List**: Tampil semua produk dari database
2. ✅ **Add to Cart**: Tap produk untuk masuk keranjang
3. ✅ **Quantity Control**: Tombol +/- untuk ubah jumlah
4. ✅ **Search/Filter**: Cari produk berdasarkan nama
5. ✅ **Checkout**: Proses pembayaran dengan validasi
6. ✅ **Stock Update**: Otomatis kurangi stok setelah checkout
7. ✅ **Save Transaction**: Simpan ke database
8. ✅ **Receipt Display**: Tampil struk thermal-style

### Bonus Features (Nice to Have)
- 📊 History Transaksi (list semua transaksi)
- 🔄 Reset Cart (kosongkan keranjang)
- 📈 Dashboard Statistics (total penjualan, dll)
- 🖨️ Share/Export Struk (PDF/Image)
- ⚙️ Manage Products (CRUD)

---

## 🧪 Testing Checklist

### Functional Testing
- [ ] Product tampil dari database
- [ ] Add to cart berhasil
- [ ] Quantity increment/decrement bekerja
- [ ] Search filter produk bekerja
- [ ] Subtotal calculation correct
- [ ] Checkout validation (uang kurang)
- [ ] Stock update after checkout
- [ ] Transaction saved to DB
- [ ] Receipt display correct data
- [ ] Back to kasir dari receipt

### Edge Cases
- [ ] Checkout dengan cart kosong → disabled
- [ ] Update quantity ke 0 → remove from cart
- [ ] Stock habis → disable add to cart
- [ ] Multiple rapid taps → debounce
- [ ] Rotation screen → state preserved
- [ ] Database migration (jika ada update schema)

---

## 📅 Timeline Implementasi

### Phase 1: Setup & Database (Hari 1)
- ✅ Setup dependencies (Room, ViewModel, dll)
- ✅ Buat Entity: Product & Transaction
- ✅ Buat DAO: ProductDao & TransactionDao
- ✅ Buat AppDatabase
- ✅ Seed sample products

### Phase 2: Repository & ViewModel (Hari 1-2)
- [ ] Buat ProductRepository & TransactionRepository
- [ ] Buat MainViewModel
- [ ] Implement CartItem data class
- [ ] Implement add/remove/update cart logic
- [ ] Implement checkout logic dengan coroutine

### Phase 3: UI Components (Hari 2-3)
- [ ] Layout MainActivity (2 kolom)
- [ ] RecyclerView Adapter untuk Product List
- [ ] RecyclerView Adapter untuk Cart
- [ ] Implement SearchView
- [ ] Design payment dialog

### Phase 4: Main Activity Implementation (Hari 3-4)
- [ ] Observe products dari ViewModel
- [ ] Handle product click (add to cart)
- [ ] Handle quantity change
- [ ] Handle search/filter
- [ ] Handle checkout button
- [ ] Show payment dialog
- [ ] Navigate to Receipt

### Phase 5: Receipt Activity (Hari 4)
- [ ] Layout thermal receipt style
- [ ] Receive Transaction ID
- [ ] Query transaction dari DB
- [ ] Parse JSON items
- [ ] Display semua data
- [ ] Handle back button

### Phase 6: Polish & Testing (Hari 5)
- [ ] Testing seluruh flow
- [ ] Fix bugs
- [ ] Improve UI/UX
- [ ] Add loading states
- [ ] Add error handling
- [ ] Code cleanup & documentation

---

## 📝 Catatan Penting

### Best Practices
1. **Gunakan Coroutines**: Semua operasi database harus `suspend`
2. **LiveData untuk UI**: Gunakan `observe()` di Activity
3. **Validation**: Selalu validasi input user
4. **Error Handling**: Try-catch untuk database operations
5. **Memory Leaks**: Gunakan `viewModelScope` untuk coroutines
6. **JSON Parsing**: Gunakan Gson untuk itemsDetailJson
7. **Theme Consistency**: Ikuti Material Design guidelines

### Poin-Poin Krusial untuk UTS
1. ✅ **Room Database** properly implemented (Entity + DAO)
2. ✅ **MVVM Architecture** clearly separated
3. ✅ **Coroutines** untuk async operations
4. ✅ **RecyclerView** dengan Adapter pattern
5. ✅ **Stock Update** logic must work correctly
6. ✅ **JSON Handling** untuk transaction items
7. ✅ **UI/UX** clean dan user-friendly

---

## 🎓 Learning Objectives

Aplikasi ini mendemonstrasikan pemahaman:
- ✅ Kotlin programming fundamentals
- ✅ Android Activity lifecycle
- ✅ MVVM architectural pattern
- ✅ Room Database (ORM)
- ✅ RecyclerView & Adapters
- ✅ Kotlin Coroutines (async programming)
- ✅ LiveData (reactive programming)
- ✅ Material Design principles
- ✅ JSON serialization/deserialization
- ✅ Navigation between Activities
- ✅ State management
- ✅ Business logic implementation

---

**Dibuat oleh**: Kelompok 5 - Muhammad Affif (Ketua)  
**Tanggal**: Januari 2025  
**Versi**: 1.0  
**Status**: 🚧 In Development
