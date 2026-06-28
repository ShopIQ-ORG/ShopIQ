package com.iti.data.repositories

import com.iti.data.sources.remote.CategoryRemoteDataSource
import com.iti.domain.models.Category
import com.iti.domain.models.Result as DomainResult
import com.iti.domain.repositories.categories.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CategoriesRepositoryImpl(
    private val remoteDataSource: CategoryRemoteDataSource
) : CategoriesRepository {

    override fun getCategories(): Flow<DomainResult<List<Category>>> = flow {
        emit(DomainResult.Loading)
        try {
            val data = remoteDataSource.getCategories()
            val categories = data.collections.edges.map { edge ->
                val node = edge.node
                Category(
                    id = node.id,
                    title = node.title,
                    handle = node.handle,
                    imageUrl = node.image?.url?.toString(),
                    productsCount = node.productsCount.count
                )
            }
            emit(DomainResult.Success(categories))
        } catch (e: IOException) {
            emit(DomainResult.Failure(e))
        } catch (e: Exception) {
            emit(DomainResult.Failure(e))
        }
    }
}
