package com.iti.data.repositories

import com.iti.data.dto.checkout.*
import com.iti.data.mappers.toDomain
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.remote.checkout.CheckoutRemoteDataSource
import com.iti.data.sources.remote.orders.OrdersRemoteDataSource
import com.iti.data.sources.remote.payment.PaymobRemoteDataSource
import com.iti.data.utils.handleException
import com.iti.domain.exceptions.AppException
import com.iti.domain.exceptions.OrderException
import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.PaymobIntentionResult
import com.iti.domain.repositories.order.OrderRepository
import com.iti.domain.util.ShopifyTokenProvider
import kotlin.collections.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val remote: OrdersRemoteDataSource,
    private val tokenProvider: ShopifyTokenProvider,
    private val paymobRemoteDataSource: PaymobRemoteDataSource,
    private val tokenLocalDataSource: ShopifyTokenLocalDataSource,
    private val secretKey: String,
    private val publicKey: String,
    private val checkoutRemoteDataSource: CheckoutRemoteDataSource
) : OrderRepository {

override suspend fun getOrders(): Result<List<Order>> {
        val accessToken = when (val tokenResult = tokenProvider.getValidToken()) {
            is Result.Success -> tokenResult.data.accessToken
            is Result.Failure -> return tokenResult
            is Result.Loading -> return Result.Loading
        }

        return safeCall {
            remote.getOrders(accessToken).map { it.toDomain() }
        }
    }

    

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

            val dto = checkoutRemoteDataSource.createDraftOrder(input)
            dto.toDomain()
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder> = withContext(Dispatchers.IO) {
        safeCall {
            checkoutRemoteDataSource.completeDraftOrder(draftOrderId).toDomain()
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

override suspend fun createPaymentIntention(
        amountCents: Long,
        currency: String,
        integrationId: Int
    ): Result<PaymobIntentionResult> {
        return try {
            val intentionResponse = paymobRemoteDataSource.createIntention(
                secretKey = secretKey,
                amountCents = amountCents,
                currency = currency,
                integrationId = integrationId
            )

            Result.Success(
                PaymobIntentionResult(
                    clientSecret = intentionResponse.client_secret,
                    publicKey = publicKey
                )
            )
        } catch (e: Exception) {
            Result.Failure(AppException.PaymentIntentionCreationFailed())
        }
    }
}
