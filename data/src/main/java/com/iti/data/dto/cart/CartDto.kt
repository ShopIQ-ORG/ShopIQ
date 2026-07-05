package com.iti.data.dto.cart

data class CartDto(
    val id: String,
    val checkoutUrl: String,
    val lines: List<CartLineDto>,
    val discountCodes: List<DiscountCodeDto>,
    val subtotalAmount: MoneyDto,
    val totalAmount: MoneyDto,
    val totalTaxAmount: MoneyDto?,
    val totalDutyAmount: MoneyDto?,
    val buyerIdentity: CartBuyerIdentityDto?,
    val deliveryGroups: List<CartDeliveryGroupDto>
)

data class CartLineDto(
    val id: String,
    val quantity: Int,
    val amountPerQuantity: MoneyDto,
    val subtotalAmount: MoneyDto,
    val totalAmount: MoneyDto,
    val variantId: String,
    val variantTitle: String,
    val isAvailableForSale: Boolean,
    val quantityAvailable: Int?,
    val imageUrl: String,
    val imageAltText: String?,
    val priceAmount: String,
    val priceCurrencyCode: String,
    val compareAtPriceAmount: String?,
    val selectedOptions: List<SelectedOptionDto>,
    val productId: String,
    val productTitle: String,
    val productHandle: String,
    val productFeaturedImageUrl: String?,
    val vendor: String?
)

data class DiscountCodeDto(
    val code: String,
    val applicable: Boolean
)

data class MoneyDto(
    val amount: String,
    val currencyCode: String
)

data class SelectedOptionDto(
    val name: String,
    val value: String
)

data class CartBuyerIdentityDto(
    val email: String?,
    val phone: String?,
    val countryCode: String?
)

data class CartDeliveryGroupDto(
    val id: String,
    val selectedDeliveryOption: CartDeliveryOptionDto?,
    val deliveryOptions: List<CartDeliveryOptionDto>
)

data class CartDeliveryOptionDto(
    val handle: String,
    val title: String?,
    val estimatedCostAmount: MoneyDto
)