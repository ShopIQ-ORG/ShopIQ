package com.iti.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme

@Composable
fun CustomNetworkImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    backgroundColor: Color = Color.Transparent
) {
    val isDark = LocalDarkTheme.current
    val fallback = if (isDark) R.drawable.logo_dark else R.drawable.logo_light

    Box(modifier = modifier.background(backgroundColor)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .placeholder(fallback)
                .error(fallback)
                .fallback(fallback)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.matchParentSize()
        )
    }
}