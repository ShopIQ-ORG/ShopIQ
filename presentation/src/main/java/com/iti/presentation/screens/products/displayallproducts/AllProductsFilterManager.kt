package com.iti.presentation.screens.products.displayallproducts

import com.iti.domain.models.Product
import com.iti.presentation.screens.products.displayallproducts.AllProductsContract.FilterState
import com.iti.presentation.screens.products.displayallproducts.AllProductsContract.SortOption

class AllProductsFilterManager {
    fun apply(
        products: List<Product>,
        query: String,
        filterState: FilterState,
        sortOption: SortOption
    ): List<Product> {
        var result = products

        // Text search
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            result = result.filter { product ->
                product.title.contains(trimmedQuery, ignoreCase = true) ||
                (product.arTitle?.contains(trimmedQuery, ignoreCase = true) == true) ||
                product.vendor.contains(trimmedQuery, ignoreCase = true) ||
                product.productType.contains(trimmedQuery, ignoreCase = true) ||
                product.tags.any { it.contains(trimmedQuery, ignoreCase = true) }
            }
        }

        // Category (tags) - as it matches NavItem Collections
        val categories = filterState.selectedCategories
        if (categories.isNotEmpty()) {
            result = result.filter { product -> 
                product.tags.any { tag -> categories.any { it.equals(tag, ignoreCase = true) } }
            }
        }

        // Sub-category / Tag Name (productType)
        val subCategories = filterState.selectedSubCategories
        if (subCategories.isNotEmpty()) {
            result = result.filter { product ->
                subCategories.any { it.equals(product.productType, ignoreCase = true) } 
            }
        }

        // Brands (vendor)
        val brands = filterState.selectedBrands
        if (brands.isNotEmpty()) {
            result = result.filter { product -> 
                brands.any { it.equals(product.vendor, ignoreCase = true) } 
            }
        }

        // Sort
        result = when (sortOption) {
            SortOption.BEST_SELLING -> result // Already ordered by Shopify best-selling
            SortOption.PRICE_ASC    -> result.sortedBy { it.minPrice.amount.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_DESC   -> result.sortedByDescending { it.minPrice.amount.toDoubleOrNull() ?: 0.0 }
        }

        return result
    }
}
