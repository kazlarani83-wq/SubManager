# SubManager — Subscription & Customer Management (Android)

A production-grade **Android Studio starter project** for managing digital subscriptions,
customers, inventory, renewals, payments and analytics — built for digital subscription
sellers and WhatsApp marketers.

This is the **buildable core foundation** of the full spec. It implements the most important
modules end-to-end with clean, scalable architecture so every remaining feature plugs in
without rewrites. See the **Roadmap** at the bottom for how the rest of the spec maps onto it.

---

## ✅ What's implemented (works today)

| Module | Status |
|---|---|
| **Dashboard** | Full — 18+ live KPI cards (customers, products, accounts, active/expired, expiring 0/3/7/15/30 days, warranty, due, sales, profit, monthly revenue, best seller) + 6-month revenue bar chart |
| **Customers** | Full CRUD — list, search (name/phone/email), favorite, detail (spend, due, subscriptions), add/edit, move-to-trash |
| **Subscriptions** | Full CRUD — list with status filters & color-coded expiry, search, add/edit with customer & product pickers, credentials (encrypted password/PIN/backup codes), validity presets, profit calc |
| **Subscription detail** | Full — credential view, dates, profit, payment, **one-click renewal** with renewal history |
| **Products / Inventory** | Full CRUD — available/sold/remaining stock, low-stock & out-of-stock alerts |
| **Renewals** | One-click renewal extends expiry, records history forever, recomputes profit |
| **Trash Bin** | Restore deleted customers; auto-purge query ready (30-day) |
| **Settings hub** | Navigable list with extension points for every remaining module |
| **Encryption** | Field-level AES-256-GCM via Android Keystore for passwords, PINs, backup codes |
| **Theming** | Material 3, dynamic color (Android 12+), automatic dark/light mode |
| **PDF Invoices** *(Phase 2)* | Generate a branded A4 PDF invoice per subscription with business info, customer/product details, totals, payment status and a verification **QR code**; auto-incrementing invoice numbers |
| **WhatsApp Sharing** *(Phase 2)* | One-tap: share invoice PDF to WhatsApp (or any app), and send login/credential details as a pre-filled WhatsApp message to the customer's number |
| **Expiry Reminders** *(Phase 2)* | Daily WorkManager job posts notifications for subscriptions expiring in 30/15/7/3/1/0 days; notification channel + Android 13 permission handled |
| **Business Info** *(Phase 2)* | Editable business name/address/phone/currency (DataStore) used on invoices — Settings → Business Information |

## 🏗️ Architecture

**MVVM + Clean Architecture**, single-module, organized by layer:

```
app/src/main/java/com/submanager/app/
├── core/
│   ├── crypto/        # CryptoManager — AES-256-GCM (Android Keystore)
│   └── util/          # DateUtils, Money, PasswordTools
├── data/
│   ├── local/
│   │   ├── entity/    # Customer, Product, Subscription, Renewal, Payment
│   │   ├── dao/       # Room DAOs (Flow-based, reactive)
│   │   ├── Converters.kt
│   │   └── AppDatabase.kt
│   └── repository/    # CustomerRepo, ProductRepo, SubscriptionRepo (auto enc/dec), RenewalRepo
├── domain/
│   ├── model/         # Enums (PaymentStatus, SubscriptionStatus, ...)
│   └── usecase/       # GetDashboardStatsUseCase, DashboardStats
├── di/                # Hilt modules (DatabaseModule)
└── ui/
    ├── theme/         # Material 3 Color / Type / Theme
    ├── navigation/    # Routes, TopLevelDestination, AppNavHost
    ├── components/    # StatCard, MiniBarChart, EmptyState, ConfirmDialog, ...
    ├── dashboard/  customers/  subscriptions/  products/  settings/  trash/
```

**Tech stack**
- Kotlin 2.0, Jetpack Compose + Material 3
- Room (reactive `Flow` DAOs) — offline-first local DB
- Hilt (dependency injection)
- Navigation-Compose
- AndroidX Security / DataStore / WorkManager / Coil (wired in Gradle, ready to use)
- Min SDK 24, Target SDK 34

---

## 🚀 How to build & run

### Option A — Android Studio (recommended)
1. Install **Android Studio Koala (2024.1)** or newer.
2. **File → Open** and select the unzipped `SubManager/` folder.
3. Let Gradle sync (it downloads Gradle 8.7 + dependencies automatically).
4. Pick an emulator or device and press **Run ▶**.

### Option B — Command line
```bash
cd SubManager
./gradlew assembleDebug      # builds app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # installs onto a connected device/emulator
```
> Requires JDK 17 and the Android SDK. Create a `local.properties` with `sdk.dir=/path/to/Android/Sdk`
> (Android Studio creates this automatically).

A Gradle wrapper (`gradlew`, `gradle/wrapper/gradle-wrapper.jar`) is included so no separate
Gradle install is needed.

---

## 🔐 Security notes
- Sensitive credential fields (`loginPassword`, `pin`, `backupCodes`) are encrypted with a
  hardware-backed AES-256-GCM key in the Android Keystore. The DB stores only ciphertext;
  `SubscriptionRepository` transparently decrypts when reading.
- `android:allowBackup="false"` and data-extraction rules exclude the DB from cloud backup.
- To add app-lock / biometric: `androidx.biometric` is the next step (permission already declared).

---

## 🗺️ Roadmap — mapping the full spec onto this foundation

The architecture is deliberately built so each remaining module is **additive**. Suggested phases:

**Phase 2 — Money & documents** ✅ *(largely implemented in this build)*
- ✅ Invoice generator (PDF) via `PdfDocument`; QR code via ZXing; share to WhatsApp / chooser via `Intent` + FileProvider
- ✅ WhatsApp deep-link messaging (`https://wa.me/<number>?text=...`) auto-filled with customer/product/login
- ✅ Expiry reminder notifications (30/15/7/3/1/expiry day) via WorkManager + Hilt
- ✅ Editable business info (DataStore) for invoice branding
- ⏭ Remaining: Payments ledger UI (entity + DAO already exist), Excel/CSV import & export (Apache POI / OpenCSV)

**Phase 3 — Engagement**
- Bulk WhatsApp messaging + reusable message templates
- Password manager screen (generator/strength already in `PasswordTools`)
- Telegram / Email share buttons (helpers already in `ShareUtils`)

**Phase 4 — Power features**
- Bulk multi-select operations (delete/edit/export/message/renew/tag)
- Quick actions on long-press, Undo snackbars, Archive system (flags already on entities)
- Version history table + restore; auto-save drafts
- Custom fields & dynamic forms (entity already has `customFields: Map`)

**Phase 5 — Scale & accounts**
- Multi-user roles (Admin/Manager/Staff/Sales) + activity logs
- Security: biometric/PIN app lock, auto-logout
- Backup & sync: local export, Google Drive backup, optional Firebase cloud sync
- Reports (daily/weekly/monthly/yearly) → PDF/Excel/CSV
- Utilities: QR/barcode scanner, calendar & renewal calendar, color labels, pinned customers

**Extension points already in code**
- `SettingsScreen` lists every Phase 2–5 module as a navigable row — wire each to its new screen.
- Entities already carry `isArchived`, `isTrashed`, `tags`, `customFields`, `attachments`,
  warranty and payment fields, so most features need only UI + a use case, not schema changes.

---

## 📄 License
Provided as a starter for your own commercial product. Customize freely.
