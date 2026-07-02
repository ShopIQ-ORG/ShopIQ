package com.iti.data.sources.remote.shopifycustomer
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.data.core.executeOrThrow
import com.iti.data.dto.shopifycustomer.ShopifyCustomerDto
import com.iti.data.dto.shopifycustomer.ShopifyCustomerTokenDto
import com.iti.data.storefront.CustomerAccessTokenCreateMutation
import com.iti.data.storefront.CustomerAccessTokenRenewMutation
import com.iti.data.storefront.CustomerCreateMutation
import com.iti.data.storefront.type.CustomerAccessTokenCreateInput
import com.iti.data.storefront.type.CustomerCreateInput
import com.iti.domain.exceptions.AuthException

class ShopifyCustomerRemoteDataSourceImpl(
    private val storefrontClient: ApolloClient
) : ShopifyCustomerRemoteDataSource {

    override suspend fun createCustomer(email: String, fullName: String, password: String): ShopifyCustomerDto {
        val input = CustomerCreateInput(
            email = email,
            password = password,
            firstName = Optional.present(fullName)
        )
        val response = storefrontClient.mutation(CustomerCreateMutation(input)).executeOrThrow()
        val errors = response.data?.customerCreate?.customerUserErrors.orEmpty()
        if (errors.isNotEmpty()) throw AuthException.EmailAlreadyInUse()

        val customer = response.data?.customerCreate?.customer
            ?: throw AuthException.UnauthorizedAccess()

        return ShopifyCustomerDto(id = customer.id, email = email)
    }

    override suspend fun createAccessToken(email: String, password: String): ShopifyCustomerTokenDto {
        val input = CustomerAccessTokenCreateInput(email = email, password = password)
        val response = storefrontClient.mutation(CustomerAccessTokenCreateMutation(input)).executeOrThrow()
        val token = response.data?.customerAccessTokenCreate?.customerAccessToken
            ?: throw AuthException.InvalidCredentials()
        return ShopifyCustomerTokenDto(customerId = null, accessToken = token.accessToken, expiresAt = token.expiresAt)
    }

    override suspend fun renewAccessToken(accessToken: String): ShopifyCustomerTokenDto {
        val response = storefrontClient.mutation(CustomerAccessTokenRenewMutation(accessToken)).executeOrThrow()
        val token = response.data?.customerAccessTokenRenew?.customerAccessToken
            ?: throw AuthException.UnauthorizedAccess()
        return ShopifyCustomerTokenDto(customerId = null, accessToken = token.accessToken, expiresAt = token.expiresAt)
    }
}