package com.iti.data.repositories

import com.iti.data.sources.remote.CategoryRemoteDataSource
import com.iti.domain.models.Category
import com.iti.domain.models.Result
import com.iti.domain.repositories.categories.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class CategoriesRepositoryImpl(
    private val remoteDataSource: CategoryRemoteDataSource
) : CategoriesRepository {

    override fun getCategories(): Flow<Result<List<Category>>> = flow {
        emit(Result.Loading)
        try {
            val data = remoteDataSource.getCategories()
            val categories = data.collections.edges.map { edge ->
                val node = edge.node
                Category(
                    id = node.id,
                    title = node.title,
                    handle = node.handle,
                    imageUrl = node.image?.url,
                    productsCount = node.productsCount.count
                )
            }
            emit(Result.Success(categories))
        } catch (e: IOException) {
            emit(Result.Error(e))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
