package com.iti.domain.usecases.categories

import com.iti.domain.models.Category
import com.iti.domain.models.Result
import com.iti.domain.repositories.categories.CategoriesRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val categoriesRepository: CategoriesRepository
) {
    operator fun invoke(): Flow<Result<List<Category>>> {
        return categoriesRepository.getCategories()
    }
}
