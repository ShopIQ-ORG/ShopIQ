package com.iti.data.sources.remote

import com.apollographql.apollo.ApolloClient
import com.iti.data.GetMainCategoriesQuery

class CategoryRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : CategoryRemoteDataSource {

    override suspend fun getCategories(): GetMainCategoriesQuery.Data {
        val response = apolloClient.query(GetMainCategoriesQuery()).execute()

        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }

        return response.data ?: throw Exception("Response data is null")
    }
}
