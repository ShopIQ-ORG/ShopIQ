//
//  CheckoutRepositoryImpl.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.repositories

import com.iti.data.dto.checkout.*
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.remote.checkout.CheckoutRemoteDataSource
import com.iti.data.utils.handleException
import com.iti.domain.models.Result
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.checkout.CheckoutRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckoutRepositoryImpl(
    private val remoteDataSource: CheckoutRemoteDataSource,
    private val tokenLocalDataSource: ShopifyTokenLocalDataSource
) : CheckoutRepository {

    override suspend fun createDraftOrder(
        cart: Cart,
        shippingAddress: Address?,
        email: String?
    ): Result<DraftOrder> = withContext(Dispatchers.IO) {
        safeCall {
            val customerId = tokenLocalDataSource.getCachedFields()?.customerId

            // Build MailingAddressInput
            val nameParts = shippingAddress?.name?.split(" ", limit = 2)
            val firstName = nameParts?.firstOrNull() ?: ""
            val lastName = nameParts?.getOrNull(1) ?: ""

            val mailingAddress = shippingAddress?.let {
                MailingAddressInput(
                    firstName = firstName,
                    lastName = lastName,
                    address1 = it.street,
                    address2 = "",
                    city = it.city,
                    province = it.city,
                    country = it.country,
                    zip = it.postalCode,
                    phone = ""
                )
            }

            // Build lineItems
            val lineItems = cart.items.map { item ->
                DraftOrderLineItemInput(
                    variantId = item.variantId,
                    quantity = item.quantity
                )
            }

            // Build appliedDiscount
            val appliedDiscount = cart.discountAmount?.let { discount ->
                AppliedDiscountInput(
                    title = cart.appliedPromoCode ?: "Discount",
                    description = "Cart discount",
                    value = discount.amount.toDoubleOrNull() ?: 0.0,
                    valueType = "FIXED_AMOUNT"
                )
            }

            // Build shippingLine
            val shippingLine = cart.shippingAmount?.let { shipping ->
                ShippingLineInput(
                    title = "Standard Shipping",
                    priceWithCurrency = MoneyInput(
                        amount = shipping.amount.toDoubleOrNull() ?: 0.0,
                        currencyCode = shipping.currencyCode
                    )
                )
            }

            // Build customAttributes
            val customAttributes = listOf(
                CustomAttributeInput(key = "cartId", value = cart.id)
            )

            val input = DraftOrderInput(
                customerId = customerId,
                email = email,
                lineItems = lineItems,
                shippingAddress = mailingAddress,
                billingAddress = mailingAddress,
                appliedDiscount = appliedDiscount,
                shippingLine = shippingLine,
                note = "Created from mobile app",
                customAttributes = customAttributes,
                tags = listOf("Mobile App"),
                taxExempt = false,
                useCustomerDefaultAddress = false
            )

            val dto = remoteDataSource.createDraftOrder(input)
            dto.toDomain()
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder> = withContext(Dispatchers.IO) {
        safeCall {
            remoteDataSource.completeDraftOrder(draftOrderId).toDomain()
        }
    }

    private fun DraftOrderDto.toDomain(): DraftOrder {
        return DraftOrder(
            id = id,
            totalPrice = totalPrice ?: "0.00",
            subtotalPrice = subtotalPrice ?: "0.00",
            totalTax = totalTax ?: "0.00",
            status = status,
            orderNumber = order?.name
        )
    }

    private inline fun <T> safeCall(block: () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }
}
