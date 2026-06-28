package com.iti.domain.usecases.products

import com.iti.domain.models.Brand
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetBrandsUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(): Flow<Result<List<Brand>>> {
        return repository.getBrands()
    }
}
