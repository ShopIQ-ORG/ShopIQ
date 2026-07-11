package com.iti.domain.usecases.categories

import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class SaveCollectionTranslationUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(
        collectionId: String,
        locale: String,
        title: String,
        bodyHtml: String
    ): Flow<Result<Unit>> {
        return repository.saveCollectionTranslation(collectionId, locale, title, bodyHtml)
    }
}
