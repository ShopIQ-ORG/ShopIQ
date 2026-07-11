package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsByNumberUseCase(
    private val repository: ProductRepository
){
    operator fun invoke(count: Int = 10) : Flow<Result<List<Product>>> {
        return repository.getProductsByNumber(count)
    }
}