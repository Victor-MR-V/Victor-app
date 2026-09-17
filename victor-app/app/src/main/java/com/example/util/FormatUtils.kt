package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FormatUtils {

    fun toBengaliDigits(input: String): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = java.lang.StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBengaliDigits(number: Long): String {
        return toBengaliDigits(number.toString())
    }

    fun toBengaliDigits(number: Int): String {
        return toBengaliDigits(number.toString())
    }

    fun formatTaka(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 0
        val formatted = formatter.format(amount)
        return "৳ " + toBengaliDigits(formatted)
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTomorrowDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun formatBengaliDate(dateString: String): String {
        if (dateString.isBlank()) return "নির্ধারিত নয়"
        return try {
            val parts = dateString.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = parts[1].toIntOrNull() ?: 1
                val day = parts[2]
                val monthNames = arrayOf(
                    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
                    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
                )
                val monthStr = if (month in 1..12) monthNames[month - 1] else month.toString()
                "${toBengaliDigits(day)} $monthStr, ${toBengaliDigits(year)}"
            } else {
                toBengaliDigits(dateString)
            }
        } catch (e: Exception) {
            toBengaliDigits(dateString)
        }
    }
}
