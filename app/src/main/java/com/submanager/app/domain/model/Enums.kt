package com.submanager.app.domain.model

enum class PaymentStatus { PAID, PARTIAL, DUE, REFUND }

enum class SubscriptionStatus { ACTIVE, EXPIRED, ARCHIVED, TRASHED }

enum class WarrantyStatus { ACTIVE, EXPIRED, NONE, CLAIMED }

enum class UserRole { ADMIN, MANAGER, STAFF, SALES }
