package com.iti.domain.repositories.categories

import com.iti.domain.models.Category
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getCategories(): Flow<Result<List<Category>>>
}
