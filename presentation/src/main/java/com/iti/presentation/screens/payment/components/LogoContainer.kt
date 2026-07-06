//
//  LogoContainer.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.payment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.SearchFieldLight

@Composable
fun LogoContainer(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, SearchFieldLight),
        shadowElevation = 0.5.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
            content()
        }
    }
}
