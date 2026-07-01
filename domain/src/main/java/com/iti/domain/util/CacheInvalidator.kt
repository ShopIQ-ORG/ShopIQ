package com.iti.domain.util

interface CacheInvalidator {
    suspend fun invalidate()
}