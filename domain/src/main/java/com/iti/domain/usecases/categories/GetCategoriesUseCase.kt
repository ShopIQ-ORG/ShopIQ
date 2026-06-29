package com.iti.domain.usecases.categories

import com.iti.domain.models.Category
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(): Flow<Result<List<Category>>> {
        return repository.getMainCategories()
    }
}
