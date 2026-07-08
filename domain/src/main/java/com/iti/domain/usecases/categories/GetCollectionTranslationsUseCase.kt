package com.iti.domain.usecases.categories

import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetCollectionTranslationsUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(collectionId: String, locale: String): Flow<Result<Map<String, String>?>> {
        return repository.getCollectionTranslations(collectionId, locale)
    }
}
