package com.submanager.app.core.util

import kotlin.random.Random

object PasswordTools {
    private const val UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    private const val LOWER = "abcdefghijkmnopqrstuvwxyz"
    private const val DIGITS = "23456789"
    private const val SYMBOLS = "!@#\$%^&*-_=+"

    fun generate(length: Int = 16, useSymbols: Boolean = true): String {
        val pool = UPPER + LOWER + DIGITS + if (useSymbols) SYMBOLS else ""
        return (1..length).map { pool[Random.nextInt(pool.length)] }.joinToString("")
    }

    /** Returns a strength score 0..4 */
    fun strength(password: String): Int {
        var score = 0
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.any { it.isDigit() } && password.any { it.isLetter() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return score.coerceIn(0, 4)
    }

    fun strengthLabel(score: Int): String = when (score) {
        0, 1 -> "Weak"
        2 -> "Fair"
        3 -> "Good"
        else -> "Strong"
    }
}
