//
//  Currency.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.models

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val rateToUsd: Double
)
