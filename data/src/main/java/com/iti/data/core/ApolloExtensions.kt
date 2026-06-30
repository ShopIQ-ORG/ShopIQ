package com.iti.data.core

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation


suspend fun <D : Operation.Data> ApolloCall<D>.executeOrThrow(): ApolloResponse<D> {
    val response = this.execute()

    response.exception?.let { networkException ->
        throw networkException
    }

    return response
}
