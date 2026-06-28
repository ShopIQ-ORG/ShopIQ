package com.iti.data.sources.remote

import com.iti.data.GetMainCategoriesQuery

interface CategoryRemoteDataSource {
    suspend fun getCategories(): GetMainCategoriesQuery.Data
}
