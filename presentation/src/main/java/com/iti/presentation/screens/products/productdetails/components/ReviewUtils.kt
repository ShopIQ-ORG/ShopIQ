package com.iti.presentation.screens.products.productdetails.components

import com.iti.domain.models.ProductReview

fun getReviewsOrDefault(reviews: List<ProductReview>): List<ProductReview> {
    return reviews
}

fun formatReviewDate(rawDate: String): String {
    return try {
        // Try parsing ISO-8601 UTC timestamp
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(rawDate) ?: return rawDate

        val outputFormat = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        rawDate
    }
}
