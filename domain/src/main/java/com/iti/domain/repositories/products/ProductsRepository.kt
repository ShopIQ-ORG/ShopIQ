package com.iti.domain.repositories.products

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getProductsByNumber(count: Int = 10): Flow<Result<List<Product>>>
    fun getProductDetails(productId: Long): Flow<Result<Product>>
}
